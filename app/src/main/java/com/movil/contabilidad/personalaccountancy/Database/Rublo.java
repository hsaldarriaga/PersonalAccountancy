package com.movil.contabilidad.personalaccountancy.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

/**
 * Created by hass-pc on 17/03/2015.
 */
public class Rublo {

    private int id;
    private String name;
    private String Description;
    private TYPE type;
    private float Expected;
    private boolean Enable;
    private boolean changed = false;
    private boolean Exists;
    public enum TYPE {INFLOWS, OUTFLOWS};

    Rublo(int id, String name, String description, int type, float expected, int enable) {
        this.id = id;
        this.name = name;
        Description = description;
        switch (type) {
            case 0: this.type = TYPE.INFLOWS; break;
            case 1: this.type = TYPE.OUTFLOWS; break;
        }
        this.Expected = expected;
        this.Enable = (enable == 0) ? false : true;
        Exists = true;
    }

    public Rublo(String name, String description, TYPE type, float expected, boolean enable) {
        this.id = -1;
        this.name = name;
        Description = description;
        this.type = type;
        Expected = expected;
        Enable = enable;
        Exists = false;
        changed = true;
    }

    public boolean SaveRublo() {
        if (changed || !Exists) {
            changed = false;
            CountableManager mg = CountableManager.getInstance();
            synchronized (mg.database) {
                SQLiteDatabase db = mg.database.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(CountableDatabase.RUB_NAME, name);
                values.put(CountableDatabase.RUB_DESC, Description);
                values.put(CountableDatabase.RUB_TYPE, type.ordinal());
                values.put(CountableDatabase.RUB_EXPVAL, Expected);
                values.put(CountableDatabase.RUB_ISENABLE, (Enable) ? 1 : 0);
                long id = -1;
                if (Exists) {
                    id = db.update(CountableDatabase.TABLE_RUBROS, values, CountableDatabase.RUB_ID + "=" + this.id, null);
                } else {
                    id = db.insert(CountableDatabase.TABLE_RUBROS, null, values);
                    String query =
                            "SELECT " + CountableDatabase.CIC_ID +
                                    " FROM " + CountableDatabase.TABLE_CICLOS;
                    Cursor c = db.rawQuery(query, null);
                    while (c.moveToNext()) {
                        ContentValues false_values = new ContentValues();
                        false_values.put(CountableDatabase.REP_CIC_ID, c.getInt(0));
                        false_values.put(CountableDatabase.REP_RUB_ID, (int) id);
                        false_values.put(CountableDatabase.REP_RUB_VALUE, 0f);
                        db.insert(CountableDatabase.TABLE_REPORTES, null, false_values);
                    }
                    c.close();
                }
                if (id != -1) {
                    if (!Exists) {
                        Exists = true;
                        this.id = (int) id;
                    }
                    return true;
                }
                db.close();
            }
            return false;
        }
        return false;
    }

    /**
     * Get a Report All Ciclos in The Rublo
     * @return Pairs Key Values where Integer = Ciclo id and Float = Value obtained
     */
    public HashMap<Integer, Float> getReport() {
        CountableManager mg = CountableManager.getInstance();
        synchronized (mg.database) {
            SQLiteDatabase db = mg.database.getReadableDatabase();
            String query =
                    "SELECT " + CountableDatabase.REP_CIC_ID + ", " + CountableDatabase.REP_RUB_VALUE +
                            " FROM " + CountableDatabase.TABLE_REPORTES +
                            " WHERE " + CountableDatabase.REP_RUB_ID + "=" + id;
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
     * In case This Rublo is not saved, always return -1
     * @return Rublo id
     */
    public int getId() {
        return id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
        changed = true;
    }

    public String getName() {
        return name;
    }

    public boolean isEnable() {
        return Enable;
    }

    public void setEnable(boolean isEnable) {
        Enable = isEnable;
        changed = true;
    }

    public TYPE getType() {
        return type;
    }

    public float getExpected() {
        return Expected;
    }

    @Override
    public String toString() {
        if (type == TYPE.INFLOWS) {
            return this.name+ "(Inflows)";
        } else{
            return this.name+ " (Outflows)";
        }
    }
}
