package com.geebeelicious.geebeelicious.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.activities.MonitoringConsultationChoice;
import com.geebeelicious.geebeelicious.R;

import com.geebeelicious.geebeelicious.fragments.ECAFragment;
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

            if(consultationHelper.isTherePatientComplaints()) {
                String hpi = consultationHelper.getHPI();
                Log.d(TAG, "HPI: " + hpi);
                consultationHelper.saveToDatabase(hpi); //closes the database after saving the hpi
            } else { //TODO: [UI PART] put the condition here if no complaints
                Log.d(TAG, "No chief complaint found ");
            }
            Intent intent = new Intent(this, MonitoringConsultationChoice.class);
            intent.putExtra("patient", patient);
            startActivity(intent);
            finish();
        }
    }

    private void setQuestion(String question){
        ECAText.setText(question);
        ecaFragment.sendToECAToSpeak(question);
    }
}
