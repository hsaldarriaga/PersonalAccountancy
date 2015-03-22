package com.movil.contabilidad.personalaccountancy;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.movil.contabilidad.personalaccountancy.Database.Ciclo;
import com.movil.contabilidad.personalaccountancy.Database.CountableManager;
import com.movil.contabilidad.personalaccountancy.Database.Rublo;

import java.text.DecimalFormat;
import java.util.HashMap;

import static android.widget.AdapterView.OnItemSelectedListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRubles extends Fragment implements OnItemSelectedListener{


    public FragmentRubles() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_fragment_rubles, container, false);
        ListRuble = (ListView)v.findViewById(R.id.Rublo_List);
        progress = (ProgressBar)v.findViewById(R.id.Rublo_progress);
        spinner = (Spinner)v.findViewById(R.id.Rublo_Spinner);
        adapter = new ViewListAdapter(this.getActivity());
        ListRuble.setAdapter(adapter);
        Rublo rublos[] = CountableManager.getInstance().getAllRublos();
        sp_adapter = new ArrayAdapter<Rublo>(this.getActivity(), R.layout.spinner_style, rublos);
        sp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sp_adapter);
        spinner.setOnItemSelectedListener(this);
        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (progress.getVisibility() == View.INVISIBLE) {
            progress.setVisibility(View.VISIBLE);
            SetRublo(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void SetRublo(final int pos) {
        progress.setVisibility(View.VISIBLE);
        final Activity act = getActivity();
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                final Rublo rublo = sp_adapter.getItem(pos);
                final HashMap<Integer, Float> report = rublo.getReport();
                final Ciclo ciclos[] = CountableManager.getInstance().getAllCiclos();
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.views.clear();
                        progress.setVisibility(View.INVISIBLE);
                        for (Ciclo cl : ciclos) {
                            float rublo_real = report.get(cl.getId());
                            if (rublo_real != 0) {
                                DecimalFormat ff = new DecimalFormat("#.####");
                                RelativeLayout view = (RelativeLayout) LayoutInflater.from(act).inflate(R.layout.item_cycle, null);
                                TextView name, exp, real, perce;
                                name = (TextView) view.findViewById(R.id.cycle_name);
                                exp = (TextView) view.findViewById(R.id.cycle_expected);
                                real = (TextView) view.findViewById(R.id.cycle_real);
                                perce = (TextView) view.findViewById(R.id.cycle_perce);
                                name.setText(cl.getName());
                                exp.setText(ff.format(rublo.getExpected()));
                                real.setText(ff.format(rublo_real));
                                float value = (rublo_real - rublo.getExpected()) / rublo.getExpected();
                                if (value > 0) {
                                    perce.setText("+" + ff.format(value) + "%");
                                    if (rublo.getType() == Rublo.TYPE.INFLOWS) {
                                        perce.setTextColor(Color.parseColor("#00FF00"));
                                    } else {
                                        perce.setTextColor(Color.parseColor("#FF0000"));
                                    }
                                } else {
                                    perce.setText(ff.format(value) + "%");
                                    if (rublo.getType() == Rublo.TYPE.INFLOWS) {
                                        perce.setTextColor(Color.parseColor("#FF0000"));
                                    } else {
                                        perce.setTextColor(Color.parseColor("#00FF00"));
                                    }
                                }
                                adapter.views.add(view);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });
        hilo.start();
    }

    private ListView ListRuble;
    private ViewListAdapter adapter;
    private ProgressBar progress;
    private ArrayAdapter<Rublo> sp_adapter;
    private Spinner spinner;
}
