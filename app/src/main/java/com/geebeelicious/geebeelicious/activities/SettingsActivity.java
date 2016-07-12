package com.geebeelicious.geebeelicious.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.adapters.EyeChartsAdapter;
import com.geebeelicious.geebeelicious.adapters.SchoolsAdapter;
import com.geebeelicious.geebeelicious.database.DatabaseAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

import com.geebeelicious.geebeelicious.models.consultation.School;

import org.w3c.dom.Text;

/**
 * Created by Kate.
 * The SettingsActivity serves as the activity containing
 * the various settings and preferences for the app.
 * These include school settings and hearing calibration.
 */

public class SettingsActivity extends ActionBarActivity {

    private School chosenSchool = null;
    private int chosenVisualAcuityChart;
    private ArrayList<School> schools;

    private Typeface chalkFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        chalkFont = Typeface.createFromAsset(getAssets(), "fonts/DJBChalkItUp.ttf");

        TextView schoolNameTV = (TextView)findViewById(R.id.schoolnameTV);
        schoolNameTV.setTypeface(chalkFont);

        TextView chartNameTV = (TextView)findViewById(R.id.visualacuitychartNameTV);
        chartNameTV.setTypeface(chalkFont);

        TextView calibrateHearingTV = (TextView)findViewById(R.id.calibrateHearingTV);
        calibrateHearingTV.setTypeface(chalkFont);

        addChooseSchoolSetting();
        addVisualAcuityChartSetting();
        addCalibrationSetting();

        Button saveButton = (Button)findViewById(R.id.saveSettingsButton);
        saveButton.setTypeface(chalkFont);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSchool();
                saveVisualAcuityChart();
                finish();
            }
        });
    }

    //Adds option to select a school to the Settings screen
    private void addChooseSchoolSetting(){
        DatabaseAdapter getBetterDb = new DatabaseAdapter(SettingsActivity.this);
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        schools = getBetterDb.getAllSchools();
        getBetterDb.closeDatabase();

        SchoolsAdapter schoolsAdapter = new SchoolsAdapter(SettingsActivity.this, schools, chalkFont);
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
    }

    //Saves schoolID of preferred school to device storage
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

    //Add option to select visual acuity chart to be used in test
    private void addVisualAcuityChartSetting(){
        ArrayList<String> chartNames = new ArrayList<>();
        chartNames.add("Snellen Eye Chart");
        chartNames.add("Tumbling E Eye Chart");
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SettingsActivity.this, R.array.visualacuity_chart_names, R.layout.item_eyechart_list);w

        EyeChartsAdapter adapter = new EyeChartsAdapter(this, chartNames, chalkFont);
        adapter.setDropDownViewResource(R.layout.item_eyechart_list);
        Spinner chartSpinner = (Spinner)findViewById(R.id.visualacuitychartSpinner);
        chartSpinner.setAdapter(adapter);
        chartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chosenVisualAcuityChart = parent.getSelectedItemPosition();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chosenVisualAcuityChart = 0;
            }
        });
    }

    //Save visual acuity chart preference
    private void saveVisualAcuityChart(){
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(chosenVisualAcuityChart);
        byte[] byteArray = b.array();

        try{
            FileOutputStream fos = openFileOutput("VisualAcuityChartPreferences", Context.MODE_PRIVATE);
            try{
                fos.write(byteArray);
                fos.close();
            } catch (IOException ioe){

            }
        } catch (FileNotFoundException fe){

        }
    }

    //Adds option to calibrate hearing test
    private void addCalibrationSetting(){
        Button calibrateButton = (Button)findViewById(R.id.calibrateHearingTestButton);
        calibrateButton.setTypeface(chalkFont);
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
