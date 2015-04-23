/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.effectivenavigation;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


//import com.jjoe64.graphview.BarGraphView;
import com.example.android.effectivenavigation.dbcontrol.MeasuresDataSource;
import com.example.android.effectivenavigation.dbcontrol.UsersDataSource;
import com.example.android.effectivenavigation.db.Measure;
import com.example.android.effectivenavigation.db.User;
import com.example.android.effectivenavigation.ui.CollectionDemoActivity;
import com.example.android.effectivenavigation.ui.Exercise_base;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
//import com.jjoe64.graphview.GraphView.GraphViewData;
//import com.jjoe64.graphview.GraphViewSeries;
//import com.jjoe64.graphview.LineGraphView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {



    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;


    /**
     * The {@link android.support.v4.view.ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    private Context context;

//    private final SharedPreferences        prefs  = getSharedPreferences("MyPrefsFile", 0);

    static SharedPreferences pref;// = .getSharedPreferences("MyPref", 0); // 0 - for private mode
    static SharedPreferences.Editor editor;// = pref.edit();

    public TextView text;
    private final long startTime = 30 * 1000;
    private final long interval = 1 * 1000;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        editor.putString("ac","0");//actionfullffilled
        editor.commit(); // commit changes



        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }






    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        protected static final String[] CONTENT = new String[] { "Activities ", "Statistics", "Couch Advice", };


        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
//                    return new LaunchpadSectionFragment();
                    return new ActivitiesSectionFragment();

                case 1:
                    /*
                    // The other sections of the app are dummy placeholders.
                    Fragment fragmentx = new DummySectionFragment();
                    Bundle argsx = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragmentx.setArguments(argsx);
                    */
                return new CoachAdviceSectionFragment();


                case 2:
                return new StatisticsSectionFragment();

//                    return new CoachAdviceSectionFragment();
                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new DummySectionFragment();
                    Bundle args = new Bundle();
                    args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }


        // CHANGE START HERE
        private int current_position=0;

        public void set_current_position(int i) {
            current_position = i;
        }
        // CHANGE ENDS HERE

        @Override
        public CharSequence getPageTitle(int position) {
            // CHANGE START HERE
            /*
            if (position == current_position-1) {
                return "Previous";
            } else if (position == current_position+1) {
                return "Next";
            }
            */
            // CHANGE ENDS HERE
            return AppSectionsPagerAdapter.CONTENT[position % CONTENT.length];
        }
    }

    /************************************************************************************
     *private static class HttpReceive extends AsyncTask<Void, Void, String> {
     */
    public static class ActivitiesSectionFragment extends Fragment implements AdapterView.OnItemSelectedListener{

        private  Context contex;




        Button but1, but2,but3,but4, but5;


        private ListView maListViewPerso ;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.activity_list_activ,container,false);


           // maListViewPerso = inflater.inflate(R.layout.affichageitem,container,false);


            //Récupération de la listview créée dans le fichier main.xml
            maListViewPerso = (ListView)rootView.findViewById(R.id.listviewperso);

            //Création de la ArrayList qui nous permettra de remplire la listView
            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

            //On déclare la HashMap qui contiendra les informations pour un item
            HashMap<String, String> map;


            //On refait la manip plusieurs fois avec des données différentes pour former les items de notre ListView

            map = new HashMap<String, String>();
            map.put("titre", "Squats");
            map.put("description", "The objective of the squats is to lift the barbell from the ground to overhead in one continuous motion.");
            map.put("img", String.valueOf(R.drawable.lift1b));
            listItem.add(map);

            map = new HashMap<String, String>();
            map.put("titre", "Clean & Jerk");
            map.put("description", "The lifter moves the barbell from the floor to a racked position across deltoids and clavicles.");
            map.put("img", String.valueOf(R.drawable.lift3b));
            listItem.add(map);

            map = new HashMap<String, String>();
            map.put("titre", "Lifting 1");
            map.put("description", "Description of the exercise");
            map.put("img", String.valueOf(R.drawable.lift1b));
            listItem.add(map);

            //Création d'une HashMap pour insérer les informations du premier item de notre listView
            map = new HashMap<String, String>();
            map.put("titre", "Lifting 2");
            map.put("description", "Description of the exercise");
            map.put("img", String.valueOf(R.drawable.lift2b));
            listItem.add(map);


            contex = new MainActivity().context;
            contex = rootView.getContext();




            //Création d'un SimpleAdapter qui se chargera de mettre les items présent dans notre list (listItem) dans la vue affichageitem
            SimpleAdapter mSchedule = new SimpleAdapter (contex, listItem, R.layout.affichageitem,
                    new String[] {"img", "titre", "description"}, new int[] {R.id.img, R.id.titre, R.id.description});

            try {
                //On attribut à notre listView l'adapter que l'on vient de créer
                maListViewPerso.setAdapter(mSchedule);
            }catch (Exception exc){
                System.err.println("ERRORRRRR:"+exc.getLocalizedMessage() + "__"+exc.getMessage());
            }



            //Enfin on met un écouteur d'évènement sur notre listView
            maListViewPerso.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                @SuppressWarnings("unchecked")
                public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    //on récupère la HashMap contenant les infos de notre item (titre, description, img)
                    HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);

                    if(map.get("titre").contains("Snatch")){
                        Intent intent = new Intent(getActivity(), Exercise_base.class);
                        startActivity(intent);
                    }else{/** CADA EJERCICIO DEBERIA TENER UNA DIFFERENTE ACTIVITY PARA MEDIR DIFERENTES COSAS*/
                        Intent intent = new Intent(getActivity(), Exercise_base.class);
                        startActivity(intent);
                    }


