package com.geebeelicious.geebeelicious.tests.colorvision;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import models.colorvision.IshiharaHelper;

public class ColorVisionMainActivity extends ActionBarActivity {

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

        ishiharaHelper.startTest();

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
    }

    private void updateResults(IshiharaHelper ishiharaHelper, ImageButton[] buttonList){
        ishiharaHelper.goToNextQuestion();
        if(ishiharaHelper.isDone()){
            //TODO: pass the results to a record
            displayResults(ishiharaHelper.getScore());
            endTest(buttonList, ishiharaHelper);
        }
    }

    private void displayResults(int score){
        String resultString = "SCORE: " + score;
        if(score>=10){
            resultString += "\nYou have NORMAL color vision.";
        } else{
            resultString += "\nYou scored lower than normal.";
        }
        TextView textView = (TextView) findViewById(R.id.cvtResultsText);
        textView.setText(resultString);
    }

    private void endTest(ImageButton[] buttonList, IshiharaHelper ishiharaHelper){
        for(ImageButton i : buttonList){
            i.setEnabled(false);
        }

        //TODO: insert code to set plateView to something else
    }





}
