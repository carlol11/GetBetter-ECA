package com.geebeelicious.geebeelicious.tests.hearing;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.geebeelicious.geebeelicious.R;

import java.util.ArrayList;

import models.hearing.Calibrator;
import models.hearing.CalibratorThread;

public class HearingCalibrationActivity extends ActionBarActivity {

    ArrayList<CalibratorThread> threads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearing_calibration);

        AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 9, 0);

        threads = new ArrayList<CalibratorThread>();
        Thread runningThread = new Thread(new Runnable() {

            public void run(){
                final CalibratorThread calibratorThread = new CalibratorThread(getBaseContext());
                System.out.println("New Calibrator Thread");
                threads.add(calibratorThread);
                calibratorThread.run();
            }

        });
        runningThread.start();
    }

    @Override
    public void onStop(){
        super.onStop();
        for(CalibratorThread ct : threads){
            ct.stopThread();
            ct.interrupt();
        }

    }


}
