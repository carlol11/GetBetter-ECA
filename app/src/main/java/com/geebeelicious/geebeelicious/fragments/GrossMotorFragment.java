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
import com.geebeelicious.geebeelicious.interfaces.OnMonitoringFragmentInteractionListener;

import java.util.concurrent.TimeUnit;

import com.geebeelicious.geebeelicious.models.grossmotor.GrossMotorSkill;
import com.geebeelicious.geebeelicious.models.grossmotor.GrossMotorTest;
import com.geebeelicious.geebeelicious.models.monitoring.Record;

/**
 * Created by Kate.
 * The GrossMotorMainFragment class serves as the main fragment
 * for the gross motor test. It uses the GrossMotorTest class
 * to perform the test.
 */

public class GrossMotorFragment extends Fragment {
    private OnMonitoringFragmentInteractionListener fragmentInteraction;
    private GrossMotorFragment.OnFragmentInteractionListener grossMotorInteraction;
    private Activity activity;

    private GrossMotorTest grossMotorTest;
    private View view;

    private CountDownTimer countDownTimer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_gross_motor, container, false);

        Button yesButton = (Button) view.findViewById(R.id.YesButton);
        Button noButton = (Button) view.findViewById(R.id.NoButton);

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

        grossMotorTest = new GrossMotorTest(activity);
        grossMotorTest.makeTest();
        startTest();

        return view;
    }

    public void onNAButtonClick(){
        TextView timerView = (TextView)view.findViewById(R.id.countdownTV);

        if (countDownTimer != null){
            countDownTimer.cancel();
        }
        grossMotorTest.getCurrentSkill().setSkillSkipped();
        goToNextQuestion();
        grossMotorTest.skipTest(timerView, grossMotorInteraction);
    }

    public void onRemarkSaveButtonClicked() {
        TextView countDownTV = (TextView)view.findViewById(R.id.countdownTV);
        ImageView countDownIV = (ImageView)view.findViewById(R.id.grossMotorIV);

        grossMotorInteraction.onHideRemarkLayout();

        countDownTV.setVisibility(View.GONE);
        countDownIV.setVisibility(View.VISIBLE);
        countDownIV.setImageResource(R.drawable.wait_for_next_test);

        CountDownTimer timer = new CountDownTimer(6000, 1000) {
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
    private void startTest(){
        grossMotorTest.setCurrentSkill(0);
        displaySkill(0);
    }

    private void endTest(){
        String resultString = grossMotorTest.getAllResults() + "\nOverall: " + grossMotorTest.getFinalResult();
        Record record = fragmentInteraction.getRecord();


        grossMotorTest.endTest();
        hideAnswerButtons();

        fragmentInteraction.setResults(resultString);

        record.setGrossMotor(fragmentInteraction.getIntResults(grossMotorTest.getFinalResult()));

        grossMotorInteraction.onHideNAButton();
        grossMotorInteraction.onShowRemarkLayout();
    }

    //Displays the skill as determined by the GrossMotorTest on the screen
    private void displaySkill(final int i){
        //TODO: Not sure why there are two hideanswerbuttons here
        hideAnswerButtons();
        final GrossMotorSkill gms = grossMotorTest.getCurrentSkill();
        String durationString = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(gms.getDuration()));
        
        fragmentInteraction.setInstructions(gms.getInstruction() +" for " + durationString +" seconds.");

        countDownTimer = new CountDownTimer(6000, 1000) {
            TextView timerView = (TextView)view.findViewById(R.id.countdownTV);

            @Override
            public void onTick(long millisUntilFinished) {
                String timerString = "" + String.format("%01d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
                timerView.setText(timerString);
            }

            @Override
            public void onFinish() {
                grossMotorTest.performSkill(i, timerView, (LinearLayout) view.findViewById(R.id.linearLayoutAnswers), grossMotorInteraction);
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
        grossMotorInteraction.onShowNAButton();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            fragmentInteraction = (OnMonitoringFragmentInteractionListener) activity;
            grossMotorInteraction = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMonitoringFragmentInteractionListener and OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener extends OnMonitoringFragmentInteractionListener {
        void onShowNAButton();
        void onHideNAButton();
        void onShowRemarkLayout();
        void onHideRemarkLayout();
    }
}
