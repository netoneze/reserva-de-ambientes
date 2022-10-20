package com.netoneze.ambientesreserva;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.netoneze.ambientesreserva.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    NavigationBarView bottomNavigationView;
    ManagementFragment managementFragment = new ManagementFragment();
    MyReservationsFragment myReservationsFragment = new MyReservationsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Ambient Reservation");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, myReservationsFragment, "").commit();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String tipoUser = bundle.getString(LoginActivity.TIPO);
            if (tipoUser.equals("Aluno")){
                bottomNavigationView.getMenu().removeItem(R.id.management_page);
            }
        }
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.my_reserve:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, myReservationsFragment).commit();
                    return true;
                case R.id.management_page:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, managementFragment).commit();
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