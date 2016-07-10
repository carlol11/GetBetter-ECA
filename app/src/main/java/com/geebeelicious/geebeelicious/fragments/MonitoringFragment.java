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
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.interfaces.MonitoringTestFragment;
import com.geebeelicious.geebeelicious.interfaces.OnMonitoringFragmentInteractionListener;

import com.geebeelicious.geebeelicious.models.monitoring.Record;

/**
 * Created by Kate.
 * The MonitoringFragment class serves as the first fragment
 * that users will encounter when they choose to perform a
 * monitoring activity. The fragment will allow users to
 * input updates for certain health determinants such as
 * height and weight.
 */

public class MonitoringFragment extends MonitoringTestFragment {
    private TextView questionView;
    private NumberPicker numberPicker;
    private TextView unitView;

    private final int[] questions = {R.string.monitoring_height, R.string.monitoring_weight};
    private final int[] questionUnit = {R.string.centimeters, R.string.kilograms};
    private final int numberOfQuestions = 2;
    private int questionsCounter = 0;

    private Record record;

    private OnMonitoringFragmentInteractionListener fragmentInteraction;

    public MonitoringFragment(){
        this.endTime = 5000;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_monitoring, container, false);

        questionView = (TextView)view.findViewById(R.id.questionView);
        unitView = (TextView)view.findViewById(R.id.unitView);
        numberPicker = (NumberPicker)view.findViewById(R.id.monitoringNumberPicker);
        numberPicker.setMinValue(0);

        setQuestion(questions[questionsCounter]);

        unitView.setText(questionUnit[questionsCounter]);
        numberPicker.setMaxValue(250);

        Button saveButton = (Button)view.findViewById(R.id.saveAnswerButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("numberPicker: " +  numberPicker.getValue() + " " + new Integer(numberPicker.getValue()).doubleValue());
                switch(questionsCounter){
                    case 0:
                        record.setHeight(new Integer(numberPicker.getValue()).doubleValue());
                        numberPicker.setMaxValue(100);
                        break;
                    case 1:
                        record.setWeight(new Integer(numberPicker.getValue()).doubleValue());
                        break;
                }
                questionsCounter++;
                if(questionsCounter < numberOfQuestions){
                    setQuestion(questions[questionsCounter]);
                    unitView.setText(questionUnit[questionsCounter]);
                }else{
                    endMonitoring();
                }
            }
        });

        return view;
    }

    private void setQuestion(int resID) {
        questionView.setText(resID);
        fragmentInteraction.setInstructions(resID);
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
        record = fragmentInteraction.getRecord();
    }

    private void endMonitoring(){
        questionsCounter = 0;
        fragmentInteraction.doneFragment();
    }
}
