package com.netoneze.ambientesreserva;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.netoneze.ambientesreserva.modelo.Reservation;
import com.netoneze.ambientesreserva.modelo.User;
import com.netoneze.ambientesreserva.utils.AdapterListReservations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyReservationsFragment} factory method to
 * create an instance of this fragment.
 */
public class MyReservationsFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ViewGroup root;
    FloatingActionButton addReserveButton;
    ExpandableListView listView;
    private User currentUser = new User();
    public MyReservationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_reservations, container, false);
        listView = root.findViewById(R.id.listViewReservations);
        addReserveButton = root.findViewById(R.id.addReserveButton);
        addReserveButton.setOnClickListener(v -> {
            Intent addReserveIntent = new Intent(getActivity(), ReserveFormActivity.class);
            startActivityForResult(addReserveIntent, 0);
        });
        populaUser();
        return root;
    }

    public void populaUser() {
        DocumentReference docRef = db.collection("user").document(user.getUid());
        docRef.get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                DocumentSnapshot document1 = task1.getResult();
                if (document1.exists()) {
                    Log.d("documentUserData", "DocumentSnapshot data: " + document1.getData());
                    for (Map.Entry<String, Object> object : document1.getData().entrySet()) {
                        if (object.getKey().equals("type")) {
                            currentUser.setType(object.getValue().toString());
                        }
                    }
                } else {
                    Log.d("noDocumentError", "No such document");
                }
                if (currentUser.getType().equals("2")) {
                    populaListaTodasReservas();
                } else {
                    populaLista();
                }
            } else {
                Log.d("failMessage", "get failed with ", task1.getException());
            }
        });
    }

    public void populaLista() {
        List<Reservation> lista = new ArrayList<>();
        db.collection("reservation")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Map<String, Object>> documentMap = new HashMap<>();
                            documentMap.put(document.getId(), document.getData());

                            for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
                                Reservation reservation = new Reservation();
                                Log.d("keyvalue", "Key = " + entry.getKey() + " Value = " + entry.getValue());
                                for (Map.Entry<String, Object> entryMap2 : entry.getValue().entrySet()) {
                                    Log.d("keyvalue2", "Key = " + entryMap2.getKey() + " Value = " + entryMap2.getValue());
                                    if (entryMap2.getKey().equals("room")) {
                                        reservation.setRoom(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("date")) {
                                        reservation.setDate(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("startTime")) {
                                        reservation.setStartTime(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("endTime")) {
                                        reservation.setEndTime(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("purpose")) {
                                        reservation.setPurpose(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("status")) {
                                        reservation.setStatus(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("situation")) {
                                        reservation.setSituation(entryMap2.getValue().toString());
                                    }
                                    reservation.setDocumentId(entry.getKey());
                                }
                                lista.add(reservation);
                            }
                        }

                        List<String> lstGrupos = new ArrayList<>();

                        for (int i = 0 ; i < lista.size() ; i++){
                            lstGrupos.add(lista.get(i).getDocumentId());
                        }

                        HashMap<String, List<Reservation>> lstItensGrupo = new HashMap<>();

                        for (int i = 0 ; i < lista.size() ; i++){
                            lstItensGrupo.put(lstGrupos.get(i), lista.subList(i, i+1));
                        }

                        AdapterListReservations adapter = new AdapterListReservations(getActivity(), lstGrupos, lstItensGrupo);

                        listView.setAdapter(adapter);

                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void populaListaTodasReservas() {
        List<Reservation> lista = new ArrayList<>();
        db.collection("reservation")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Map<String, Object>> documentMap = new HashMap<>();
                            documentMap.put(document.getId(), document.getData());

                            for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
                                Reservation reservation = new Reservation();
                                Log.d("keyvalue", "Key = " + entry.getKey() + " Value = " + entry.getValue());
                                for (Map.Entry<String, Object> entryMap2 : entry.getValue().entrySet()) {
                                    Log.d("keyvalue2", "Key = " + entryMap2.getKey() + " Value = " + entryMap2.getValue());
                                    if (entryMap2.getKey().equals("room")) {
                                        reservation.setRoom(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("date")) {
                                        reservation.setDate(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("startTime")) {
                                        reservation.setStartTime(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("endTime")) {
                                        reservation.setEndTime(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("purpose")) {
                                        reservation.setPurpose(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("status")) {
                                        reservation.setStatus(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("situation")) {
                                        reservation.setSituation(entryMap2.getValue().toString());
                                    }
                                    reservation.setDocumentId(entry.getKey());
                                }
                                lista.add(reservation);
                            }
                        }

                        List<String> lstGrupos = new ArrayList<>();

                        for (int i = 0 ; i < lista.size() ; i++){
                            lstGrupos.add(lista.get(i).getDocumentId());
                        }

                        HashMap<String, List<Reservation>> lstItensGrupo = new HashMap<>();

                        for (int i = 0 ; i < lista.size() ; i++){
                            lstItensGrupo.put(lstGrupos.get(i), lista.subList(i, i+1));
                        }

                        AdapterListReservations adapter = new AdapterListReservations(getActivity(), lstGrupos, lstItensGrupo);

                        listView.setAdapter(adapter);

                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void populaListaTodasReservasBy(String status) {
        List<Reservation> lista = new ArrayList<>();
        db.collection("reservation")
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Map<String, Object>> documentMap = new HashMap<>();
                            documentMap.put(document.getId(), document.getData());

                            for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
                                Reservation reservation = new Reservation();
                                Log.d("keyvalue", "Key = " + entry.getKey() + " Value = " + entry.getValue());
                                for (Map.Entry<String, Object> entryMap2 : entry.getValue().entrySet()) {
                                    Log.d("keyvalue2", "Key = " + entryMap2.getKey() + " Value = " + entryMap2.getValue());
                                    if (entryMap2.getKey().equals("room")) {
                                        reservation.setRoom(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("date")) {
                                        reservation.setDate(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("startTime")) {
                                        reservation.setStartTime(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("endTime")) {
                                        reservation.setEndTime(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("purpose")) {
                                        reservation.setPurpose(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("status")) {
                                        reservation.setStatus(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("situation")) {
                                        reservation.setSituation(entryMap2.getValue().toString());
                                    }
                                    reservation.setDocumentId(entry.getKey());
                                }
                                lista.add(reservation);
                            }
                        }

                        List<String> lstGrupos = new ArrayList<>();

                        for (int i = 0 ; i < lista.size() ; i++){
                            lstGrupos.add(lista.get(i).getDocumentId());
                        }

                        HashMap<String, List<Reservation>> lstItensGrupo = new HashMap<>();

                        for (int i = 0 ; i < lista.size() ; i++){
                            lstItensGrupo.put(lstGrupos.get(i), lista.subList(i, i+1));
                        }

                        AdapterListReservations adapter = new AdapterListReservations(getActivity(), lstGrupos, lstItensGrupo);

                        listView.setAdapter(adapter);

                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void populaListaBy(String status) {
        List<Reservation> lista = new ArrayList<>();
        db.collection("reservation")
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("status", status)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Map<String, Object>> documentMap = new HashMap<>();
                            documentMap.put(document.getId(), document.getData());

                            for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
                                Reservation reservation = new Reservation();
                                Log.d("keyvalue", "Key = " + entry.getKey() + " Value = " + entry.getValue());
                                for (Map.Entry<String, Object> entryMap2 : entry.getValue().entrySet()) {
                                    Log.d("keyvalue2", "Key = " + entryMap2.getKey() + " Value = " + entryMap2.getValue());
                                    if (entryMap2.getKey().equals("room")) {
                                        reservation.setRoom(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("date")) {
                                        reservation.setDate(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("startTime")) {
                                        reservation.setStartTime(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("endTime")) {
                                        reservation.setEndTime(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("purpose")) {
                                        reservation.setPurpose(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("status")) {
                                        reservation.setStatus(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("situation")) {
                                        reservation.setSituation(entryMap2.getValue().toString());
                                    }
                                    reservation.setDocumentId(entry.getKey());
                                }
                                lista.add(reservation);
                            }
                        }

                        List<String> lstGrupos = new ArrayList<>();

                        for (int i = 0 ; i < lista.size() ; i++){
                            lstGrupos.add(lista.get(i).getDocumentId());
                        }

                        HashMap<String, List<Reservation>> lstItensGrupo = new HashMap<>();

                        for (int i = 0 ; i < lista.size() ; i++){
                            lstItensGrupo.put(lstGrupos.get(i), lista.subList(i, i+1));
                        }

                        AdapterListReservations adapter = new AdapterListReservations(getActivity(), lstGrupos, lstItensGrupo);

                        listView.setAdapter(adapter);

                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (currentUser.getType().equals("2")) {
                populaListaTodasReservas();
            } else {
                populaLista();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}