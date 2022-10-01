package com.netoneze.ambientesreserva;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RoomFormActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText nameEditText, typeEditText;
    private CheckBox chkBoxNeedsKey, chkBoxHasAirConditioner, chkBoxHasNetworkPoint, chxBoxHasProjector, chxBoxHasTV, chkAutomaticApproval;
    private Button buttonSave, buttonClean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_form);

        nameEditText = findViewById(R.id.editTextRoomName);
        typeEditText = findViewById(R.id.editTextRoomType);

        chkBoxNeedsKey = findViewById(R.id.checkBoxNeedsKey);
        chkBoxHasAirConditioner = findViewById(R.id.checkBoxHasAirConditioner);
        chkBoxHasNetworkPoint = findViewById(R.id.checkBoxHasNetworkPoint);
        chxBoxHasProjector = findViewById(R.id.checkBoxHasProjector);
        chxBoxHasTV = findViewById(R.id.checkBoxHasTV);
        chkAutomaticApproval = findViewById(R.id.automaticApproval);

        buttonSave = findViewById(R.id.buttonSaveRoom);
        buttonClean = findViewById(R.id.buttonCleanRoom);

        buttonSave.setOnClickListener(v -> {
            Map<String, Object> room = new HashMap<>();
            Map<String, Boolean> especificacoes = new HashMap<>();
            room.put("aprovacaoAutomatica", chkAutomaticApproval.isChecked());
            room.put("type", typeEditText.getText().toString());
            room.put("name", nameEditText.getText().toString());
            room.put("responsibleUid", user.getUid());
            especificacoes.put("necessita_chave", chkBoxNeedsKey.isSelected());
            especificacoes.put("possui_ar_condicionado", chkBoxHasAirConditioner.isSelected());
            especificacoes.put("possui_ponto_rede_habilitado", chkBoxHasNetworkPoint.isSelected());
            especificacoes.put("possui_projetor", chxBoxHasProjector.isSelected());
            especificacoes.put("possui_tv", chxBoxHasTV.isSelected());
            room.put("especificacoes", especificacoes);

            db.collection("room")
                    .add(room)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "Saved room!", Toast.LENGTH_SHORT).show();
                        cleanFields();
                    })
                    .addOnFailureListener(e -> Log.w("failure", "Error adding document", e));
        });

        buttonClean.setOnClickListener(v -> cleanFields());
    }

    private void cleanFields() {
        nameEditText.setText("");
        typeEditText.setText("");

        chkBoxNeedsKey.setChecked(false);
        chkBoxHasAirConditioner.setChecked(false);
        chkBoxHasNetworkPoint.setChecked(false);
        chxBoxHasProjector.setChecked(false);
        chxBoxHasTV.setChecked(false);
        chkAutomaticApproval.setChecked(false);
    }
}