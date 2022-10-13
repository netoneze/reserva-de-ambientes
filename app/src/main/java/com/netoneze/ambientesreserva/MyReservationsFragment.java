package com.netoneze.ambientesreserva;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.netoneze.ambientesreserva.modelo.Reservation;
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
    ExpandableListView listView;
    public MyReservationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_reservations, container, false);
        listView = root.findViewById(R.id.listViewReservations);
        populaLista();
        return root;
    }

    public void populaLista(){
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
}