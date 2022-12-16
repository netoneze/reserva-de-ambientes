package com.netoneze.ambientesreserva;

import static android.app.Activity.RESULT_OK;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
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
    List<DocumentReference> documentReferences = new ArrayList<>();
    public ReserveRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_reservations_requests, container, false);
        listView = root.findViewById(R.id.listViewReservationsRequests);
        responsibleRoomsList = new ArrayList<>();
        reservationList = new ArrayList<>();
        populaUser();
        return root;
    }

    public void populaSalasResponsavel() {
        responsibleRoomsList = new ArrayList<>();
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
        responsibleRoomsList = new ArrayList<>();
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
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.activeCheckFilter3 = "all";
                        populaReservas();
                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void populaReservas() {
        reservationList = new ArrayList<>();
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
                                        if (entryMap2.getKey().equals("situation")) {
                                            reservation.setSituation(entryMap2.getValue().toString());
                                        }
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
                                        if (entryMap2.getKey().equals("usertype")) {
                                            reservation.setUsertype(entryMap2.getValue().toString());
                                        }
                                        reservation.setDocumentId(entry.getKey());
                                    }
                                    reservationList.add(reservation);
                                }
                            }
//                            setListenerForMyReservationsRequests(reservationList);
                            populaLista(reservationList);
                        } else {
                            Log.d("erro", "Error getting documents: ", task.getException());
                        }
                    });
        }

    }

    public void populaSalasResponsavelBy(String status) {
        responsibleRoomsList = new ArrayList<>();
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
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.activeCheckFilter3 = status;
                        populaReservasBy(status);
                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void populaReservasBy(String status) {
        reservationList = new ArrayList<>();
        for(Room room : responsibleRoomsList) {
            db.collection("reservation")
                    .whereEqualTo("room", room.getName())
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
                                        if (entryMap2.getKey().equals("username")) {
                                            reservation.setUserName(entryMap2.getValue().toString());
                                        }
                                        if (entryMap2.getKey().equals("usertype")) {
                                            reservation.setUsertype(entryMap2.getValue().toString());
                                        }
                                        if (entryMap2.getKey().equals("situation")) {
                                            reservation.setSituation(entryMap2.getValue().toString());
                                        }
                                        reservation.setDocumentId(entry.getKey());
                                    }
                                    reservationList.add(reservation);
                                }
                            }
