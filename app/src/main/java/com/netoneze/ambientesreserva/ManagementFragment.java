package com.netoneze.ambientesreserva;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.netoneze.ambientesreserva.modelo.Reservation;
import com.netoneze.ambientesreserva.modelo.Room;
import com.netoneze.ambientesreserva.utils.AdapterListReservations;
import com.netoneze.ambientesreserva.utils.AdapterListRooms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagementFragment} factory method to
 * create an instance of this fragment.
 */
public class ManagementFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ViewGroup root;
    ExpandableListView listView;
    FloatingActionButton addRoomButton;
    public ManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_management, container, false);
        listView = root.findViewById(R.id.listViewRooms);
        addRoomButton = root.findViewById(R.id.addRoomButton);
        addRoomButton.setOnClickListener(v -> {
            Intent addRoomIntent = new Intent(getActivity(), RoomFormActivity.class);
            startActivity(addRoomIntent);
        });
        populaLista();
        return root;
    }

    public void populaLista(){
        List<Room> lista = new ArrayList<>();
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
                                    if (entryMap2.getKey().equals("details")) {
                                        room.setDetails(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("name")) {
                                        room.setName(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("aprovacaoAutomatica")) {
                                        room.setAutomaticApproval((Boolean) entryMap2.getValue());
                                    }
                                    if (entryMap2.getKey().equals("type")) {
                                        room.setType(entryMap2.getValue().toString());
                                    }
                                    if (entryMap2.getKey().equals("especificacoes")) {
                                        room.setSpecifications((Map<String, Boolean>) entryMap2.getValue());
                                    }
                                }
                                lista.add(room);
                            }
                        }

                        List<String> lstGrupos = new ArrayList<>();

                        for (int i = 0 ; i < lista.size() ; i++){
                            lstGrupos.add(lista.get(i).getName());
                        }

                        HashMap<String, List<Room>> lstItensGrupo = new HashMap<>();

                        for (int i = 0 ; i < lista.size() ; i++){
                            lstItensGrupo.put(lstGrupos.get(i), lista.subList(i, i+1));
                        }

                        AdapterListRooms adapter = new AdapterListRooms(getActivity(), lstGrupos, lstItensGrupo);

                        listView.setAdapter(adapter);

                    } else {
                        Log.d("erro", "Error getting documents: ", task.getException());
                    }
                });
    }

}