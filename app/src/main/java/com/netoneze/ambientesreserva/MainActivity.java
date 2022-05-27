package com.netoneze.ambientesreserva;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.netoneze.ambientesreserva.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    NavigationBarView bottomNavigationView;
    ManagementFragment managementFragment = new ManagementFragment();
    ReserveFragment reserveFragment = new ReserveFragment();
    MyReservationsFragment myReservationsFragment = new MyReservationsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Ambient Reservation");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, reserveFragment, "").commit();

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
                case R.id.reserve_page:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, reserveFragment, "").commit();
                    return true;
                case R.id.agenda_page:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, myReservationsFragment).commit();
                    return true;
                case R.id.management_page:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, managementFragment).commit();
                    return true;
            }
            return true;
        });


    }
}