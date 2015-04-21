package com.example.android.effectivenavigation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_MEASURES = "measures";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_FALLS = "falls";

    public static final String COLUMNUS_ID = "_idu";
    public static final String COLUMNUS_USERNAME = "usernameSto";

	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TSTAMP = "timestamp";
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";
    public static final String COLUMN_Z = "z";

    public static final String COLUMNFALL_ID = "_idf";
    public static final String COLUMN_GRADE = "grade";


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
            + TABLE_MEASURES+ "("   + COLUMN_ID + " integer primary key autoincrement, "
                                    + COLUMN_X+ " real, "
                                    + COLUMN_Y+ " real, "
                                    + COLUMN_Z+ " real, "
                                    + COLUMN_TSTAMP+ " real, "
                                    + COLUMNUS_ID+ " integer "
                            +");";


    private static final String TABLEFALL_CREATE = "create table "
            + TABLE_FALLS+ "("   + COLUMNFALL_ID + " integer primary key autoincrement, "
            + COLUMN_X+ " real, "
            + COLUMN_Y+ " real, "
            + COLUMN_Z+ " real, "
            + COLUMN_TSTAMP+ " real, "
            + COLUMN_GRADE+ " real, "
            + COLUMNUS_ID+ " integer "
            +");";


    private static final String TABLEUS_CREATE = "create table "
            + TABLE_USERS+ "("   + COLUMNUS_ID + " integer primary key autoincrement, "
            + COLUMNUS_USERNAME+ " text "
            +");";

    /*
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_MEASURES+ "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_AVERAGE
			+ " text not null);";
*/
	public MySQLiteHelper(Context context, String DatabaseName) {
     		super(context, DatabaseName+".db", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
        System.err.println("(OK) DATABASE MEASURES CREATED_sql:"+DATABASE_CREATE + "--TABLE>"+TABLEUS_CREATE);
        database.execSQL(TABLEUS_CREATE);
        database.execSQL(TABLEFALL_CREATE);

        database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEASURES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FALLS);

		onCreate(db);
	}

}
