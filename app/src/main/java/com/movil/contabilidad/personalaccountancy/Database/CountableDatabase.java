package com.movil.contabilidad.personalaccountancy.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hass-pc on 17/03/2015.
 */
class CountableDatabase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CountableDatabase";

    public static final String TABLE_RUBROS = "Rubros";
    public static final String RUB_ID = "Rubro_id";
    public static final String RUB_NAME = "Rubro_name";
    public static final String RUB_DESC = "Rubro_desc";
    public static final String RUB_TYPE = "Rubro_type";
    public static final String RUB_EXPVAL = "Rubro_expval";
    public static final String RUB_ISENABLE = "Rubro_isenable";

    public static final String TABLE_CICLOS = "Ciclos";
    public static final String CIC_ID = "Ciclo_id";
    public static final String CIC_NAME = "Ciclo_name";

    public static final String TABLE_REPORTES = "Reportes";
    public static final String REP_ID = "Report_id";
    public static final String REP_RUB_ID = "rep_rub_id";
    public static final String REP_CIC_ID = "rep_cic_id";
    public static final String REP_RUB_VALUE = "rep_rub_value";

    public static final String DATABASE_CREATION_RUBROS =
            "CREATE TABLE " + TABLE_RUBROS + "("
            + RUB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  "
            + RUB_NAME + " TEXT NOT NULL, "
            + RUB_DESC + " TEXT, "
            + RUB_TYPE + " INTEGER, "
            + RUB_EXPVAL + " REAL, "
            + RUB_ISENABLE + " INTEGER); ";

    public static final String DATABASE_CREATION_CICLOS =
            "CREATE TABLE " + TABLE_CICLOS + "("
            + CIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CIC_NAME + " TEXT NOT NULL); ";

    public static final String DATABASE_CREATION_REPORTES =
            "CREATE TABLE " + TABLE_REPORTES + "("
            + REP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + REP_RUB_ID + " INTEGER, "
            + REP_CIC_ID + " INTEGER, "
            + REP_RUB_VALUE + " FLOAT);";

    public static final String DATABASE_DELETE_RUBROS = "DROP TABLE IF EXISTS " + TABLE_RUBROS + "; ";
    public static final String DATABASE_DELETE_CICLOS = "DROP TABLE IF EXISTS " + TABLE_CICLOS + "; ";
    public static final String DATABASE_DELETE_REPORTES = "DROP TABLE IF EXISTS " + TABLE_REPORTES + "; ";

    public CountableDatabase (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATION_RUBROS);
        db.execSQL(DATABASE_CREATION_CICLOS);
        db.execSQL(DATABASE_CREATION_REPORTES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATABASE_CREATION_RUBROS);
        db.execSQL(DATABASE_CREATION_CICLOS);
        db.execSQL(DATABASE_CREATION_REPORTES);
        onCreate(db);
    }

}
