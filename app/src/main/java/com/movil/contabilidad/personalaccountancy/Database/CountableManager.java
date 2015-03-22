package com.movil.contabilidad.personalaccountancy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by hass-pc on 17/03/2015.
 */
public class CountableManager {

    private static CountableManager ourInstance;
    private float lock = 0;
    CountableDatabase database;
    private Context c;
    public static CountableManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new CountableManager(context);
        }
        return ourInstance;
    }

    public static CountableManager getInstance() {
        return ourInstance;
    }

    private CountableManager(Context context) {
        database = new CountableDatabase(context);
        this.c = context;
        database.getWritableDatabase().close();
    }

    public Rublo[] getAllRublos() {
        synchronized (database) {
            SQLiteDatabase db = database.getReadableDatabase();
            String query =
                    "SELECT *" +
                            " FROM " + CountableDatabase.TABLE_RUBROS;
            Cursor c = db.rawQuery(query, null);
            int size = c.getCount();
            Rublo rublos[] = new Rublo[size];
            int i = 0;
            while (c.moveToNext()) {
                rublos[i] = new Rublo(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getFloat(4), c.getInt(5));
                i++;
            }
            c.close();
            db.close();
            return rublos;
        }
    }

    public Ciclo[] getAllCiclos() {
        synchronized (database) {
            SQLiteDatabase db = database.getReadableDatabase();
            String query =
                    "SELECT *" +
                            " FROM " + CountableDatabase.TABLE_CICLOS +
                            " ORDER BY " + CountableDatabase.CIC_ID;
            Cursor c = db.rawQuery(query, null);
            int size = c.getCount();
            Ciclo Ciclos[] = new Ciclo[size];
            int i = 0;
            while (c.moveToNext()) {
                Ciclos[i] = new Ciclo(c.getInt(0), c.getString(1));
                i++;
            }
            c.close();
            db.close();
            return Ciclos;
        }
    }

    public void GenerateReport(Random r, Rublo[] Rublos, String name) {
        synchronized (database) {
            if (Rublos.length > 0) {
                Ciclo actual = new Ciclo(name);
                actual.SaveCiclo();
                SQLiteDatabase db = database.getWritableDatabase();
                for (int i = 0; i < Rublos.length; i++) {
                    float random = (3.66f * r.nextFloat()) / 100f;
                /*
                    3.66% promedio variacion del precio de los productos y servicios
                    de la canasta familiar en colombia en el año 2014
                */
                    float value = Rublos[i].getExpected();
                    if ((r.nextInt(10) % 2 == 0))
                        value += Rublos[i].getExpected() * random;
                    else
                        value -= Rublos[i].getExpected() * random;
                /*
                    Probabilidad de 1/10 de obtener un valor inesperado.
                */
                    if (r.nextInt(10) == 1) {
                        if ((r.nextInt(10) % 2 == 0))
                            value += Rublos[i].getExpected() * 0.05 * r.nextFloat();
                        else
                            value -= Rublos[i].getExpected() * 0.05 * r.nextFloat();
                    }
                    ContentValues values = new ContentValues();
                    values.put(CountableDatabase.REP_CIC_ID, actual.getId());
                    values.put(CountableDatabase.REP_RUB_ID, Rublos[i].getId());
                    values.put(CountableDatabase.REP_RUB_VALUE, Rublos[i].isEnable() ? value : 0f);
                    db.insert(CountableDatabase.TABLE_REPORTES, null, values);
                }
                db.close();
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(c, "There is not Rubles", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void GenerateReport(Random r, String name) {
        GenerateReport(r,getAllRublos(), name);
    }
}
