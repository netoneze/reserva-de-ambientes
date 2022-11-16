package com.netoneze.ambientesreserva;

import static android.app.Activity.RESULT_OK;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.netoneze.ambientesreserva.modelo.Reservation;
import com.netoneze.ambientesreserva.modelo.User;
import com.netoneze.ambientesreserva.utils.AdapterListReservations;
import com.netoneze.ambientesreserva.utils.UtilsGUI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyReservationsFragment} factory method to
 * create an instance of this fragment.
 */
public class MyReservationsFragment extends Fragment {
    private static final String CHANNEL_ID = "1234213";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ViewGroup root;
    FloatingActionButton addReserveButton;
    ExpandableListView listView;
    List<Reservation> lista = new ArrayList<>();
    List<DocumentReference> documentReferences = new ArrayList<>();
    private User currentUser = new User();
    private int snapshotCount = 0;
    public MyReservationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.fragment_reservations, container, false);
        listView = root.findViewById(R.id.listViewReservations);
        registerForContextMenu(listView);
        addReserveButton = root.findViewById(R.id.addReserveButton);
        addReserveButton.setOnClickListener(v -> {
            Intent addReserveIntent = new Intent(getActivity(), ReserveFormActivity.class);
            startActivityForResult(addReserveIntent, 0);
        });
        populaUser();
        populaListaAndSetListener();
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
        lista = new ArrayList<>();
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
                                    if (entryMap2.getKey().equals("username")) {
                                        reservation.setUserName(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("usertype")) {
                                        reservation.setUsertype(entryMap2.getValue().toString());
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
        lista = new ArrayList<>();
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
                                    if (entryMap2.getKey().equals("username")) {
                                        reservation.setUserName(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("usertype")) {
                                        reservation.setUsertype(entryMap2.getValue().toString());
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
        lista = new ArrayList<>();
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
                                    if (entryMap2.getKey().equals("username")) {
                                        reservation.setUserName(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("usertype")) {
                                        reservation.setUsertype(entryMap2.getValue().toString());
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
        lista = new ArrayList<>();
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
                                    if (entryMap2.getKey().equals("username")) {
                                        reservation.setUserName(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("usertype")) {
                                        reservation.setUsertype(entryMap2.getValue().toString());
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

    public void populaListaAndSetListener() {
        List<Reservation> listaForListener = new ArrayList<>();
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
                                    if (entryMap2.getKey().equals("username")) {
                                        reservation.setUserName(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("usertype")) {
                                        reservation.setUsertype(entryMap2.getValue().toString());
                                    }
                                    reservation.setDocumentId(entry.getKey());
                                }
                                listaForListener.add(reservation);
                            }
                        }

                        setListenerForMyReservations(listaForListener);
                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void setListenerForMyReservations(List<Reservation> listaReservations) {
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
                        for (Reservation reservationFromList : lista) {
                            if (reservationFromList.getDocumentId().equals(reservationUpdated.getDocumentId()) &&
                            !reservationFromList.getStatus().equals(reservationUpdated.getStatus())) {
                                notifyBool = true;
                            }
                        }
                        if (notifyBool) {
                            Notification notification = new Notification.Builder(getContext(), channelId)
                                    .setContentTitle("Your Reservation Status Changed!")
                                    .setContentText("The reservation of room " + reservationUpdated.getRoom() + " changed to " + reservationUpdated.getStatus())
                                    .setSmallIcon(android.R.drawable.stat_notify_chat)
                                    .build();

                            notificationManager.notify(notifyId, notification);
                        }
                    }
                } else {
                    Log.d("listener", "Current data: null");
                }
            });
            documentReferences.add(docRef);
        }
    }

    private void cancelReservation(int posicao) {
        Reservation reservation = (Reservation) listView.getExpandableListAdapter().getChild(posicao, 0);

        String mensagem = getString(R.string.deseja_realmente_cancelar)
                + "\n" + reservation.getRoom() + "?";

        DialogInterface.OnClickListener listener =
                (dialog, which) -> {

                    switch(which){
                        case DialogInterface.BUTTON_POSITIVE:
                            if (reservation.getSituation().equals("cancelled")) {
                                Toast.makeText(getContext(), "Cannot cancel a cancelled reserve!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
                            Date todayDate = new Date();
                            try {
                                Date reservationDate = sdf.parse(reservation.getDate() + " " + reservation.getStartTime());
                                assert reservationDate != null;
                                if (reservationDate.before(todayDate)) {
                                    Toast.makeText(getContext(), "Cannot cancel reserve, it already started or its finished!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            DocumentReference reserveRef = db.collection("reservation").document(reservation.getDocumentId());
                            reserveRef.update("situation", "cancelled")
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("success", "DocumentSnapshot successfully updated!");
                                        Toast.makeText(getContext(), "Cancelled Reserve!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Log.w("fail", "Error updating document", e));

                            if (currentUser.getType().equals("2")) {
                                populaListaTodasReservas();
                            } else {
                                populaLista();
                            }

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                };

        UtilsGUI.confirmaAcao(getContext(), mensagem, listener);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info;
        info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();

        int groupPos = 0;

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
        {
            groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        }

        switch(item.getItemId()){

            case R.id.excluir_menu_item_reservation:
                cancelReservation(groupPos);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();

        ExpandableListView.ExpandableListContextMenuInfo info;

        info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            inflater.inflate(R.menu.menu_context_list_reservations, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
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