package com.movil.contabilidad.personalaccountancy.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

/**
 * Created by hass-pc on 17/03/2015.
 */
public class Ciclo {

    private int id;
    private String name;
    private boolean Exists;
    Ciclo(int id, String name) {
        this.id = id;
        this.name = name;
        Exists = true;
    }

    public Ciclo(String name) {
        this.name = name;
        Exists = false;
        this.id = -1;
    }

    public boolean SaveCiclo() {
        CountableManager mg = CountableManager.getInstance();
        synchronized (mg.database) {
            SQLiteDatabase db = mg.database.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(CountableDatabase.CIC_NAME, name);
            long id = -1;
            if (Exists) {
                id = db.update(CountableDatabase.TABLE_CICLOS, values, CountableDatabase.CIC_ID + "=" + id, null);
            } else {
                id = db.insert(CountableDatabase.TABLE_CICLOS, null, values);
            }
            if (id != -1) {
                if (!Exists) {
                    Exists = true;
                    this.id = (int) id;
                }
                return true;
            }
            db.close();
            return false;
        }
    }

    /**
     * Get a Report All Rublos in The Ciclo
     * @return Pairs Key Values where Integer = Rublo id and Float = Value obtained
     */
    public HashMap<Integer, Float> getReport() {
        CountableManager mg = CountableManager.getInstance();
        synchronized (mg.database) {
            SQLiteDatabase db = mg.database.getReadableDatabase();
            String query =
                    "SELECT " + CountableDatabase.REP_RUB_ID + ", " + CountableDatabase.REP_RUB_VALUE +
                            " FROM " + CountableDatabase.TABLE_REPORTES +
                            " WHERE " + CountableDatabase.REP_CIC_ID + "=" + id;
            Cursor c = db.rawQuery(query, null);
            HashMap<Integer, Float> valores = new HashMap<>(c.getCount());
            while (c.moveToNext()) {
                valores.put(c.getInt(0), c.getFloat(1));
            }
            c.close();
            db.close();
            return valores;
        }
    }
    /**
     * In case This Ciclo is not saved, always return -1
     * @return Ciclo id
     */
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isExists() {
        return Exists;
    }

    public void setExists(boolean exists) {
        this.Exists = exists;
    }

    @Override
    public String toString() {
        return name;
    }
}
