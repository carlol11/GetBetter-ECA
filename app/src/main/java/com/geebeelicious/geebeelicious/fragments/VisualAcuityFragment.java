package com.geebeelicious.geebeelicious.fragments;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.interfaces.MonitoringTestFragment;
import com.geebeelicious.geebeelicious.interfaces.OnMonitoringFragmentInteractionListener;

import com.geebeelicious.geebeelicious.models.monitoring.Record;
import com.geebeelicious.geebeelicious.models.visualacuity.ChartHelper;
import com.geebeelicious.geebeelicious.models.visualacuity.DistanceCalculator;
import com.geebeelicious.geebeelicious.models.visualacuity.VisualAcuityResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Kate.
 * The VisualAcuityFragment serves as the fragment
 * for the visual acuity test. It uses the ChartHelper,
 * DistanceCalculator, and VisualAcuityResult classes
 * to perform the visual acuity test.
 */

public class VisualAcuityFragment extends MonitoringTestFragment {
    private OnMonitoringFragmentInteractionListener fragmentInteraction;
    private Record record;

    private Button yesButton;
    private Button noButton;
    private ImageView chartView;

    public VisualAcuityFragment(){
        this.introStringResource = R.string.visualAcuity_intro;
        this.introTime = 3000;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_visual_acuity, container, false);

        chartView = (ImageView)view.findViewById(R.id.chartLine);
        final ChartHelper chartHelper = new ChartHelper(chartView, getChartPreference());
        yesButton = (Button) view.findViewById(R.id.YesButton);
        noButton = (Button) view.findViewById(R.id.NoButton);
        Typeface chalkFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DJBChalkItUp.ttf");
        yesButton.setTypeface(chalkFont);
        noButton.setTypeface(chalkFont);

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
        fragmentInteraction.setInstructions("Move " +  String.format("%.2f", distance) +
                " meters away from the tablet. Then " + getString(R.string.visualAcuity_instruction_left) + " Then tell me what you see.");
        record = fragmentInteraction.getRecord();
    }

    private void updateResults(ChartHelper chartHelper){
        VisualAcuityResult rightEyeResult = null;
        VisualAcuityResult leftEyeResult = null;

        if(!chartHelper.isRightTested() && rightEyeResult == null){
            rightEyeResult = new VisualAcuityResult("Right", chartHelper.getResult());
            chartHelper.setIsRightTested();
            chartHelper.startTest();
            displayResults(rightEyeResult);
            record.setVisualActuityRight(rightEyeResult.getVisualAcuity());

            fragmentInteraction.setInstructions(R.string.visualAcuity_instruction_right);
        }
        else if(!chartHelper.isLeftTested() && leftEyeResult == null){
            leftEyeResult = new VisualAcuityResult("Left", chartHelper.getResult());
            chartHelper.setIsLeftTested();
            displayResults(leftEyeResult);
            record.setVisualAcuityLeft(leftEyeResult.getVisualAcuity());

            updateTestEndRemark(leftEyeResult.getLineNumber());

            fragmentInteraction.doneFragment();
        }
    }

    private void displayResults(VisualAcuityResult result){
        String resultString = "";
        resultString += (result.getEye().toUpperCase() + "\nLine Number: " +
                result.getLineNumber() + "\nVisual Acuity: " +
                result.getVisualAcuity());
        fragmentInteraction.setResults(resultString);
    }

    private int getChartPreference(){
        int chartPreference = 0; //default is 0 for Snellen chart
        byte[] byteArray = new byte[4];
        try{
            FileInputStream fis = getActivity().getApplicationContext().openFileInput("VisualAcuityChartPreferences");
            fis.read(byteArray, 0, 4);
            fis.close();
        } catch(IOException e){
            return 1;
        }

        ByteBuffer b = ByteBuffer.wrap(byteArray);
        chartPreference = b.getInt();

        return chartPreference;
    }

    private void updateTestEndRemark(String lineNumber) {
        int lineNum = Integer.parseInt(lineNumber);

        if (lineNum < 8){
            this.isEndEmotionHappy = false;
            this.endStringResource = R.string.visual_acuity_fail;
            this.endTime = 5000;
        } else {
            this.isEndEmotionHappy = true;
            this.endStringResource = R.string.visual_acuity_pass;
            this.endTime = 3000;
        }
    }
}
