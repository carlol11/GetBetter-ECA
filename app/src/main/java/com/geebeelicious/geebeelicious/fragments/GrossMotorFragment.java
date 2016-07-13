package com.geebeelicious.geebeelicious.fragments;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.interfaces.MonitoringTestFragment;
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

public class GrossMotorFragment extends MonitoringTestFragment {
    private OnMonitoringFragmentInteractionListener fragmentInteraction;
    private GrossMotorFragment.OnFragmentInteractionListener grossMotorInteraction;
    private Activity activity;

    private GrossMotorTest grossMotorTest;
    private CountDownTimer countDownTimer;

    private Typeface chalkFont;

    private Button naButton;

    public GrossMotorFragment(){
        this.introStringResource = R.string.grossmotor_intro;
        this.introTime = 3000;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_gross_motor, container, false);
        chalkFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DJBChalkItUp.ttf");

        Button yesButton = (Button) view.findViewById(R.id.YesButton);
        Button noButton = (Button) view.findViewById(R.id.NoButton);
        naButton = (Button) view.findViewById(R.id.NAButton);

        yesButton.setTypeface(chalkFont);
        noButton.setTypeface(chalkFont);
        naButton.setTypeface(chalkFont);

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
              onNAButtonClick();
            }
        });


        grossMotorTest = new GrossMotorTest(activity);
        grossMotorTest.makeTest();
        startTest();

        grossMotorInteraction.expandECAToMatchHeight();
        return view;
    }

    public void onNAButtonClick(){
        TextView timerView = (TextView)view.findViewById(R.id.countdownTV);
        timerView.setTypeface(chalkFont);
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
        grossMotorTest.getCurrentSkill().setSkillSkipped();
        goToNextQuestion();
        grossMotorTest.skipTest(timerView, grossMotorInteraction);
    }

    public void onRemarkSaveButtonClicked() {
        grossMotorInteraction.onHideRemarkLayout();
        updateTestEndRemark(grossMotorTest.getIntFinalResult());
        fragmentInteraction.doneFragment();
    }

    public void onBackPressed(){
        grossMotorTest.endTest();
    }

    private void updateTestEndRemark(int result) {
        switch (result){
            case 0:
                this.isEndEmotionHappy = true;
                this.endStringResource = R.string.grossmotor_pass;
                this.endTime = 3000;
                break;
            case 1:
                this.isEndEmotionHappy = false;
                this.endStringResource = R.string.grossmotor_fail;
                this.endTime = 5000;
                break;
            default:
                this.isEndEmotionHappy = true;
                this.endStringResource = R.string.grossmotor_na;
                this.endTime = 4000;
                break;
        }
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

        naButton.setVisibility(View.GONE);

        grossMotorInteraction.shrinkECAToOriginalHeight();
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
            TextView timerView = (TextView)(view.findViewById(R.id.countdownTV));
            @Override
            public void onTick(long millisUntilFinished) {
                String timerString = "" + String.format("%01d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
                timerView.setTypeface(chalkFont);
                timerView.setText(timerString);
            }

            @Override
            public void onFinish() {
                grossMotorTest.performSkill(i, timerView, (LinearLayout) view.findViewById(R.id.linearLayoutAnswers), naButton);
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
        naButton.setVisibility(View.VISIBLE);
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
        void onShowRemarkLayout();
        void onHideRemarkLayout();
        void expandECAToMatchHeight();
        void shrinkECAToOriginalHeight();
    }
}
