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
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Timer;


public class SwayDetect extends Activity {





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
    private List<User> usersList;

    private double  startTimestamp = 0.0;

    private MeasuresDataSource measuresDataSource;


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

    private ToggleButton togglebut;
    Spinner spinner1, spinner2;
    private ArrayAdapter<String> adapter_state;
    private String username = "";
    private int idusername = 0;

    private SQLiteDatabase myDatabase=null;

    static SharedPreferences pref;// = .getSharedPreferences("MyPref", 0); // 0 - for private mode
    static SharedPreferences.Editor editor;// = pref.edit();

    private String databaseName = "sway";


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sway_detect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // action with ID action_entries was selected
/*            case R.id.action_entries:

                measuresDataSource = new MeasuresDataSource(context);
                measuresDataSource.open();



                List<Measure> measuresListSize =  measuresDataSource.getAllMeasures();
                for (Iterator<Measure> it = measuresListSize.iterator(); it.hasNext();) {
                    Measure measu = it.next();
                    System.err.println("(OK)====>"+measu.getX()+"=="+measu.getY()+"=="+measu.getZ()+"=="+measu.getTimestamp()+"=="+measu.getId_user());

                }


                System.err.println("(DATABASE SIZE):"+measuresListSize.size());
                measuresDataSource.close();

                Toast.makeText(this, "Entries in DataBase", Toast.LENGTH_SHORT)
                        .show();
                break;
            // action with ID action_sending was selected
*/
            case R.id.action_saving:

                ExportDatabaseCSVTask exportdb = new ExportDatabaseCSVTask();
                exportdb.execute();

/*
                ExportFallTableCSVTask exportFall = new ExportFallTableCSVTask();
                exportFall.execute();
*/

                Toast.makeText(this, "Saving DB into a TXT file", Toast.LENGTH_SHORT)
                        .show();
                break;
/*
            case R.id.action_sending:


                System.err.println("******************** COLLECTS AND SENDS AL THE DATA TO SERVERm, with user:"+user + "  userXXX:"+pref.getString("user", null));

                measuresDataSource = new MeasuresDataSource(context);
                measuresDataSource.open();

                List<Measure> measuresListSizeSend =  measuresDataSource.getAllMeasures();

                System.err.println("(DATABASE SIZE TO SEND):"+measuresListSizeSend.size());
                measuresDataSource.close();



                for (Iterator<Measure> it = measuresListSizeSend.iterator(); it.hasNext(); ) {





                    Measure meas = it.next();
                    List<NameValuePair> pairs = new ArrayList<NameValuePair>();

                    pairs.add(new BasicNameValuePair("key1", String.valueOf(meas.getX())));
                    pairs.add(new BasicNameValuePair("key2", String.valueOf(meas.getY())));
                    pairs.add(new BasicNameValuePair("key3", String.valueOf(meas.getZ())));
                    //pairs.add(new BasicNameValuePair("key4", user));
                    //pairs.add(new BasicNameValuePair("key4", pref.getString("user", null)));
                    pairs.add(new BasicNameValuePair("key4", String.valueOf(meas.getId_user())));


                    pairs.add(new BasicNameValuePair("key5", String.valueOf(meas.getTimestamp())));
                    //pairs.add(new BasicNameValuePair("key5",String.valueOf(timestamp)));

                    pairs.add(new BasicNameValuePair("key6", "m3"));

                    System.out.println("\t (OK) PAIRS" + pairs.size());



                    SendingInBackground(pairs);

                }


                Toast.makeText(this, "Sending Data", Toast.LENGTH_SHORT)
                        .show();
                break;
*/

            default:
                break;
        }

        return true;
        /*
        int id = item.getItemId();
        if (id == R.id.action_sending) {
            return true;
        }
        return super.onOptionsItemSelected(item);
        */
    }



    public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
        }

        protected Boolean doInBackground(final String... args) {
            //File dbFile = getDatabasePath("measures.db");
            File dbFile = getDatabasePath(databaseName);
            System.err.println(dbFile);  // displays the data base path in your logcat
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");

            if (!exportDir.exists()) { exportDir.mkdirs(); }

            File file = new File(exportDir, "swaymeasures.csv");
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
                Toast.makeText(SwayDetect.this, "Export successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SwayDetect.this, "Export failed", Toast.LENGTH_SHORT).show();
            }
        }
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sway_detect);
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();

        user = pref.getString("user", null);

        vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE) ;


