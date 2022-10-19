package com.netoneze.ambientesreserva.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.netoneze.ambientesreserva.MainActivity;
import com.netoneze.ambientesreserva.databinding.ActivityLoginBinding;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    public static final String TIPO = "TIPO";
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    FirebaseUser user = null;
    final String[] username = new String[1];
    final Long[] userType = new Long[1];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Ambient Reservation");
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            DocumentReference docRef = db.collection("user").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    DocumentSnapshot document = task1.getResult();
                    if (document.exists()) {
                        Log.d("documentUserData", "DocumentSnapshot data: " + document.getData());
                        for (Map.Entry<String, Object> object : document.getData().entrySet()) {
                            if (object.getKey().equals("username")) {
                                Log.d("userData", object.getValue().toString());
                                username[0] = object.getValue().toString();
                                Log.d("userData2", username[0]);
                            }
                            if (object.getKey().equals("type")) {
                                if ( (Long) object.getValue() == 0) {
                                    intent.putExtra(TIPO, "Aluno");
                                }
                                Log.d("userType", object.getValue().toString());
                                userType[0] = (Long) object.getValue();
                            }
                        }

                        startActivity(intent);
                        setResult(Activity.RESULT_OK);
                        loadingProgressBar.setVisibility(View.GONE);
                    } else {
                        Log.d("noDocumentError", "No such document");
                    }
                } else {
                    loadingProgressBar.setVisibility(View.GONE);
                    Log.d("failMessage", "get failed with ", task1.getException());
                }
            });
        }

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            assert user != null;
                            Log.i("login", "signInWithEmail:success" + user.getEmail());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            DocumentReference docRef = db.collection("user").document(user.getUid());
                            docRef.get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot document = task1.getResult();
                                    if (document.exists()) {
                                        Log.d("documentUserData", "DocumentSnapshot data: " + document.getData());
                                        for (Map.Entry<String, Object> object : document.getData().entrySet()) {
                                            if (object.getKey().equals("username")) {
                                                Log.d("userData", object.getValue().toString());
                                                username[0] = object.getValue().toString();
                                                Log.d("userData2", username[0]);
                                            }
                                            if (object.getKey().equals("type")) {
                                                if ( (Long) object.getValue() == 0) {
                                                    intent.putExtra(TIPO, "Aluno");
                                                }
                                                Log.d("userType", object.getValue().toString());
                                                userType[0] = (Long) object.getValue();
                                            }
                                        }
                                        loadingProgressBar.setVisibility(View.GONE);
                                        startActivity(intent);
                                        setResult(Activity.RESULT_OK);
                                        passwordEditText.setText("");
                                        usernameEditText.setText("");
                                    } else {
                                        Log.d("noDocumentError", "No such document");
                                    }
                                } else {
                                    Log.d("failMessage", "get failed with ", task1.getException());
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Login falhou!", Toast.LENGTH_SHORT).show();
                            loadingProgressBar.setVisibility(View.GONE);
                        }
                    });

        });
    }
}