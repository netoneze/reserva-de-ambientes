package com.netoneze.ambientesreserva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netoneze.ambientesreserva.modelo.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomFormActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText nameEditText;
    private Spinner typeSpinner;
    private CheckBox chkBoxNeedsKey, chkBoxHasAirConditioner, chkBoxHasNetworkPoint, chxBoxHasProjector, chxBoxHasTV, chkAutomaticApproval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_form);
        Bundle bundle = getIntent().getExtras();

        nameEditText = findViewById(R.id.editTextRoomName);
        typeSpinner = (Spinner) findViewById(R.id.spinnerRoomType);

        populaSpinner();

        chkBoxNeedsKey = findViewById(R.id.checkBoxNeedsKey);
        chkBoxHasAirConditioner = findViewById(R.id.checkBoxHasAirConditioner);
        chkBoxHasNetworkPoint = findViewById(R.id.checkBoxHasNetworkPoint);
        chxBoxHasProjector = findViewById(R.id.checkBoxHasProjector);
        chxBoxHasTV = findViewById(R.id.checkBoxHasTV);
        chkAutomaticApproval = findViewById(R.id.automaticApproval);

        Button buttonSave = findViewById(R.id.buttonSaveRoom);
        Button buttonClean = findViewById(R.id.buttonCleanRoom);

        buttonSave.setOnClickListener(v -> {
            //Validation
            if (nameEditText.getText().toString().equals("")) {
               Toast.makeText(this, "The room must have a Name", Toast.LENGTH_SHORT).show();
               return;
            }
            if (typeSpinner.getSelectedItemPosition() == 0){
                Toast.makeText(this, "Select a type!", Toast.LENGTH_SHORT).show();
                return;
            }

            String roomName = nameEditText.getText().toString().toUpperCase();
            Map<String, Object> room = new HashMap<>();
            Map<String, Boolean> especificacoes = new HashMap<>();
            room.put("aprovacaoAutomatica", chkAutomaticApproval.isChecked());
            room.put("type", typeSpinner.getSelectedItem().toString());
            room.put("name", roomName);
            room.put("responsibleUid", user.getUid());
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
                        cleanFields();
                        Intent intentListagem = new Intent(this, ManagementFragment.class);
                        setResult(Activity.RESULT_OK, intentListagem);
                        finish();
                    })
                    .addOnFailureListener(e -> Log.w("failure", "Error adding document", e));
        });

        buttonClean.setOnClickListener(v -> cleanFields());

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
            chkAutomaticApproval.setChecked(room.getAutomaticApproval());

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

    private void cleanFields() {
        nameEditText.setText("");
        typeSpinner.setSelection(0);
        chkBoxNeedsKey.setChecked(false);
        chkBoxHasAirConditioner.setChecked(false);
        chkBoxHasNetworkPoint.setChecked(false);
        chxBoxHasProjector.setChecked(false);
        chxBoxHasTV.setChecked(false);
        chkAutomaticApproval.setChecked(false);
        Toast.makeText(getApplicationContext(), "Cleaned fields!", Toast.LENGTH_SHORT).show();
    }
}