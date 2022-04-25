package com.netoneze.ambientesreserva;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationBarView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) item -> {
            switch (item.getItemId()) {
                case R.id.home_page:
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.schedule_page:
                    Toast.makeText(MainActivity.this, "Schedule", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.agenda_page:
                    Toast.makeText(MainActivity.this, "Agenda", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.profile_page:
                    Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        });
    }
}