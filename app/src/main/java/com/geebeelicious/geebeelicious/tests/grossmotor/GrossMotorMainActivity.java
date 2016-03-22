package com.geebeelicious.geebeelicious.tests.grossmotor;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;


import java.util.concurrent.TimeUnit;

import models.grossmotor.GrossMotorSkill;
import models.grossmotor.GrossMotorTest;

public class GrossMotorMainActivity extends ActionBarActivity {

    GrossMotorTest grossMotorTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gross_motor_main);
        grossMotorTest = new GrossMotorTest(getApplicationContext());
        grossMotorTest.makeTest();
        startTest();
    }

    @Override
    protected void onStop() {
        super.onStop();
        grossMotorTest.endTest();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        grossMotorTest.endTest();
    }

    private void startTest(){
        grossMotorTest.setCurrentSkill(0);
        displaySkill(0);
    }

    private void endTest(){
        ((TextView)findViewById(R.id.gmSkillTypeTV)).setText("");
        ((TextView)findViewById(R.id.gmInstructionsTV)).setText("");
        ((TextView)findViewById(R.id.gmDurationTV)).setText("");
        ((TextView)findViewById(R.id.gmAssessmentTV)).setText("");
        ((TextView)findViewById(R.id.gmSkillNameTV)).setText(grossMotorTest.getAllResults() +
                                                            "\nOverall: " + grossMotorTest.getFinalResult());

        
        hideAnswerButtons();

    }

    private void displaySkill(final int i){
        final GrossMotorSkill gms = grossMotorTest.getCurrentSkill();
        String activityString = "Activity: " + gms.getSkillName();
        String typeString = "Type: " + gms.getType();
        String durationString = "Duration: " + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(gms.getDuration()));
        ((TextView)findViewById(R.id.gmSkillNameTV)).setText(activityString);
        ((TextView)findViewById(R.id.gmSkillTypeTV)).setText(typeString);
        ((TextView)findViewById(R.id.gmInstructionsTV)).setText(gms.getInstruction());
        ((TextView)findViewById(R.id.gmDurationTV)).setText(durationString);

        hideAnswerButtons();

        final CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {
            TextView timerView = (TextView)findViewById(R.id.countdownTV);

            @Override
            public void onTick(long millisUntilFinished) {
                String timerString = "" + String.format("%01d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
                timerView.setText(timerString);
            }

            @Override
            public void onFinish() {
                grossMotorTest.performSkill(i, timerView, (LinearLayout)findViewById(R.id.linearLayoutAnswers));
            }
        };

        countDownTimer.start();


    }

    public void answerYes(View view) {
        grossMotorTest.getCurrentSkill().setSkillPassed();
        goToNextQuestion();
    }

    public void answerNo(View view) {
        grossMotorTest.getCurrentSkill().setSkillFailed();
        goToNextQuestion();
    }

    public void answerNA(View view) {
        grossMotorTest.getCurrentSkill().setSkillSkipped();
        goToNextQuestion();
    }

    private void goToNextQuestion(){
        grossMotorTest.getCurrentSkill().setTested();
        int currentSkill = grossMotorTest.getCurrentSkillNumber();
        if(currentSkill < 2){
            currentSkill++;
            grossMotorTest.setCurrentSkill(currentSkill);
            displaySkill(currentSkill);
        } else if(currentSkill == 2){
            endTest();
        }
    }

    private void hideAnswerButtons(){
        LinearLayout answers = (LinearLayout)findViewById(R.id.linearLayoutAnswers);
        answers.setVisibility(View.GONE);
        for (int j = 0; j<answers.getChildCount(); j++){
            View view = answers.getChildAt(j);
            view.setVisibility(View.GONE);
        }
    }

}
