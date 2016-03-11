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
        System.out.println("DONE");
        final Button yesButton = (Button)findViewById(R.id.YesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hearingTest.setHeard();
                if(hearingTest.isDone()){
                    endTest();
                }

            }
        });
            System.out.println("HERE");
         //hearingTest.performTest(calibrationData);
            System.out.println("HELLO");

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

        Thread screenThread = new Thread(new Runnable(){
            public void run(){
                while(hearingTest.isInLoop()){
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

    private void endTest(){
        for(Thread t : threads){
            t.interrupt();
        }
        Button yesButton = (Button) findViewById(R.id.YesButton);
        yesButton.setEnabled(false);
        displayResults();
    }

    private void displayResults(){
        TextView resultsView = (TextView) findViewById(R.id.hearingResultsTV);
        resultsView.setText(hearingTest.getResults());
    }




}
