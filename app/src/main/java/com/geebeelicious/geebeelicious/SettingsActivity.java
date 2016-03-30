package com.geebeelicious.geebeelicious;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.geebeelicious.geebeelicious.adapters.SchoolsAdapter;
import com.geebeelicious.geebeelicious.database.DataAdapter;
import com.geebeelicious.geebeelicious.tests.hearing.HearingCalibrationActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

import models.consultation.School;

public class SettingsActivity extends ActionBarActivity {

    private School chosenSchool = null;
    private ArrayList<School> schools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        addChooseSchoolSetting();
        addCalibrationSetting();
    }

    private void addChooseSchoolSetting(){
        DataAdapter getBetterDb = new DataAdapter(SettingsActivity.this);
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        schools = getBetterDb.getAllSchools();
        getBetterDb.closeDatabase();

        SchoolsAdapter schoolsAdapter = new SchoolsAdapter(SettingsActivity.this, schools);
        schoolsAdapter.setDropDownViewResource(R.layout.item_school_list);
        Spinner schoolSpinner = (Spinner)findViewById(R.id.schoolSpinner);
        schoolSpinner.setAdapter(schoolsAdapter);
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenSchool = schools.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chosenSchool = schools.get(1);
            }
        });

        Button saveButton = (Button)findViewById(R.id.saveSettingsButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSchool();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
        //TODO: [URGENT] Move calibration test to settings
        //TODO: [URGENT] Set onBackPressed()
    }

    private void saveSchool(){
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(chosenSchool.getSchoolId());
        byte[] byteArray = b.array();

        try{
            FileOutputStream fos = openFileOutput("SchoolIDPreferences", Context.MODE_PRIVATE);
            try{
                fos.write(byteArray);
                fos.close();
            } catch(IOException ioe){

            }
        } catch(FileNotFoundException fe){

        }
    }

    private void addCalibrationSetting(){
        Button calibrateButton = (Button)findViewById(R.id.calibrateHearingTestButton);
        calibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, HearingCalibrationActivity.class);
                finish();
                startActivity(intent);
            }
        });

    }

}
