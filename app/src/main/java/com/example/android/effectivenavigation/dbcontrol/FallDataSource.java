package com.example.android.effectivenavigation.dbcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.effectivenavigation.db.FallMeasure;
import com.example.android.effectivenavigation.db.MySQLiteHelper;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Esteban on 2014-10-14.
 */
public class FallDataSource {

    // Database fields
    private SQLiteDatabase database;

    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMNFALL_ID, MySQLiteHelper.COLUMN_X,
            MySQLiteHelper.COLUMN_Y, MySQLiteHelper.COLUMN_Z, MySQLiteHelper.COLUMN_TSTAMP, MySQLiteHelper.COLUMN_GRADE ,MySQLiteHelper.COLUMNUS_ID };

    //private String[] averColumn = { MySQLiteHelper.COLUMN_AVERAGE};


    public FallDataSource(Context context, String DatabaseName) {
        dbHelper = new MySQLiteHelper(context, DatabaseName);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public FallMeasure createFallMeasure(String x, String y, String z, String timestamp, String grade, long id_user ) {

        ContentValues values = new ContentValues();


        values.put(MySQLiteHelper.COLUMN_X, x);
        values.put(MySQLiteHelper.COLUMN_Y, y);
        values.put(MySQLiteHelper.COLUMN_Z, z);
        values.put(MySQLiteHelper.COLUMN_TSTAMP, timestamp);
        values.put(MySQLiteHelper.COLUMN_GRADE, grade);
        values.put(MySQLiteHelper.COLUMNUS_ID, id_user);

//        System.err.println("(OK) values size:"+values.size()+" MySQLiteHelper.TABLE_MEASURES:"+MySQLiteHelper.TABLE_MEASURES);


        long insertId = database.insert(MySQLiteHelper.TABLE_FALLS, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FALLS, allColumns, MySQLiteHelper.COLUMNFALL_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        FallMeasure newFallMeasure = cursorToFallMeasure(cursor);
        cursor.close();
        return newFallMeasure;

    }

    public Cursor getCursor() {


        Cursor cursor = database.query(MySQLiteHelper.TABLE_FALLS, allColumns,
                null, null, null, null, null);

        return cursor;

    }


    public FallMeasure createFallMeasure(double x, double y, double z, double timestamp,  double grade, long id_user) {

        ContentValues values = new ContentValues();


        values.put(MySQLiteHelper.COLUMN_X, x);
        values.put(MySQLiteHelper.COLUMN_Y, y);
        values.put(MySQLiteHelper.COLUMN_Z, z);
        values.put(MySQLiteHelper.COLUMN_TSTAMP, timestamp);
        values.put(MySQLiteHelper.COLUMN_GRADE, grade);
        values.put(MySQLiteHelper.COLUMNUS_ID, id_user);


        long insertId = database.insert(MySQLiteHelper.TABLE_FALLS, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FALLS, allColumns, MySQLiteHelper.COLUMNFALL_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        FallMeasure newFallMeasure = cursorToFallMeasure(cursor);
        cursor.close();
        return newFallMeasure;

    }

    public void deleteFallMeasure(FallMeasure fallmeasure) {
        long id = fallmeasure.getId_fall();
        System.out.println("Fall Measure deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_FALLS, MySQLiteHelper.COLUMNFALL_ID + " = "
                + id, null);
    }






    public List<FallMeasure> getAllFallMeasures() {
        List<FallMeasure> fallmeasures = new ArrayList<FallMeasure>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_FALLS, allColumns,
                null, null, null, null, null);



        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FallMeasure fall = cursorToFallMeasure(cursor);
            fallmeasures.add(fall);

            cursor.moveToNext();

        }
        // make sure to close the cursor
        cursor.close();
        return fallmeasures;
    }




    private FallMeasure cursorToFallMeasure(Cursor cursor) {
        FallMeasure fallMasure = new FallMeasure();
        fallMasure.setId_fall(cursor.getLong(0));
        fallMasure.setX(cursor.getDouble(1));
        fallMasure.setY(cursor.getDouble(2));
        fallMasure.setZ(cursor.getDouble(3));
        fallMasure.setTimestamp(cursor.getDouble(4));
        fallMasure.setGrade(cursor.getDouble(5));
        fallMasure.setId_user(cursor.getLong(6));
        return fallMasure;
    }

}
