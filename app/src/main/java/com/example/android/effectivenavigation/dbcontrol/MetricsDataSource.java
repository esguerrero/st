package com.example.android.effectivenavigation.dbcontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


import com.example.android.effectivenavigation.db.MetricsEntity;
import com.example.android.effectivenavigation.db.MetricsSQL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Esteban on 2015-04-21.
 */
public class MetricsDataSource {

    // Database fields
    private SQLiteDatabase database;

    private MetricsSQL dbHelper;
    private String[] allColumns = { MetricsSQL.COLUMN_ID, MetricsSQL.COLUMN_WEIGHT,
            MetricsSQL.COLUMN_HEIGHT, MetricsSQL.COLUMN_MAXPOW, MetricsSQL.COLUMN_AVGPOW,
            MetricsSQL.COLUMN_MAXFORCE, MetricsSQL.COLUMN_MAXFORCE, MetricsSQL.COLUMN_AVGFORCE,
            MetricsSQL.COLUMN_AVGTIME, MetricsSQL.COLUMN_MAXDIST, MetricsSQL.COLUMN_AVGDIST,
            MetricsSQL.COLUMN_ID_SUBACT};

    //private String[] averColumn = { MetricsSQL.COLUMN_AVERAGE};




    public MetricsDataSource(Context context, String databaseName) {
        dbHelper = new MetricsSQL(context, databaseName.toString());
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public MetricsEntity createMetric(String weight, String height, String maxpow,
                                      String avgpow, String maxforce, String avgforce, String avgtime,
                                      String maxdist, String avgdist, long id_subact ) {

        /**
         public static final String COLUMN_WEIGHT = "weight";
         public static final String COLUMN_HEIGHT = "height";
         public static final String COLUMN_MAXPOW = "maxpow";
         public static final String COLUMN_AVGPOW = "avgpow";
         public static final String COLUMN_MAXFORCE = "maxforce";
         public static final String COLUMN_AVGFORCE = "avgforce";
         public static final String COLUMN_AVGTIME = "avgtime";
         public static final String COLUMN_MAXDIST = "maxdist";
         public static final String COLUMN_AVGDIST = "avgdist";
         */

        ContentValues values = new ContentValues();
        values.put(MetricsSQL.COLUMN_WEIGHT, weight);
        values.put(MetricsSQL.COLUMN_HEIGHT, height);
        values.put(MetricsSQL.COLUMN_MAXPOW, maxpow);
        values.put(MetricsSQL.COLUMN_AVGPOW, avgpow);
        values.put(MetricsSQL.COLUMN_MAXFORCE, maxforce);
        values.put(MetricsSQL.COLUMN_AVGFORCE, avgforce);
        values.put(MetricsSQL.COLUMN_AVGTIME, avgtime);
        values.put(MetricsSQL.COLUMN_MAXDIST, maxdist);
        values.put(MetricsSQL.COLUMN_AVGDIST, avgdist);
        values.put(MetricsSQL.COLUMN_ID_SUBACT, id_subact);


//        System.err.println("(OK) values size:"+values.size()+" MetricsSQL.TABLE_MEASURES:"+MetricsSQL.TABLE_MEASURES);


        long insertId = database.insert(MetricsSQL.TABLE_METRICS, null, values);

        Cursor cursor = database.query(MetricsSQL.TABLE_METRICS, allColumns, MetricsSQL.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        MetricsEntity newMetric = cursorToMetric(cursor);
        cursor.close();
        return newMetric;




    }

    public Cursor getCursor() {


        Cursor cursor = database.query(MetricsSQL.TABLE_METRICS, allColumns,
                null, null, null, null, null);

        return cursor;

    }


    public MetricsEntity createMetric(double weight, double height, double maxpow,
                                       double avgpow, double maxforce, double avgforce, double avgtime,
                                       double maxdist, double avgdist, long id_subact) {


        ContentValues values = new ContentValues();
        values.put(MetricsSQL.COLUMN_WEIGHT, weight);
        values.put(MetricsSQL.COLUMN_HEIGHT, height);
        values.put(MetricsSQL.COLUMN_MAXPOW, maxpow);
        values.put(MetricsSQL.COLUMN_AVGPOW, avgpow);
        values.put(MetricsSQL.COLUMN_MAXFORCE, maxforce);
        values.put(MetricsSQL.COLUMN_AVGFORCE, avgforce);
        values.put(MetricsSQL.COLUMN_AVGTIME, avgtime);
        values.put(MetricsSQL.COLUMN_MAXDIST, maxdist);
        values.put(MetricsSQL.COLUMN_AVGDIST, avgdist);
        values.put(MetricsSQL.COLUMN_ID_SUBACT, id_subact);


        //System.err.println("(OK) values size:"+values.size());


        long insertId = database.insert(MetricsSQL.TABLE_METRICS, null, values);

        Cursor cursor = database.query(MetricsSQL.TABLE_METRICS, allColumns, MetricsSQL.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();

        MetricsEntity newMetric = cursorToMetric(cursor);
        cursor.close();
        return newMetric;

    }

    public void deleteMetric(MetricsEntity metric) {
        long id = metric.get_id();
        System.out.println("Metric deleted with id: " + id);
        database.delete(MetricsSQL.TABLE_METRICS, MetricsSQL.COLUMN_ID + " = "
                + id, null);
    }



    public List<MetricsEntity> getAllMetrics() {
        List<MetricsEntity> metrics = new ArrayList<MetricsEntity>();

        Cursor cursor = database.query(MetricsSQL.TABLE_METRICS, allColumns,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MetricsEntity metric = cursorToMetric(cursor);
            metrics.add(metric);
            cursor.moveToNext();

        }
        // make sure to close the cursor
        cursor.close();
        return metrics;
    }


    public List<MetricsEntity> getMetricbyId(long id_subact) {
        List<MetricsEntity> metrics = new ArrayList<MetricsEntity>();

        String id_suba = String.valueOf(id_subact);

        Cursor cursor = database.rawQuery("select * from "+MetricsSQL.TABLE_METRICS+" where "+MetricsSQL.COLUMN_ID_SUBACT +"="+ id_suba,null );

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MetricsEntity metric = cursorToMetric(cursor);
            metrics.add(metric);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return metrics;
    }


    /**
     * methods for return max mins
     * @param cursor
     * @return
     */

/*
    public double getMaxMeasuresTimebyId(long id_user) {
        List<Measure> measures = new ArrayList<Measure>();

        double maxTime =0;
        String idus = String.valueOf(id_user);

        Cursor cursor = database.rawQuery("select MAX("+MetricsSQL.COLUMN_TSTAMP+") from "+MetricsSQL.TABLE_MEASURES,null );
//        Cursor cursor = database.rawQuery("select max(timestamp) from "+MetricsSQL.TABLE_MEASURES,null );
//        Cursor cursor = database.rawQuery("select * from "+MetricsSQL.TABLE_MEASURES+" where "+MetricsSQL.COLUMNUS_ID +"="+ idus,null );


        //       Cursor cursor = database.query(MetricsSQL.TABLE_MEASURES, new String[] {"MAX(timestamp)"},null,                null, null, null, null);


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            maxTime = cursorToMeasureTime(cursor);

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

        Cursor cursor = database.rawQuery("select MIN(timestamp) from "+MetricsSQL.TABLE_MEASURES,null );

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
*/



    private MetricsEntity cursorToMetric(Cursor cursor) {
        MetricsEntity measure = new MetricsEntity();
        measure.set_id(cursor.getInt(0));
        measure.setWeight(cursor.getInt(1));
        measure.setHeight(cursor.getInt(2));
        measure.setMaxPower(cursor.getInt(3));
        measure.setAvgPower(cursor.getInt(4));
        measure.setMaxForce(cursor.getInt(5));
        measure.setAvgForce(cursor.getInt(6));
        measure.setAvgTime(cursor.getInt(7));
        measure.setMaxDistance(cursor.getInt(8));
        measure.setAvgDistance(cursor.getInt(9));
        measure.set_id_activity(cursor.getInt(10));

        return measure;
    }

    private double cursorToMeasureTime(Cursor cursor) {
        double eee = 0;
        MetricsEntity measure = new MetricsEntity();

        eee = cursor.getDouble(4);
        return eee;
    }


}



