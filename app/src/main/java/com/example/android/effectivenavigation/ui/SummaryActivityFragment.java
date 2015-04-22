package com.example.android.effectivenavigation.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.effectivenavigation.R;
import com.example.android.effectivenavigation.db.Measure;
import com.example.android.effectivenavigation.dbcontrol.MeasuresDataSource;
import com.example.android.effectivenavigation.dbcontrol.UsersDataSource;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.List;
import java.util.Random;



/**
 * A placeholder fragment containing a simple view.
 */
public class SummaryActivityFragment extends Fragment {
/*
    public SummaryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }



}


public class HistoricSectionFragment extends android.support.v4.app.Fragment implements AdapterView.OnItemSelectedListener{

    */
static SharedPreferences pref;// = .getSharedPreferences("MyPref", 0); // 0 - for private mode

    static SharedPreferences.Editor editor;// = pref.edit();



    String dato = "";
    private UsersDataSource datasource;
    private ArrayAdapter<String> adapter_state;
    private Context contxt;
    private Button button;

    private MeasuresDataSource measuresDataSource;
    private String databaseName = "chairstanding";
    private String username = "";
    private int idusername = 0;
    private GraphView graphView;
    private LinearLayout layoutx;

    private TextView textmaxacc, textotaltime, textmaxpow;

    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeries0;
    private LineGraphSeries<DataPoint> mSeries1;
    private PointsGraphSeries<DataPoint> mSeries2;

    private int maxacc, maxpow, totaltime;


    private double graph2LastXValue = 5d;

    private GraphView summarygraph;
    private GraphView summarygraph2;


    public SummaryActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //   new HttpReceive().execute();

//            odaberiRazinu.setDisplayedValues( new String[] { "FirstAnswer", "SecondAnswer", "ThirdAnswer", "FourthAnswer"} );
        contxt = rootView.getContext();


        CharSequence text = "Select the user!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(contxt, text, duration);
        toast.show();


        summarygraph = (GraphView) rootView.findViewById(R.id.summarygraph);
        summarygraph2 = (GraphView) rootView.findViewById(R.id.summarygraph2);

        summarygraph.getViewport().setScalable(true);
        summarygraph.getViewport().setScrollable(true);
        summarygraph2.getViewport().setScalable(true);
        summarygraph2.getViewport().setScrollable(true);

        mSeries0 = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 0)
        });

        mSeries2 = new PointsGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 0)
        });

        summarygraph.addSeries(mSeries0);
        summarygraph2.addSeries(mSeries2);

        drawSummaryGraph();


        textmaxacc = (TextView)rootView.findViewById(R.id.textmaxacc);
        textotaltime = (TextView)rootView.findViewById(R.id.texttotaltime);
        textmaxpow = (TextView)rootView.findViewById(R.id.textmaxpow);

        pref = contxt.getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();


        maxacc = pref.getInt("maxacc",1 );
        totaltime = pref.getInt("totaltime", 0);
        maxpow = pref.getInt("maxpow", 0);

        System.err.println("\t (-----------------> "+pref.getString("userN", null));


        textmaxacc.setText(String.valueOf(maxacc));
        textotaltime.setText(String.valueOf(totaltime));
        textmaxpow.setText(String.valueOf(maxpow));



        return rootView;

    }

    /**
     * Returns data form Measures table of DB
     *
     * @param typeofGraph
     * @return
     */
    private DataPoint[] generateData(int typeofGraph) {
        DataPoint[] values;

        long lid = 0;
        long mid = 0;

        int sizeList = 0;
        measuresDataSource = new MeasuresDataSource(contxt, databaseName);
        measuresDataSource.open();

        List<Measure> measuresListSizeId = measuresDataSource.getMeasuresbyId(0);

        sizeList = measuresListSizeId.size();

        if (sizeList > 1) {
            DataPoint v = new DataPoint(0, 0);
            Random mRand = new Random();
            int count = sizeList;
            values = new DataPoint[count];
            if (typeofGraph == 0) {

                /**
                 * Z T series
                 */
                for (int j = 0; j < count; j++) {
                    v = new DataPoint(measuresListSizeId.get(j).getTimestamp(), measuresListSizeId.get(j).getZ());
                    values[j] = v;
                }
            } else if (typeofGraph == 1) {
                /**
                 *  X Y series
                 */
                for (int j = 0; j < count; j++) {
                    v = new DataPoint(measuresListSizeId.get(j).getX(), measuresListSizeId.get(j).getY());
                    values[j] = v;
                }
            }
        } else

        {
            int count = 1;
            values = new DataPoint[count];
            for (int i = 0; i < count; i++) {
                DataPoint v = new DataPoint(0, 0);
                values[i] = v;
            }

        }

        measuresDataSource.close();
        return values;
    }


    public void drawSummaryGraph() {

        summarygraph.removeAllSeries();
        summarygraph2.removeAllSeries();

        DataPoint[] newDataXY = generateData(1);
        DataPoint[] newDataZT = generateData(0);

        LineGraphSeries<DataPoint> mSeriesNewZT = new LineGraphSeries<DataPoint>(newDataZT);
        PointsGraphSeries<DataPoint> mSeriesNewXY = new PointsGraphSeries<DataPoint>(newDataXY);
        //LineGraphSeries<DataPoint> mSeriesNewXY = new LineGraphSeries<DataPoint>(newDataXY);s

        summarygraph.removeSeries(mSeries1);
        summarygraph.removeSeries(mSeriesNewZT);
        summarygraph.addSeries(mSeriesNewZT);


        mSeriesNewZT.setTitle("Acceleration vs. Time");
        mSeriesNewZT.setColor(Color.GREEN);
        mSeriesNewZT.setDrawDataPoints(true);
        mSeriesNewZT.setDataPointsRadius(6);

        mSeriesNewZT.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getActivity(), "Data Point: " + dataPoint, Toast.LENGTH_SHORT).show();
            }
        });

        mSeriesNewXY.setSize(4);
        summarygraph2.removeSeries(mSeries2);
        summarygraph2.removeSeries(mSeriesNewXY);
        summarygraph2.addSeries(mSeriesNewXY);


    }

}

