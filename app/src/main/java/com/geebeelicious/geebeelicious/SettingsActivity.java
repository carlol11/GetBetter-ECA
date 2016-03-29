package com.geebeelicious.geebeelicious;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import models.consultation.School;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //TODO: [URGENT, DB] Get list of schools from DB
        ArrayList<School> schools = new ArrayList<School>();
        schools.add(new School(1, "Hammy School of the Arts", "Los Angeles"));
        schools.add(new School(2, "East High School", "Albuquerque"));

        Spinner schoolSpinner = (Spinner)findViewById(R.id.schoolSpinner);
        //TODO: [URGENT, DB] Set school to whatever is selected when save button is clicked
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

}
