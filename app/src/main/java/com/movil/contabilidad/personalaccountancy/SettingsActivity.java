package com.movil.contabilidad.personalaccountancy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.movil.contabilidad.personalaccountancy.Database.CountableManager;
import com.movil.contabilidad.personalaccountancy.Database.DialogCicleName;
import com.movil.contabilidad.personalaccountancy.Database.DialogRubleName;
import com.movil.contabilidad.personalaccountancy.Database.Rublo;

public class SettingsActivity extends ActionBarActivity implements DialogRubleName.Response{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void Initialize() {
        top = (EditText)findViewById(R.id.Sett_Edit_top);
        down = (EditText)findViewById(R.id.Sett_Edit_down);
        list_ruble = (ListView)findViewById(R.id.Sett_list);
        progress = (ProgressBar)findViewById(R.id.Sett_progress);
        if(android.os.Build.VERSION.SDK_INT < 10) {
            Display display = getWindowManager().getDefaultDisplay();
            width = display.getWidth();
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            width = metrics.widthPixels;
        }
        View v = LayoutInflater.from(this).inflate(R.layout.footer_rublo_listview, null, false);
        list_ruble.addFooterView(v);
        ImageButton img = (ImageButton)v.findViewById(R.id.Sett_footer_imgbt);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Agregar nuevo Rublo
                DialogFragment frag = new DialogRubleName();
                frag.show(getSupportFragmentManager(), "Dialog_rubre");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        top.setText(pref.getFloat(getString(R.string.Preference_Top_value), 0.05f)+"");
        down.setText(pref.getFloat(getString(R.string.Preference_Down_value), 0.05f) + "");
        progress.setVisibility(View.VISIBLE);
        final Context c = this;
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                final Rublo rublos[] = CountableManager.getInstance().getAllRublos();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter == null) {
                            adapter = new RubloAdapter(c, rublos, width);
                            list_ruble.setAdapter(adapter);
                        } else {
                            adapter.ChangeContent(rublos);
                            adapter.Commit();
                        }
                        progress.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        hilo.start();
    }

    @Override
    protected void onStop() {
        SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(this).edit();
        pref.putFloat(getString(R.string.Preference_Top_value), Float.parseFloat(top.getText().toString()));
        pref.putFloat(getString(R.string.Preference_Down_value), Float.parseFloat(down.getText().toString()));
        pref.commit();
        for (int i = 0; i < adapter.getCount(); i++) {
            Rublo rubl = (Rublo)adapter.getItem(i);
            rubl.SaveRublo();
        }
        super.onStop();
    }

    @Override
    public void Action(String name, String Description, float expected, int type) {
        Rublo rbl = new Rublo(name, Description, Rublo.TYPE.values()[type], expected, true);
        adapter.AddRuble(rbl);
    }

    @Override
    public void ActionCancel() {

    }

    private EditText top, down;
    private ListView list_ruble;
    private ProgressBar progress;
    int width;
    private RubloAdapter adapter;
}
