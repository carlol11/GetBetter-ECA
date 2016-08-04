package com.geebeelicious.geebeelicious.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.geebeelicious.geebeelicious.R;

import edu.usc.ict.vhmobile.VHMobileLib;
import edu.usc.ict.vhmobile.VHMobileMain;
import edu.usc.ict.vhmobile.VHMobileSurfaceView;

/**
 * Created by MG.
 * The ECAFragment serves as the fragment that contains the
 * ECA. This fragment uses VHMobile library to implement the ECA
 */
public class ECAFragment extends Fragment {

    private static final String TAG = "ECAFragment";

    private Activity activity;
    private String ecaString;

    protected VHMobileMain vhmain = null;
    protected VHMobileSurfaceView _VHview = null;
    
    public enum Emotion {
        HAPPY, CONCERN
    }

    public ECAFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_eca, container, false);
        final Button replayButton = (Button) view.findViewById(R.id.replayButton);

        ViewTreeObserver vto = view.getViewTreeObserver();

        //sets the size of the replayButton depending on the size of the fragment
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int viewHeight = view.getHeight() / 10;
                ViewGroup.LayoutParams params = replayButton.getLayoutParams();
                params.width = viewHeight;
                params.height = viewHeight;

                replayButton.setLayoutParams(params);
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ecaString != null){
                    sendToECAToSpeak(ecaString);
                }
            }
        });


        //ECA integration
        VHMobileMain.setupVHMobile();

        Log.d(TAG,  "The onCreate() event");

        vhmain = new VHMobileMain(activity);
        vhmain.init();
        _VHview = (VHMobileSurfaceView)view.findViewById(R.id.vhview);

        return view;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public void onStart()
    {
        Log.d(TAG,  "The onStart() event");
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
         * The activity must call the GL surface view's
         * onResume() on activity onResume().
         */
        if (_VHview != null)
            _VHview.onResume();
        Log.d(TAG, "The onResume() event");

    }

    @Override
    public void onPause() {
        super.onPause();
        /*
         * The activity must call the GL surface view's
         * onResume() on activity onResume().
         */
        if (_VHview != null)
            _VHview.onPause();
        Log.d(TAG, "The onPause() event");

    }

    /** Called when the activity is no longer visible. */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "The onStop() event");
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "The onDestroy() event");
    }

    public void sendToECAToSpeak(String sentence){
        ecaString = sentence;

        Log.d(TAG, "ECA speaks: " + sentence);
        VHMobileLib.executeSB("saySomething(characterName, \""+ sentence+"\")");
    }

    public void sendToECAToSPeak(int resID){
        String sentence = getString(resID);
        sendToECAToSpeak(sentence);
    }

    public void sendToECAToEmote(Emotion emotion, int i){
        switch (emotion){
            case HAPPY:
                VHMobileLib.executeSB("setToHappy(characterName, "+i+")");
                break;
            case CONCERN:
                VHMobileLib.executeSB("setToConcern(characterName, "+i+")");
                break;
        }
    }
}