/*


                    //on créer une boite de dialogue
                    AlertDialog.Builder adb = new AlertDialog.Builder(contex);
                    //on attribut un titre à notre boite de dialogue
                    adb.setTitle("Sélection Item");
                    //on insère un message à notre boite de dialogue, et ici on affiche le titre de l'item cliqué
                    adb.setMessage("Votre choix : "+map.get("titre"));
                    //on indique que l'on veut le bouton ok à notre boite de dialogue
                    adb.setPositiveButton("Ok", null);
                    //on affiche la boite de dialogue
                    adb.show();
*/


                }
            });



/*


        but1 = (Button)rootView.findViewById(R.id.button);
        but2 = (Button)rootView.findViewById(R.id.button2);

            rootView.findViewById(R.id.button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), ChairStanding.class);
                            startActivity(intent);
                        }
                    });


            rootView.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), SwayDetect.class);
                    startActivity(intent);
                }
            });

*/
            return rootView;//super.onCreateView(inflater, container, savedInstanceState);
        }



        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }


    }


    /**************************************************************************************
     * THis is the fragment showing User content (first tab section)
     */
    public static class StatisticsSectionFragment extends Fragment{

        String uricntdwn= "android.resource://com.example.android.effectivenavigation/"+R.drawable.squatb;

        Spinner spinner1, spinner2;
        String dato="";
        private UsersDataSource datasource;
        private ArrayAdapter<String> adapter_state;
        private Context contxt;
        private VideoView videointro;
        private int position = 0;
        private String ipconf ="";
      //  private ProgressDialog progressDialog;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_user, container, false);
            StrictMode.ThreadPolicy policy = new StrictMode.
                    ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
         //   new HttpReceive().execute();

//            odaberiRazinu.setDisplayedValues( new String[] { "FirstAnswer", "SecondAnswer", "ThirdAnswer", "FourthAnswer"} );
            contxt = rootView.getContext();

            int duration = Toast.LENGTH_SHORT;

            videointro = (VideoView)rootView.findViewById(R.id.videoViewSquat);

/*
            progressDialog = new ProgressDialog(contxt);
            progressDialog.setTitle("JavaCodeGeeks Android Video View Example");
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
*/
            videointro.setVideoURI(Uri.parse(uricntdwn));
            videointro.setMediaController(new MediaController(contxt));
//            videointro.start();

/*
            videointro.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    videointro.seekTo(position);


                }
            });


            videointro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    System.out.println("ja");
                    videointro.start();

                }
            });

  */




            return rootView;

        }







}



    /**************************************************************************************
     * THis is the fragment showing User content (first tab section)
     */
    public static class CoachAdviceSectionFragment extends Fragment implements AdapterView.OnItemSelectedListener{



        String dato="";
        private UsersDataSource datasource;
        private ArrayAdapter<String> adapter_state;
        private Context contxt;
        private Button button;

        private MeasuresDataSource measuresDataSource;
        private String databaseName = "chairstanding";
        private  static Spinner  spinner2;
        private List<User> usersList;
        private String username = "";
        private int idusername = 0;
        private GraphView graphView;
        private LinearLayout layoutx;


        private final Handler mHandler = new Handler();
        private Runnable mTimer1;
        private Runnable mTimer2;
        private LineGraphSeries<DataPoint> mSeries0;
        private LineGraphSeries<DataPoint> mSeries1;
        private LineGraphSeries<DataPoint> mSeries2;

        private double graph2LastXValue = 5d;

        private GraphView graph;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_history, container, false);
            StrictMode.ThreadPolicy policy = new StrictMode.
                    ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //   new HttpReceive().execute();

