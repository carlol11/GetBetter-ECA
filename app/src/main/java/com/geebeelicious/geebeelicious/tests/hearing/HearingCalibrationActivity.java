package com.geebeelicious.geebeelicious.tests.hearing;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import models.hearing.Calibrator;

public class HearingCalibrationActivity extends ActionBarActivity {

    private Calibrator calibrator;
    Thread calibrationThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearing_calibration);

        calibrator = new Calibrator(getApplicationContext());
        calibrationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                calibrator.calibrate();
                endCalibration();
                Intent intent = new Intent(HearingCalibrationActivity.this, HearingMainActivity.class);
                HearingCalibrationActivity.this.startActivity(intent);
            }
        });
        calibrationThread.start();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        calibrator.stopThread();
        calibrationThread.interrupt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        calibrator.stopThread();
        calibrationThread.interrupt();
    }

    private void endCalibration(){
        calibrator.stopThread();
        calibrationThread.interrupt();
        if(calibrator.isDone()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.calibrationProgressBar);
                    progressBar.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) findViewById(R.id.calibrationInProgressTextView);
                    textView.setText("Calibration Done!");
                }
            });
        }
    }





}
