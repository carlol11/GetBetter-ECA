package com.geebeelicious.geebeelicious.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import com.geebeelicious.geebeelicious.interfaces.ECAActivity;
import com.geebeelicious.geebeelicious.models.consultation.ConsultationHelper;
import com.geebeelicious.geebeelicious.models.consultation.Patient;

/**
 * Created by Mary Grace Malana.
 * The ConsultationActivity class is the main activity for consultation.
 * This covers consultation with patients and the succeeding generation of HPI.
 * It allows the user to view questions and answer with Yes or No inputs.
 */

public class ConsultationActivity extends ECAActivity{
    private TextView ECAText;
    private ConsultationHelper consultationHelper;
    private final static String TAG = "ConsultationActivity";
    private boolean isOnGoingFlag;
    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);

        Button yesButton = (Button) findViewById(R.id.YesButton);
        Button noButton = (Button) findViewById(R.id.NoButton);

        patient = getIntent().getExtras().getParcelable("patient");
        String dateConsultation = getIntent().getStringExtra("currentDate");

        isOnGoingFlag = true;
        consultationHelper = new ConsultationHelper(this, patient, dateConsultation);
        ECAText = (TextView) findViewById(R.id.placeholderECAText);

        integrateECA();

        setQuestion(consultationHelper.getFirstQuestion());

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!consultationHelper.isConsultationDone()){ //checks if consultation is still ongoing
                    onAnswer(true); //true because yes
                }
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!consultationHelper.isConsultationDone()){ //checks if consultation is still ongoing
                    onAnswer(false);  //false because no
                }
            }
        });
    }

    //Determines the course of action depending on user input (Yes or No)
    private void onAnswer(boolean isYes) {
        String nextQuestion = consultationHelper.getNextQuestion(isYes);
        if(nextQuestion == null) {
            doWhenConsultationDone();
        }
        else {
            setQuestion(nextQuestion);
        }
    }

    //Performed when consultation is done; Saves generated HPI to database if patient has complaints
    private synchronized void doWhenConsultationDone(){
        if(isOnGoingFlag){
            isOnGoingFlag = false;

            String hpi = consultationHelper.getHPI();
            TextView hpiTextView = (TextView) findViewById(R.id.hpiPlaceholder);

            Log.d(TAG, "HPI: " + hpi);
            consultationHelper.saveToDatabase(hpi); //closes the database after saving the hpi
            hpiTextView.setText(hpi);

            doneConsultation();
        }
    }

    private void doneConsultation() {
        CountDownTimer timer;
        LinearLayout hpiLayout = (LinearLayout) findViewById(R.id.hpiLayout);
        RelativeLayout choicesLayout = (RelativeLayout) findViewById(R.id.choicesLayout);

        timer = new CountDownTimer(6000, 6000) { //timer for the transition
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                finish();
            }
        };

        hpiLayout.setVisibility(View.VISIBLE);
        choicesLayout.setVisibility(View.GONE);

        ecaFragment.sendToECAToSPeak(R.string.consultation_end);
        timer.start();
    }

    private void setQuestion(String question){
        ECAText.setText(question);
        ecaFragment.sendToECAToSpeak(question);
    }
}
