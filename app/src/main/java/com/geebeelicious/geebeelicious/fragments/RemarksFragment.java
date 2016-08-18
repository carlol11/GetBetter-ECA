package com.geebeelicious.geebeelicious.fragments;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.geebeelicious.geebeelicious.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
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

    private OnFragmentInteractionListener mListener;
    private MediaRecorder mRecorder;
    private String mFileName;
    private MediaPlayer mPlayer;

    public RemarksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_remarks, container, false);

        final Button recordButton = (Button) view.findViewById(R.id.recordButton);
        final Button playButton = (Button) view.findViewById(R.id.playButton);
        Button saveButton = (Button) view.findViewById(R.id.saveButton);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

        recordButton.setOnClickListener(new View.OnClickListener() {
            boolean mStartRecording = true;

            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    recordButton.setText("Stop");
                } else {
                    recordButton.setText("Record");
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
                    playButton.setText("Stop");
                } else {
                    playButton.setText("Play");
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText remarkText = (EditText) view.findViewById(R.id.remarkText);
                byte[] remarkAudio = null;

                try {
                InputStream is = new BufferedInputStream(new FileInputStream(mFileName));

                ByteArrayOutputStream bos = new ByteArrayOutputStream();


                    while (is.available() > 0) {
                        bos.write(is.read());
                    }
                    remarkAudio = bos.toByteArray();

                } catch (IOException e) {
                    Log.e(TAG, "File error", e);
                }
                mListener.onDoneRemarks(remarkText.getText().toString(), remarkAudio);
            }
        });

        return view;
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
}
