package com.netoneze.ambientesreserva.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.netoneze.ambientesreserva.R;
import com.netoneze.ambientesreserva.modelo.Reservation;

import java.util.HashMap;
import java.util.List;

public class AdapterListReservations extends BaseExpandableListAdapter {
    private List<String> lstGrupos;
    private HashMap<String, List<Reservation>> lstItensGrupos;
    private Context context;

    public AdapterListReservations(Context context, List<String> grupos, HashMap<String, List<Reservation>> itensGrupos) {
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

        tfTitulo.setText((String) getGroup(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // cria os subitens (itens dos grupos)

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_grupo, null);
        }

        TextView tfDate = convertView.findViewById(R.id.tf_date_body);
        TextView tfStartTime = convertView.findViewById(R.id.tf_startTime_body);
        TextView tfEndTime = convertView.findViewById(R.id.tf_endTime_body);
        TextView tfPurpose = convertView.findViewById(R.id.tf_purpose_body);

        Reservation reservation = (Reservation) getChild(groupPosition, childPosition);

        tfDate.setText(reservation.getDate());
        tfStartTime.setText(reservation.getStartTime());
        tfEndTime.setText(reservation.getEndTime());
        tfPurpose.setText(reservation.getPurpose());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // retorna se o subitem (item do grupo) é selecionável
        return true;
    }
}
