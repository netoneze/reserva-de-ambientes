package com.netoneze.ambientesreserva;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.netoneze.ambientesreserva.modelo.Reservation;
import com.netoneze.ambientesreserva.modelo.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReserveFormActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    User currentUser = new User();
    Spinner spinnerRooms;
    EditText editTextDate, editTextStartTime, editTextEndTime, editTextPurpose;
    Date myReservationDateStartTime, myReservationDateEndTime, myReservationDateStartTimeLimit, myReservationDateEndTimeLimit;
    Calendar myCalendarDate = Calendar.getInstance();
    Calendar myCalendarTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        setTitle(getString(R.string.create_a_reservation));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Reserve fields
        spinnerRooms = findViewById(R.id.spinnerRoom);
        editTextDate = findViewById(R.id.editTextDate);
        editTextStartTime = findViewById(R.id.editTextStartTime);
        editTextEndTime = findViewById(R.id.editTextEndTime);
        editTextPurpose = findViewById(R.id.editTextPurpose);

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

        editTextStartTime.setOnClickListener(v -> new TimePickerDialog(this, startTimeListener, myCalendarTime
                .get(Calendar.HOUR_OF_DAY), myCalendarTime.get(Calendar.MINUTE), true).show());
        editTextEndTime.setOnClickListener(v -> new TimePickerDialog(this, endTimeListener, myCalendarTime
                .get(Calendar.HOUR_OF_DAY), myCalendarTime.get(Calendar.MINUTE), true).show());

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendarDate.set(Calendar.YEAR, year);
            myCalendarDate.set(Calendar.MONTH, monthOfYear);
            myCalendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        };

        editTextDate.setOnClickListener(v -> new DatePickerDialog(this, date, myCalendarDate
                .get(Calendar.YEAR), myCalendarDate.get(Calendar.MONTH),
                myCalendarDate.get(Calendar.DAY_OF_MONTH)).show());

        populaSpinner();
        populaUser();
    }

    private void beginSaveReserve() {
        //Validation
        if (spinnerRooms.getSelectedItemPosition() == 0) {
            Toast.makeText(this, R.string.select_a_room, Toast.LENGTH_SHORT).show();
            return;
        }
        if (editTextDate.getText().toString().equals("") ||
                editTextStartTime.getText().toString().equals("") ||
                editTextEndTime.getText().toString().equals("") ||
                editTextPurpose.getText().toString().equals("")
        ) {
            Toast.makeText(this, R.string.fill_all_field, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
            Date todayDate = new Date();
            Date reservationDate = sdf.parse(editTextDate.getText().toString() + " " + editTextStartTime.getText().toString());

            assert reservationDate != null;
            if (reservationDate.before(todayDate)) {
                Toast.makeText(this, R.string.incorrect_date, Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(editTextStartTime.getText().toString()) < 0) {
                Toast.makeText(this, R.string.incorrect_starttime, Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(editTextEndTime.getText().toString()) < 0) {
                Toast.makeText(this, R.string.incorrect_endtime, Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> reserve = new HashMap<>();
        reserve.put("date", editTextDate.getText().toString());
        reserve.put("room", spinnerRooms.getSelectedItem().toString());
        reserve.put("startTime", editTextStartTime.getText().toString());
        reserve.put("endTime", editTextEndTime.getText().toString());
        reserve.put("userId", user.getUid());
        reserve.put("purpose", editTextPurpose.getText().toString());
        reserve.put("username", user.getDisplayName());

        String userType = "";
        switch(currentUser.getType()) {
            case "0":
                userType = "Student";
                break;
            case "1":
                userType = "Federal Employee";
                break;
            case "2":
                userType = "Admin";
                break;
            default:
                break;
        }

        reserve.put("usertype", userType);
        reserve.put("situation", "active");

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
                                    if (entryMap2.getKey().equals("status")) {
                                        reservation.setStatus(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("situation")) {
                                        reservation.setSituation(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("username")) {
                                        reservation.setUserName(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("usertype")) {
                                        reservation.setUsertype(entryMap2.getValue().toString());
                                    }
                                }
                                lista.add(reservation);
                            }
                        }
                        DocumentReference docRef = db.collection("room").document(spinnerRooms.getSelectedItem().toString());
                        docRef.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot document1 = task1.getResult();
                                if (document1.exists()) {
                                    String reserveStatus = "";
                                    Log.d("documentUserData", "DocumentSnapshot data: " + document1.getData());
                                    for (Map.Entry<String, Object> object : document1.getData().entrySet()) {
                                        if (object.getKey().equals("aprovacaoAutomatica")) {
                                            if ( (Long) object.getValue() == 0) {
                                                if (currentUser.getType().equals("1") || currentUser.getType().equals("2") ) {
                                                    reserveStatus = "approved";
                                                }
                                                if (currentUser.getType().equals("0")) {
                                                    reserveStatus = "pending";
                                                }
                                            } else if ( (Long) object.getValue() == 1) {
                                                reserveStatus = "approved";
                                            } else if ( (Long) object.getValue() == 2) {
                                                reserveStatus = "pending";
                                            }
                                        }
                                    }
                                    reserve.put("status", reserveStatus);
                                    verifyReserveTime(lista, reserve);
                                } else {
                                    Log.d("noDocumentError", "No such document");
                                }
                            } else {
                                Log.d("failMessage", "get failed with ", task1.getException());
                            }
                        });
                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
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
        Toast.makeText(this, R.string.cleaned_fields, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, R.string.starttime_after_7, Toast.LENGTH_SHORT).show();
                return;
            } else if (myReservationDateEndTime.after(myReservationDateEndTimeLimit)) {
                Toast.makeText(this, R.string.endtime_before_23, Toast.LENGTH_SHORT).show();
                return;
            }

            long difference_In_Time = myReservationDateEndTime.getTime() - myReservationDateStartTime.getTime();
            long difference_In_Hours = TimeUnit.MILLISECONDS.toHours(difference_In_Time) % 24;
            if (difference_In_Hours == 0) {
                Toast.makeText(this, R.string.reservation_must_have_60minutes, Toast.LENGTH_SHORT).show();
                return;
            }
            if (difference_In_Hours > 4) {
                Toast.makeText(this, R.string.maximum_limit_reservation, Toast.LENGTH_SHORT).show();
                return;
            }
            assert myReservationDateEndTime != null;
            if (myReservationDateEndTime.before(myReservationDateStartTime)) {
                Toast.makeText(this, R.string.enddate_must_be_after_startdate, Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!lista.isEmpty()) {
            for (Reservation reserveItem : lista) {
                try {
                    if (reserveItem.getSituation().equals("cancelled")) {
                        continue;
                    }
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
            Toast.makeText(this, R.string.there_is_already_a_reservation, Toast.LENGTH_SHORT).show();
        }
    }

    public void saveReserve(Map <String, Object> reserve) {
        db.collection("reservation")
                .add(reserve)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), R.string.saved_reserve, Toast.LENGTH_SHORT).show();
                    Intent intentListagem = new Intent(this, ManagementFragment.class);
                    setResult(Activity.RESULT_OK, intentListagem);
                    finish();
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
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);

        spinnerRooms.setAdapter(adapter);
    }

    public void populaUser() {
        DocumentReference docRef = db.collection("user").document(user.getUid());
        docRef.get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DocumentSnapshot document1 = task1.getResult();
                if (document1.exists()) {
                    Log.d("documentUserData", "DocumentSnapshot data: " + document1.getData());
                    for (Map.Entry<String, Object> object : document1.getData().entrySet()) {
                        if (object.getKey().equals("type")) {
                            currentUser.setType(object.getValue().toString());
                        }
                        if (object.getKey().equals("username")) {
                            currentUser.setUsername(object.getValue().toString());
                        }
                    }
                } else {
                    Log.d("noDocumentError", "No such document");
                }
            } else {
                Log.d("failMessage", "get failed with ", task1.getException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_bar_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btnSave:
                beginSaveReserve();
                break;
            case R.id.btnCleanFields:
                cleanFields();
                break;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
