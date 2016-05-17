package com.geebeelicious.geebeelicious.tests.colorvision;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.activities.MonitoringConsultationChoice;
import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.tests.hearing.HearingMainActivity;

import com.geebeelicious.geebeelicious.models.colorvision.IshiharaHelper;

/**
 * Created by Kate.
 * The ColorVisionMainActivity serves as the main activity for
 * the color vision test. The activity utilizes the
 * IshiharaHelper class to perform the test.
 */

public class ColorVisionMainActivity extends ActionBarActivity {

    private Bundle record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_vision_main);

        ImageButton option1 = (ImageButton) findViewById(R.id.cvt_option1);
        ImageButton option2 = (ImageButton) findViewById(R.id.cvt_option2);
        ImageButton option3 = (ImageButton) findViewById(R.id.cvt_option3);
        ImageButton option4 = (ImageButton) findViewById(R.id.cvt_option4);
        ImageButton option5 = (ImageButton) findViewById(R.id.cvt_option5);
        final ImageButton[] buttonList = {option1, option2, option3, option4, option5};
        final IshiharaHelper ishiharaHelper = new IshiharaHelper((ImageView) findViewById(R.id.ishiharaPlate), buttonList);

        record = this.getIntent().getExtras();

        option1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ishiharaHelper.answerQuestion(0);
                updateResults(ishiharaHelper, buttonList);
            }
        });

        option2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ishiharaHelper.answerQuestion(1);
                updateResults(ishiharaHelper, buttonList);
            }
        });

        option3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ishiharaHelper.answerQuestion(2);
                updateResults(ishiharaHelper, buttonList);
            }
        });

        option4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ishiharaHelper.answerQuestion(3);
                updateResults(ishiharaHelper, buttonList);
            }
        });

        option5.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ishiharaHelper.answerQuestion(4);
                updateResults(ishiharaHelper, buttonList);
            }
        });

        ishiharaHelper.startTest();
    }

    //Allows test to either go to the next question and save results if the test is done
    private void updateResults(IshiharaHelper ishiharaHelper, ImageButton[] buttonList){
        ishiharaHelper.goToNextQuestion();
        if(ishiharaHelper.isDone()){
            record.putString("colorVision", ishiharaHelper.getResult());
            displayResults(ishiharaHelper.getScore());
            endTest(buttonList);
        }
    }

    //Displays test results in a TextView
    private void displayResults(int score){
        TextView textView;
        String resultString = "SCORE: " + score;

        if(score>=10){
            resultString += "\nYou have NORMAL color vision.";
        } else{
            resultString += "\nYou scored lower than normal.";
        }
        textView = (TextView) findViewById(R.id.cvtResultsText);
        textView.setText(resultString);
    }

    //Sets view for end of test
    private void endTest(ImageButton[] buttonList){
        ImageView chartView = (ImageView)findViewById(R.id.ishiharaPlate);
        CountDownTimer timer;

        for(ImageButton i : buttonList){
            i.setVisibility(View.GONE);
            i.setEnabled(false);
        }

        chartView.setImageResource(R.drawable.wait_for_next_test);

        timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(ColorVisionMainActivity.this, HearingMainActivity.class);
                intent.putExtras(record);
                finish();
                startActivity(intent);
            }
        };
        timer.start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ColorVisionMainActivity.this, MonitoringConsultationChoice.class);
        finish();
        startActivity(intent);
    }

}
