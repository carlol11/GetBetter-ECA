package com.geebeelicious.geebeelicious;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.geebeelicious.geebeelicious.consultation.ConsultationActivity;
import com.geebeelicious.geebeelicious.tests.hearing.HearingCalibrationActivity;
import com.geebeelicious.geebeelicious.tests.visualacuity.VisualAcuityMainActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import models.consultation.Patient;

public class MonitoringConsultationChoice extends ActionBarActivity {
    private DateFormat dateFormat;
    private Patient chosenPatient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_consultation_choice);
        Button mButton = (Button)findViewById(R.id.monitoringButton);
        Button cButton = (Button)findViewById(R.id.consultationButton);
        //final Patient patient = new Patient(1, "Kristoff", "McHamm", "2/7/2008", 0, 1, 1);

        //TODO: [URGENT, DB] Use the patientID from Intent to get the patient from the database
        ArrayList<Patient> patients = new ArrayList<>();
        patients.add(new Patient(1, "Sammy", "McHam IV", "02/07/2008", 0, 1, 0));
        patients.add(new Patient(2, "Electra", "Woman", "03/04/2010", 1, 1, 0));
        int patientID = this.getIntent().getIntExtra("patientID", 0);
        for(Patient p : patients){
            if(p.getPatientID() == patientID){
                chosenPatient = p;
                break;
            }
        }

        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle record = new Bundle();
                record.putParcelable("patient", chosenPatient);
                record.putString("currentDate", dateFormat.format(new Date()));
                Intent intent = new Intent(MonitoringConsultationChoice.this, VisualAcuityMainActivity.class);
                intent.putExtras(record);
                startActivity(intent);
            }
        });

        cButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonitoringConsultationChoice.this, ConsultationActivity.class);
                intent.putExtra("currentDate", dateFormat.format(new Date()));
                intent.putExtra("patient", chosenPatient);
                startActivity(intent);
            }
        });
    }

}