//                            setListenerForMyReservationsRequests(reservationList);
                            populaLista(reservationList);
                        } else {
                            Log.d("erro", "Error getting documents: ", task.getException());
                        }
                    });
        }

    }

    public void populaSalasResponsavelTodasBy(String status) {
        responsibleRoomsList = new ArrayList<>();
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
                        populaReservasBy(status);
                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
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
                    reservationList = new ArrayList<>();
                    responsibleRoomsList = new ArrayList<>();
                    populaSalasResponsavelTodas();
                } else {
                    reservationList = new ArrayList<>();
                    responsibleRoomsList = new ArrayList<>();
                    populaSalasResponsavelBy("pending");
                }
            } else {
                Log.d("failMessage", "get failed with ", task1.getException());
            }
        });
    }

    public void setListenerForMyReservationsRequests(List<Reservation> listaReservations) {
        for (Reservation reservation : listaReservations) {
            final DocumentReference docRef = db.collection("reservation").document(reservation.getDocumentId());
            docRef.addSnapshotListener(MetadataChanges.INCLUDE, (snapshot, e) -> {
                if (e != null) {
                    Log.w("listener", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("listener", "Current data: " + snapshot.getData());
                    HashMap<String, Map<String, Object>> documentMap = new HashMap<>();
                    documentMap.put(snapshot.getId(), snapshot.getData());
                    Reservation reservationUpdated = new Reservation();
                    for (Map.Entry<String, Map<String, Object>> entry : documentMap.entrySet()) {
                        Log.d("keyvalue", "Key = " + entry.getKey() + " Value = " + entry.getValue());
                        for (Map.Entry<String, Object> entryMap2 : entry.getValue().entrySet()) {
                            Log.d("keyvalue2", "Key = " + entryMap2.getKey() + " Value = " + entryMap2.getValue());
                            if (entryMap2.getKey().equals("room")) {
                                reservationUpdated.setRoom(entryMap2.getValue().toString());
                            }
                            if (entryMap2.getKey().equals("date")) {
                                reservationUpdated.setDate(entryMap2.getValue().toString());
                            }
                            if (entryMap2.getKey().equals("startTime")) {
                                reservationUpdated.setStartTime(entryMap2.getValue().toString());
                            }
                            if (entryMap2.getKey().equals("endTime")) {
                                reservationUpdated.setEndTime(entryMap2.getValue().toString());
                            }
                            if (entryMap2.getKey().equals("purpose")) {
                                reservationUpdated.setPurpose(entryMap2.getValue().toString());
                            }
                            if (entryMap2.getKey().equals("status")) {
                                reservationUpdated.setStatus(entryMap2.getValue().toString());
                            }
                            if (entryMap2.getKey().equals("situation")) {
                                reservationUpdated.setSituation(entryMap2.getValue().toString());
                            }
                            if (entryMap2.getKey().equals("username")) {
                                reservationUpdated.setUserName(entryMap2.getValue().toString());
                            }
                            if (entryMap2.getKey().equals("usertype")) {
                                reservationUpdated.setUsertype(entryMap2.getValue().toString());
                            }
                            reservationUpdated.setDocumentId(entry.getKey());
                        }
                    }

                    String channelId = "123123";

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        if (getContext() == null) { return; }
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

                        if (notificationManager.getNotificationChannel("123123") == null ) {
                            return;
                        }

                        int notifyId = (int) Math.random();
                        boolean notifyBool = false;
                        boolean atualizaBool = false;
                        for (Reservation reservationFromList : listaReservations) {
                            if (reservationFromList.getDocumentId().equals(reservationUpdated.getDocumentId()) &&
                                    !reservationFromList.getSituation().equals(reservationUpdated.getSituation())) {
                                notifyBool = true;
                            }
                            if (reservationFromList.getDocumentId().equals(reservationUpdated.getDocumentId()) &&
                                    (!reservationFromList.getStatus().equals(reservationUpdated.getStatus()) ||
                                            !reservationFromList.getStartTime().equals(reservationUpdated.getStartTime()) ||
                                            !reservationFromList.getEndTime().equals(reservationUpdated.getEndTime()) ||
                                            !reservationFromList.getDate().equals(reservationUpdated.getDate()) ||
                                            !reservationFromList.getPurpose().equals(reservationUpdated.getPurpose()) ||
                                            !reservationFromList.getUsertype().equals(reservationUpdated.getUsertype()) ||
                                            !reservationFromList.getRoom().equals(reservationUpdated.getRoom()) ||
                                            !reservationFromList.getUserName().equals(reservationUpdated.getUserName()))
                            ) {
                                atualizaBool = true;
                            }
                        }
                        if (notifyBool) {
                            Notification notification = new Notification.Builder(getContext(), channelId)
                                    .setContentTitle(getString(R.string.reservation_request_situation_changed))
                                    .setContentText(getString(R.string.the_situation_of_the_reservation) +  " " + reservationUpdated.getRoom() + " " + getString(R.string.changed_to) + " " + reservationUpdated.getSituation())
                                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                                    .build();

                            notificationManager.notify(notifyId, notification);
                            reservationList = new ArrayList<>();
                            responsibleRoomsList = new ArrayList<>();
                            populaUser();
                        }
                        if (atualizaBool) {
                            reservationList = new ArrayList<>();
                            responsibleRoomsList = new ArrayList<>();
                            populaUser();
                        }
                    }
                } else {
                    Log.d("listener", "Current data: null");
                }
            });
            documentReferences.add(docRef);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            populaSalasResponsavel();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
