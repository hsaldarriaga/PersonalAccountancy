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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.movil.contabilidad.personalaccountancy.R;

/**
 * Created by hass-pc on 19/03/2015.
 */
public class DialogRubleName extends DialogFragment{

    public interface Response {
        public void Action(String name, String Description, float expected, int type);
        public void ActionCancel();
    }

    private Response response;
    private ArrayAdapter<String> sp_adapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_ruble, null));
        final DialogFragment frag = this;
        builder.setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog alter = (AlertDialog)dialog;
                String name = ((EditText)alter.findViewById(R.id.ruble_name)).getText().toString();
                String desc = ((EditText)alter.findViewById(R.id.ruble_desc)).getText().toString();
                int type = ((Spinner)alter.findViewById(R.id.ruble_spinner)).getSelectedItemPosition();
                float value = Float.valueOf(((EditText)alter.findViewById(R.id.ruble_expected)).getText().toString());
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                dialog.dismiss();
                response.Action(name, desc, value, type);
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
    public void onResume() {
        super.onResume();
        if (sp_adapter==null) {
            Spinner spinner = (Spinner)getDialog().findViewById(R.id.ruble_spinner);
            String data[] = new String[]{ getString(R.string.dialog_option_Inflow), getString(R.string.dialog_option_Outflow)};
            sp_adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner_style, data);
            sp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(sp_adapter);
            spinner.setSelection(0);
        }
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
