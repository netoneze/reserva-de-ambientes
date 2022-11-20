package com.netoneze.ambientesreserva.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.netoneze.ambientesreserva.R;
import com.netoneze.ambientesreserva.modelo.Reservation;

import java.util.HashMap;
import java.util.List;

public class AdapterListRequests extends BaseExpandableListAdapter {
    private List<String> lstGrupos;
    private HashMap<String, List<Reservation>> lstItensGrupos;
    private Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdapterListRequests(Context context, List<String> grupos, HashMap<String, List<Reservation>> itensGrupos) {
        // inicializa as variáveis da classe
        this.context = context;
        lstGrupos = grupos;
        lstItensGrupos = itensGrupos;
    }

    @Override
    public int getGroupCount() {
        // retorna a quantidade de grupos
        return lstGrupos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // retorna a quantidade de itens de um grupo
        return lstItensGrupos.get(getGroup(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // retorna um grupo
        return lstGrupos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // retorna um item do grupo
        return lstItensGrupos.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        // retorna o id do grupo, porém como nesse exemplo
        // o grupo não possui um id específico, o retorno
        // será o próprio groupPosition
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // retorna o id do item do grupo, porém como nesse exemplo
        // o item do grupo não possui um id específico, o retorno
        // será o próprio childPosition
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        // retorna se os ids são específicos (únicos para cada
        // grupo ou item) ou relativos
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // cria os itens principais (grupos)

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.grupo, null);
        }

        TextView tfTitulo = convertView.findViewById(R.id.tf_name);

        Reservation reservation = (Reservation) getChild(groupPosition, 0);

        String capitalizedStatus = reservation.getStatus().substring(0, 1).toUpperCase() + reservation.getStatus().substring(1);
        tfTitulo.setText(reservation.getRoom() + " (" + capitalizedStatus + ")");

        String upperCasedSituation = "";
        if (reservation.getSituation().equals("cancelled")) {
            upperCasedSituation = reservation.getSituation().toUpperCase();
            tfTitulo.setText(reservation.getRoom() + " (" + capitalizedStatus + ")" + " " + upperCasedSituation);
        }

        if (reservation.getStatus().equals("approved")) {
            convertView.setBackgroundColor(Color.parseColor("#b3ffcc"));
        }
        if (reservation.getStatus().equals("pending")) {
            convertView.setBackgroundColor(Color.parseColor("#ccebff"));
        }
        if (reservation.getStatus().equals("disapproved")) {
            convertView.setBackgroundColor(Color.parseColor("#ff8080"));
        }
        if (reservation.getStatus().equals("disapproved") || reservation.getSituation().equals("cancelled")) {
            convertView.setBackgroundColor(Color.parseColor("#ff8080"));
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // cria os subitens (itens dos grupos)

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_grupo_reserve_request, null);
        }

        TextView tfDate = convertView.findViewById(R.id.tf_date_body);
        TextView tfStartTime = convertView.findViewById(R.id.tf_startTime_body);
        TextView tfEndTime = convertView.findViewById(R.id.tf_endTime_body);
        TextView tfPurpose = convertView.findViewById(R.id.tf_purpose_body);
        TextView tfUsername = convertView.findViewById(R.id.tf_username_body);
        TextView tfUsertype = convertView.findViewById(R.id.tf_usertype_body);
        Reservation reservation = (Reservation) getChild(groupPosition, childPosition);

        tfDate.setText(reservation.getDate());
        tfStartTime.setText(reservation.getStartTime());
        tfEndTime.setText(reservation.getEndTime());
        tfPurpose.setText(reservation.getPurpose());
        tfUsername.setText(reservation.getUserName());
        tfUsertype.setText(reservation.getUsertype());
        Button approveBtn = convertView.findViewById(R.id.buttonApprove);
        Button disapproveBtn = convertView.findViewById(R.id.buttonDisapprove);

        View finalConvertView = convertView;

        if (reservation.getStatus().equals("approved") || reservation.getStatus().equals("disapproved")) {
            approveBtn.setVisibility(View.GONE);
            disapproveBtn.setVisibility(View.GONE);
        }

        approveBtn.setOnClickListener(v -> {
            if (reservation.getStatus().equals("pending")) {
                db.collection("reservation")
                        .document(reservation.getDocumentId())
                        .update("status", "approved")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(finalConvertView.getContext(), "Reservation Approved!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        disapproveBtn.setOnClickListener(v -> {
            if (reservation.getStatus().equals("pending")) {
                db.collection("reservation")
                        .document(reservation.getDocumentId())
                        .update("status", "disapproved")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(finalConvertView.getContext(), "Reservation Disapproved!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // retorna se o subitem (item do grupo) é selecionável
        return true;
    }
}
