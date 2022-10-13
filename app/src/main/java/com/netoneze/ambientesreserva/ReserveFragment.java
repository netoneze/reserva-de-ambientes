package com.netoneze.ambientesreserva;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.netoneze.ambientesreserva.modelo.Reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    EditText editTextDate, editTextStartTime, editTextEndTime, editTextPurpose;
    Date myReservationDateStartTime, myReservationDateEndTime, myReservationDateStartTimeLimit, myReservationDateEndTimeLimit;
    Calendar myCalendarDate = Calendar.getInstance();
    Calendar myCalendarTime = Calendar.getInstance();

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
        editTextPurpose = (EditText) root.findViewById(R.id.editTextPurpose);

        TimePickerDialog.OnTimeSetListener startTimeListener = (view, hour, minute) -> {
            myCalendarTime.set(Calendar.HOUR_OF_DAY, hour);
            myCalendarTime.set(Calendar.MINUTE, minute);
            updateStartTimeLabel();
        };

        TimePickerDialog.OnTimeSetListener endTimeListener = (view, hour, minute) -> {
            myCalendarTime.set(Calendar.HOUR_OF_DAY, hour);
            myCalendarTime.set(Calendar.MINUTE, minute);
            updateEndTimeLabel();
        };

        editTextStartTime.setOnClickListener(v -> new TimePickerDialog(getContext(), startTimeListener, myCalendarTime
                .get(Calendar.HOUR_OF_DAY), myCalendarTime.get(Calendar.MINUTE), true).show());
        editTextEndTime.setOnClickListener(v -> new TimePickerDialog(getContext(), endTimeListener, myCalendarTime
                .get(Calendar.HOUR_OF_DAY), myCalendarTime.get(Calendar.MINUTE), true).show());

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarDate.set(Calendar.YEAR, year);
            myCalendarDate.set(Calendar.MONTH, monthOfYear);
            myCalendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        };

        editTextDate.setOnClickListener(v -> new DatePickerDialog(getContext(), date, myCalendarDate
                .get(Calendar.YEAR), myCalendarDate.get(Calendar.MONTH),
                myCalendarDate.get(Calendar.DAY_OF_MONTH)).show());

        buttonSave.setOnClickListener(v -> {
            //Validation
            if (spinnerRooms.getSelectedItemPosition() == 0){
                Toast.makeText(getActivity(), "Select a room!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (editTextDate.getText().toString().equals("") ||
                editTextStartTime.getText().toString().equals("") ||
                editTextEndTime.getText().toString().equals("") ||
                editTextPurpose.getText().toString().equals("")
            ) {
                Toast.makeText(getActivity(), "Fill all the fields!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
                Date todayDate = new Date();
                Date reservationDate = sdf.parse(editTextDate.getText().toString() + " " + editTextStartTime.getText().toString());

                assert reservationDate != null;
                if (reservationDate.before(todayDate)) {
                    Toast.makeText(getActivity(), "Incorrect date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.parseInt(editTextStartTime.getText().toString()) < 0) {
                    Toast.makeText(getActivity(), "Incorrect StartTime", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.parseInt(editTextEndTime.getText().toString()) < 0) {
                    Toast.makeText(getActivity(), "Incorrect EndTime", Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            Map <String, Object> reserve = new HashMap<>();
            reserve.put("date", editTextDate.getText().toString());
            reserve.put("room", spinnerRooms.getSelectedItem().toString());
            reserve.put("startTime", editTextStartTime.getText().toString());
            reserve.put("endTime", editTextEndTime.getText().toString());
            reserve.put("userId", user.getUid());
            reserve.put("purpose", editTextPurpose.getText().toString());

            List<Reservation> lista = new ArrayList<>();
            db.collection("reservation")
                    .whereEqualTo("room", spinnerRooms.getSelectedItem().toString())
                    .whereEqualTo("date", editTextDate.getText().toString())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                HashMap<String, Map<String, Object>> documentMap = new HashMap<>();
                                documentMap.put(document.getId(), document.getData());

                                for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
                                    Reservation reservation = new Reservation();
                                    Log.d("keyvalue", "Key = " + entry.getKey() + " Value = " + entry.getValue());
                                    for (Map.Entry<String, Object> entryMap2 : entry.getValue().entrySet()) {
                                        Log.d("keyvalue2", "Key = " + entryMap2.getKey() + " Value = " + entryMap2.getValue());
                                        if (entryMap2.getKey().equals("room")) {
                                            reservation.setRoom(entryMap2.getValue().toString());
                                        }
                                        if (entryMap2.getKey().equals("date")) {
                                            reservation.setDate(entryMap2.getValue().toString());
                                        }
                                        if (entryMap2.getKey().equals("startTime")) {
                                            reservation.setStartTime(entryMap2.getValue().toString());
                                        }
                                        if (entryMap2.getKey().equals("endTime")) {
                                            reservation.setEndTime(entryMap2.getValue().toString());
                                        }
                                        if (entryMap2.getKey().equals("purpose")) {
                                            reservation.setPurpose(entryMap2.getValue().toString());
                                        }
                                    }
                                    lista.add(reservation);
                                }
                            }
                            verifyReserveTime(lista, reserve);
                        } else {
                            Log.d("erro", "Error getting documents: ", task.getException());
                        }
                    });
        });

        buttonClean.setOnClickListener(v -> cleanFields());

        populaSpinner();
        return root;
    }

    private void updateDateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

        editTextDate.setText(sdf.format(myCalendarDate.getTime()));
    }

    private void updateStartTimeLabel() {
        String myFormat = "HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

        editTextStartTime.setText(sdf.format(myCalendarTime.getTime()));
    }

    private void updateEndTimeLabel() {
        String myFormat = "HH:mm"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt","BR"));

        editTextEndTime.setText(sdf.format(myCalendarTime.getTime()));
    }

    public void cleanFields() {
        editTextDate.setText("");
        editTextStartTime.setText("");
        editTextEndTime.setText("");
        editTextPurpose.setText("");
        spinnerRooms.setSelection(0);
    }

    public void verifyReserveTime(List<Reservation> lista, Map <String, Object> reserve) {
        boolean saveReservation = true;

        SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);

        try {
            myReservationDateStartTime = sdfTime.parse(editTextDate.getText().toString() +
                    " " +
                    editTextStartTime.getText().toString());
            myReservationDateEndTime = sdfTime.parse(editTextDate.getText().toString() +
                    " " +
                    editTextEndTime.getText().toString());

            myReservationDateStartTimeLimit = sdfTime.parse(editTextDate.getText().toString() + " " + "7:00");
            myReservationDateEndTimeLimit = sdfTime.parse(editTextDate.getText().toString() + " " + "23:00");

            if (myReservationDateStartTime.before(myReservationDateStartTimeLimit)) {
                Toast.makeText(getActivity(), "The start time must be equal or after 7:00", Toast.LENGTH_SHORT).show();
                return;
            } else if (myReservationDateEndTime.after(myReservationDateEndTimeLimit)) {
                Toast.makeText(getActivity(), "The end time must be equal or before 23:00", Toast.LENGTH_SHORT).show();
                return;
            }

            assert myReservationDateEndTime != null;
            if (myReservationDateEndTime.before(myReservationDateStartTime)) {
                Toast.makeText(getActivity(), "The end date must be after the start date!", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!lista.isEmpty()) {
            for (Reservation reserveItem : lista) {
                try {
                    Date listReservationDateStartTime = sdfTime.parse(reserveItem.getDate() +
                            " " +
                            reserveItem.getStartTime());
                    Date listReservationDateEndTime = sdfTime.parse(reserveItem.getDate() +
                            " " +
                            reserveItem.getEndTime());

                    if ((myReservationDateStartTime.after(listReservationDateStartTime) || myReservationDateStartTime.compareTo(listReservationDateStartTime) == 0) &&
                            (myReservationDateEndTime.before(listReservationDateEndTime) || myReservationDateEndTime.compareTo(listReservationDateEndTime) == 0)) {
                        saveReservation = false;
                    }

                    if ((myReservationDateStartTime.after(listReservationDateStartTime) || myReservationDateStartTime.compareTo(listReservationDateStartTime) == 0) &&
                            (myReservationDateStartTime.before(listReservationDateEndTime) || myReservationDateStartTime.compareTo(listReservationDateEndTime) == 0) &&
                            myReservationDateEndTime.after(listReservationDateEndTime)) {
                        saveReservation = false;
                    }

                    if ((myReservationDateStartTime.before(listReservationDateStartTime) || myReservationDateStartTime.compareTo(listReservationDateStartTime) == 0) &&
                            ((myReservationDateEndTime.before(listReservationDateEndTime) || myReservationDateEndTime.compareTo(listReservationDateEndTime) == 0) &&
                            myReservationDateEndTime.after(listReservationDateStartTime))) {
                        saveReservation = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (saveReservation) {
            saveReserve(reserve);
        } else {
            Toast.makeText(getActivity(), "There already is a reservation at this date/time!", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveReserve(Map <String, Object> reserve) {
        db.collection("reservation")
                .add(reserve)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getActivity(), "Saved reserve!", Toast.LENGTH_SHORT).show();
                    cleanFields();
                })
                .addOnFailureListener(e -> Log.w("failure", "Error adding document", e));
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