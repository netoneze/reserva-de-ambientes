package com.netoneze.ambientesreserva;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.netoneze.ambientesreserva.modelo.User;
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
    User currentUser = new User();

    MenuItem menuItemAll = null;
    MenuItem menuItemPending = null;
    MenuItem menuItemApproved = null;
    MenuItem menuItemDispproved = null;

    String activeCheckFilter = "";
    String activeCheckFilter1 = "";
    String activeCheckFilter2 = "";
    String activeCheckFilter3 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.title_activity_login));
        user = FirebaseAuth.getInstance().getCurrentUser();

        fm.beginTransaction().add(R.id.fragment_container_view, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.fragment_container_view, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragment_container_view,fragment1, "1").commit();
        assert user != null;
        if (user.getDisplayName() == null) {
            Intent usernameIntent = new Intent(this, NameActivity.class);
            startActivity(usernameIntent);
        } else {
            currentUser.setUsername(user.getDisplayName());
        }
        setTitle(getString(R.string.my_reservations_title));
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && user != null) {
            String tipoUser = bundle.getString(LoginActivity.TIPO);
            currentUser.setType(bundle.getString(LoginActivity.TIPO));
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
                    setTitle(getString(R.string.my_reservations_title));
                    active = fragment1;
                    if (!activeCheckFilter1.isEmpty()) {
                        setSelectedItemColor(activeCheckFilter1);
                    }
                    return true;
                case R.id.management_page:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    setTitle(getString(R.string.room_management_title));
                    active = fragment2;
                    cleanAllMenuItemsCheck();
                    return true;
                case R.id.reservation_requests:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    setTitle(getString(R.string.reservation_requests_title));
                    active = fragment3;
                    if (!activeCheckFilter3.isEmpty()) {
                        setSelectedItemColor(activeCheckFilter3);
                    }
                    return true;
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_bar_menu, menu);

        menuItemAll = menu.findItem(R.id.filter_all);
        menuItemPending = menu.findItem(R.id.filter_pending);
        menuItemApproved = menu.findItem(R.id.filter_approved);
        menuItemDispproved = menu.findItem(R.id.filter_disapproved);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fm.beginTransaction().detach(active).commitNow();
                    fm.beginTransaction().attach(active).commitNow();
                    Toast.makeText(this, R.string.refreshed, Toast.LENGTH_SHORT).show();
                    if (active == fragment1 && !activeCheckFilter1.isEmpty()) {
                        setSelectedItemColor("all");
                    }
                    if (active == fragment3 && !activeCheckFilter3.isEmpty()) {
                        if (currentUser.getType().equals("Aluno") || currentUser.getType().equals("Servidor")) {
                            setSelectedItemColor("pending");
                        } else {
                            setSelectedItemColor("all");
                        }
                    }
                } else {
                    fm.beginTransaction().detach(active).attach(active).commit();
                    Toast.makeText(this, R.string.refreshed, Toast.LENGTH_SHORT).show();
                    if (active == fragment1 && !activeCheckFilter1.isEmpty()) {
                        setSelectedItemColor(activeCheckFilter1);
                    }
                    if (active == fragment3 && !activeCheckFilter3.isEmpty()) {
                        setSelectedItemColor(activeCheckFilter3);
                    }
                }
                break;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(task -> {
                            NotificationManager notificationManager = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                notificationManager = getSystemService(NotificationManager.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    notificationManager.deleteNotificationChannel("123123");
                                }
                            }
                            Toast.makeText(this, R.string.logged_out, Toast.LENGTH_SHORT).show();
                        });

                navigateUpTo(new Intent(getBaseContext(), MainActivity.class));
                break;
            case R.id.filter_all:
                if (active == fragment1) {
                    if (currentUser.getType().equals("Aluno") || currentUser.getType().equals("Servidor")) {
                        fragment1.populaLista();
                        Toast.makeText(this, R.string.listing_all, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("all");
                    } else {
                        fragment1.populaListaTodasReservas();
                        Toast.makeText(this, R.string.listing_all, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("all");
                    }
                }
                if (active == fragment2) {
                    Toast.makeText(this, R.string.nothing_to_filter, Toast.LENGTH_SHORT).show();
                }
                if (active == fragment3) {
                    if (currentUser.getType().equals("Aluno") || currentUser.getType().equals("Servidor")) {
                        fragment3.populaSalasResponsavel();
                        Toast.makeText(this, R.string.listing_all, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("all");
                    } else {
                        fragment3.populaSalasResponsavelTodas();
                        Toast.makeText(this, R.string.listing_all, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("all");
                    }
                }
                break;
            case R.id.filter_pending:
                if (active == fragment1) {
                    if (currentUser.getType().equals("Aluno") || currentUser.getType().equals("Servidor")) {
                        fragment1.populaListaBy("pending");
                        Toast.makeText(this, R.string.listing_pending, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("pending");
                    } else {
                        fragment1.populaListaTodasReservasBy("pending");
                        Toast.makeText(this, R.string.listing_pending, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("pending");
                    }
                }
                if (active == fragment2) {
                    Toast.makeText(this, R.string.nothing_to_filter, Toast.LENGTH_SHORT).show();
                }
                if (active == fragment3) {
                    if (currentUser.getType().equals("Aluno") || currentUser.getType().equals("Servidor")) {
                        fragment3.populaSalasResponsavelBy("pending");
                        Toast.makeText(this, R.string.listing_pending, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("pending");
                    } else {
                        fragment3.populaSalasResponsavelTodasBy("pending");
                        Toast.makeText(this, R.string.listing_pending, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("pending");
                    }
                }
                break;
            case R.id.filter_approved:
                if (active == fragment1) {
                    if (currentUser.getType().equals("Aluno") || currentUser.getType().equals("Servidor")) {
                        fragment1.populaListaBy("approved");
                        Toast.makeText(this, R.string.listing_approved, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("approved");
                    } else {
                        fragment1.populaListaTodasReservasBy("approved");
                        Toast.makeText(this, R.string.listing_approved, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("approved");
                    }
                }
                if (active == fragment2) {
                    Toast.makeText(this, R.string.nothing_to_filter, Toast.LENGTH_SHORT).show();
                }
                if (active == fragment3) {
                    if (currentUser.getType().equals("Aluno") || currentUser.getType().equals("Servidor")) {
                        fragment3.populaSalasResponsavelBy("approved");
                        Toast.makeText(this, R.string.listing_approved, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("approved");
                    } else {
                        fragment3.populaSalasResponsavelTodasBy("approved");
                        Toast.makeText(this, R.string.listing_approved, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("approved");
                    }
                }
                break;
            case R.id.filter_disapproved:
                if (active == fragment1) {
                    if (currentUser.getType().equals("Aluno") || currentUser.getType().equals("Servidor")) {
                        fragment1.populaListaBy("disapproved");
                        Toast.makeText(this, R.string.listing_disapproved, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("disapproved");
                    } else {
                        fragment1.populaListaTodasReservasBy("disapproved");
                        Toast.makeText(this, R.string.listing_disapproved, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("disapproved");
                    }
                }
                if (active == fragment2) {
                    Toast.makeText(this, R.string.nothing_to_filter, Toast.LENGTH_SHORT).show();
                }
                if (active == fragment3) {
                    if (currentUser.getType().equals("Aluno") || currentUser.getType().equals("Servidor")) {
                        fragment3.populaSalasResponsavelBy("disapproved");
                        Toast.makeText(this, R.string.listing_disapproved, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("disapproved");
                    } else {
                        fragment3.populaSalasResponsavelTodasBy("disapproved");
                        Toast.makeText(this, R.string.listing_disapproved, Toast.LENGTH_SHORT).show();
                        setSelectedItemColor("disapproved");
                    }
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setActiveCheck(String activeCheck) {
        if (active == fragment1) {
            activeCheckFilter1 = activeCheck;
        }
        if (active == fragment2) {
            activeCheckFilter2 = activeCheck;
        }
        if (active == fragment3) {
            activeCheckFilter3 = activeCheck;
        }
    }
    public void setSelectedItemColor(String item) {
        cleanAllMenuItemsCheck();
        switch (item) {
            case "all":
                if (!menuItemAll.getTitle().toString().equals(getString(R.string.all) + " ✅")) {
                    menuItemAll.setTitle(getString(R.string.all) + " ✅");
                    setActiveCheck(item);
                }
                break;
            case "pending":
                if (!menuItemPending.getTitle().toString().equals(getString(R.string.pending) + " ✅")) {
                    menuItemPending.setTitle(getString(R.string.pending) + " ✅");
                    setActiveCheck(item);
                }
                break;
            case "approved":
                if (!menuItemApproved.getTitle().toString().equals(getString(R.string.approved) + " ✅")) {
                    menuItemApproved.setTitle(getString(R.string.approved) + " ✅");
                    setActiveCheck(item);
                }
                break;
            case "disapproved":
                if (!menuItemDispproved.getTitle().toString().equals(getString(R.string.disapproved) + " ✅")) {
                    menuItemDispproved.setTitle(getString(R.string.disapproved) + " ✅");
                    setActiveCheck(item);
                }
                break;
            default:
                break;
        }
    }

    public void cleanAllMenuItemsCheck() {
        if (menuItemAll.getTitle().toString().equals(getString(R.string.all) + " ✅")) {
            menuItemAll.setTitle(getString(R.string.all));
        }
        if (menuItemPending.getTitle().toString().equals(getString(R.string.pending) + " ✅")) {
            menuItemPending.setTitle(getString(R.string.pending));
        }
        if (menuItemApproved.getTitle().toString().equals(getString(R.string.approved) + " ✅")) {
            menuItemApproved.setTitle(getString(R.string.approved));
        }
        if (menuItemDispproved.getTitle().toString().equals(getString(R.string.disapproved) + " ✅")) {
            menuItemDispproved.setTitle(getString(R.string.disapproved));
        }
    }
}