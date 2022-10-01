package com.netoneze.ambientesreserva.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.netoneze.ambientesreserva.R;
import com.netoneze.ambientesreserva.modelo.Room;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterListRooms extends BaseExpandableListAdapter {
    private List<String> lstGrupos;
    private HashMap<String, List<Room>> lstItensGrupos;
    private Context context;

    public AdapterListRooms(Context context, List<String> grupos, HashMap<String, List<Room>> itensGrupos) {
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
            convertView = layoutInflater.inflate(R.layout.grupo_room, null);
        }

        TextView tfTitulo = convertView.findViewById(R.id.tf_name_room);

        Room room = (Room) getChild(groupPosition, 0);

        tfTitulo.setText((String) getGroup(groupPosition));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // cria os subitens (itens dos grupos)

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_grupo_room, null);
        }

        TextView tfType = convertView.findViewById(R.id.tf_type_body);
        TextView tfDetails = convertView.findViewById(R.id.tf_details_room_body);
        TextView tfSpecifications = convertView.findViewById(R.id.tf_specifications_room_body);

        Room room = (Room) getChild(groupPosition, childPosition);
        tfType.setText(room.getType());
        tfDetails.setText(room.getDetails());

        Map<String, Boolean> specifications = room.getSpecifications();
        StringBuilder specificationsText = new StringBuilder();

        for (Map.Entry<String, Boolean> entry : specifications.entrySet()) {
            switch (entry.getKey()) {
                case "necessita_chave":
                    specificationsText.append(" Necessita chave,");
                    break;
                case "possui_ar_condicionado":
                    specificationsText.append(" Possui ar-condicionado,");
                    break;
                case "possui_ponto_rede_habilitado":
                    specificationsText.append(" Possui ponto de rede,");
                    break;
                case "possui_projetor":
                    specificationsText.append(" Possui projetor,");
                    break;
                case "possui_tv":
                    specificationsText.append(" Possui TV,");
                    break;
                default:
                    break;
            }
        }

        tfSpecifications.setText(specificationsText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // retorna se o subitem (item do grupo) é selecionável
        return true;
    }
}
