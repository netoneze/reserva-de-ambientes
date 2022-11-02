package com.netoneze.ambientesreserva;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.netoneze.ambientesreserva.modelo.Reservation;
import com.netoneze.ambientesreserva.modelo.Room;
import com.netoneze.ambientesreserva.modelo.User;
import com.netoneze.ambientesreserva.utils.AdapterListReservations;
import com.netoneze.ambientesreserva.utils.AdapterListRooms;
import com.netoneze.ambientesreserva.utils.UtilsGUI;

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
    private User currentUser = new User();

    public static final String MODO = "MODO";
    public static final String ROOM = "ROOM";
    public static final int ALTERAR_CADASTRO = 1;
    public ManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_management, container, false);
        listView = root.findViewById(R.id.listViewRooms);
        getActivity().setTitle("Room Management");
        registerForContextMenu(listView);
        addRoomButton = root.findViewById(R.id.addRoomButton);
        addRoomButton.setOnClickListener(v -> {
            Intent addRoomIntent = new Intent(getActivity(), RoomFormActivity.class);
            startActivityForResult(addRoomIntent, 0);
        });
        populaUser();
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

    public void populaListaTodas(){
        List<Room> lista = new ArrayList<>();
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

    private void delete(int posicao) {
        Room room = (Room) listView.getExpandableListAdapter().getChild(posicao, 0);

        String mensagem = getString(R.string.deseja_realmente_apagar)
                + "\n" + room.getName() + "?";

        DialogInterface.OnClickListener listener =
                (dialog, which) -> {

                    switch(which){
                        case DialogInterface.BUTTON_POSITIVE:

                            db.collection("room").document(room.getName())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("deleteFirestone", room.getName() + " DocumentSnapshot successfully deleted!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("deleteFirestone", "Error deleting document", e);
                                        }
                                    });

                            populaLista();

                            break;
                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                };

        UtilsGUI.confirmaAcao(getContext(), mensagem, listener);
    }

    public void goToEditRoom(int posicao) {
        Room room = (Room) listView.getExpandableListAdapter().getChild(posicao, 0);

        Intent intentAlterarCadastro = new Intent(getActivity(), RoomFormActivity.class);

        intentAlterarCadastro.putExtra(MODO, ALTERAR_CADASTRO);
        intentAlterarCadastro.putExtra(ROOM, room);
        startActivityForResult(intentAlterarCadastro, ALTERAR_CADASTRO);
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
            } else {
                Log.d("failMessage", "get failed with ", task1.getException());
            }
            if (currentUser.getType().equals("0") || currentUser.getType().equals("1")) {
                addRoomButton.setVisibility(View.GONE);
                populaLista();
            } else if (currentUser.getType().equals("2")) {
                populaListaTodas();
            }
        });
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

            case R.id.editar_menu_item_room:
                goToEditRoom(groupPos);
                return true;

            case R.id.excluir_menu_item_room:
                delete(groupPos);
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
            inflater.inflate(R.menu.menu_context_list_rooms, menu);
            if (currentUser.getType().equals("0") || currentUser.getType().equals("1")) {
                menu.removeItem(R.id.excluir_menu_item_room);
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK){
            populaUser();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}