package com.movil.contabilidad.personalaccountancy;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCycles extends Fragment implements AdapterView.OnItemSelectedListener{


    public FragmentCycles() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_cycles, container, false);
        ListCycle = (ListView)v.findViewById(R.id.Cycle_List);
        spinner = (Spinner)v.findViewById(R.id.Cycle_Spinner);
        progress = (ProgressBar)v.findViewById(R.id.Cycle_progress);
        Initialize();
        return v;
    }

    private void Initialize()
    {
        adapter = new ViewListAdapter(this.getActivity());
        ListCycle.setAdapter(adapter);
        Ciclo ciclos[] = CountableManager.getInstance().getAllCiclos();
        sp_adapter = new ArrayAdapter<Ciclo>(this.getActivity(), R.layout.spinner_style, ciclos);
        sp_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(sp_adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (progress.getVisibility() == View.INVISIBLE) {
            progress.setVisibility(View.VISIBLE);
            SetCycle(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void SetCycle(int position) {
        final int pos = position;
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                SetItemCycle(pos);
            }
        });
        hilo.start();
    }

    public void SetItemCycle(int pos) { //Asyncronous
        final Rublo rublos[] = CountableManager.getInstance().getAllRublos();
        bal_geral_entry_real = 0f; bal_geral_out_real = 0f;
        bal_geral_entry_expt = 0f; bal_geral_out_expt = 0f;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        float top = pref.getFloat(getString(R.string.Preference_Top_value), 0.02f);
        float down = pref.getFloat(getString(R.string.Preference_Down_value), 0.02f);
        final HashMap<Integer, Float> report = sp_adapter.getItem(pos).getReport();
        final HashMap<Integer, Boolean> anormal_rublos = new HashMap<>(); //true excedio, false por debajo
        for (Rublo rbl : rublos) {
            float real_value = report.get(rbl.getId());
            if (real_value != 0f) {
                switch (rbl.getType()) {
                    case INFLOWS:
                        bal_geral_entry_real += real_value;
                        bal_geral_entry_expt += rbl.getExpected();
                        break;
                    case OUTFLOWS:
                        bal_geral_out_real += real_value;
                        bal_geral_out_expt += rbl.getExpected();
                        break;
                }
                float percentage = (real_value - rbl.getExpected()) / rbl.getExpected();
                if (percentage > 0) { //Excedio
                    percentage = Math.abs(percentage);
                    if (percentage > top) {
                        anormal_rublos.put(rbl.getId(), true);
                    }
                } else { // Por debajo
                    percentage = Math.abs(percentage);
                    if (percentage > down) {
                        anormal_rublos.put(rbl.getId(), false);
                    }
                }
            }
        }
        final Context c = getActivity();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.views.clear();
                progress.setVisibility(View.INVISIBLE);
                DecimalFormat ff = new DecimalFormat("#.####");
                //region General Balance
                RelativeLayout linear = (RelativeLayout)LayoutInflater.from(c).inflate(R.layout.item_general_balance,null);
                TextView TOTAL = (TextView)linear.findViewById(R.id.item_general);
                TextView ER = (TextView)linear.findViewById(R.id.item_inflow);
                TextView SL = (TextView)linear.findViewById(R.id.item_outflow);
                ER.setTextColor(Color.parseColor("#808080"));
                SL.setTextColor(Color.parseColor("#808080"));
                ER.setText(ff.format(bal_geral_entry_real) + "$");
                SL.setText(ff.format(bal_geral_out_real) + "$");
                float resu = bal_geral_entry_real - bal_geral_out_real;
                TOTAL.setText(ff.format(resu) + "$");
                if (resu > 0) {
                    TOTAL.setTextColor(Color.parseColor("#00FF00"));
                } else {
                    TOTAL.setTextColor(Color.parseColor("#FF0000"));
                }
                adapter.views.add(linear);
                //endregion
                //region Entradas
                LinearLayout bal_entry = adapter.getContainer();
                bal_entry.setOrientation(LinearLayout.VERTICAL);
                TextView entry = new TextView(getActivity()), entry_label = new TextView(getActivity());
                entry_label.setTextSize(20);
                entry.setTextSize(15);
                resu = bal_geral_entry_real - bal_geral_entry_expt;
                entry.setText(ff.format(resu) + "$");
                entry_label.setText("Inflows Balance");
                if (resu > 0) {
                    entry.setTextColor(Color.parseColor("#00FF00"));
                } else {
                    entry.setTextColor(Color.parseColor("#FF0000"));
                }
                bal_entry.addView(entry_label);
                bal_entry.addView(entry);
                adapter.views.add(bal_entry);
                //endregion
                //region Salidas
                LinearLayout bal_out = adapter.getContainer();
                bal_out.setOrientation(LinearLayout.VERTICAL);
                TextView out = new TextView(getActivity()), out_label = new TextView(getActivity());
                out_label.setTextSize(20);
                out.setTextSize(15);
                resu = bal_geral_out_real - bal_geral_out_expt;
                out.setText(ff.format(resu) + "$");
                out_label.setText("Outflows Balance");
                if (resu > 0) {
                    out.setTextColor(Color.parseColor("#00FF00"));
                } else {
                    out.setTextColor(Color.parseColor("#FF0000"));
                }
                bal_out.addView(out_label);
                bal_out.addView(out);
                adapter.views.add(bal_out);
                //endregion
                //region Rublos Excedidos
                for (Rublo rbl : rublos) {
                    if (anormal_rublos.containsKey(rbl.getId())) {
                        LinearLayout exceed = adapter.getContainer();
                        exceed.setOrientation(LinearLayout.VERTICAL);
                        TextView rubro_name, rubro_desc, rubro_valor_exc, rubro_perce;
                        rubro_name = new TextView(getActivity());
                        rubro_desc = new TextView(getActivity());
                        rubro_valor_exc = new TextView(getActivity());
                        rubro_perce = new TextView(getActivity());

                        rubro_name.setTextSize(20);
                        rubro_desc.setTextSize(10);
                        rubro_valor_exc.setTextSize(15);
                        rubro_perce.setTextSize(15);

                        if (rbl.getType() == Rublo.TYPE.INFLOWS) {
                            rubro_name.setText(rbl.getName() + " (Inflow)");
                        } else {
                            rubro_name.setText(rbl.getName() + " (Outflow)");
                        }
                        rubro_desc.setText(rbl.getDescription());
                        resu = report.get(rbl.getId()) - rbl.getExpected();
                        if (anormal_rublos.get(rbl.getId())) { //Excedio
                            rubro_valor_exc.setText("+" + ff.format(resu) + "$");
                            rubro_perce.setText("+" + ff.format(resu / rbl.getExpected()) + "%");
                            if (rbl.getType() == Rublo.TYPE.INFLOWS) {
                                rubro_perce.setTextColor(Color.parseColor("#00FF00"));
                            } else {
                                rubro_perce.setTextColor(Color.parseColor("#FF0000"));
                            }
                        } else { //por debajo
                            rubro_valor_exc.setText(ff.format(resu) + "$");
                            rubro_perce.setText(ff.format(resu / rbl.getExpected()) + "%");
                            if (rbl.getType() == Rublo.TYPE.INFLOWS) {
                                rubro_perce.setTextColor(Color.parseColor("#FF0000"));
                            } else {
                                rubro_perce.setTextColor(Color.parseColor("#00FF00"));
                            }
                        }
                        exceed.addView(rubro_name);
                        exceed.addView(rubro_desc);
                        exceed.addView(rubro_valor_exc);
                        exceed.addView(rubro_perce);
                        adapter.views.add(exceed);
                    }
                }
                //endregion
                adapter.notifyDataSetChanged();
            }
        });

    }

    private ListView ListCycle;
    private ViewListAdapter adapter;
    private ArrayAdapter<Ciclo> sp_adapter;
    private ProgressBar progress;
    private Spinner spinner;
    float bal_geral_entry_real = 0f, bal_geral_out_real = 0f;
    float bal_geral_entry_expt = 0f, bal_geral_out_expt = 0f;

}
