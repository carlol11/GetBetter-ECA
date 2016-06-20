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

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.interfaces.OnMonitoringFragmentInteractionListener;

import com.geebeelicious.geebeelicious.models.monitoring.Record;
import com.geebeelicious.geebeelicious.models.visualacuity.ChartHelper;
import com.geebeelicious.geebeelicious.models.visualacuity.DistanceCalculator;
import com.geebeelicious.geebeelicious.models.visualacuity.VisualAcuityResult;

/**
 * Created by Kate.
 * The VisualAcuityFragment serves as the fragment
 * for the visual acuity test. It uses the ChartHelper,
 * DistanceCalculator, and VisualAcuityResult classes
 * to perform the visual acuity test.
 */

public class VisualAcuityFragment extends Fragment {
    private OnMonitoringFragmentInteractionListener fragmentInteraction;
    private Record record;

    private Button yesButton;
    private Button noButton;
    private ImageView chartView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_visual_acuity, container, false);

        chartView = (ImageView)view.findViewById(R.id.chartLine);
        final ChartHelper chartHelper = new ChartHelper(chartView);
        yesButton = (Button) view.findViewById(R.id.YesButton);
        noButton = (Button) view.findViewById(R.id.NoButton);


        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartHelper.goToNextLine();
                if (chartHelper.isDone() && !chartHelper.isBothTested()) {
                    updateResults(chartHelper);
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chartHelper.setResult();
                if (chartHelper.isDone() && !chartHelper.isBothTested()) {
                    updateResults(chartHelper);
                }
            }
        });
        chartHelper.startTest();
        return view;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //distance calculator
        DistanceCalculator distanceCalculator = new DistanceCalculator();
        float distance = distanceCalculator.getUserDistance(getActivity(), chartView);
        fragmentInteraction.setInstructions("Distance yourself " +  String.format("%.2f", distance) +
                " meters away from the tablet.");
        record = fragmentInteraction.getRecord();
    }

    private void endTest(){
        CountDownTimer timer;

        yesButton.setVisibility(View.GONE);
        noButton.setVisibility(View.GONE);
        yesButton.setEnabled(false);
        noButton.setEnabled(false);

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

    private void updateResults(ChartHelper chartHelper){
        VisualAcuityResult rightEyeResult = null;
        VisualAcuityResult leftEyeResult = null;

        if(!chartHelper.isRightTested() && rightEyeResult == null){
            rightEyeResult = new VisualAcuityResult("Right", chartHelper.getResult());
            chartHelper.setIsRightTested();
            chartHelper.startTest();
            displayResults(rightEyeResult, R.id.rightEyeResultsTextView);
            record.setVisualActuityRight(rightEyeResult.getVisualAcuity());
        }
        else if(!chartHelper.isLeftTested() && leftEyeResult == null){
            leftEyeResult = new VisualAcuityResult("Left", chartHelper.getResult());
            chartHelper.setIsLeftTested();
            displayResults(leftEyeResult, R.id.leftEyeResultsTextView);
            record.setVisualAcuityLeft(leftEyeResult.getVisualAcuity());
            endTest();
        }
    }

    private void displayResults(VisualAcuityResult result, int id){
        String resultString = "";
        resultString += (result.getEye().toUpperCase() + "\nLine Number: " +
                result.getLineNumber() + "\nVisual Acuity: " +
                result.getVisualAcuity());
        fragmentInteraction.setResults(resultString);
    }

}
