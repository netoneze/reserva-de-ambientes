package com.netoneze.ambientesreserva;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReserveFragment} factory method to
 * create an instance of this fragment.
 */
public class ReserveFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ViewGroup root;
    Spinner spinnerRooms;
    public ReserveFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_reserve, container, false);
        spinnerRooms = (Spinner) root.findViewById(R.id.spinnerRoom);
        populaSpinner();
        return root;
    }

    public void populaSpinner(){
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