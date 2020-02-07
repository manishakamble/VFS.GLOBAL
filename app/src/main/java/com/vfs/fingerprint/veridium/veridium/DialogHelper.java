package com.vfs.fingerprint.veridium.veridium;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by razvan on 2/5/16.
 */
public class DialogHelper {

    public static interface OnClickWithTextField {
        public void onClick(DialogInterface dialog, int which, String text);
    }

    public static void textFieldDialog(Context ctx, String title, String message, int inputType, String initialText, String hint, final OnClickWithTextField onOk, DialogInterface.OnClickListener onCancel){

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        if (null != message)
            builder.setMessage(message);

        final EditText input = new EditText(ctx);
        input.setInputType(inputType);
        if (null != initialText)
            input.setText(initialText);
        if (null != hint)
            input.setHint(hint);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onOk.onClick(dialog, which, input.getText().toString());
            }
        });
        if (null == onCancel)
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        else
            builder.setNegativeButton("Cancel", onCancel);

        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }

    public static void simpleDialog(Context ctx, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        if (null != message)
            builder.setMessage(message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }
}
