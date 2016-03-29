package com.geebeelicious.geebeelicious;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.adapters.PatientsAdapter;
import com.geebeelicious.geebeelicious.database.DataAdapter;
import com.geebeelicious.geebeelicious.expertsystem.ExpertSystem;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;

import models.consultation.Patient;

public class PatientListActivity extends ActionBarActivity {

    private DataAdapter getBetterDb;
    private ArrayList<Patient> patients = null;
    private Patient chosenPatient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        initializeDatabase();
        //patients = getBetterDb.getAllPatients();

        //TODO [URGENT, DB]: Replace with database call to get patient list
        patients = new ArrayList<>();
        patients.add(new Patient(1, "Sammy", "McHam IV", "02/07/2008", 0, 1, 0));
        patients.add(new Patient(2, "Electra", "Woman", "03/04/2010", 1, 1, 0));

        PatientsAdapter patientsAdapter = new PatientsAdapter(PatientListActivity.this, patients);
        ListView patientListView = (ListView)findViewById(R.id.patientListView);
        patientListView.setAdapter(patientsAdapter);

        patientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: [NOT URGENT, UI PART] Make separate text view for each attribute instead of concatenating string
                chosenPatient = patients.get(position);
                String patientInfo = "";
                patientInfo = "First Name: " + chosenPatient.getFirstName() +
                        "\nLast Name: " + chosenPatient.getLastName() +
                        "\nBirthdate: " + chosenPatient.getBirthday() +
                        "\nGender: " + chosenPatient.getGenderString();
                TextView patientInfoView = (TextView) findViewById(R.id.patientDetailsTV);
                patientInfoView.setText(patientInfo);
            }
        });

        Button selectPatientButton = (Button)findViewById(R.id.selectPatientButton);
        selectPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientListActivity.this, MonitoringConsultationChoice.class);
                intent.putExtra("patientID", chosenPatient.getPatientID());
                startActivity(intent);
            }
        });

        //TODO: [NOT URGENT] Handle creating new patient

    }

    private void initializeDatabase () {
        getBetterDb = new DataAdapter(PatientListActivity.this);

        try {
            getBetterDb.createDatabase();
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
