package com.example.android.effectivenavigation.dbcontrol;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.effectivenavigation.db.Measure;
import com.example.android.effectivenavigation.db.MySQLiteHelper;

public class MeasuresDataSource {

	// Database fields
	private SQLiteDatabase database;

	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_X,
			MySQLiteHelper.COLUMN_Y, MySQLiteHelper.COLUMN_Z, MySQLiteHelper.COLUMN_TSTAMP, MySQLiteHelper.COLUMNUS_ID };

	//private String[] averColumn = { MySQLiteHelper.COLUMN_AVERAGE};
	

	public MeasuresDataSource(Context context, String databaseName) {
		dbHelper = new MySQLiteHelper(context, databaseName);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

    public Measure createMeasure(String x, String y, String z, String timestamp, long id_user ) {

        ContentValues values = new ContentValues();


        values.put(MySQLiteHelper.COLUMN_X, x);
        values.put(MySQLiteHelper.COLUMN_Y, y);
        values.put(MySQLiteHelper.COLUMN_Z, z);
        values.put(MySQLiteHelper.COLUMN_TSTAMP, timestamp);
        values.put(MySQLiteHelper.COLUMNUS_ID, id_user);

//        System.err.println("(OK) values size:"+values.size()+" MySQLiteHelper.TABLE_MEASURES:"+MySQLiteHelper.TABLE_MEASURES);


        long insertId = database.insert(MySQLiteHelper.TABLE_MEASURES, null, values);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_MEASURES, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        Measure newMeasure = cursorToMeasure(cursor);
        cursor.close();
        return newMeasure;

    }

    public Cursor getCursor() {


        Cursor cursor = database.query(MySQLiteHelper.TABLE_MEASURES, allColumns,
                null, null, null, null, null);

        return cursor;

    }


	public Measure createMeasure(double x, double y, double z, double timestamp, long id_user) {

		ContentValues values = new ContentValues();

	
		values.put(MySQLiteHelper.COLUMN_X, x);
		values.put(MySQLiteHelper.COLUMN_Y, y);
		values.put(MySQLiteHelper.COLUMN_Z, z);
		values.put(MySQLiteHelper.COLUMN_TSTAMP, timestamp);
		values.put(MySQLiteHelper.COLUMNUS_ID, id_user);

        //System.err.println("(OK) values size:"+values.size());


		long insertId = database.insert(MySQLiteHelper.TABLE_MEASURES, null, values);

		Cursor cursor = database.query(MySQLiteHelper.TABLE_MEASURES, allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();

		Measure newMeasure = cursorToMeasure(cursor);
		cursor.close();
		return newMeasure;

	}

	public void deleteMeasure(Measure measure) {
		long id = measure.getId();
		System.out.println("Measure deleted with id: " + id);
		database.delete(MySQLiteHelper.TABLE_MEASURES, MySQLiteHelper.COLUMN_ID + " = "
				+ id, null);
	}

	
	
	/*
	public float getTotalAverage() {
		List<Measure> measures = new ArrayList<Measure>();
		List averaData = new ArrayList();
		float averag = 0.0f;
		
		Cursor cursor = database.query(MySQLiteHelper.TABLE_MEASURES, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Measure measure = cursorToMeasure(cursor);
			measures.add(measure);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		
		for (Iterator iterator = measures.iterator(); iterator.hasNext();) {
			Measure measu = (Measure) iterator.next();
			averag = averag + measu.getAverage();
			
			//averaData.add(measu.getAverage());			
		}
		
		
		
		return  (averag/measures.size());
	}
	*/


	
	public List<Measure> getAllMeasures() {
		List<Measure> measures = new ArrayList<Measure>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_MEASURES, allColumns,
				null, null, null, null, null);



		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Measure measure = cursorToMeasure(cursor);
			measures.add(measure);

			cursor.moveToNext();

		}
		// make sure to close the cursor
		cursor.close();
		return measures;
	}


    public List<Measure> getMeasuresbyId(long id_user) {
        List<Measure> measures = new ArrayList<Measure>();

        //Cursor cursor = database.query(MySQLiteHelper.TABLE_MEASURES, allColumns, null, null, null, null, null);

        //Cursor cursor = database.rawQuery("select * from "+MySQLiteHelper.TABLE_MEASURES+" where "+MySQLiteHelper.COLUMN_ID +"= ?", new String[]{String.valueOf(id_user)});
        String idus = String.valueOf(id_user);



        Cursor cursor = database.rawQuery("select * from "+MySQLiteHelper.TABLE_MEASURES+" where "+MySQLiteHelper.COLUMNUS_ID +"="+ idus,null );
        //Cursor cursor = database.query(MySQLiteHelper.TABLE_MEASURES, allColumns, MySQLiteHelper.COLUMN_ID + " = " + id_user, null, null, null, null);

        //Cursor cursor = database.query(MySQLiteHelper.TABLE_MEASURES, null, MySQLiteHelper.COLUMN_ID +"=" + idus, null, null, null, null);
//        Cursor cursor = database.query(false, MySQLiteHelper.TABLE_MEASURES, null, MySQLiteHelper.COLUMN_ID +"=" + idus, new String[] {"_id"}, null, null,null, null);

       // System.out.println("=====>:"+"select "+MySQLiteHelper.COLUMN_ID+","+ MySQLiteHelper.COLUMN_X+","+MySQLiteHelper.COLUMN_Y+","+ MySQLiteHelper.COLUMN_Z+","+ MySQLiteHelper.COLUMN_TSTAMP+","+ MySQLiteHelper.COLUMNUS_ID+" from "+MySQLiteHelper.TABLE_MEASURES+" where "+MySQLiteHelper.COLUMN_ID +"="+ idus);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Measure measure = cursorToMeasure(cursor);
            measures.add(measure);

            cursor.moveToNext();

        }
        // make sure to close the cursor
        cursor.close();
        return measures;
    }


    public double getMaxMeasuresTimebyId(long id_user) {
        List<Measure> measures = new ArrayList<Measure>();

        double maxTime =0;
        String idus = String.valueOf(id_user);

        Cursor cursor = database.rawQuery("select MAX("+MySQLiteHelper.COLUMN_TSTAMP+") from "+MySQLiteHelper.TABLE_MEASURES,null );
//        Cursor cursor = database.rawQuery("select max(timestamp) from "+MySQLiteHelper.TABLE_MEASURES,null );
//        Cursor cursor = database.rawQuery("select * from "+MySQLiteHelper.TABLE_MEASURES+" where "+MySQLiteHelper.COLUMNUS_ID +"="+ idus,null );


 //       Cursor cursor = database.query(MySQLiteHelper.TABLE_MEASURES, new String[] {"MAX(timestamp)"},null,                null, null, null, null);


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            maxTime = cursorToMeasureTime(cursor);


/*
            if(measure!=null){
                maxTime= measure.getTimestamp();
            }
*/
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return maxTime;
    }



    public double getMinMeasuresTimebyId(long id_user) {
        List<Measure> measures = new ArrayList<Measure>();

        double minTime =0;
        String idus = String.valueOf(id_user);

        Cursor cursor = database.rawQuery("select MIN(timestamp) from "+MySQLiteHelper.TABLE_MEASURES,null );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Measure measure = cursorToMeasure(cursor);

            if(measure!=null){
                minTime= measure.getTimestamp();
            }



            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return minTime;
    }

	private Measure cursorToMeasure(Cursor cursor) {
		Measure measure = new Measure();
		measure.setId(cursor.getLong(0));
		measure.setX(cursor.getDouble(1));
		measure.setY(cursor.getDouble(2));
		measure.setZ(cursor.getDouble(3));
		measure.setTimestamp(cursor.getDouble(4));
        measure.setId_user(cursor.getLong(5));
		return measure;
	}

    private double cursorToMeasureTime(Cursor cursor) {
        double eee = 0;
        Measure measure = new Measure();

        eee = cursor.getDouble(4);
        return eee;
    }


}
