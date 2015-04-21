package com.example.android.effectivenavigation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Esteban on 5/22/14.
 */
public class UserBean extends SQLiteOpenHelper {

    public static final String TABLE_USERS = "user";
    public static final String COLUMN_ID = "idusers";
     public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_USERNAME = "usernameSto";

    private static final String DATABASE_NAME = "users";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_USERS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_USERNAME + " real, "
            + COLUMN_COMMENT + " real);";

    public UserBean(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        System.out.println(DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(VibEntity.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

}
