package com.geebeelicious.geebeelicious.tests.hearing;

import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import java.util.ArrayList;

import models.hearing.HearingTest;

public class HearingMainActivity extends ActionBarActivity {

    ArrayList<Thread> threads;
    HearingTest hearingTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearing_main);

        AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 9, 0);

        hearingTest = new HearingTest();
        final double[] calibrationData = hearingTest.getCalibrationData(getApplicationContext());
        final Button yesButton = (Button)findViewById(R.id.YesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hearingTest.setHeard();
            }
        });

        final Runnable backgroundFlash = new Runnable(){
            public void run(){
                yesButton.setBackgroundColor(Color.BLUE);
            }
        };

        final Runnable backgroundNormal = new Runnable(){
            public void run(){
                yesButton.setBackgroundColor(Color.parseColor("#98ff88"));
            }
        };

        final Runnable disableTest = new Runnable() {
            @Override
            public void run() {
                yesButton.setEnabled(false);
                TextView resultsView = (TextView) findViewById(R.id.hearingResultsTV);
                resultsView.setText(hearingTest.getResults());
            }
        };

        Thread screenThread = new Thread(new Runnable(){
            public void run(){
                while(hearingTest.isInLoop()){
                    if(!hearingTest.isRunning()){
                        return;
                    }
                    if(hearingTest.isHeard()){
                        runOnUiThread(backgroundFlash);
                        while(hearingTest.isHeard()){

                        }
                    }
                }
            }
        });

        Thread timingThread = new Thread(new Runnable(){
            public void run(){
                while(hearingTest.isInLoop()){
                    if(!hearingTest.isRunning()){
                        return;
                    }
                    if(hearingTest.isHeard()){
                        try{
                            Thread.sleep(500);
                        } catch(InterruptedException ie){

                        }
                        runOnUiThread(backgroundNormal);
                    }
                }
            }
        });

        Thread testThread = new Thread(new Runnable() {
            @Override
            public void run() {
                hearingTest.performTest(calibrationData);
                if(hearingTest.isDone()){
                    runOnUiThread(backgroundFlash);
                    runOnUiThread(disableTest);
                    endTest();
                }
            }
        });

        threads = new ArrayList<Thread>();
        threads.add(screenThread);
        threads.add(timingThread);
        threads.add(testThread);
        screenThread.start();
        timingThread.start();
        testThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        endTest();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        endTest();
    }

    private void endTest(){
        hearingTest.setIsNotRunning();
        for(int i = 0; i<threads.size(); i++){
            threads.get(i).interrupt();
        }
    }




}
