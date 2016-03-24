package com.geebeelicious.geebeelicious.tests.hearing;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.MonitoringConsultationChoice;
import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.tests.grossmotor.GrossMotorMainActivity;

import java.util.ArrayList;

import models.hearing.HearingTest;

public class HearingMainActivity extends ActionBarActivity {

    ArrayList<Thread> threads;
    HearingTest hearingTest;
    Bundle record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearing_main);

        AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 9, 0);

        hearingTest = new HearingTest();
        final double[] calibrationData = hearingTest.getCalibrationData(getApplicationContext());
        record = this.getIntent().getExtras();

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
                yesButton.setVisibility(View.GONE);
                yesButton.setEnabled(false);
                ImageView imageView = (ImageView)findViewById(R.id.hearingTestImageView);
                imageView.setImageResource(R.drawable.wait_for_next_test);
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
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {

                    }
                    Intent intent = new Intent(HearingMainActivity.this, GrossMotorMainActivity.class);
                    intent.putExtras(record);
                    finish();
                    startActivity(intent);
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

    private void endTest(){
        record.putString("hearingRight", hearingTest.getPureToneAverageInterpretation("Right"));
        record.putString("hearingLeft", hearingTest.getPureToneAverageInterpretation("Left"));

        stopTest();
    }

    private void stopTest(){
        hearingTest.setIsNotRunning();
        for(int i = 0; i<threads.size(); i++){
            threads.get(i).interrupt();
        }
    }

    @Override
    public void onBackPressed() {
        stopTest();
        Intent intent = new Intent(HearingMainActivity.this, MonitoringConsultationChoice.class);
        finish();
        startActivity(intent);
    }




}
