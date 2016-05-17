package com.geebeelicious.geebeelicious.fragments;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.interfaces.MonitoringFragmentInteraction;

import models.finemotor.FineMotorHelper;
import models.monitoring.Record;

/**
 * Created by MG.
 * The FineMotorFragment class serves as the main fragment
 * for the fine motor test. It uses the FineMotorHelper class
 * to perform the test.
 */

public class FineMotorFragment extends Fragment {
    private Record record;
    private MonitoringFragmentInteraction fragmentInteraction;


    private static final String TAG = "FineMotorActivity";

    private View view;

    private ImageView imageViewPathToTrace;

    //Set the color for the start and end of the path
    private final int START_COLOR = Color.parseColor("#09BCD4");//update the instruction if you change this
    private final int END_COLOR = Color.parseColor("#E71E63");  //update the instruction if you change this

    private int currentTest; //0 for non dominant, dominant, ask assistance

    private boolean isTestOngoing = true;
    private boolean hasStarted = false; //has user started
    private FineMotorHelper fineMotorHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_fine_motor, container, false);

        imageViewPathToTrace = (ImageView) view.findViewById(R.id.imageViewPathToTrace);

        currentTest = 0;
        fineMotorHelper = new FineMotorHelper(getActivity(), imageViewPathToTrace);

        imageViewPathToTrace.setOnTouchListener(image_Listener);
        initializeButtons();

        return view;
    }

    //OnTouchListener for tracing the path
    public View.OnTouchListener image_Listener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Bitmap bitmap = ((BitmapDrawable) imageViewPathToTrace.getDrawable()).getBitmap();
            float eventX = event.getX();
            float eventY = event.getY();
            int[] xY = fineMotorHelper.getBitMapCoordinates(bitmap, event.getX(), event.getY());
            int pixel = bitmap.getPixel(xY[0], xY[1]);

            if(hasStarted){ //if user have pressed start_color
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP: //go back to start if finger is lifted
                        hasStarted = false;
                        fragmentInteraction.setInstructions(fineMotorHelper.doIfTouchIsUp());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(pixel == END_COLOR) { //if user done
                            if(hasStarted){
                                hasStarted = false;
                                if(currentTest == 0){
                                    fineMotorHelper.doTestWithPen();
                                    currentTest = 1;
                                } else if(currentTest == 1) {
                                    fragmentInteraction.setInstructions(fineMotorHelper.askAssistantOfPen());
                                    currentTest = 2;
                                    showAnswerButtons();
                                }
                            }
                        } else if(pixel == 0){ //if touch is outside path
                            fineMotorHelper.doIfOutSideThePath();
                        } else {
                            fineMotorHelper.doIfWithinPath();
                        }
                        break;
                    default:
                        return false;
                }
                Log.d(TAG, "Touch event position: " + eventX + ", " + eventY + "\n" +
                        "Pixel: " + pixel);
                return true;
            } else {
                if(pixel == START_COLOR && (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)){
                    hasStarted = true;
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN){
                    fineMotorHelper.setInstructions(currentTest);
                    return true;
                }
                return false;
            }
        }
    };

    //Define OnClickListener for buttons
    private void initializeButtons(){
        Button buttonYes = (Button) view.findViewById(R.id.YesButton);
        Button buttonNo = (Button) view.findViewById(R.id.NoButton);


        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fineMotorHelper.setResult(2, true);
                sendResults();
            }
        });

        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fineMotorHelper.setResult(2, false);
                sendResults();
            }
        });
    }

    private void sendResults(){
        String resultString ;
        boolean[] result = fineMotorHelper.getResults();
        CountDownTimer timer;
        //TODO: [Testing] remove the resultString na

        if(isTestOngoing){ //this is to avoid double clicking
            isTestOngoing = false;

            resultString = "Non dominant hand: " + result[0] +
                    "\nDominant hand: " + result[1] +
                    "\nUsing pen: " + result[2];

            record.setFineMotorNDominant(result[0] ? 0: 1);
            record.setFineMotorDominant(result[1] ? 0: 1);
            record.setFineMotorHold(result[2] ? 0: 1);
            fragmentInteraction.setInstructions(resultString);
            imageViewPathToTrace.setBackgroundColor(Color.WHITE);
            imageViewPathToTrace.setImageResource(R.drawable.wait_for_next_test);
            hideAnswerButtons();
        }



        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            private int getIntResults(String result){
                switch(result){
                    case "Pass":
                        return 0;
                    case "Fail":
                        return 1;
                    default:
                        return 2;
                }
            }

            @Override
            public void onFinish() {
                fragmentInteraction.doneFragment();
            }
        };
        timer.start();
    }

    private void hideAnswerButtons(){
        LinearLayout answers = (LinearLayout)view.findViewById(R.id.linearLayoutAnswers);
        for (int j = 0; j<answers.getChildCount(); j++){
            View view = answers.getChildAt(j);
            view.setEnabled(false);
            view.setVisibility(View.INVISIBLE);
        }
        answers.setVisibility(View.INVISIBLE);

    }

    private void showAnswerButtons(){
        LinearLayout answers = (LinearLayout)view.findViewById(R.id.linearLayoutAnswers);
        for (int j = 0; j<answers.getChildCount(); j++){
            View view = answers.getChildAt(j);
            view.setEnabled(true);
            view.setVisibility(View.VISIBLE);
        }
        answers.setVisibility(View.VISIBLE);

    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
