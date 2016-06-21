package com.geebeelicious.geebeelicious.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.interfaces.OnMonitoringFragmentInteractionListener;

import com.geebeelicious.geebeelicious.models.colorvision.IshiharaHelper;
import com.geebeelicious.geebeelicious.models.monitoring.Record;

/**
 * Created by Kate.
 * The ColorVisionMainFragment serves as the main fragment for
 * the color vision test. The activity utilizes the
 * IshiharaHelper class to perform the test.
 * */
public class ColorVisionFragment extends Fragment {
    private OnMonitoringFragmentInteractionListener fragmentInteraction;

    private ImageView chartView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_color_vision, container, false);

        ImageButton option1 = (ImageButton) view.findViewById(R.id.cvt_option1);
        ImageButton option2 = (ImageButton) view.findViewById(R.id.cvt_option2);
        ImageButton option3 = (ImageButton) view.findViewById(R.id.cvt_option3);
        ImageButton option4 = (ImageButton) view.findViewById(R.id.cvt_option4);
        ImageButton option5 = (ImageButton) view.findViewById(R.id.cvt_option5);
        final ImageButton[] buttonList = {option1, option2, option3, option4, option5};
        final IshiharaHelper ishiharaHelper = new IshiharaHelper((ImageView) view.findViewById(R.id.ishiharaPlate), buttonList);

        chartView = (ImageView)view.findViewById(R.id.ishiharaPlate);


        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ishiharaHelper.answerQuestion(0);
                updateResults(ishiharaHelper, buttonList);
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ishiharaHelper.answerQuestion(1);
                updateResults(ishiharaHelper, buttonList);
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ishiharaHelper.answerQuestion(2);
                updateResults(ishiharaHelper, buttonList);
            }
        });

        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ishiharaHelper.answerQuestion(3);
                updateResults(ishiharaHelper, buttonList);
            }
        });

        option5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ishiharaHelper.answerQuestion(4);
                updateResults(ishiharaHelper, buttonList);
            }
        });

        ishiharaHelper.startTest();

        fragmentInteraction.setInstructions(R.string.colorVision_instruction);

        return view;
    }

    //Allows test to either go to the next question and save results if the test is done
    private void updateResults(IshiharaHelper ishiharaHelper, ImageButton[] buttonList){
        ishiharaHelper.goToNextQuestion();
        if(ishiharaHelper.isDone()){
            Record record = fragmentInteraction.getRecord();
            record.setColorVision(ishiharaHelper.getResult());
            displayResults(ishiharaHelper.getScore());
            endTest(buttonList);
        }
    }

    //Displays test results in a TextView
    private void displayResults(int score){
        String resultString = "SCORE: " + score;

        if(score>=10){
            resultString += "\nYou have NORMAL color vision.";
        } else{
            resultString += "\nYou scored lower than normal.";
        }
        fragmentInteraction.setResults(resultString);

    }

    //Sets view for end of test
    private void endTest(ImageButton[] buttonList){
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
                fragmentInteraction.doneFragment();
            }
        };
        timer.start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            fragmentInteraction = (OnMonitoringFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMonitoringFragmentInteractionListener");
        }
    }

}
