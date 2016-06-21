package com.geebeelicious.geebeelicious.fragments;


import android.app.Activity;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.interfaces.OnMonitoringFragmentInteractionListener;

import java.util.ArrayList;

import com.geebeelicious.geebeelicious.models.hearing.HearingTest;
import com.geebeelicious.geebeelicious.models.monitoring.Record;

/**
 * Created by Kate.
 * The HearingMainFragment serves as the fragment
 * for the hearing test. It uses the HearingTest class
 * to perform the hearing test.
 */

public class HearingMainFragment extends Fragment {
    private OnMonitoringFragmentInteractionListener fragmentInteraction;

    private ArrayList<Thread> threads;
    private HearingTest hearingTest;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_hearing_main, container, false);
        AudioManager audioManager = (AudioManager)activity.getSystemService(activity.AUDIO_SERVICE);

        hearingTest = new HearingTest();
        final double[] calibrationData = hearingTest.getCalibrationData(activity);

        final Button yesButton = (Button)view.findViewById(R.id.YesButton);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hearingTest.setHeard();
            }
        });

        final Runnable backgroundFlash = new Runnable(){
            public void run(){
                yesButton.setBackgroundColor(Color.parseColor("#18FFFF"));
            }
        };

        final Runnable backgroundNormal = new Runnable(){
            public void run(){
                yesButton.setBackgroundColor(Color.parseColor("#80DEEA"));
            }
        };

        final Runnable disableTest = new Runnable() {
            @Override
            public void run() {
                yesButton.setVisibility(View.GONE);
                yesButton.setEnabled(false);
                ImageView imageView = (ImageView)view.findViewById(R.id.hearingTestImageView);
                imageView.setImageResource(R.drawable.wait_for_next_test);
                fragmentInteraction.setResults(hearingTest.getResults());
            }
        };

        Thread screenThread = new Thread(new Runnable(){
            public void run(){
                while(hearingTest.isInLoop()){
                    if(!hearingTest.isRunning()){
                        return;
                    }
                    if(hearingTest.isHeard()){
                        activity.runOnUiThread(backgroundFlash);
                        while(hearingTest.isHeard()){

                        }
                    }
                }
            }
        });

        Thread timingThread = new Thread(new Runnable(){
            public void run(){
                while(hearingTest.isInLoop()){
                    if(!hearingTest.isRunning()){
                        return;
                    }
                    if(hearingTest.isHeard()){
                        try{
                            Thread.sleep(500);
                        } catch(InterruptedException ie){

                        }
                        activity.runOnUiThread(backgroundNormal);
                    }
                }
            }
        });

        Thread testThread = new Thread(new Runnable() {
            @Override
            public void run() {
                hearingTest.performTest(calibrationData);
                if(hearingTest.isDone()){
                    activity.runOnUiThread(backgroundFlash);
                    activity.runOnUiThread(disableTest);
                    endTest();
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {

                    }
                    fragmentInteraction.doneFragment();
                }
            }
        });

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 9, 0);
        threads = new ArrayList<Thread>();
        threads.add(screenThread);
        threads.add(timingThread);
        threads.add(testThread);
        screenThread.start();
        timingThread.start();
        testThread.start();

        fragmentInteraction.setInstructions(R.string.hearing_instruction);

        return view;
    }

    private void endTest(){
        Record record = fragmentInteraction.getRecord();
        record.setHearingRight(hearingTest.getPureToneAverageInterpretation("Right"));
        record.setHearingLeft(hearingTest.getPureToneAverageInterpretation("Left"));

        stopTest();
    }

    private void stopTest(){
        hearingTest.setIsNotRunning();
        for(int i = 0; i<threads.size(); i++){
            threads.get(i).interrupt();
        }
    }

    //For testing purposes only
    public void endTestShortCut(){
        Record record = fragmentInteraction.getRecord();
        record.setHearingRight("Mild Hearing Loss");
        record.setHearingLeft("Moderately-Severe Hearing Loss");

        stopTest();
        fragmentInteraction.doneFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
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
