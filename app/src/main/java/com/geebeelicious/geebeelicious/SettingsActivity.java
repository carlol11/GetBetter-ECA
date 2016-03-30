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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Set;

import models.consultation.School;

public class SettingsActivity extends ActionBarActivity {

    private School chosenSchool = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Hereee");
        setContentView(R.layout.activity_settings);

        addChooseSchoolSetting();

    }

    private void addChooseSchoolSetting(){
        System.out.println("Here");
        //TODO: [URGENT, DB] Get list of schools from DB
        final ArrayList<School> schools = new ArrayList<School>();
        schools.add(new School(1, "Hammy School of the Arts", "Los Angeles"));
        schools.add(new School(2, "East High School", "Albuquerque"));

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

}
