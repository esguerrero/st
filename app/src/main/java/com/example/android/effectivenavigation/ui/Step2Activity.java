package com.example.android.effectivenavigation.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.android.effectivenavigation.R;
import com.example.android.effectivenavigation.db.Measure;

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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class Step2Activity extends Activity {


    private String ipconf ="";

    private Sensor mAccelerometer;


    private static final String MEDIA_PATH = new String("/sdcard/data/ctdwn.mp3");
    private List<String> songs = new ArrayList<String>();
    private MediaPlayer mp = new MediaPlayer();
    private int currentPosition = 0;
    private int vcurrentPosition = 0;
    private int vduration = 0;
    private List<NameValuePair> pairsX,pairsY,pairsZ;
    private List<Measure> measuresList;


    String SrcPath = "rtsp://v5.cache1.c.youtube.com/CjYLENy73wIaLQnhycnrJQ8qmRMYESARFEIJbXYtZ29vZ2xlSARSBXdhdGNoYPj_hYjnq6uUTQw=/0/0/0/video.3gp";

    //String uribeep= "http://www8.cs.umu.se/~esteban/audio/beep.mp3";
    String uribeep= "android.resource://com.example.android.effectivenavigation/"+ R.drawable.beep;


    String urivid= "http://www8.cs.umu.se/~esteban/vid/bxx.mp4";
    //String uriendbeep= "http://www8.cs.umu.se/~esteban/vid/stoph.mp4";
    String uriendbeep= "android.resource://com.example.android.effectivenavigation/"+R.drawable.stoph;

    //String uricntdwn= "http://www8.cs.umu.se/~esteban/vid/bcountdown.mp4";
    String uricntdwn= "android.resource://com.example.android.effectivenavigation/"+R.drawable.bcountdown;

    String urividb= "/storage/extSdCard/data/bc.wmv";


    private Button butiBack, buttRepeat;
    private ImageButton butstart;

    private VideoView videointro;
    private VideoView videocountdown;
    private VideoView videoAfterMeas;

    //    private RunningGetIO runx;
    private Vibrator vibe;

    private final long startTime = 10 * 1000;
    private CountDownTimer countDownTimer;
    private final long interval = 1 * 1000;
    private    RunningSensor runSensor;
    final Context context = this;
    private String user ="";
    ProgressDialog mDialog;

    static SharedPreferences pref;// = .getSharedPreferences("MyPref", 0); // 0 - for private mode
    static SharedPreferences.Editor editor;// = pref.edit();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.step2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sending) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
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
        videointro = (VideoView)findViewById(R.id.videoViews2);
        videointro.setAlpha(0.00f);

        butstart = (ImageButton) findViewById(R.id.imageButton2);
        butstart.setAlpha(0.05f);

        addListenerOnBackButton();


        runSensor= new RunningSensor();


        runSensor.AfterVideoFinishes();



        //countDownTimer = new MyCountDownTimer(startTime, interval);

        measuresList = new ArrayList<Measure>();



        setIpconf(ReadProps("ip.wired"));

    }


    /**
     * Listener of BACK button
     * Finishes the activity
     */
    public void addListenerOnBackButton() {

        butiBack = (Button) findViewById(R.id.buttons2);

        butiBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                System.err.println("******************** CLICKED BACK BUTTON >");
                finish();

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

            public void onCompletion(MediaPlayer arg0) {
                System.err.println("******************** BEEP SOUND FINISHES>");
            }

        });
    }


