package com.netoneze.ambientesreserva;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.netoneze.ambientesreserva.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    NavigationBarView bottomNavigationView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = null;

    final MyReservationsFragment fragment1 = new MyReservationsFragment();
    final ManagementFragment fragment2 = new ManagementFragment();
    final ReserveRequestsFragment fragment3 = new ReserveRequestsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Ambient Reservation");
        user = FirebaseAuth.getInstance().getCurrentUser();

        fm.beginTransaction().add(R.id.fragment_container_view, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.fragment_container_view, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragment_container_view,fragment1, "1").commit();

        assert user != null;
        if (user.getDisplayName() == null) {
            Intent usernameIntent = new Intent(this, NameActivity.class);
            startActivity(usernameIntent);
        }
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && user != null) {
            String tipoUser = bundle.getString(LoginActivity.TIPO);
            if (tipoUser.equals("Aluno")) {
                bottomNavigationView.getMenu().removeItem(R.id.management_page);
                bottomNavigationView.getMenu().removeItem(R.id.reservation_requests);
            } else if (tipoUser.equals("Servidor")) {
                db.collection("room")
                        .whereEqualTo("responsibleUid", user.getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot document = task.getResult();
                                if (document.getDocuments().isEmpty()) {
                                    bottomNavigationView.getMenu().removeItem(R.id.management_page);
                                    bottomNavigationView.getMenu().removeItem(R.id.reservation_requests);
                                }
                            }
                        });
            }
        }
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.my_reserve:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;
                case R.id.management_page:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;
                case R.id.reservation_requests:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.logout_menu) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // ...
                        }
                    });

            navigateUpTo(new Intent(getBaseContext(), MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}