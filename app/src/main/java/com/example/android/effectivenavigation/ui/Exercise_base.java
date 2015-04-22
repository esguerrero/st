package com.example.android.effectivenavigation.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.android.effectivenavigation.R;
import com.example.android.effectivenavigation.dbcontrol.MeasuresDataSource;
import com.example.android.effectivenavigation.db.FallMeasure;
import com.example.android.effectivenavigation.db.Measure;
import com.example.android.effectivenavigation.db.User;
import com.example.android.effectivenavigation.util.CSVWriter;
import com.example.android.effectivenavigation.util.SystemUiHider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


//import org.apache.commons.math3.*
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * Created by Esteban on 2015-04-15.
 */

public class Exercise_base extends Activity {


    private String ipconf ="";

    private Sensor mAccelerometer;
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private SystemUiHider mSystemUiHider;


    private static final String MEDIA_PATH = new String("/sdcard/data/ctdwn.mp3");
    private List<String> songs = new ArrayList<String>();
    private MediaPlayer mp = new MediaPlayer();
    private int currentPosition = 0;
    private int vcurrentPosition = 0;
    private int vduration = 0;
    private List<Measure> measuresList;
    private List<FallMeasure> fallmeasuresList;
    private List<User> usersList;
    private List<Double> zPeakList;
    private List<Double> zList;

    private double  startTimestamp = 0.0;

    private MeasuresDataSource measuresDataSource;
    //private FallDataSource fallDataSource;


    String uribeep= "android.resource://com.example.android.effectivenavigation/"+ R.drawable.beep;
    String uribeepin= "android.resource://com.example.android.effectivenavigation/"+R.drawable.beepin;

    String uribeepalarm = "android.resource://com.example.android.effectivenavigation/"+R.drawable.beepalarm;



    String urividb= "/storage/extSdCard/data/bc.wmv";

    int count=0;


    private Button butiBack, buttRepeat, saveButton;
//    private ImageButton butstart;


    //    private RunningGetIO runx;
    private Vibrator vibe;

    private final long startTime = 10 * 1000;
    private CountDownTimer countDownTimer;
    private final long interval = 1 * 1000;
    private    RunningSensor runSensor;
    final Context context = this;
    private String user ="";
    private String userN ="";
    ProgressDialog mDialog;

    private TextView countText;
    private TextView repetitionText;
    private TextView forceText;
    private TextView powerText;
    private TextView timeText;

    private ToggleButton togglebut;
    private ArrayAdapter<String> adapter_state;
    private String username = "";
    private int idusername = 0;

    private SQLiteDatabase myDatabase=null;

    static SharedPreferences pref;// = .getSharedPreferences("MyPref", 0); // 0 - for private mode
    static SharedPreferences.Editor editor;// = pref.edit();

    private String databaseName = "chairstanding";




    public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(context);
        final static private String APP_KEY = "9pbq9vxe38fkmy4";
        final static private String APP_SECRET = "8cje97ufafs2jjj";
        final static private String APP_TOKEN = "ra_c9KPzDsoAAAAAAAAABtol2YWkxcB6hws-tZJ__fp3ww5a1hmts1AA5158Vbja";

        // In the class declaration section:
        private DropboxAPI<AndroidAuthSession> mDBApi;

