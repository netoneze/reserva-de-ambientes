package com.netoneze.ambientesreserva;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReserveFragment} factory method to
 * create an instance of this fragment.
 */
public class ReserveFragment extends Fragment {
    private Calendar calendarDataTarefa;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ViewGroup root;
    Spinner spinnerRooms;
    Button buttonSave, buttonClean;
    EditText editTextDate, editTextStartTime, editTextEndTime;

    public ReserveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_reserve, container, false);

        //Buttons
        buttonSave = (Button) root.findViewById(R.id.buttonSave);
        buttonClean = (Button) root.findViewById(R.id.buttonClean);

        //Reserve fields
        spinnerRooms = (Spinner) root.findViewById(R.id.spinnerRoom);
        editTextDate = (EditText) root.findViewById(R.id.editTextDate);
        editTextStartTime = (EditText) root.findViewById(R.id.editTextStartTime);
        editTextEndTime = (EditText) root.findViewById(R.id.editTextEndTime);

        buttonSave.setOnClickListener(v -> {
            // Do something in response to button click
            if (spinnerRooms.getSelectedItemPosition() == 0){
                Toast.makeText(getActivity(), "Select a room!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (editTextDate.getText().toString().equals("") ||
                editTextStartTime.getText().toString().equals("") ||
                editTextEndTime.getText().toString().equals("")
            ) {
                Toast.makeText(getActivity(), "Fill all the fields!", Toast.LENGTH_SHORT).show();
                return;
            }
            Map <String, Object> reserve = new HashMap<>();
            reserve.put("date", editTextDate.getText().toString());
            reserve.put("room", spinnerRooms.getSelectedItem().toString());
            reserve.put("startTime", editTextStartTime.getText().toString());
            reserve.put("endTime", editTextEndTime.getText().toString());
            reserve.put("userId", user.getUid());

            db.collection("reservation")
                    .add(reserve)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getActivity(), "Saved room!", Toast.LENGTH_SHORT).show();
                        cleanFields();
                    })
                    .addOnFailureListener(e -> Log.w("failure", "Error adding document", e));
        });

        buttonClean.setOnClickListener(v -> cleanFields());

        populaSpinner();
        return root;
    }

    public void cleanFields() {
        editTextDate.setText(null);
        editTextStartTime.setText(null);
        editTextEndTime.setText(null);
        spinnerRooms.setSelection(0);
    }

    public void populaSpinner() {
        ArrayList<String> lista = new ArrayList<>();

        db.collection("room")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for ( QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Map<String, Object>> documentMap = new HashMap<>();
                            documentMap.put(document.getId(), document.getData());

                            for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
                                Log.d("keyvalue", "Key = " + entry.getKey() + " Value = " + entry.getValue());
                                for (Map.Entry<String, Object> entryMap2 : entry.getValue().entrySet()) {
                                    Log.d("keyvalue2", "Key = " + entryMap2.getKey() + " Value = " + entryMap2.getValue());
                                    if (entryMap2.getKey().equals("name")) {
                                        lista.add(entryMap2.getValue().toString());
                                    }
                                }
                            }
                        }
                    } else {
                        Log.w("", "Error getting documents.", task.getException());
                    }
                });

        lista.add(getString(R.string.room_select));

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, lista);

        spinnerRooms.setAdapter(adapter);
    }
}