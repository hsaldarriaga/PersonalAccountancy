package com.movil.contabilidad.personalaccountancy;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.movil.contabilidad.personalaccountancy.Database.Rublo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hass-pc on 19/03/2015.
 */
public class RubloAdapter extends BaseAdapter implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener{

    public RubloAdapter(Context c, Rublo[] rublos, int width) {
        inflater = LayoutInflater.from(c);
        this.rublos = new ArrayList<>(Arrays.asList(rublos));
        Width = width;
        Commit();
    }

    public void ChangeContent(Rublo[] rublos) {
        this.rublos = new ArrayList<>(Arrays.asList(rublos));
        Commit();
    }

    public void AddRuble(Rublo rbl) {
        rublos.add(rbl);
        Commit();
    }

    @Override
    public int getCount() {
        return rublos.size();
    }

    @Override
    public Object getItem(int position) {
        return rublos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder= null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.rublo_item_layout, parent, false);
            holder = new ViewHolder();
            holder.text_header = (TextView)convertView.findViewById(R.id.item_text_header);
            holder.text_desc = (TextView)convertView.findViewById(R.id.item_text_desc);
            holder.checkBox = (CheckBox)convertView.findViewById(R.id.item_checkBox);
            holder.move = (RelativeLayout)convertView.findViewById(R.id.move_layout);
            convertView.setTag(R.string.tag_content, holder);
            convertView.setOnTouchListener(this);
            holder.checkBox.setOnCheckedChangeListener(this);
        } else {
            holder = (ViewHolder)convertView.getTag(R.string.tag_content);
        }
        Rublo rublo = rublos.get(position);
        if (!holder.text_header.getText().equals(rublo.getName())) {
            convertView.setTag(R.string.tag_position, position);
            holder.checkBox.setTag(position);
            holder.text_header.setText(rublo.getName());
            holder.text_desc.setText(rublo.getDescription());
            holder.checkBox.setChecked(rublo.isEnable());
            if (rublo.getType() == Rublo.TYPE.INFLOWS)
                holder.move.setBackgroundColor(Color.parseColor("#90EE90"));
            else
                holder.move.setBackgroundColor(Color.parseColor("#FF8C00"));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.move.getLayoutParams();
            if (params.leftMargin!=0) {
                params.leftMargin = 0;
                holder.move.setLayoutParams(params);
                holder.move.requestLayout();
            }
        }
        return convertView;
    }


    public void Commit() {
        notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float deltax, value; boolean entry = false;
        final AbsListView.LayoutParams parent_param = (AbsListView.LayoutParams)v.getLayoutParams();
        final RelativeLayout move = (RelativeLayout)v.findViewById(R.id.move_layout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)move.getLayoutParams();
        final int pos = Integer.parseInt(v.getTag(R.string.tag_position).toString());
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                deltax = event.getX() - x;
                if (IsMoving || Math.abs(deltax) > Width/6) {
                    IsMoving = true;
                    params.leftMargin = (int) deltax;
                    params.width = move.getMeasuredWidth();
                    move.setLayoutParams(params);
                    move.requestLayout();
                } else {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
                IsMoving = false;
                value = event.getX() - x;
                if (Math.abs(value) > Width/2) {
                    Rublo rbl = rublos.get(pos);
                    if (rbl.getId() != -1) {
                        entry = true;
                    } else {
                        if (value > 0) {
                            //move.animate().translationXBy(Width-value).setDuration(1000).start()
                            TranslateAnimation anima = new TranslateAnimation(0, Width-value,0,0);
                            anima.setDuration(1000);
                            anima.setFillAfter(true);
                            anima.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    rublos.remove(pos);
                                    Commit();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            move.startAnimation(anima);
                        } else {
                            TranslateAnimation anima = new TranslateAnimation(0, -Width+value,0,0);
                            anima.setDuration(1000);
                            anima.setFillAfter(true);
                            anima.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    rublos.remove(pos);
                                    Commit();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            move.startAnimation(anima);
                        }
                    }
                    Commit();
                } else {
                    entry = true;
                }
                if (entry) {
                    params.leftMargin = 0;
                    move.setLayoutParams(params);
                    move.requestLayout();
                }
                x = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                IsMoving = false;
                params.leftMargin = 0;
                move.setLayoutParams(params);
                move.requestLayout();
                break;
        }
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = Integer.parseInt(buttonView.getTag().toString());
        Rublo rbl = rublos.get(pos);
        if (isChecked != rbl.isEnable())
            rbl.setEnable(isChecked);
    }

    private float x;
    private LayoutInflater inflater;
    private List<Rublo> rublos;
    private int Width;
    private boolean IsMoving = false;

    static class ViewHolder {
        TextView text_header, text_desc;
        CheckBox checkBox;
        RelativeLayout move;
    }
}
