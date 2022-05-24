package com.netoneze.ambientesreserva;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;
import com.netoneze.ambientesreserva.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {
    NavigationBarView bottomNavigationView;
    ManagementFragment managementFragment = new ManagementFragment();
    ReserveFragment reserveFragment = new ReserveFragment();
    AgendaFragment agendaFragment = new AgendaFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, reserveFragment, "").commit();
                    return true;
                case R.id.agenda_page:
                    Toast.makeText(MainActivity.this, "Agenda", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, agendaFragment).commit();
                    return true;
                case R.id.management_page:
                    Toast.makeText(MainActivity.this, "Schedule", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, managementFragment).commit();
                    return true;
            }
            return true;
        });


    }
}