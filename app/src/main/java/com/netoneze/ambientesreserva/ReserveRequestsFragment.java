package com.netoneze.ambientesreserva;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.netoneze.ambientesreserva.modelo.Reservation;
import com.netoneze.ambientesreserva.modelo.Room;
import com.netoneze.ambientesreserva.modelo.User;
import com.netoneze.ambientesreserva.utils.AdapterListRequests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyReservationsFragment} factory method to
 * create an instance of this fragment.
 */
public class ReserveRequestsFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ViewGroup root;
    ExpandableListView listView;
    ArrayList<Room> responsibleRoomsList = new ArrayList<>();
    List<Reservation> reservationList = new ArrayList<>();
    private User currentUser = new User();
    public ReserveRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_reservations_requests, container, false);
        getActivity().setTitle("Reservation Requests");
        listView = root.findViewById(R.id.listViewReservationsRequests);
        responsibleRoomsList = new ArrayList<>();
        reservationList = new ArrayList<>();
        populaUser();
        return root;
    }

    public void populaSalasResponsavel() {
        db.collection("room")
                .whereEqualTo("responsibleUid", user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Map<String, Object>> documentMap = new HashMap<>();
                            documentMap.put(document.getId(), document.getData());

                            for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
                                Room room = new Room();
                                Log.d("keyvalue", "Key = " + entry.getKey() + " Value = " + entry.getValue());
                                for (Map.Entry<String, Object> entryMap2 : entry.getValue().entrySet()) {
                                    Log.d("keyvalue2", "Key = " + entryMap2.getKey() + " Value = " + entryMap2.getValue());
                                    if (entryMap2.getKey().equals("name")) {
                                        room.setName(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("aprovacaoAutomatica")) {
                                        room.setAutomaticApproval(Integer.parseInt(entryMap2.getValue().toString()));
                                    }
                                    if (entryMap2.getKey().equals("type")) {
                                        room.setType(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("especificacoes")) {
                                        room.setSpecifications((Map<String, Boolean>) entryMap2.getValue());
                                    }
                                    if (entryMap2.getKey().equals("responsibleUid")) {
                                        room.setResponsibleUid(entryMap2.getValue().toString());
                                    }
                                }
                                responsibleRoomsList.add(room);
                            }
                        }
                        populaReservas();
                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void populaSalasResponsavelTodas() {
        db.collection("room")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Map<String, Object>> documentMap = new HashMap<>();
                            documentMap.put(document.getId(), document.getData());

                            for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
                                Room room = new Room();
                                Log.d("keyvalue", "Key = " + entry.getKey() + " Value = " + entry.getValue());
                                for (Map.Entry<String, Object> entryMap2 : entry.getValue().entrySet()) {
                                    Log.d("keyvalue2", "Key = " + entryMap2.getKey() + " Value = " + entryMap2.getValue());
                                    if (entryMap2.getKey().equals("name")) {
                                        room.setName(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("aprovacaoAutomatica")) {
                                        room.setAutomaticApproval(Integer.parseInt(entryMap2.getValue().toString()));
                                    }
                                    if (entryMap2.getKey().equals("type")) {
                                        room.setType(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("especificacoes")) {
                                        room.setSpecifications((Map<String, Boolean>) entryMap2.getValue());
                                    }
                                    if (entryMap2.getKey().equals("responsibleUid")) {
                                        room.setResponsibleUid(entryMap2.getValue().toString());
                                    }
                                }
                                responsibleRoomsList.add(room);
                            }
                        }
                        populaReservas();
                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void populaReservas() {
        for(Room room : responsibleRoomsList) {
            db.collection("reservation")
                    .whereEqualTo("room", room.getName())
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
                                        if (entryMap2.getKey().equals("username")) {
                                            reservation.setUserName(entryMap2.getValue().toString());
                                        }
                                        reservation.setDocumentId(entry.getKey());
                                    }
                                    reservationList.add(reservation);
                                }
                            }
                            populaLista(reservationList);
                        } else {
                            Log.d("erro", "Error getting documents: ", task.getException());
                        }
                    });
        }

    }

    public void populaLista(List<Reservation> reservationList) {
        List<String> lstGrupos = new ArrayList<>();

        for (int i = 0; i < reservationList.size(); i++) {
            lstGrupos.add(reservationList.get(i).getDocumentId());
        }

        HashMap<String, List<Reservation>> lstItensGrupo = new HashMap<>();

        for (int i = 0; i < reservationList.size(); i++) {
            lstItensGrupo.put(lstGrupos.get(i), reservationList.subList(i, i + 1));
        }

        AdapterListRequests adapter = new AdapterListRequests(getActivity(), lstGrupos, lstItensGrupo);

        listView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
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
                        if (object.getKey().equals("username")) {
                            currentUser.setUsername(object.getValue().toString());
                        }
                    }
                } else {
                    Log.d("noDocumentError", "No such document");
                }
                if (currentUser.getType().equals("2")) {
                    populaSalasResponsavelTodas();
                } else {
                    populaSalasResponsavel();
                }
            } else {
                Log.d("failMessage", "get failed with ", task1.getException());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            populaSalasResponsavel();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
