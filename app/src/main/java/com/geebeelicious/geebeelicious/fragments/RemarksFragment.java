package com.geebeelicious.geebeelicious.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RemarksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class RemarksFragment extends Fragment {
    private final static String TAG = "RemarksFragment";

    private View view;

    private OnFragmentInteractionListener mListener;
    private MediaRecorder mRecorder;
    private String mFileName;
    private MediaPlayer mPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_remarks, container, false);

        final ImageButton recordButton = (ImageButton) view.findViewById(R.id.recordButton);
        final ImageButton playButton = (ImageButton) view.findViewById(R.id.playButton);
        final EditText remarkText = (EditText) view.findViewById(R.id.remarkText);
        final RelativeLayout remarkLayout = (RelativeLayout) view.findViewById(R.id.remarkLayout);

        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        Button yesButton = (Button) view.findViewById(R.id.yesButton);
        final Button noButton = (Button) view.findViewById(R.id.noButton);


        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        replaceFonts((ViewGroup) view);

        recordButton.setOnClickListener(new View.OnClickListener() {
            boolean mStartRecording = true;
            LinearLayout recordingProgressBar = (LinearLayout) view.findViewById(R.id.recordingProgressLayout);

            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);

                if (mStartRecording) {
                    recordingProgressBar.setVisibility(View.VISIBLE);
                    remarkText.setVisibility(View.GONE);
                    playButton.setVisibility(View.GONE);
                } else {
                    recordingProgressBar.setVisibility(View.GONE);
                    remarkText.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.VISIBLE);
                }
                mStartRecording = !mStartRecording;

            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            boolean mStartPlaying = true;

            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    playButton.setImageResource(R.drawable.btn_stop);
                } else {
                    playButton.setImageResource(R.drawable.btn_play);
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout remarkQuestionLayout = (RelativeLayout) view.findViewById(R.id.remarksQuestionLayout);

                remarkLayout.setVisibility(View.VISIBLE);
                remarkQuestionLayout.setVisibility(View.GONE);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDoneRemarks();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] remarkAudio = null;

                try {
                    InputStream is = new BufferedInputStream(new FileInputStream(mFileName));

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    while (is.available() > 0) {
                        bos.write(is.read());
                    }

                    remarkAudio = bos.toByteArray();

                    if (remarkAudio.length == 0){
                        remarkAudio = null;
                    }


                    File file = new File(mFileName);
                    file.delete();
                } catch (IOException e) {
                    Log.e(TAG, "File error", e);
                }

                mListener.onDoneRemarks(remarkText.getText().toString(), remarkAudio);
            }
        });

        remarkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });
        
        mListener.setRemarksQuestion();

        return view;
    }

    public void setRemarkQuestion(int question){
        TextView remarkQuestion = (TextView) view.findViewById(R.id.remarksQuestion);
        remarkQuestion.setText(question);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void replaceFonts(ViewGroup viewTree)
    {
        View child;
        Typeface chalkFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/DJBChalkItUp.ttf");

        for(int i = 0; i < viewTree.getChildCount(); ++i)
        {
            child = viewTree.getChildAt(i);
            if(child instanceof ViewGroup)
            {
                // recursive call
                replaceFonts((ViewGroup)child);
            }
            else if(child instanceof TextView)
            {
                // base case
                ((TextView) child).setTypeface(chalkFont);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onDoneRemarks(String remarkString, byte[] remarkAudio);
        void onDoneRemarks();

        /**
         * Sets the remarkQuestion that is specific for the implementing class
         */
        void setRemarksQuestion();
    }

    private void onRecord(boolean start) {
        if (start) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        mRecorder.setOutputFile(mFileName);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed", e);
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed", e);
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)((Activity)mListener).getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
