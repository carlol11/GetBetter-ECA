package com.geebeelicious.geebeelicious.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import com.geebeelicious.geebeelicious.models.hearing.Calibrator;

/**
 * Created by Kate.
 * The HearingCalibrationActivity serves as the activity
 * for the hearing test calibration module. It uses the
 * Calibrator class to perform the calibration.
 */

public class HearingCalibrationActivity extends ActionBarActivity {

    private Calibrator calibrator;
    private Thread calibrationThread;

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
                    TextView textView = (TextView) findViewById(R.id.calibrationInProgressTextView);
                    progressBar.setVisibility(View.INVISIBLE);
                    textView.setText("Calibration Done!");
                }
            });
        }

        Intent intent = new Intent(HearingCalibrationActivity.this, SettingsActivity.class);
        finish();
        startActivity(intent);
    }

}