/*
    class GetTask extends AsyncTask<Object, Void, String> {
        Context context;

        GetTask(Context context, String userid) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(getApplicationContext());
            mDialog.setMessage("Please wait...");
            mDialog.show();
        }

        @Override
        protected String doInBackground(Object... params) {
            // here you can get the details from db or web and fetch it..
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            mDialog.dismiss();
        }
    }
*/


    /**
     * Class for running the sensor
     */
    private class  RunningSensor  implements SensorEventListener{

        private final long startTimez = 10 * 1000;
        private CountDownTimer countDownTimerx;
        private final long intervalz = 1 * 1000;

        private SensorManager mSensorManagerxx;
        private Sensor mAccelerometerxx;
        CountDownTimer cdt;
        MyCountDownTimerX cucu;




        /**
         * Activates the sensor mamanger
         */
        private void StartMeasures(){
            System.err.println("******************** START MEASURES >");

            mSensorManagerxx = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            mAccelerometerxx = mSensorManagerxx
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManagerxx.registerListener(this, mAccelerometerxx,
                    SensorManager.SENSOR_DELAY_UI);

            setupCountDown();


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
                StartVideoAfterbeep();
            }catch (IOException ex){
                System.err.println("********************  exception playing the beep or starting final video:"+ex.getLocalizedMessage());
            }


            //STARTS THE SAVE DIALOG

//            StartCountDownVideo();
//AfterVideo2();

        }








        public void StartButtonImage(){


            butstart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vibe.vibrate(50); // 50 is time in ms
                    System.err.println("********************* START BUTTON IS PRESSED **************");

                    butstart.setEnabled(false);
                    butstart.setAlpha(0.4f);

                    StartCountDownVideo();// countdown video
                    // AfterVideo2();

                }
            });

        }


        /**
         * Plays the countdown video
         */
        public void StartCountDownVideo(){
            videocountdown = (VideoView)findViewById(R.id.videoViews2);
            videocountdown.setAlpha(0.0f);

            videocountdown.setVideoURI(Uri.parse(uricntdwn));
            videocountdown.setMediaController(new MediaController(context));
            videocountdown.start();
            System.err.println("********************* THE  COUNTDOWN VIDEO STARTS");

            videoCountDownListener();


        }


        public void videoCountDownListener(){
            videocountdown.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer arg0) {
                    System.err.println("********************* START BUTTON FINISHES **************");
                    mDialog = new ProgressDialog(context);
                    mDialog.setMessage("Vänta...");
                    mDialog.show();
                    StartMeasures();                          //NO
                }

            });

        }



        public void StartVideoAfterbeep(){
  /*
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.id.videoView);
            dialog.show();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            lp.copyFrom(dialog.getWindow().getAttributes());
            dialog.getWindow().setAttributes(lp);
            //final VideoView videoview = (VideoView) findViewById(R.id.videoView);
            final VideoView videoview = (VideoView) dialog.findViewById(R.id.videoView);
            Uri uri = Uri.parse(uriendbeep);
            videoview.setVideoURI(uri);
            videoview.start();
    */


            videoAfterMeas = (VideoView)findViewById(R.id.videoViews2);
            videoAfterMeas.setAlpha(1.0f);
            videoAfterMeas.setVideoURI(Uri.parse(uriendbeep));
            videoAfterMeas.setMediaController(new MediaController(context));
            videoAfterMeas.start();

            System.err.println("********************* START VIDEO AFTER BEEP **************");
            AfterVideoMeasur();
        }


        /**
         * Shows a Dialog with Questions after the Video FINAL finishes
         */
        public void AfterVideoMeasur(){
            videoAfterMeas.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer arg0) {
                    videoAfterMeas.setAlpha(0.0f);

                    System.err.println("********************* SHOW A FIRST DIALOG WITH QUESTIONS AFTER VIDEO INTRO**************");
                    SaveDialog();//TODO ESTE SE DEBERIA PONER EN UN LISTENER CUANDO SE ACABA EL VIDEO AFTER BEEP


                }

            });

        }


        /**
         * Shows a green button after the Video Intro finishes
         */
        public void AfterVideoFinishes(){
 /*
            videointro.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer arg0) {
                    System.err.println("********************* PAINTS THE START BUTTON AFTER VIDEO INTRO**************");
*/
            butstart.setAlpha(1.0f);
            StartButtonImage();
/*
                }

            });
*/
        }


        /**
         * A dialog asking for the performance of the test,
         * if OK--> sends data to server
         * if NO--> opens another dialog asking Why?
         */
        public void SaveDialog(){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("Fråga");

            alertDialogBuilder.setMessage("Klarade du testet utan att förlora balansen eller ta stöd med händer eller andra kroppsdelar?")
                    .setCancelable(false)
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("\t(OK) TAK (save)");
                            long timestamp = System.currentTimeMillis();

                            System.err.println("******************** COLLECTS AND SENDS AL THE DATA TO SERVER >");
                            for (Iterator<Measure> it = measuresList.iterator(); it.hasNext(); ) {
                                Measure meas = it.next();
                                List<NameValuePair> pairs = new ArrayList<NameValuePair>();

                                pairs.add(new BasicNameValuePair("key1", String.valueOf(meas.getX())));
                                pairs.add(new BasicNameValuePair("key2", String.valueOf(meas.getY())));
                                pairs.add(new BasicNameValuePair("key3", String.valueOf(meas.getZ())));
                                pairs.add(new BasicNameValuePair("key4", user));

                                pairs.add(new BasicNameValuePair("key5", String.valueOf(meas.getTimestamp())));
                                //pairs.add(new BasicNameValuePair("key5",String.valueOf(timestamp)));

                                pairs.add(new BasicNameValuePair("key6", "m2"));

                                System.out.println("\t (OK) PAIRS" + pairs.size());

                                SendingInBackground(pairs);

                            }
                            //TODO AQUI DEBE ENVIARSE AL FULLSCREEN 2
                            Intent intent = new Intent(getApplicationContext(), FullscreenStep3.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            System.out.println("\t(OK) NIE (back)");
                            QuestionDialog();

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }


        public void QuestionDialog(){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

            alertDialogBuilder.setTitle("Fråga");

            alertDialogBuilder.setMessage("Vill du prova igen eller gå vidare till styrketestet?")
                    .setCancelable(false)
                    .setPositiveButton("Gå vidare", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(context, Step4Activity.class);
                            startActivity(intent);




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

            double x = sensorEventxxx.values[0];
            double y = sensorEventxxx.values[1];
            double z = sensorEventxxx.values[2];
            long timestamp = System.currentTimeMillis();

            measureSens.setX((float)x);
            measureSens.setY((float) y);
            measureSens.setZ((float) z);
            //measureSens.setTimestamp((float)sensorEventxxx.timestamp);
            measureSens.setTimestamp((float)timestamp);
            measuresList.add(measureSens);

            String xxx = Double.toString(x);
            String yyy = Double.toString((double) (y-9.82008));
            String zzz = Double.toString((double) (z-9.82008));


            System.err.println("\t \t(OK) xxx:"+xxx + "|yyy"+yyy+ " |zzz"+zzz +" |timstamp:"+sensorEventxxx.timestamp);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        /**
         * CountDown class
         */
        public class MyCountDownTimerX extends CountDownTimer{

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


    public String SendingInBackground(List<NameValuePair> pairsss) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        // HttpGet httpGet = new
        // HttpGet("http://www.cheesejedi.com/rest_services/get_big_cheese.php?puzzle=1");
        // HttpPost httpPost = new
        // HttpPost("http://130.239.41.93:8080/AAL2v1.0/resources/greeting/walk");
//			HttpPut httpPost = new HttpPut(
//					"http://130.239.41.93:8080/AAL2v1.4/resources/greeting/ping");



        //line
//            HttpPut httpPost = new HttpPut(
//                    "http://130.239.41.93:8080/Balance3.2/resources/balance/ping");

        //wireless
        //HttpPut httpPost = new HttpPut("http://"+getIpconf()+":8080/Balance3.2/resources/balance/ping");
        HttpPut httpPost = new HttpPut("http://"+getIpconf()+":8080/Balance4.0/resources/balance/ping");






        //http://localhost:8080/Balance3.2/
//			HttpPut httpPost = new HttpPut(
//					"http://130.239.41.93:8080/AAL2v2.1/resources/balance/ping");


			/* funciona */
        // httpPost.setHeader("Accept", "text/plain");
        // httpPost.setHeader("Content-type",
        // "application/x-www-form-urlencoded");
			/* funciona */
        // HttpPut.setHeader("Content-type", "application/json");
        // httpPut.setHeader("Accept-Charset", "utf-8");
        try {
            // Add your data

            //pairs.add(new BasicNameValuePair(pairsX.get(pairsX.size()-1).getName(),pairsX.get(0).getValue()));


            long timestamp = System.currentTimeMillis();


            //TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
            java.util.Date timest  = new java.util.Date((long) timestamp * 1000);

            String timstart = String.valueOf(timest);

            long nanoTime = System.nanoTime();
            System.err.println("\t (OK)nanotime:"+nanoTime);
            System.err.println("\t (OK)timestamp:"+timstart);
            System.err.println("\t (OK)Systimestmp:"+timestamp);
/*
                pairs.add(new BasicNameValuePair("key1",pairsX.get(pairsX.size()-1).getValue()));
                pairs.add(new BasicNameValuePair("key2",pairsY.get(pairsY.size()-1).getValue()));
                pairs.add(new BasicNameValuePair("key3",pairsZ.get(pairsZ.size()-1).getValue()));
                pairs.add(new BasicNameValuePair("key4","userID"));//TODO SEND THE USER ID
                //pairs.add(new BasicNameValuePair("key5",String.valueOf(timestamp)));
                pairs.add(new BasicNameValuePair("key5",String.valueOf(timestamp)));
                pairs.add(new BasicNameValuePair("key6","m1"));//TODO SEND THE KEY DEPNDG OF TYPE OF MEASUREMENT
*/



            httpPost.setEntity(new UrlEncodedFormEntity(pairsss));
            String text = "";

            try {
                // HttpResponse response = httpClient.execute(httpGet,
                // localContext);
                HttpResponse response = httpClient.execute(httpPost,
                        localContext);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
                System.err.println("\t (OK) >Response:" + text);
            } catch (Exception e) {
                System.err.println("\t (OK)(EROR):" + e.getLocalizedMessage());
                return e.getLocalizedMessage();
            }




				/* funciona */
            // httpPost.setEntity(new StringEntity(obj.toString(),
            // "UTF-8"));
				/* funciona */
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        // HttpGet httpGet = new
        // HttpGet("http://130.239.41.93:8080/AAL2v1.0/resources/greeting/create2");

        return "";
    }



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
}



