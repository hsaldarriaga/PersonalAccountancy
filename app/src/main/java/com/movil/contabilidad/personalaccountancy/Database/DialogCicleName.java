package com.movil.contabilidad.personalaccountancy.Database;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.movil.contabilidad.personalaccountancy.R;

/**
 * Created by hass-pc on 17/03/2015.
 */
public class DialogCicleName extends DialogFragment{

    public interface Response {
        public void Action(String name);
        public void ActionCancel();
    }

    private Response response;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_cycle, null));
        final DialogFragment frag = this;
        builder.setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = ((EditText)((AlertDialog)dialog).findViewById(R.id.Text_name)).getText().toString();
                dialog.dismiss();
                response.Action(text);
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                response.ActionCancel();
                dialog.cancel();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            response = (Response)activity;
        } catch (ClassCastException ex) {
            Log.e("Error Casting class to Response Interface", ex.getMessage());
        }
    }
}
