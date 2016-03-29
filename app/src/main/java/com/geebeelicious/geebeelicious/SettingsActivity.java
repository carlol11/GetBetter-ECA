package com.geebeelicious.geebeelicious;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.geebeelicious.geebeelicious.adapters.SchoolsAdapter;

import java.util.ArrayList;

import models.consultation.School;

public class SettingsActivity extends ActionBarActivity {

    private School chosenSchool = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //TODO: [URGENT, DB] Get list of schools from DB
        final ArrayList<School> schools = new ArrayList<School>();
        schools.add(new School(1, "Hammy School of the Arts", "Los Angeles"));
        schools.add(new School(2, "East High School", "Albuquerque"));

        SchoolsAdapter schoolsAdapter = new SchoolsAdapter(SettingsActivity.this, schools);
        Spinner schoolSpinner = (Spinner)findViewById(R.id.schoolSpinner);
        schoolSpinner.setAdapter(schoolsAdapter);
        //TODO: [URGENT, DB] Set school to whatever is selected when save button is clicked
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenSchool = schools.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}
