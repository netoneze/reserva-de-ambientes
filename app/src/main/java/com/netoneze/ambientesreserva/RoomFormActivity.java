package com.netoneze.ambientesreserva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.netoneze.ambientesreserva.modelo.Room;
import com.netoneze.ambientesreserva.modelo.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomFormActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText nameEditText;
    private Spinner typeSpinner, responsibleSpinner;
    private RadioGroup responsibleRadioGroup, automaticApprovalRadioGroup;
    private RadioButton responsibleRadioButtonNo, responsibleRadioButtonYes, automaticApprovalOnlyFederalRadioButton, automaticApprovalEveryoneRadioButton, automaticApprovalNobodyRadioButton;
    private CheckBox chkBoxNeedsKey, chkBoxHasAirConditioner, chkBoxHasNetworkPoint, chxBoxHasProjector, chxBoxHasTV;
    private Integer automaticApproval;
    private ArrayList<User> listaUsers = new ArrayList<>();
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_form);
        bundle = getIntent().getExtras();

        setTitle("Create a Room");
        nameEditText = findViewById(R.id.editTextRoomName);
        typeSpinner = findViewById(R.id.spinnerRoomType);
        responsibleSpinner = findViewById(R.id.spinnerResponsible);

        populaUsers();

        populaSpinnerResponsible(true);
        populaSpinner();

        chkBoxNeedsKey = findViewById(R.id.checkBoxNeedsKey);
        chkBoxHasAirConditioner = findViewById(R.id.checkBoxHasAirConditioner);
        chkBoxHasNetworkPoint = findViewById(R.id.checkBoxHasNetworkPoint);
        chxBoxHasProjector = findViewById(R.id.checkBoxHasProjector);
        chxBoxHasTV = findViewById(R.id.checkBoxHasTV);

        automaticApprovalRadioGroup = findViewById(R.id.radioGroupAutomaticApproval);
        automaticApprovalOnlyFederalRadioButton = findViewById(R.id.radioButtonOnlyFederalServant);
        automaticApprovalEveryoneRadioButton = findViewById(R.id.radioButtonEveryone);
        automaticApprovalNobodyRadioButton = findViewById(R.id.radioButtonNoOne);

        if (bundle != null) {
            Room room = bundle.getParcelable(ManagementFragment.ROOM);
            Map<String, Boolean> roomSpecifications = room.getSpecifications();

            nameEditText.setText(room.getName());
            switch (room.getType()) {
                case "Classroom":
                    typeSpinner.setSelection(1);
                    break;
                case "Meeting Room":
                    typeSpinner.setSelection(2);
                    break;
                case "Informatic Laboratory":
                    typeSpinner.setSelection(3);
                case "Amphitheater":
                    typeSpinner.setSelection(4);
                default:
                    break;
            }

            switch (room.getAutomaticApproval()) {
                case 0:
                    automaticApprovalOnlyFederalRadioButton.setChecked(true);
                    break;
                case 1:
                    automaticApprovalEveryoneRadioButton.setChecked(true);
                    break;
                case 2:
                    automaticApprovalNobodyRadioButton.setChecked(true);
                    break;
                default:
                    break;
            }



            for(Map.Entry<String, Boolean> entry : roomSpecifications.entrySet()) {
                switch (entry.getKey()) {
                    case "necessita_chave":
                        if (entry.getValue()) {
                            chkBoxNeedsKey.setChecked(true);
                        }
                        break;
                    case "possui_ar_condicionado":
                        if (entry.getValue()) {
                            chkBoxHasAirConditioner.setChecked(true);
                        }
                        break;
                    case "possui_ponto_rede_habilitado":
                        if (entry.getValue()) {
                            chkBoxHasNetworkPoint.setChecked(true);
                        }
                        break;
                    case "possui_projetor":
                        if (entry.getValue()) {
                            chxBoxHasProjector.setChecked(true);
                        }
                        break;
                    case "possui_tv":
                        if (entry.getValue()) {
                            chxBoxHasTV.setChecked(true);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void beginSaveRoom() {
        //Validation
        if (nameEditText.getText().toString().equals("")) {
            Toast.makeText(this, "The room must have a Name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (typeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select a type!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (automaticApprovalRadioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Select an automatic approval type!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (responsibleSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Select a responsible!", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (automaticApprovalRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radioButtonOnlyFederalServant:
                automaticApproval = 0;
                break;
            case R.id.radioButtonEveryone:
                automaticApproval = 1;
                break;
            case R.id.radioButtonNoOne:
                automaticApproval = 2;
                break;
            default:
                automaticApproval = -1;
                break;
        }

        String roomName = nameEditText.getText().toString().toUpperCase();
        Map<String, Object> room = new HashMap<>();
        Map<String, Boolean> especificacoes = new HashMap<>();
        room.put("aprovacaoAutomatica", automaticApproval);
        room.put("type", typeSpinner.getSelectedItem().toString());
        room.put("name", roomName);

        for (User firebaseUser : listaUsers) {
            if (firebaseUser.getUsername().equals(responsibleSpinner.getSelectedItem().toString())) {
                room.put("responsibleUid", firebaseUser.getUserId());
            }
        }

        especificacoes.put("necessita_chave", chkBoxNeedsKey.isChecked());
        especificacoes.put("possui_ar_condicionado", chkBoxHasAirConditioner.isChecked());
        especificacoes.put("possui_ponto_rede_habilitado", chkBoxHasNetworkPoint.isChecked());
        especificacoes.put("possui_projetor", chxBoxHasProjector.isChecked());
        especificacoes.put("possui_tv", chxBoxHasTV.isChecked());
        room.put("especificacoes", especificacoes);

        db.collection("room").document(roomName)
                .set(room)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Saved room!", Toast.LENGTH_SHORT).show();
                    Intent intentListagem = new Intent(this, ManagementFragment.class);
                    setResult(Activity.RESULT_OK, intentListagem);
                    finish();
                })
                .addOnFailureListener(e -> Log.w("failure", "Error adding document", e));
    }

    public void populaUsers() {
        db.collection("user")
                .whereEqualTo("type", 1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for ( QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Map<String, Object>> documentMap = new HashMap<>();
                            documentMap.put(document.getId(), document.getData());

                            for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
                                Log.d("keyvalue", "Key = " + entry.getKey() + " Value = " + entry.getValue());
                                User authUser = new User();
                                authUser.setUserId(entry.getKey());
                                for (Map.Entry<String, Object> entryMap2 : entry.getValue().entrySet()) {
                                    Log.d("keyvalue2", "Key = " + entryMap2.getKey() + " Value = " + entryMap2.getValue());
                                    if (entryMap2.getKey().equals("username")) {
                                        authUser.setUsername(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("type")) {
                                        authUser.setType(entryMap2.getValue().toString());
                                    }
                                }
                                listaUsers.add(authUser);
                            }
                        }
                        populaSpinnerResponsible(false);
                        setResponsibleSpinnerOnEdit();
                    } else {
                        Log.w("", "Error getting documents.", task.getException());
                    }
                });
    }

    public void populaSpinner() {
        ArrayList<String> lista = new ArrayList<>();

        lista.add(getString(R.string.room_select)); // 0
        lista.add("Classroom"); // 1
        lista.add("Meeting Room"); // 2
        lista.add("Informatic Laboratory"); // 3
        lista.add("Amphitheater"); // 4
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);

        typeSpinner.setAdapter(adapter);
    }

    public void populaSpinnerResponsible(Boolean onCreate) {
        ArrayList<String> lista = new ArrayList<>();

        lista.add(getString(R.string.responsible_select)); // 0

        if (!onCreate) {
            for(User firebaseUser : listaUsers) {
                lista.add(firebaseUser.getUsername());
            }
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lista);

        responsibleSpinner.setAdapter(adapter);
    }

    public void setResponsibleSpinnerOnEdit() {
        if (bundle != null) {
            Room room = bundle.getParcelable(ManagementFragment.ROOM);
            for (int i = 0; i < responsibleSpinner.getCount() ; i++) {
                String username = "";
                for(User firebaseUser : listaUsers) {
                    if (firebaseUser.getUserId().equals(room.getResponsibleUid())) {
                        username = firebaseUser.getUsername();
                    }
                }
                if(responsibleSpinner.getItemAtPosition(i).toString().equals(username)) {
                    responsibleSpinner.setSelection(i);
                }
            }
        }

    }
    private void cleanFields() {
        nameEditText.setText("");
        typeSpinner.setSelection(0);
        responsibleSpinner.setSelection(0);
        chkBoxNeedsKey.setChecked(false);
        chkBoxHasAirConditioner.setChecked(false);
        chkBoxHasNetworkPoint.setChecked(false);
        chxBoxHasProjector.setChecked(false);
        chxBoxHasTV.setChecked(false);
        automaticApprovalRadioGroup.clearCheck();
        responsibleRadioButtonNo.setChecked(true);
        Toast.makeText(getApplicationContext(), "Cleaned fields!", Toast.LENGTH_SHORT).show();
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
                beginSaveRoom();
                break;
            case R.id.btnCleanFields:
                cleanFields();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}