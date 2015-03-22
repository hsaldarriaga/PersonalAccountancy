package com.movil.contabilidad.personalaccountancy;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by hass-pc on 17/03/2015.
 */
public class ViewListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    public ArrayList<View> views;
    public ViewListAdapter(Context c) {
        inflater = LayoutInflater.from(c);
        views = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public Object getItem(int position) {
        return views.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_layout, parent, false);
        }
        ViewGroup gr = (ViewGroup)convertView;
        gr.removeAllViews();
        View v = views.get(position);
        if (v.getParent()!=null)
            ((ViewGroup)v.getParent()).removeView(v);
        gr.addView(v);
        return gr;
    }

    public LinearLayout getContainer() {
        LinearLayout layout = new LinearLayout(inflater.getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        Resources resources = inflater.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int)(10 * (metrics.densityDpi / 160f));
        layout.setPadding(px,px,px,px);
        layout.setLayoutParams(params);
        layout.setBackgroundResource(R.drawable.item_background);
        return layout;
    }
}