//            odaberiRazinu.setDisplayedValues( new String[] { "FirstAnswer", "SecondAnswer", "ThirdAnswer", "FourthAnswer"} );
            contxt = rootView.getContext();



//            LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.graph1);
            usersList = new ArrayList<User>();

            spinner2 = (Spinner) rootView.findViewById(R.id.spinner2);
            List<String> list = new ArrayList<String>();
            list.add("Choose the Day...");
            list.add("Day 1");
            list.add("Day 2");
            list.add("Day 3");
            list.add("Day 4");
            list.add("Day 5");
            list.add("Day 6");
            list.add("Day 7");
            list.add("Day 8");
            list.add("Day 9");



            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(contxt,android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner2.setAdapter(dataAdapter);




/*

            GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
                    new GraphViewData(1, 2.0d)
                    , new GraphViewData(2, 1.5d)
                    , new GraphViewData(2.5, 3.0d) // another frequency
                    , new GraphViewData(3, 2.5d)
                    , new GraphViewData(4, 1.0d)
                    , new GraphViewData(5, 3.0d)
            });

           GraphView graphView = new LineGraphView(contxt, "GraphViewDemo");
                graphView.addSeries(exampleSeries);

                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.graph1);//findViewById(R.id.graph1);
                ((LineGraphView) graphView).setDrawBackground(true);
                ((LineGraphView) graphView).setBackgroundColor(Color.rgb(80, 30, 30));
               layout.addView(graphView);
*/




/*
            graphView = new LineGraphView(contxt, "General Balance Status" );
            //GraphView graphView = new BarGraphView(contxt, "General Balance Status" );
            graphView.addSeries(exampleSeries); // data
            */
            //layoutx = (LinearLayout)rootView.findViewById(R.id.graph1);



            graph = (GraphView) rootView.findViewById(R.id.grapht);

            graph.getViewport().setScalable(true);
            graph.getViewport().setScrollable(true);

            //mSeries1 = new LineGraphSeries<DataPoint>(generateData());
            mSeries0 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(0, 0)
            });
            graph.addSeries(mSeries0);

            listenerList();


            /*
            button = (Button) rootView.findViewById(R.id.buttonUrl);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(contxt, FullscreenGraphic.class);
                    startActivity(intent);
                }
            });
*/



            return rootView;

        }

        private DataPoint[] generateData() {


            DataPoint[] values;

            long lid = 0;
            long mid = 0;

            int sizeList =0;
            try {
                lid= Long.parseLong(pref.getString("userList", null));
                System.out.println("long USER ID = " + lid);
            } catch (NumberFormatException nfe) {
                System.out.println("NumberFormatException: " + nfe.getMessage());
            }

            measuresDataSource = new MeasuresDataSource(contxt,databaseName);
            measuresDataSource.open();
       //     List<Measure> measuresListSize =  measuresDataSource.getAllMeasures();
            List<Measure> measuresListSizeId =  measuresDataSource.getMeasuresbyId(lid);

            sizeList = measuresListSizeId.size();




           // DataPoint[] values = new DataPoint[measuresListSize.size()+10];

/*
            for (Iterator<Measure> it = measuresListSize.iterator(); it.hasNext();) {
                Measure measu = it.next();

                //TODO QUE SAQUE EN UNA LISTA LOS QUE SON  userN = measu.getId_user()

                  //  System.err.println("(OK)====>"+measu.getX()+"=="+measu.getY()+"=="+measu.getZ()+"=="+measu.getTimestamp()+"=="+measu.getId_user());



            }

            DataPoint v = new DataPoint(0, 0);
            values[0] = v;
            */


            if (sizeList > 1){
//                values = new DataPoint[sizeList];
//                for (int i=0; i<sizeList; i++) {

                    DataPoint v = new DataPoint(0, 0);
//                    values[i] = v;

                System.out.println("SIZE FULL::"+sizeList + "SIZE-id:"+measuresListSizeId.size());

                    Random mRand = new Random();
                    int count = sizeList;
                    values = new DataPoint[count];

                    for (int j=0; j<count; j++) {



                        //double x = j;
                        //double f = mRand.nextDouble()*0.15+0.3;
                        //double y = Math.sin(j*f+2) + mRand.nextDouble()*0.3;
                        //DataPoint v = new DataPoint(0, 0);
                        v = new DataPoint(measuresListSizeId.get(j).getTimestamp(), measuresListSizeId.get(j).getZ());
                        values[j] = v;
                    }

                    /*

                    Measure measu = measuresListSize.get(i);
                    mid = measu.getId_user();

                    System.out.println("lid--->"+lid + " mid==>"+mid);

                    if(mid == lid ){
                        System.out.println("TRUE");
                        DataPoint v = new DataPoint(measu.getTimestamp(), measu.getZ());
                        values[i] = v;
                    }
                    */


                    //System.err.println("(OK)====>"+measu.getX()+"=="+measu.getY()+"=="+measu.getZ()+"=="+measu.getTimestamp()+"=="+measu.getId_user());
//                }

            }else{
                int count = 1;
                values = new DataPoint[count];

                for (int i=0; i<count; i++) {
                    DataPoint v = new DataPoint(0, 0);
                    //DataPoint v = new DataPoint(x, y);
                    values[i] = v;
                }

            }




            /*
            for (int i=1; i<measuresListSize.size(); i++) {

                Measure measu = measuresListSize.get(i);
                mid = measu.getId_user();

                System.out.println("lid--->"+lid + " mid==>"+mid);

                if(mid == lid ){
                    System.out.println("TRUE");
                    v = new DataPoint(measu.getTimestamp(), measu.getZ());
                    values[i] = v;
                }


                //System.err.println("(OK)====>"+measu.getX()+"=="+measu.getY()+"=="+measu.getZ()+"=="+measu.getTimestamp()+"=="+measu.getId_user());
            }

*/


            measuresDataSource.close();


/*

            Random mRand = new Random();
            int count = 30;

            for (int i=0; i<count; i++) {
                double x = i;
                double f = mRand.nextDouble()*0.15+0.3;
                double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
                DataPoint v = new DataPoint(x, y);
                values[i] = v;
            }

            */

            return values;
        }







        public void listenerList(){

            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    graph.removeAllSeries();

                    String selectedItem = (String)spinner2.getSelectedItem();
                    System.out.println("\t (OK) item selected:"+position + "  >"+selectedItem);


                    if(selectedItem.contains("Choose")){
                        mSeries0 = new LineGraphSeries<DataPoint>(new DataPoint[] {
                                new DataPoint(0, 0)
                        });
                        graph.addSeries(mSeries0);
                    }else{


                        editor.putString("userList", String.valueOf(position-1)); // Storing string
                        editor.commit(); // commit changes


                        System.out.println("++++++++++++++ USERLLIST:"+pref.getString("userList", null));

                        DataPoint[] newData = generateData();


                            LineGraphSeries<DataPoint> mSeriesNew  = new LineGraphSeries<DataPoint>(newData);

                            graph.removeSeries(mSeries1);
                            graph.removeSeries(mSeriesNew);
                            graph.addSeries(mSeriesNew);
                            mSeriesNew.setTitle("Acceleration vs. Time");
                            mSeriesNew.setColor(Color.GREEN);
                            mSeriesNew.setDrawDataPoints(true);
                            mSeriesNew.setDataPointsRadius(6);

               /*
                            // set manual X bounds
                            graph.getViewport().setXAxisBoundsManual(true);
                            graph.getViewport().setMinX(0.5);
                            graph.getViewport().setMaxX(3.5);

    // set manual Y bounds
                            graph.getViewport().setYAxisBoundsManual(true);
                            graph.getViewport().setMinY(3.5);
                            graph.getViewport().setMaxY(8);

    */

                            //mSeriesNew.setThickness(8);
                            mSeriesNew.setOnDataPointTapListener(new OnDataPointTapListener() {
                                @Override
                                public void onTap(Series series, DataPointInterface dataPoint) {
                                    Toast.makeText(getActivity(), "Data Point: "+dataPoint, Toast.LENGTH_SHORT).show();
                                }
                            });



                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        private double getTimeTest() {


            double timeMaxValue=0;
            double timeMinValue=0;
            System.err.println("\t (OK) -user--> "+pref.getString("user", null));
            System.err.println("\t (OK) -userN--> "+pref.getString("userN", null));
            long lid = 0;
            long mid = 0;

            int sizeList =0;
            try {
                lid= Long.parseLong(pref.getString("userN", null));
                System.out.println("long USER ID = " + lid);
            } catch (NumberFormatException nfe) {
                System.out.println("NumberFormatException: " + nfe.getMessage());
            }

          //  measuresDataSource = new MeasuresDataSource(contxt,databaseName);
          //  measuresDataSource.open();
           // timeMaxValue=  measuresDataSource.getMaxMeasuresTimebyId(lid);
          //  timeMinValue=  measuresDataSource.getMinMeasuresTimebyId(lid);

          //  measuresDataSource.close();

            return timeMaxValue-timeMinValue;
        }



        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

/*
        private GraphViewSeries generateData(){

            GraphViewSeries   dataTemp = new GraphViewSeries(new GraphViewData[] {
                    new GraphViewData(1, 40)
            });

            return dataTemp;
        }
*/

        /* dataSeries = new GraphViewSeries(new GraphViewData[] {
                new GraphViewData(1, 40)
                , new GraphViewData(2, 12)
                , new GraphViewData(3, 7)
                , new GraphViewData(2, 8)
                , new GraphViewData(2, 10)
                , new GraphViewData(3, 26)
                , new GraphViewData(1, 37)
                , new GraphViewData(1, 53)
                , new GraphViewData(3, 253)
        });
*/

        public List<User> getUsersList() {
            return usersList;
        }

        public void setUsersList(List<User> usersList) {
            this.usersList = usersList;
        }
    }

    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

            // Demonstration of a collection-browsing activity.
            rootView.findViewById(R.id.demo_collection_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CollectionDemoActivity.class);
                            startActivity(intent);
                        }
                    });

            // Demonstration of navigating to external activities.
            rootView.findViewById(R.id.demo_external_activity)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an intent that asks the user to pick a photo, but using
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                            // the application from the device home screen does not return
                            // to the external activity.
                            Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                            externalActivityIntent.setType("image/*");
                            externalActivityIntent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            startActivity(externalActivityIntent);
                        }
                    });

            return rootView;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


    /*********************************************
     *
     */
    private static class HttpReceive extends AsyncTask<Void, Void, String> {





        private List<NameValuePair> dataToReceive;

        private List<String> users = new ArrayList<String>();
        private UsersDataSource datasource;


        protected String getASCIIContentFromEntity(HttpEntity entity)
                throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);
                if (n > 0)
                    out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();


            // httpPost.setHeader("Accept", "text/plain");
            // httpPost.setHeader("Content-type",
            // "application/x-www-form-urlencoded");

