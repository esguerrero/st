package com.example.android.effectivenavigation.ui;

import android.os.CountDownTimer;

/**
 * Created by Esteban on 5/25/14.
 */
public class Tempor extends CountDownTimer {

    private boolean finished= true;
    public Tempor(long startTime, long interval) {
        super(startTime, interval);
    }

    @Override
    public void onFinish() {

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