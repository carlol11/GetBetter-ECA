package com.geebeelicious.geebeelicious.consultation;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.MonitoringConsultationChoice;
import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.database.DataAdapter;

import models.consultation.ConsultationHelper;
import models.consultation.Patient;

public class ConsultationActivity extends ActionBarActivity {
    private TextView ECAText;
    private ConsultationHelper consultationHelper;
    private final static String TAG = "ConsultationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);

        Button yesButton = (Button) findViewById(R.id.YesButton);
        Button noButton = (Button) findViewById(R.id.NoButton);

        Patient patient = getIntent().getExtras().getParcelable("patient");
        String dateConsultation = getIntent().getStringExtra("currentDate");

        consultationHelper = new ConsultationHelper(this, patient, dateConsultation);
        ECAText = (TextView) findViewById(R.id.placeholderECAText);

        ECAText.setText(consultationHelper.getFirstQuestion());

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

    //TODO: [NOT URGENT] handle multiple clicks. mageerror ata sa
    private void onAnswer (boolean isYes){
        String nextQuestion = consultationHelper.getNextQuestion(isYes);
        if(nextQuestion == null) {
            doWhenConsultationDone();
        }
        else {
            ECAText.setText(nextQuestion);
        }
    }

    //TODO: Put intent stuff here. or save to database
    private void doWhenConsultationDone(){
        String hpi = consultationHelper.getHPI();
        Log.d(TAG, "HPI: " + hpi);
        consultationHelper.saveToDatabase(hpi);
        finish();
        startActivity(new Intent(this, MonitoringConsultationChoice.class));
    }
}
