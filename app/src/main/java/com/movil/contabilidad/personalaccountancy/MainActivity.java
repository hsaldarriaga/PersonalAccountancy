package com.movil.contabilidad.personalaccountancy;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import com.movil.contabilidad.personalaccountancy.Database.CountableManager;
import com.movil.contabilidad.personalaccountancy.Database.DialogCicleName;

import java.util.Random;


public class MainActivity extends ActionBarActivity implements DialogCicleName.Response {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bt_generate = (ImageButton)findViewById(R.id.Bt_generate);
        CountableManager.getInstance(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Accountancy) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Initialize() {
        Anim_rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Anim_rotate.setDuration(800);
        Anim_rotate.setInterpolator(new LinearInterpolator());
        Anim_rotate.setRepeatCount(Animation.INFINITE);

        Anim_rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (!Animating)
                    animation.cancel();
            }
        });

        Anim_scale_big = new ScaleAnimation(1f, 1.3f, 1f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Anim_scale_big.setDuration(800);

        Anim_scale_normal = new ScaleAnimation(1.3f,1f,1.3f,1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Anim_scale_normal.setDuration(800);
        Anim_scale_normal.setStartOffset(801);

        Anim_set = new AnimationSet(false);
    }

    public void onClick_generate_report(View v) {
        final View view = v;
        if (!Animating) {
            Initialize();
            Anim_set.addAnimation(Anim_rotate);
            Anim_set.addAnimation(Anim_scale_big);
            Anim_set.addAnimation(Anim_scale_normal);
            Anim_set.setFillAfter(false);
            Anim_set.setFillBefore(false);
            DialogFragment frag = new DialogCicleName();
            frag.show(getSupportFragmentManager(), "Dialog_name");
        }
    }

    public void onClick_Browse(View v) {
        if (!Animating) {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        }
    }

    public void onClick_Setting(View v) {
        if (!Animating) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void Action(String name) {
        final String name_final = name;
        Animating = true;
        Bt_generate.setAnimation(null);
        Bt_generate.startAnimation(Anim_set);
        min_time = System.currentTimeMillis();
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                CountableManager.getInstance().GenerateReport(new Random(), name_final);
                min_time = Math.abs(System.currentTimeMillis() - min_time);
                if (min_time<1600) {
                    try {
                        Thread.sleep(1600 - min_time);
                        Animating = false;
                    } catch (InterruptedException e) {
                        Animating = false;
                        Log.e("ERROR SLEEPING", e.getMessage());
                    }
                }
            }
        });
        hilo.start();
    }

    @Override
    public void ActionCancel() {
        Animating = false;
    }

    private ImageButton Bt_generate;
    private RotateAnimation Anim_rotate;
    private ScaleAnimation Anim_scale_big, Anim_scale_normal;
    private AnimationSet Anim_set;
    private boolean Animating = false;
    private long min_time;
}
