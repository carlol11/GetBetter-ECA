package com.geebeelicious.geebeelicious;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.adapters.PatientsAdapter;
import com.geebeelicious.geebeelicious.database.DataAdapter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

import models.consultation.Patient;

public class PatientListActivity extends ActionBarActivity {

    private ArrayList<Patient> patients = null;
    private Patient chosenPatient = null;
    private PatientsAdapter patientsAdapter;
    private EditText inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);

        DataAdapter getBetterDb = new DataAdapter(PatientListActivity.this);
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        patients = new ArrayList<>();
        patients = getBetterDb.getPatientsFromSchool(getSchoolPreferences());
        getBetterDb.closeDatabase();

        inputSearch = (EditText)findViewById(R.id.search_input);

        patientsAdapter = new PatientsAdapter(PatientListActivity.this, patients);
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

                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(inputSearch.getWindowToken(), 0);
            }
        });

        Button selectPatientButton = (Button)findViewById(R.id.selectPatientButton);
        selectPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientListActivity.this, MonitoringConsultationChoice.class);
                intent.putExtra("patient", chosenPatient);
                startActivity(intent);
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                patientsAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                patientsAdapter.filter(s.toString());
            }
        });

        //TODO: [NOT URGENT] Handle creating new patient

    }

    private int getSchoolPreferences(){
        int schoolID = 0; //default schoolID
        byte[] byteArray = new byte[4];
        try{
            FileInputStream fis = openFileInput("SchoolIDPreferences");
            fis.read(byteArray, 0, 4);
            fis.close();
        } catch(IOException e){
        }

        ByteBuffer b = ByteBuffer.wrap(byteArray);
        schoolID = b.getInt();

        return schoolID;
    }

}
