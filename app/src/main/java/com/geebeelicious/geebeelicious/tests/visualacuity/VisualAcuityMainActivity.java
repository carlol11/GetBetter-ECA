package com.geebeelicious.geebeelicious.tests.visualacuity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.geebeelicious.geebeelicious.MonitoringConsultationChoice;
import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.tests.colorvision.ColorVisionMainActivity;

import java.util.Timer;

import models.visualacuity.ChartHelper;
import models.visualacuity.DistanceCalculator;
import models.visualacuity.VisualAcuityResult;

public class VisualAcuityMainActivity extends ActionBarActivity {

    private Bundle record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_acuity_main);

        final ChartHelper chartHelper = new ChartHelper((ImageView) findViewById(R.id.chartLine));
        Button yesButton = (Button) findViewById(R.id.YesButton);
        Button noButton = (Button) findViewById(R.id.NoButton);

        yesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chartHelper.goToNextLine();
                if (chartHelper.isDone() && !chartHelper.isBothTested()) {
                    updateResults(chartHelper);
                }
            }
        });

        noButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chartHelper.setResult();
                if (chartHelper.isDone() && !chartHelper.isBothTested()) {
                    updateResults(chartHelper);
                }
            }
        });

        record = this.getIntent().getExtras();
        chartHelper.startTest();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DistanceCalculator distanceCalculator = new DistanceCalculator();
        float distance = distanceCalculator.getUserDistance(this, (ImageView) findViewById(R.id.chartLine));
        TextView tv = (TextView) findViewById(R.id.distanceTextView);
        tv.setText("Distance: " + String.format("%.2f", distance) + " meters");
    }

    private void endTest(){
        Button yesButton = (Button) findViewById(R.id.YesButton);
        Button noButton = (Button) findViewById(R.id.NoButton);
        yesButton.setVisibility(View.GONE);
        noButton.setVisibility(View.GONE);
        yesButton.setEnabled(false);
        noButton.setEnabled(false);

        ImageView chartView = (ImageView)findViewById(R.id.chartLine);
        chartView.setImageResource(R.drawable.wait_for_next_test);

        CountDownTimer timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(VisualAcuityMainActivity.this, ColorVisionMainActivity.class);
                intent.putExtras(record);
                finish();
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void updateResults(ChartHelper chartHelper){
        VisualAcuityResult rightEyeResult = null;
        VisualAcuityResult leftEyeResult = null;

        if(!chartHelper.isRightTested() && rightEyeResult == null){
            rightEyeResult = new VisualAcuityResult("Right", chartHelper.getResult());
            chartHelper.setIsRightTested();
            chartHelper.startTest();
            displayResults(rightEyeResult, R.id.rightEyeResultsTextView);
            record.putString("visualAcuityRight", rightEyeResult.getVisualAcuity());
        }
        else if(!chartHelper.isLeftTested() && leftEyeResult == null){
            leftEyeResult = new VisualAcuityResult("Left", chartHelper.getResult());
            chartHelper.setIsLeftTested();
            displayResults(leftEyeResult, R.id.leftEyeResultsTextView);
            record.putString("visualAcuityLeft", leftEyeResult.getVisualAcuity());
            endTest();
        }
    }

    private void displayResults(VisualAcuityResult result, int id){
        String resultString = "";
        resultString += (result.getEye().toUpperCase() + "\nLine Number: " +
                        result.getLineNumber() + "\nVisual Acuity: " +
                        result.getVisualAcuity());
        TextView textView = (TextView) findViewById(id);
        textView.setText(resultString);
    }

}
