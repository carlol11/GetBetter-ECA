package com.geebeelicious.geebeelicious.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.interfaces.MonitoringFragmentInteraction;

import java.util.concurrent.TimeUnit;

import models.grossmotor.GrossMotorSkill;
import models.grossmotor.GrossMotorTest;
import models.monitoring.Record;

/**
 * Created by Kate.
 * The GrossMotorMainFragment class serves as the main fragment
 * for the gross motor test. It uses the GrossMotorTest class
 * to perform the test.
 */

public class GrossMotorFragment extends Fragment {
    private Record record;
    private MonitoringFragmentInteraction fragmentInteraction;
    private Activity activity;

    private GrossMotorTest grossMotorTest;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_gross_motor, container, false);

        Button yesButton = (Button) view.findViewById(R.id.YesButton);
        Button noButton = (Button) view.findViewById(R.id.NoButton);
        Button naButton = (Button) view.findViewById(R.id.NAButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set skill to pass
                grossMotorTest.getCurrentSkill().setSkillPassed();
                goToNextQuestion();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set skill to fail
                grossMotorTest.getCurrentSkill().setSkillFailed();
                goToNextQuestion();
            }
        });

        naButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set skill to na
                grossMotorTest.getCurrentSkill().setSkillSkipped();
                goToNextQuestion();
            }
        });
        grossMotorTest = new GrossMotorTest(activity);
        grossMotorTest.makeTest();
        startTest();

        return view;
    }

    private void startTest(){
        grossMotorTest.setCurrentSkill(0);
        displaySkill(0);
    }

    private void endTest(){
        String resultString = grossMotorTest.getAllResults() + "\nOverall: " + grossMotorTest.getFinalResult();


        TextView countDownTV = (TextView)view.findViewById(R.id.countdownTV);
        ImageView countDownIV = (ImageView)view.findViewById(R.id.grossMotorIV);
        CountDownTimer timer;

        grossMotorTest.endTest();
        hideAnswerButtons();
        /*
        TODO: Fix this after naayos mo na yung instructions and placeholder
        ((TextView)view.findViewById(R.id.gmSkillTypeTV)).setText("");
        ((TextView)view.findViewById(R.id.gmInstructionsTV)).setText("");
        ((TextView)view.findViewById(R.id.gmDurationTV)).setText("");
        ((TextView)view.findViewById(R.id.gmAssessmentTV)).setText("");
        ((TextView)view.findViewById(R.id.gmSkillNameTV)).setText(resultString);
        */
        record.setGrossMotor(fragmentInteraction.getIntResults(grossMotorTest.getFinalResult()));

        countDownTV.setVisibility(View.GONE);
        countDownIV.setVisibility(View.VISIBLE);
        countDownIV.setImageResource(R.drawable.wait_for_next_test);

        timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                fragmentInteraction.doneFragment();
            }
        };
        timer.start();

    }

    //Displays the skill as determined by the GrossMotorTest on the screen
    private void displaySkill(final int i){
        hideAnswerButtons();
        final CountDownTimer countDownTimer;
        final GrossMotorSkill gms = grossMotorTest.getCurrentSkill();
        String activityString = "Activity: " + gms.getSkillName();
        String typeString = "Type: " + gms.getType();
        String durationString = "Duration: " + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(gms.getDuration()));

        /*
        TODO: Fix this after the instructions crap is fixed
        ((TextView)findViewById(R.id.gmSkillNameTV)).setText(activityString);
        ((TextView)findViewById(R.id.gmSkillTypeTV)).setText(typeString);
        ((TextView)findViewById(R.id.gmInstructionsTV)).setText(gms.getInstruction());
        ((TextView)findViewById(R.id.gmDurationTV)).setText(durationString);
        */
        countDownTimer = new CountDownTimer(6000, 1000) {
            TextView timerView = (TextView)view.findViewById(R.id.countdownTV);

            @Override
            public void onTick(long millisUntilFinished) {
                String timerString = "" + String.format("%01d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
                timerView.setText(timerString);
            }

            @Override
            public void onFinish() {
                grossMotorTest.performSkill(i, timerView, (LinearLayout) view.findViewById(R.id.linearLayoutAnswers));
            }
        };
        hideAnswerButtons();
        countDownTimer.start();

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
        LinearLayout answers = (LinearLayout)view.findViewById(R.id.linearLayoutAnswers);
        for (int j = 0; j<answers.getChildCount(); j++){
            View view = answers.getChildAt(j);
            view.setEnabled(false);
            view.setVisibility(View.GONE);
        }
        answers.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            fragmentInteraction = (MonitoringFragmentInteraction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MonitoringFragmentInteraction");
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        record = fragmentInteraction.getRecord();
    }
}
