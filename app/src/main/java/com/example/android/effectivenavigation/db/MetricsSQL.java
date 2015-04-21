package com.example.android.effectivenavigation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Esteban on 2015-04-21.
 */
public class MetricsSQL extends SQLiteOpenHelper {

    public static final String TABLE_METRICS = "metrics";


    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_MAXPOW = "maxpow";
    public static final String COLUMN_AVGPOW = "avgpow";
    public static final String COLUMN_MAXFORCE = "maxforce";
    public static final String COLUMN_AVGFORCE = "avgforce";
    public static final String COLUMN_AVGTIME = "avgtime";
    public static final String COLUMN_MAXDIST = "maxdist";
    public static final String COLUMN_AVGDIST = "avgdist";
    public static final String COLUMN_ID_SUBACT = "idsubact";

    //private static final String DATABASE_NAME = "measures.db";
    private String DATABASE_NAME = "";
    private static final int DATABASE_VERSION = 1;

    public String getDATABASE_NAME() {
        return DATABASE_NAME;
    }

    public void setDATABASE_NAME(String DATABASE_NAME) {
        this.DATABASE_NAME = DATABASE_NAME;
    }

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_METRICS+ "("   + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_WEIGHT+ " integer, "
            + COLUMN_HEIGHT+ " integer, "
            + COLUMN_MAXPOW+ " integer, "
            + COLUMN_AVGPOW+ " integer, "
            + COLUMN_MAXFORCE+ " integer "
            + COLUMN_AVGFORCE+ " integer "
            + COLUMN_AVGTIME+ " integer "
            + COLUMN_MAXDIST+ " integer "
            + COLUMN_AVGDIST+ " integer "
            + COLUMN_ID_SUBACT+ " integer "
            +");";


    public MetricsSQL(Context context, String DatabaseName) {
        super(context, DatabaseName+".db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        System.err.println("(OK) DATABASE METRICS CREATED_sql:" + DATABASE_CREATE + "--TABLE>" + TABLE_METRICS);
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MetricsSQL.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_METRICS);
        onCreate(db);
    }

}
