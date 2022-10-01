package com.netoneze.ambientesreserva.utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import com.netoneze.ambientesreserva.R;

public class UtilsGUI {
    public static void confirmaAcao(Context contexto,
                                    String mensagem,
                                    DialogInterface.OnClickListener listener){

        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

        builder.setTitle(R.string.confirmation);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setMessage(mensagem);

        builder.setPositiveButton(R.string.yes, listener);
        builder.setNegativeButton(R.string.no, listener);

        AlertDialog alert = builder.create();
        alert.show();
    }
}