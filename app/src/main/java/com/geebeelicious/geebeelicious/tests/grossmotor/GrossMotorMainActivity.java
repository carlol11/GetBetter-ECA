package com.geebeelicious.geebeelicious.tests.grossmotor;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.MonitoringConsultationChoice;
import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.tests.finemotor.FineMotorActivity;


import java.util.concurrent.TimeUnit;

import models.grossmotor.GrossMotorSkill;
import models.grossmotor.GrossMotorTest;

/**
 * Created by Kate.
 * The GrossMotorMainActivity class serves as the main activity
 * for the gross motor test. It uses the GrossMotorTest class
 * to perform the test.
 */

public class GrossMotorMainActivity extends ActionBarActivity {

    private GrossMotorTest grossMotorTest;
    private Bundle record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gross_motor_main);
        grossMotorTest = new GrossMotorTest(getApplicationContext());

        record = this.getIntent().getExtras();

        grossMotorTest.makeTest();
        startTest();
    }

    private void startTest(){
        grossMotorTest.setCurrentSkill(0);
        displaySkill(0);
    }

    private void endTest(){
        grossMotorTest.endTest();
        hideAnswerButtons();
        ((TextView)findViewById(R.id.gmSkillTypeTV)).setText("");
        ((TextView)findViewById(R.id.gmInstructionsTV)).setText("");
        ((TextView)findViewById(R.id.gmDurationTV)).setText("");
        ((TextView)findViewById(R.id.gmAssessmentTV)).setText("");
        ((TextView)findViewById(R.id.gmSkillNameTV)).setText(grossMotorTest.getAllResults() +
                "\nOverall: " + grossMotorTest.getFinalResult());

        record.putString("grossMotor", grossMotorTest.getFinalResult());

        TextView countDownTV = (TextView)findViewById(R.id.countdownTV);
        countDownTV.setVisibility(View.GONE);

        ImageView countDownIV = (ImageView)findViewById(R.id.grossMotorIV);
        countDownIV.setVisibility(View.VISIBLE);
        countDownIV.setImageResource(R.drawable.wait_for_next_test);

        CountDownTimer timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(GrossMotorMainActivity.this, FineMotorActivity.class);
                intent.putExtras(record);
                finish();
                startActivity(intent);
            }
        };
        timer.start();

    }

    //Displays the skill as determined by the GrossMotorTest on the screen
    private void displaySkill(final int i){
        hideAnswerButtons();
        final GrossMotorSkill gms = grossMotorTest.getCurrentSkill();
        String activityString = "Activity: " + gms.getSkillName();
        String typeString = "Type: " + gms.getType();
        String durationString = "Duration: " + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(gms.getDuration()));
        ((TextView)findViewById(R.id.gmSkillNameTV)).setText(activityString);
        ((TextView)findViewById(R.id.gmSkillTypeTV)).setText(typeString);
        ((TextView)findViewById(R.id.gmInstructionsTV)).setText(gms.getInstruction());
        ((TextView)findViewById(R.id.gmDurationTV)).setText(durationString);

        final CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {
            TextView timerView = (TextView)findViewById(R.id.countdownTV);

            @Override
            public void onTick(long millisUntilFinished) {
                String timerString = "" + String.format("%01d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
                timerView.setText(timerString);
            }

            @Override
            public void onFinish() {
                grossMotorTest.performSkill(i, timerView, (LinearLayout) findViewById(R.id.linearLayoutAnswers));
            }
        };
        hideAnswerButtons();
        countDownTimer.start();

    }

    //Sets current skill to "Pass" and goes to next question
    public void answerYes(View view) {
        grossMotorTest.getCurrentSkill().setSkillPassed();
        goToNextQuestion();
    }

    //Sets current skill to "Fail" and goes to next question
    public void answerNo(View view) {
        grossMotorTest.getCurrentSkill().setSkillFailed();
        goToNextQuestion();
    }

    //Sets current skill to "NA" and goes to next question
    public void answerNA(View view) {
        grossMotorTest.getCurrentSkill().setSkillSkipped();
        goToNextQuestion();
    }

    //Allows the test to move to the next question or ends the test
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
        for (int j = 0; j<answers.getChildCount(); j++){
            View view = answers.getChildAt(j);
            view.setEnabled(false);
            view.setVisibility(View.GONE);
        }
        answers.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GrossMotorMainActivity.this, MonitoringConsultationChoice.class);
        grossMotorTest.endTest();
        finish();
        startActivity(intent);
    }

}
