package com.geebeelicious.geebeelicious.tests.visualacuity;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geebeelicious.geebeelicious.R;

import models.visualacuity.ChartHelper;
import models.visualacuity.DistanceCalculator;
import models.visualacuity.Result;

public class VisualAcuityMainActivity extends ActionBarActivity {

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
                if(chartHelper.isDone() && !chartHelper.isBothTested()){
                    updateResults(chartHelper);
                }
            }
        });

        noButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chartHelper.setResult();
                if(chartHelper.isDone() && !chartHelper.isBothTested()){
                    updateResults(chartHelper);
                }
            }
        });

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
        yesButton.setEnabled(false);
        noButton.setEnabled(false);
        //TODO: insert code to set chartView to something else (maybe GeeBee or results screen?)
    }

    private void updateResults(ChartHelper chartHelper){
        //TODO: pass the results to a record
        Result rightEyeResult = null;
        Result leftEyeResult = null;

        if(!chartHelper.isRightTested() && rightEyeResult == null){
            rightEyeResult = new Result("Right", chartHelper.getResult());
            chartHelper.setIsRightTested();
            chartHelper.startTest();
            displayResults(rightEyeResult, R.id.rightEyeResultsTextView);
        }
        else if(!chartHelper.isLeftTested() && leftEyeResult == null){
            leftEyeResult = new Result("Left", chartHelper.getResult());
            chartHelper.setIsLeftTested();
            displayResults(leftEyeResult, R.id.leftEyeResultsTextView);
            endTest();
        }
    }

    private void displayResults(Result result, int id){
        String resultString = "";
        resultString += (result.getEye().toUpperCase() + "\nLine Number: " +
                        result.getLineNumber() + "\nVisual Acuity: " +
                        result.getVisualAcuity());
        TextView textView = (TextView) findViewById(id);
        textView.setText(resultString);
    }








}
