package com.in2world.ccs.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.in2world.ccs.R;

public class MessageHelper {
    public static void AppDialog (Context context, int title, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(context.getResources().getString(title));
        dialog.setMessage(message);
        dialog.setCancelable(false);

        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

}
