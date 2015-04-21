package com.example.android.effectivenavigation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VibEntity extends SQLiteOpenHelper {

	public static final String TABLE_MEASURES = "vib";
	public static final String COLUMN_ID = "_id";
	// public static final String COLUMN_COMMENT = "comment";
	public static final String COLUMN_TSTAMP = "timestamp";
	public static final String COLUMN_AVERAGE = "average";
	public static final String COLUMN_X = "x";
	public static final String COLUMN_Y = "y";
	public static final String COLUMN_Z = "z";

	private static final String DATABASE_NAME = "measures";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MEASURES + "("  + COLUMN_ID + " integer primary key autoincrement,"
                                    + COLUMN_X + " real, "
			                        + COLUMN_Y + " real, "
                                    + COLUMN_Z + " real, "
			                        + COLUMN_TSTAMP + " real" +
                                ");";

	public VibEntity(Context context) {
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
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASURES);
		onCreate(db);
	}

}