/*

        videointro = (VideoView)findViewById(R.id.videoView);
        videointro.setVideoURI(Uri.parse(urivid));
        videointro.setMediaController(new MediaController(this));
        videointro.requestFocus();
        videointro.start();
        System.err.println("******************** VIDEO 1 STARTED >");

*/


//        butstart = (ImageButton) findViewById(R.id.imageButton3);
        //       butstart.setAlpha(0.00f);

//        addListenerOnBackButton();


        togglebut = (ToggleButton)findViewById(R.id.toggleButton);


        saveButton = (Button)findViewById(R.id.button);

        runSensor= new RunningSensor();

        runSensor.StartToggle();

//        runSensor.AfterVideoFinishes();

        countText = (TextView)findViewById(R.id.textView);
        countText.setText("5:00");

        //countDownTimer = new MyCountDownTimer(startTime, interval);

        measuresList = new ArrayList<Measure>();
        usersList = new ArrayList<User>();



        setIpconf(ReadProps("ip.wired"));



//        List<String> userx = GetUsersList();
//        System.err.println("\t (OK) numList:"+userx.size());

        spinner1 = (Spinner)findViewById(R.id.spinner);


        //findViewById(R.id.spinner)

        //spinner1.setAdapter(adapter_state);
        addItemsOnSpinner2();


        listenerList();

        listenerSaveButton();

        System.out.println("XX");


        //findViewById(R.id.dummy_button_step1).setOnTouchListener(mDelayHideTouchListener);
        addListenerOnButton();

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



    // add items into spinner dynamically
    public void addItemsOnSpinner2() {

        spinner2 = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("Individual 1A");
        list.add("Individual 1B");
        list.add("Individual 1C");
        list.add("Individual 2A");
        list.add("Individual 2B");
        list.add("Individual 2C");
        list.add("Individual 3A");
        list.add("Individual 3B");
        list.add("Individual 3C");
        list.add("Individual 4A");
        list.add("Individual 4B");
        list.add("Individual 4C");
        list.add("Individual 5A");
        list.add("Individual 5B");
        list.add("Individual 5C");
        list.add("Individual 6A");
        list.add("Individual 6B");
        list.add("Individual 6C");
        list.add("Individual 7A");
        list.add("Individual 7B");
        list.add("Individual 7C");
        list.add("Individual 8A");
        list.add("Individual 8B");
        list.add("Individual 8C");
        list.add("Individual 9A");
        list.add("Individual 9B");
        list.add("Individual 9C");
        list.add("Individual 10A");
        list.add("Individual 10B");
        list.add("Individual 10C");
        list.add("Individual 11A");
        list.add("Individual 11B");
        list.add("Individual 11C");
        list.add("Individual 12A");
        list.add("Individual 12B");
        list.add("Individual 12C");
        list.add("Individual 13A");
        list.add("Individual 13B");
        list.add("Individual 13C");
        list.add("Individual 14A");
        list.add("Individual 14B");
        list.add("Individual 14C");
        list.add("Individual 15A");
        list.add("Individual 15B");
        list.add("Individual 15C");
        list.add("Individual 16A");
        list.add("Individual 16B");
        list.add("Individual 16C");
        list.add("Individual 17A");
        list.add("Individual 17B");
        list.add("Individual 17C");
        list.add("Individual 18A");
        list.add("Individual 18B");
        list.add("Individual 18C");
        list.add("Individual 19A");
        list.add("Individual 19B");
        list.add("Individual 19C");
        list.add("Individual 20A");
        list.add("Individual 20B");
        list.add("Individual 20C");
        list.add("Individual 21A");
        list.add("Individual 21B");
        list.add("Individual 21C");
        list.add("Individual 22A");
        list.add("Individual 22B");
        list.add("Individual 22C");
        list.add("Individual 23A");
        list.add("Individual 23B");
        list.add("Individual 23C");
        list.add("Individual 24A");
        list.add("Individual 24B");
        list.add("Individual 24C");
        list.add("Individual 25A");
        list.add("Individual 25B");
        list.add("Individual 25C");
        list.add("Individual 26A");
        list.add("Individual 26B");
        list.add("Individual 26C");
        list.add("Individual 27A");
        list.add("Individual 27B");
        list.add("Individual 27C");
        list.add("Individual 28A");
        list.add("Individual 28B");
        list.add("Individual 28C");
        list.add("Individual 29A");
        list.add("Individual 29B");
        list.add("Individual 29C");
        list.add("Individual 30A");
        list.add("Individual 30B");
        list.add("Individual 30C");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }



    public void listenerList(){

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedItem = (String)spinner1.getSelectedItem();
                System.out.println("\t (OK) item selected:"+i + "  >"+selectedItem);


                editor.putString("user", selectedItem); // Storing string
                editor.putString("userN", String.valueOf(i)); // Storing string
                editor.putString("ac1","NO");
                editor.putString("ac2","NO");
                editor.putString("ac3","NO");
                editor.putString("ac4","NO");
                editor.putString("ac5","NO");
                editor.putString("ac","0");


                editor.commit(); // commit changes


                System.err.println("\t (OK) -user--> "+pref.getString("user", null));
                System.err.println("\t (OK) -userN--> "+pref.getString("userN", null));

                List<User> tempuser = new ArrayList<User>();

                tempuser = getUsersList();
                for (Iterator<User> it = tempuser.iterator(); it.hasNext();) {
                    User user = it.next();
                    if(user.getUsername().contains(username)){
                        idusername = user.getIduser();
                    }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }




    public void listenerToggleButton(){

        togglebut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean on = togglebut.isChecked();

                if (on) {
                    // Enable vibrate
                    System.out.println("ON");
                } else {
                    System.out.println("OFF");
                    // Disable vibrate
                }
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






    private class RunMeasurement implements SensorEventListener {
        private final long startTimez = 10 * 1000;
        private CountDownTimer countDownTimerx;
        private final long intervalz = 1 * 1000;
        private SensorManager mSensorManagerxx;
        private Sensor mAccelerometerxx;;
        CountDownTimer cdt;
        MyCountDownTimerX cucu;


        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

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
//                StopMeasures();
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





    /**%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
     * Class for running the sensor
     */
    private class  RunningSensor  implements SensorEventListener {

        private final long startTimez = 10 * 1000;
        private CountDownTimer countDownTimerx;
        private final long intervalz = 1 * 1000;

        private SensorManager mSensorManagerxx;
        private Sensor mAccelerometerxx;
        CountDownTimer cdt;
        MyCountDownTimerX cucu;

        private Timer tiempo =new Timer();


        private void StartToggle(){
            togglebut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (b){


                        System.err.println("********************* START BUTTON IS PRESSED **************");

                        System.out.println("ON");

                        togglebut.setClickable(false);

                        vibe.vibrate(50); // 50 is time in ms

                        new CountDownTimer(5000, 100) {

                            public void onTick(long millisUntilFinished) {
                                countText.setTextColor(Color.RED);
                                countText.setText("  " + millisUntilFinished / 1000+ " : "+millisUntilFinished/60 + ":00");

                            }

                            public void onFinish() {
                                StartMeasures();
                                countText.setText("0:00");
                                vibe.vibrate(30); // 50 is time in ms

                                try {
                                    playBeep(uribeepin);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                /*
                                mDialog = new ProgressDialog(context);
                                mDialog.setMessage("Vänta...");
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

                countText.setTextColor(Color.GREEN);
                countText.setText("Sensing: "+ String.format("%d:%02d", minutes, seconds));

                timerHandler.postDelayed(this, 500);
            }
        };



        /**
         * Activates the sensor mamanger
         */
        private void StartMeasures(){
            measuresList.clear();

            System.err.println("******************** START MEASURES measureList:>"+measuresList.size());

            startTimestamp = System.currentTimeMillis();

            mSensorManagerxx = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            mAccelerometerxx = mSensorManagerxx
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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

            System.err.println("\t \t(OK) TOTAL CAPTURED:"+measuresList.size() );


            try{
                System.err.println("******************** PLAYS THE BEEP >");
                playBeep(uribeep);

                System.err.println("******************** START THE FINAL VIDEO  >");

                //StartVideoAfterbeep();

                SaveDialog();//TODO ESTE SE DEBERIA PONER EN UN LISTENER CUANDO SE ACABA EL VIDEO AFTER BEEP

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

            alertDialogBuilder.setTitle("Fråga");
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
                                pairs.add(new BasicNameValuePair("key4", pref.getString("user", null)));


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
                            countText.setText("5:00");

                            // QuestionDialog();
                            //videoAfterMeas.setAlpha(0.0f);
                            //         butstart.setAlpha(1.0f);
                            //        butstart.setEnabled(true);


                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();



/*
            measuresDataSource = new MeasuresDataSource(context);
            measuresDataSource.open();



            List<Measure> measuresListSize =  measuresDataSource.getAllMeasures();
            for (Iterator<Measure> it = measuresListSize.iterator(); it.hasNext();) {
                Measure measu = it.next();
                System.err.println("(OK)====>"+measu.getX()+"=="+measu.getY()+"=="+measu.getZ()+"=="+measu.getTimestamp()+"=="+measu.getId_user());

            }


            System.err.println("(DATABASE SIZE):"+measuresListSize.size());
            measuresDataSource.close();

            */

        }


        /***
         Easy: show dialog onPreExecute, register() in doInBackground and hide dialog in onPostExecute. Finally, do new RegisterTask().execute() in your onclick
         */
        private class StoringTask extends AsyncTask<Void, Void, Boolean> {
            //private final ProgressDialog dialog = new ProgressDialog(YourClass.this);
            List<Measure> meassuList;
            //           String usernamex = "";
            //          int idusernamex = 0;

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
/*
                tempuser = getUsersList();
                for (Iterator<User> it = tempuser.iterator(); it.hasNext();) {
                    User user = it.next();
                    if(user.getUsername().contains(usernameSto)){
                        idusername = user.getIduser();
                    }
                }*/

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
                /*if (this.dialog.isShowing()) {
                    this.dialog.dismiss();
                }
                if (result.booleanValue()) {
                    //also show register success dialog
                }
                */
            }
        }






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
                    System.out.println(meas.getX()+"::"+ meas.getY()+"::"+ meas.getZ()+"::"+  meas.getTimestamp()+"::"+ idusername + ":::"+ usernameSto);
                    newMeasure =   measuresDataSource.createMeasure(meas.getX(), meas.getY(), meas.getZ(),  meas.getTimestamp(), idusername);
                }

                //newMeasure =   measuresDataSource.createMeasure((float)x, (float)y, (float)z,  (float)timestamp);
                measuresDataSource.close();

                // Gets data from the incoming Intent
                //String dataString = workIntent.getDataString();
                //               mDialog.dismiss();
            }
        }






        public void QuestionDialog(){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("Fråga");

            alertDialogBuilder.setMessage("Vill du prova igen eller gå vidare till styrketestet?")
                    .setCancelable(false)
                    .setPositiveButton("Gå vidare", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //   Intent intent = new Intent(context, Step4Activity.class);
                            //   startActivity(intent);




                        }
                    })
                    .setNegativeButton("Prova igen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("\t(OK) NIE (back)");

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


        @Override
        public void onSensorChanged(SensorEvent sensorEventxxx) {
            Measure measureSens = new Measure();
            FallMeasure fall = new FallMeasure();

            List<User> tempuser = new ArrayList<User>();
            int iduserx = 0;

            tempuser = getUsersList();
            for (Iterator<User> it = tempuser.iterator(); it.hasNext();) {
                User user = it.next();
                if(user.getUsername().contains(pref.getString("userN", null))){
                    iduserx = user.getIduser();
                }
            }

            iduserx = Integer.parseInt(pref.getString("userN", null));

            double x = sensorEventxxx.values[0];
            double y = sensorEventxxx.values[1] - 9.733861;
            double z = sensorEventxxx.values[2];

            long timestamp = System.currentTimeMillis();
            double timestamp2 = sensorEventxxx.timestamp;

            String xxx = Double.toString(x);


            double NS2mS = 1.0f / 1000000.0f;

            double dT = ((timestamp2  /1000000)- startTimestamp)/1000;

            measureSens.setX(x);
            measureSens.setY(y);
            measureSens.setZ(z);
            //measureSens.setTimestamp(sensorEventxxx.timestamp);
            measureSens.setTimestamp(dT);




            measureSens.setId_user(iduserx);


            //measureSens.setTimestamp((float)timestamp);
            measuresList.add(measureSens);



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



