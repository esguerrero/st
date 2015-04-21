package com.example.android.effectivenavigation.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import android.view.View.OnClickListener;

import com.example.android.effectivenavigation.R;

public class IntroActivity extends FragmentActivity {

    //String SrcPath = "rtsp://v5.cache1.c.youtube.com/CjYLENy73wIaLQnhycnrJQ8qmRMYESARFEIJbXYtZ29vZ2xlSARSBXdhdGNoYPj_hYjnq6uUTQw=/0/0/0/video.3gp";
//    String urivid= "/storage/extSdCard/data/axx.mp4";
    String urivid= "http://www8.cs.umu.se/~esteban/vid/axx.mp4";
    String urividintro= "http://www8.cs.umu.se/~esteban/vid/introsppb.mp4";


    private Surface surface;

    private Button butiBack, buttRepeat;

    private VideoView videointro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        videointro = (VideoView)findViewById(R.id.videoView);
        videointro.setVideoURI(Uri.parse(urividintro));
        videointro.setMediaController(new MediaController(this));
        videointro.requestFocus();
        videointro.start();

        addListenerOnButton();

        repListenerOnButton();
    }


    public void addListenerOnButton() {

        butiBack = (Button) findViewById(R.id.button);

        butiBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();

            }

        });

    }

    public void repListenerOnButton() {

        buttRepeat = (Button) findViewById(R.id.button2);

        buttRepeat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                videointro.stopPlayback();
                videointro.seekTo(0);
                videointro.start();

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.intro, menu);



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

}