        private String dBFileName = "chairmeasures.csv";



        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
        }

        protected Boolean doInBackground(final String... args) {

            File dbFile = getDatabasePath(databaseName);
            //File dbFile = getDatabasePath("measures.db");
            System.err.println(dbFile);  // displays the data base path in your logcat
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");

            if (!exportDir.exists()) { exportDir.mkdirs(); }

            File file = new File(exportDir, dBFileName);
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

                measuresDataSource = new MeasuresDataSource(context,databaseName);
                measuresDataSource.open();

                Cursor curCSV = measuresDataSource.getCursor();// myDatabase.rawQuery("select * from " + Table_Name,null);

                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext()) {
                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1),curCSV.getString(2), curCSV.getString(3),curCSV.getString(4),curCSV.getString(5)};
                    csvWrite.writeNext(arrStr);
                }
                csvWrite.close();
                curCSV.close();
                measuresDataSource.close();

                return true;
            } catch(SQLException sqlEx) {
                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
                return false;
            } catch (IOException e) {
                Log.e("MainActivity", e.getMessage(), e);
                return false;
            }

        }



        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) { this.dialog.dismiss(); }
            if (success) {
                Toast.makeText(Exercise_base.this, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Exercise_base.this, "Export failed", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise1);
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        user = pref.getString("user", null);

        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;

        togglebut = (ToggleButton)findViewById(R.id.toggleButton);


        saveButton = (Button)findViewById(R.id.button);

        runSensor= new RunningSensor();

        runSensor.StartToggle();

        countText = (TextView)findViewById(R.id.textView);
        countText.setText("5:00");

        repetitionText = (TextView)findViewById(R.id.text_rep);

        forceText = (TextView)findViewById(R.id.text_force);
        powerText = (TextView)findViewById(R.id.text_power);
        timeText = (TextView)findViewById(R.id.text_time);



        measuresList = new ArrayList<Measure>();
        fallmeasuresList = new ArrayList<FallMeasure>();
        usersList = new ArrayList<User>();
        zPeakList = new ArrayList();
        zList = new ArrayList();

        setIpconf(ReadProps("ip.wired"));

        setUser();

        listenerSaveButton();

        System.out.println("XX");

        addListenerOnButton();

    }


    /**
     * Dummy method for set the username userid
     * it should be modified when is connected to ACKTUS or another auth service
     */
    private void setUser(){
        editor.putString("user", String.valueOf(0)); // Storing string id user: 1
        editor.putString("userN", String.valueOf(0)); // Storing string id user: 1
        editor.commit(); // commit changes

    }


    /**
     * Sets a number of repetitions perfromed in an activity
     */
    public void setRepetitionNumb(int repetitionNumber){
        editor.putInt("repetitions", repetitionNumber);
        editor.commit(); // commit changes

    }



    public void addListenerOnButton() {

        butiBack = (Button) findViewById(R.id.dummy_button_step1);

        butiBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();

            }

        });

    }



    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void listenerSaveButton(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportDatabaseCSVTask exportdb = new ExportDatabaseCSVTask();
                exportdb.execute();

                Toast.makeText(context, "Saving DB into a TXT file", Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }



    /**
     * Plays the beep sound when the timer (10 seconds) finishes
     *
     * @param songPath
     * @throws java.io.IOException
     */
    private void playBeep(String songPath) throws IOException {

        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(songPath));
        mediaPlayer.prepare();
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                System.err.println("******************** BEEP SOUND FINISHES AND RELEASED>");

            }
        });

    }



    /**%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     * Class for running the sensor
     */
    private class  RunningSensor  implements SensorEventListener {


        static final float ALPHA = 0.1f; // if ALPHA = 1 OR 0, no filter applies.
        private float grav[] = new float[3];


        protected float[] gravSensorVals;
        protected float[] magSensorVals;

        private float RTmp[] = new float[9];
        private float Rot[] = new float[9];
        private float I[] = new float[9];
        private float results[] = new float[3];


        private final long startTimez = 10 * 1000;
        private final long intervalz = 1 * 1000;

        private SensorManager mSensorManagerxx;
        private Sensor mAccelerometerxx;
        CountDownTimer cdt;
        MyCountDownTimerX cucu;

        private int repetitionCount;


        private void StartToggle(){
            togglebut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (b){


                        System.err.println("********************* START BUTTON IS PRESSED **************");

                        repetitionCount = 0;

                        togglebut.setClickable(false);

                        vibe.vibrate(50); // 50 is time in ms

                        new CountDownTimer(5000, 100) {

                            public void onTick(long millisUntilFinished) {
                                countText.setTextColor(Color.RED);
                                countText.setText("  " + millisUntilFinished / 1000+ " : "+millisUntilFinished/60);



                            }

                            public void onFinish() {
                                StartMeasures();
                                countText.setTextColor(Color.RED);
                                countText.setText("GO!");

                                countText.setText("");

                                vibe.vibrate(30); // 50 is time in ms

                                try {
                                    playBeep(uribeepin);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                /*
                                mDialog = new ProgressDialog(context);
                                mDialog.setMessage("wait...");
                                mDialog.show();
                                */
                                togglebut.setClickable(true);
                                startTime = System.currentTimeMillis();
                                timerHandler.postDelayed(timerRunnable, 0);



                            }
                        }.start();


                    }else{
                        timerHandler.removeCallbacks(timerRunnable);
                        System.out.println("OFF");
                        StopMeasures();

                        countText.setText("5:00");
                        // tiempo.cancel();
                        //  tiempo.purge();

                    }

                }
            });

        }

        protected float[] lowPass( float[] input, float[] output ) {
            if ( output == null ) return input;
            for ( int i=0; i<input.length; i++ ) {
                output[i] = output[i] + ALPHA * (input[i] - output[i]);
            }
            return output;
        }






        TextView timerTextView;
        long startTime = 0;

        //runs without a timer by reposting this handler at the end of the runnable
        Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {

            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

             //   countText.setTextColor(Color.GREEN);
             //   countText.setText("Sensing: " + String.format("%d:%02d", minutes, seconds));
                timeText.setText(String.format("%d:%02d", minutes, seconds));

                timerHandler.postDelayed(this, 500);
            }
        };



        /**
         * Activates the sensor mamanger
         */
        private void StartMeasures(){
            measuresList.clear();
            fallmeasuresList.clear();

            zPeakList.clear();
            zList.clear();

            System.err.println("******************** START MEASURES measureList:>"+measuresList.size());

            startTimestamp = System.currentTimeMillis();

            mSensorManagerxx = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            mAccelerometerxx = mSensorManagerxx
                    //.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            mSensorManagerxx.registerListener(this, mAccelerometerxx,
                    //SensorManager.SENS);
                    SensorManager.SENSOR_DELAY_NORMAL);
            //SensorManager.SENSOR_DELAY_GAME);
            //SensorManager.SENSOR_DELAY_FASTEST);

            //   setupCountDown();




        }

        /**
         * Starts the countdown
         */
        private void setupCountDown() {

            cucu = new MyCountDownTimerX(startTimez,intervalz);

            cucu.start();
            System.err.println("******************** START COUNTDOWN >");

        }


        /**
         * Stops the sensor data
         */
        public  void StopMeasures(){
            mSensorManagerxx.unregisterListener(this);

            System.err.println("\t \t(OK) TOTAL CAPTURED:" + measuresList.size());


            try{
                System.err.println("******************** PLAYS THE BEEP >");
                playBeep(uribeep);

                System.err.println("******************** START THE FINAL VIDEO  >");

                //StartVideoAfterbeep();

                SaveDialog();




            }catch (IOException ex){
                System.err.println("********************  exception playing the beep or starting final video:"+ex.getLocalizedMessage());
            }
        }


        /**
         * A dialog asking for the performance of the test,
         * if OK--> sends data to server
         * if NO--> opens another dialog asking Why?
         */
        public void SaveDialog(){

            boolean optionDialog = false;

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("Save");
            //alertDialogBuilder2.setTitle("wait");

            alertDialogBuilder.setMessage("Save the measurements?")
                    .setCancelable(false)
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            System.out.println("\t(OK) TAK (save)");
                            long timestamp = System.currentTimeMillis();

/*
                            Intent intenti = new Intent();
                            Bundle bundle = intenti.getExtras();
                            StoringService stor = new StoringService(measuresList);
                            stor.onHandleIntent(intenti);
*/
                            StoringTask storingT = new StoringTask(measuresList);
                            storingT.execute();

/*
                            StoringFallTask storFall = new StoringFallTask(fallmeasuresList);
                            storFall.execute();
*/
                            //_initTask.execute(this);



/*
                            System.err.println("******************** COLLECTS AND SENDS AL THE DATA TO SERVERm, with user:"+user + "  userXXX:"+pref.getString("user", null));
                            for (Iterator<Measure> it = measuresList.iterator(); it.hasNext(); ) {
                                Measure meas = it.next();
                                List<NameValuePair> pairs = new ArrayList<NameValuePair>();

                                pairs.add(new BasicNameValuePair("key1", String.valueOf(meas.getX())));
                                pairs.add(new BasicNameValuePair("key2", String.valueOf(meas.getY())));
                                pairs.add(new BasicNameValuePair("key3", String.valueOf(meas.getZ())));
                                //pairs.add(new BasicNameValuePair("key4", user));
                                pairs.add(new BasicNameValuePair("key4", f.getString("user", null)));


                                pairs.add(new BasicNameValuePair("key5", String.valueOf(meas.getTimestamp())));9o
                                //pairs.add(new BasicNameValuePair("key5",String.valueOf(timestamp)));

                                pairs.add(new BasicNameValuePair("key6", "m3"));

                                System.out.println("\t (OK) PAIRS" + pairs.size());



                                //SendingInBackground(pairs);                                                                           ********** ****** ***************   *************

                            }
*/


                            //Intent intent = new Intent(getApplicationContext(), FullscreenStep4.class);
                            //   Intent intent = new Intent(getApplicationContext(), Step3Activity.class);

                            //   startActivity(intent);
                        }
                    })
                    .setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("\t(OK) NIE (back)---->user:" + user);
                            //  countText.setTextColor(Color.BLACK);
                            //countText.setText("5:00");


                            // QuestionDialog();
                            //videoAfterMeas.setAlpha(0.0f);
                            //         butstart.setAlpha(1.0f);
                            //        butstart.setEnabled(true);


                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();



        }


        /***
         Easy: show dialog onPreExecute, register() in doInBackground and hide dialog in onPostExecute. Finally, do new RegisterTask().execute() in your onclick
         */
        private class StoringTask extends AsyncTask<Void, Void, Boolean> {
            //private final ProgressDialog dialog = new ProgressDialog(YourClass.this);
            List<Measure> meassuList;

            List<User> tempuser = new ArrayList<User>();

            private StoringTask(List<Measure> measList) {
                //this.measList = measList;
                meassuList = new ArrayList<Measure>();
                meassuList = measList;
                // usernameSto = pref.getString("user", null);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {

                int numlis =0;

                Measure newMeasure = new Measure();

                measuresDataSource = new MeasuresDataSource(context,databaseName);
                measuresDataSource.open();

                for (Iterator<Measure> it = meassuList.iterator(); it.hasNext(); ) {
                    numlis = numlis +1;
                    Measure meas = it.next();

                    //newMeasure =   measuresDataSource.createMeasure(String.valueOf(meas.getX()), String.valueOf(meas.getY()), String.valueOf(meas.getZ()),  String.valueOf(meas.getTimestamp()));
                    //    System.out.println(meas.getX()+"::"+ meas.getY()+"::"+ meas.getZ()+"::"+  meas.getTimestamp()+"::"+ idusername + ":::"+ usernameSto);
                    //newMeasure =   measuresDataSource.createMeasure(meas.getX(), meas.getY(), meas.getZ(),  meas.getTimestamp(), idusername);f
                    newMeasure =   measuresDataSource.createMeasure(meas.getX(), meas.getY(), meas.getZ(),  meas.getTimestamp(), meas.getId_user());
                }

                //newMeasure =   measuresDataSource.createMeasure((float)x, (float)y, (float)z,  (float)timestamp);
                measuresDataSource.close();

                System.err.println("(OK) END OF STORING: number of total measures stored:"+numlis + ":: for user:"+idusername+":"+username);

                return true;
            }

            protected void onPreExecute() {
                mDialog = new ProgressDialog(context);
                mDialog.setMessage("Storing...");
                mDialog.show();

                //this.dialog.setMessage("Signing in...");
                //this.dialog.show();
            }

            /*
            protected Boolean doInBackground(final Void unused) {
                //return Main.this.register(); //don't interact with the ui!
            }
            */

            protected void onPostExecute(final Boolean result) {
                mDialog.dismiss();

                Intent intent = new Intent(context, SummaryActivity.class);
                startActivity(intent);


                /*if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
                if (result.booleanValue()) {
                    //also show register success dialog
                }
                */
            }
        }




        /***
         Easy: show dialog onPreExecute, register() in doInBackground and hide dialog in onPostExecute. Finally, do new RegisterTask().execute() in your onclick

         private class StoringFallTask extends AsyncTask<Void, Void, Boolean> {
         //private final ProgressDialog dialog = new ProgressDialog(YourClass.this);
         List<FallMeasure> fallmeassuList;

         List<User> tempuser = new ArrayList<User>();


         private StoringFallTask(List<FallMeasure> fallmeasList) {
         //this.measList = measList;
         fallmeassuList = new ArrayList<FallMeasure>();
         fallmeassuList = fallmeasList;
         // usernameSto = pref.getString("user", null);
         }

         @Override
         protected Boolean doInBackground(Void... voids) {

         int numlis =0;

         FallMeasure newfallMeasure = new FallMeasure();

         fallDataSource = new FallDataSource(context, databaseName);

         fallDataSource.open();

         for (Iterator<FallMeasure> it = fallmeassuList.iterator(); it.hasNext(); ) {
         numlis = numlis +1;
         FallMeasure fallmeas = it.next();

         newfallMeasure =   fallDataSource.createFallMeasure(fallmeas.getX(), fallmeas.getY(), fallmeas.getZ(), fallmeas.getTimestamp(), fallmeas.getGrade(), fallmeas.getId_user());
         }

         fallDataSource.close();

         System.err.println("(OK) END OF STORING: number of total fall measures stored:"+numlis + ":: for user:"+idusername+":"+username);

         return true;
         }

         protected void onPreExecute() {
         }

         protected void onPostExecute(final Boolean result) {
         }
         }

         */







        public class StoringService extends IntentService {
            List<Measure> measList;
            String usernameSto = "";
            int idusername = 0;

            List<User> tempuser = new ArrayList<User>();

            public StoringService(List<Measure> measuresList) {
                super("Storing");
                measList = new ArrayList<Measure>();
                measList = measuresList;
                usernameSto = pref.getString("userN", null);
            }
            public void setList(List<Measure> measuresList){
                measList = new ArrayList<Measure>();
                measList = measuresList;
            }

            public StoringService() {
                super("Storing");
            }

            @Override
            protected void onHandleIntent(Intent workIntent) {


                tempuser = getUsersList();
                for (Iterator<User> it = tempuser.iterator(); it.hasNext();) {
                    User user = it.next();
                    if(user.getUsername().contains(usernameSto)){
                        idusername = user.getIduser();
                    }
                }

                Measure newMeasure = new Measure();

                measuresDataSource = new MeasuresDataSource(context,databaseName);
                measuresDataSource.open();

                for (Iterator<Measure> it = measList.iterator(); it.hasNext(); ) {
                    Measure meas = it.next();

                    //newMeasure =   measuresDataSource.createMeasure(String.valueOf(meas.getX()), String.valueOf(meas.getY()), String.valueOf(meas.getZ()),  String.valueOf(meas.getTimestamp()));
                    System.out.println(meas.getX()+"::"+ meas.getY()+"::"+ meas.getZ()+"::"+  meas.getTimestamp()+"::--idusername_"+ idusername + ":::"+ usernameSto);
                    newMeasure =   measuresDataSource.createMeasure(meas.getX(), meas.getY(), meas.getZ(),  meas.getTimestamp(), idusername);
                }

                //newMeasure =   measuresDataSource.createMeasure((float)x, (float)y, (float)z,  (float)timestamp);
                measuresDataSource.close();

                // Gets data from the incoming Intent
                //String dataString = workIntent.getDataString();
                //               mDialog.dismiss();
            }
        }


        /**
         * Sensor change options
         * @param sensorEventxxx
         */
        @Override
        public void onSensorChanged(SensorEvent sensorEventxxx) {

            double xx,yy,zz =0;



            gravSensorVals = lowPass(sensorEventxxx.values.clone(), gravSensorVals);
            xx  = sensorEventxxx.values[0];
            yy = sensorEventxxx.values[1];
            zz = sensorEventxxx.values[2];


            Measure measureSens = new Measure();

            List<User> tempuser = new ArrayList<User>();
            int iduserx = 0;

            tempuser = getUsersList();


            /*
            for (Iterator<User> it = tempuser.iterator(); it.hasNext();) {
                User user = it.next();
                if(user.getUsername().contains(pref.getString("userN", null))){
                    iduserx = user.getIduser();
                }
            }
            */


            iduserx = Integer.parseInt(pref.getString("userN", null));

            double x = sensorEventxxx.values[0];
            double y = sensorEventxxx.values[1];
            double z = sensorEventxxx.values[2];

            double timestamp2 = sensorEventxxx.timestamp;


            double dT = ((timestamp2  /1000000)- startTimestamp)/1000;

          //  System.out.println("X:" + x + "  Y:" + y + "  Z:" + z + "--z->:"+zz + " ----grav-->:"+gravSensorVals[2]);



            measureSens.setX(x);
            measureSens.setY(y);
            measureSens.setZ(z);
            measureSens.setTimestamp(dT);
            measureSens.setId_user(iduserx);
            measuresList.add(measureSens);


            System.out.println("measuresList size:" + measuresList.size());

            zList.add(z);

            // Get a SummaryStatistics instance
            SummaryStatistics stats = new SummaryStatistics();
            for (double tempz : zList) {
                stats.addValue(tempz);
            }


// Compute the statistics
            double mean = stats.getMean();
            double std = stats.getStandardDeviation();
            double maxxx = Collections.max(zList);

            double differ = maxxx - std;
            double differ2 = maxxx*0.1;




            /**
             * algorithm for obtaining peaks==exercise repetitions
             *
             * if (price(t) deviates more than mean(t-1) + k * std(t-1) )
             {
             construct signal
             construct new mean   : mean(t) = mean(t-1)
             construct new std    : std(t)  = std(t-1)   // std = standard deviation
             } else
             {
             construct new mean   : mean(t) = (mean(t-1) + price(t)) / 2
             construct new std    : std(t) = std(t-1) + sqrt((price(t) - mean(t-1))^2) /2
             }
             *
             */

            System.out.println("*************!  z:"+z+"   differ:" + differ + " ****** EXP_differ2:"+ differ2 + "   mean:"+mean);


            if(z > std){
                zPeakList.add(z);
                System.out.println("****************************PEAK!  z:" + z + "   differ:" + differ + " ****** EXP_differ2:" + differ2);
                repetitionCount = repetitionCount +1;

                repetitionText.setText(String.valueOf(repetitionCount));
                setRepetitionNumb(repetitionCount);






            }






        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
        /**
         * CountDown class
         */
        public class MyCountDownTimerX extends CountDownTimer {

            private boolean finished= true;
            public MyCountDownTimerX(long startTime, long interval) {
                super(startTime, interval);
            }

            @Override
            public void onFinish() {
                System.err.println("******************** COUNTDOWN FINISHES >");

                try{
                    System.err.println("******************** STOPPING MEASUREMENTS >");
                    StopMeasures();
                    mDialog.dismiss();



                }catch (Exception e){
                    System.err.println("\t (OK)(ERROR TRYING TO STOP MEASUREMENTS):"+e.getLocalizedMessage());
                }

            }

            @Override
            public void onTick(long millisUntilFinished) {
                System.err.println("\t (OK)" + millisUntilFinished / 1000) ;
            }

            public boolean isFinished() {
                return finished;
            }

            public void setFinished(boolean finished) {
                this.finished = finished;
            }
        }
    }

    // * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%








    public String ReadProps(String key){
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();
        String prop = "";
// Read from the /assets directory
        try {
            InputStream inputStream = assetManager.open("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            System.out.println("The properties are now loaded");
            prop = properties.getProperty(key);
            return prop;
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
            e.printStackTrace();
        }
        return prop;
    }

    public String getIpconf() {
        return ipconf;
    }

    public void setIpconf(String ipconf) {
        this.ipconf = ipconf;
    }


    public List<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }
}