//			HttpPost httpPost = new HttpPost("http://130.239.41.93:8080/Balance3.2/resources/balance/userdata");

            //HttpPut httpPost = new HttpPut("http://130.239.213.140:8080/Balance3.2/resources/balance/userdata");
            HttpPut httpPost = new HttpPut("http://130.239.213.140:8080/Balance4.0/resources/balance/userdata");

//            HttpGet httpGet = new HttpGet("http://130.239.41.93:8080/Balance3.2/resources/balance/users");


            try {
                // Add your data
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();

                //pairs.add(new BasicNameValuePair(pairsX.get(pairsX.size()-1).getName(),pairsX.get(0).getValue()));


                long timestamp = System.currentTimeMillis();


                //TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
                java.util.Date timest  = new java.util.Date((long) timestamp * 1000);

                String timstart = String.valueOf(timest);

                long nanoTime = System.nanoTime();
                System.out.println("nanotime:"+nanoTime);
                System.out.println("timestamp:"+timstart);
                System.out.println("Systimestmp:"+timestamp);

                pairs.add(new BasicNameValuePair("key1",""));
                pairs.add(new BasicNameValuePair("key2",""));
                pairs.add(new BasicNameValuePair("key3",""));
                pairs.add(new BasicNameValuePair("key4","userID"));//TODO SEND THE USER ID
                //pairs.add(new BasicNameValuePair("key5",String.valueOf(timestamp)));
                pairs.add(new BasicNameValuePair("key5",""));
                pairs.add(new BasicNameValuePair("key6","m1"));




                //httpPost.setEntity(new UrlEncodedFormEntity(pairs));

                httpPost.setEntity(new UrlEncodedFormEntity(pairs));



				/* funciona */
                // httpPost.setEntity(new StringEntity(obj.toString(),
                // "UTF-8"));
				/* funciona */
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // HttpGet httpGet = new
            // HttpGet("http://130.239.41.93:8080/AAL2v1.0/resources/greeting/create2");
            String text = null;
            try {
                // HttpResponse response = httpClient.execute(httpGet,
                // localContext);
                HttpResponse response = httpClient.execute(httpPost,
                        localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

                System.out.println("--> * * * * ->Response:" + text);



                StringTokenizer st2 = new StringTokenizer(text, ",");
                List<String> uslist = new ArrayList<String>();
                List<User> uList = new ArrayList<User>();



                while (st2.hasMoreElements()) {



/*

                    datasource = new UsersDataSource();
                    datasource.open();

                    User newuser = datasource.createUser((String)st2.nextElement().toString(), "comment");

                    if (newuser != null) {
                        System.out.println("parece que si grabo el new User:"+newuser.getUsername() + "|"+newuser.getComment());
                    } else {
                        System.out
                                .println("NO, no graba: New User");
                    }
                    datasource.close();

*/


                    uslist.add((String)st2.nextElement());


/*
                    Users user = new Users();
                    user.setUsername((String)st2.nextElement());
                    uList.add(user);
*/
                    //uslist.add((String)st2.nextElement());
                    //System.out.println(st2.nextElement());
                }
                setUsers(uslist);



            } catch (Exception e) {
                System.out.println("EROR:" + e.getLocalizedMessage());
                return e.getLocalizedMessage();
            }
            return text;
        }

        protected void onPostExecute(String results) {
            if (results != null) {
                //	EditText et = (EditText) findViewById(R.id.my_edit);
                //	et.setText(results);w
            }
            //	Button b = (Button) findViewById(R.id.my_button);
            //	b.setClickable(true);
        }

        public List<NameValuePair> getDataToReceive() {
            return dataToReceive;
        }

        public void setDataToReceive(List<NameValuePair> dataToReceive) {
            this.dataToReceive = dataToReceive;
        }


        public List<String> getUsers() {
            return users;
        }

        public void setUsers(List<String> users) {
            this.users = users;
        }
    }




}
