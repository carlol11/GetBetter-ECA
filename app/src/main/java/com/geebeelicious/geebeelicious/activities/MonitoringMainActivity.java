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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.database.DatabaseAdapter;
import com.geebeelicious.geebeelicious.fragments.ECAFragment;
import com.geebeelicious.geebeelicious.fragments.MonitoringFragment;
import com.geebeelicious.geebeelicious.fragments.VaccinationFragment;
import com.geebeelicious.geebeelicious.interfaces.OnMonitoringFragmentInteractionListener;
import com.geebeelicious.geebeelicious.fragments.ColorVisionFragment;
import com.geebeelicious.geebeelicious.fragments.FineMotorFragment;
import com.geebeelicious.geebeelicious.fragments.GrossMotorFragment;
import com.geebeelicious.geebeelicious.fragments.HearingMainFragment;
import com.geebeelicious.geebeelicious.fragments.VisualAcuityFragment;

import com.geebeelicious.geebeelicious.models.consultation.Patient;
import com.geebeelicious.geebeelicious.models.monitoring.Record;

import java.sql.SQLException;

/**
 * Created by MG.
 * The MonitoringMainActivity serves as the main activity for all the monitoring activities
 * Each test are executed through this activity
 */

public class MonitoringMainActivity extends ActionBarActivity implements OnMonitoringFragmentInteractionListener, ECAFragment.OnFragmentInteractionListener {
    private final static String TAG = "MonitoringMainActivity";

    private ECAFragment ecaFragment;

    private Record record;

    private TextView ECAText;
    private TextView resultsText;

    private String[] fragments;
    private int currentFragmentIndex;
    private FragmentManager fragmentManager;
    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_main);

        ECAText = (TextView) findViewById(R.id.placeholderECAText);
        resultsText = (TextView) findViewById(R.id.placeholderResults);

        //so that the fragments can be dynamically initialized
        fragments = new String[]{ //does not include the initial fragment
                MonitoringFragment.class.getName(),
                VaccinationFragment.class.getName(),
                VisualAcuityFragment.class.getName(),
                ColorVisionFragment.class.getName(),
                HearingMainFragment.class.getName(),
                GrossMotorFragment.class.getName(),
                FineMotorFragment.class.getName(),
        };

        fragmentManager = getSupportFragmentManager();


        if (savedInstanceState == null) { //if first launch
            Bundle patientRecord = getIntent().getExtras();

            currentFragmentIndex = 0;

            patient = patientRecord.getParcelable("patient");
            record = new Record();
            record.setDateCreated(patientRecord.getString("currentDate"));
            record.setPatient_id(patient.getPatientID());
        } else {
            currentFragmentIndex = savedInstanceState.getInt("fragmentIndex");
            patient = savedInstanceState.getParcelable("patient");
            record = savedInstanceState.getParcelable("record");
        }

        integrateECA();

        initializeOldFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("record", record);
        outState.putParcelable("patient", patient);
        outState.putInt("fragmentIndex", currentFragmentIndex);
    }

    @Override
    public Record getRecord(){
        return record;
    }

    @Override
    public void setInstructions(String instructions) {
        ECAText.setText(instructions);
        ecaFragment.sendToECAToSpeak(instructions);
    }

    @Override
    public void setInstructions(int resID) {
        ECAText.setText(resID);
        ecaFragment.sendToECAToSpeak(getString(resID));
    }

    @Override
    public void setResults(String results) {
        resultsText.append("\n" + results);
    }

    @Override
    public void doneFragment(){
        if(currentFragmentIndex + 1 >= fragments.length){
            DatabaseAdapter db = new DatabaseAdapter(this);

            try {
                db.openDatabaseForRead();
                record.printRecord();

                db.insertRecord(record);
            } catch (SQLException e) {
                Log.e(TAG, "Database error", e);
            }

            Intent intent = new Intent(this, MonitoringConsultationChoice.class);
            finish();
            startActivity(intent);
        } else {
            clearTextViews();
            nextFragment();
        }
    }

    @Override
    public int getIntResults(String result){
        switch(result){
            case "Pass":
                return 0;
            case "Fail":
                return 1;
            default:
                return 2;
        }
    }

    private void clearTextViews() {
        ECAText.setText("Placeholder for Instructions");
        resultsText.setText("Placeholder for Results");
    }

    private void nextFragment(){
        try {
            currentFragmentIndex++;
            Fragment newFragment = (Fragment) Class.forName(fragments[currentFragmentIndex]).newInstance();
            replaceFragment(newFragment);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Log.e(TAG, "Error in initializing the fragment", e);
        }
    }

    private void initializeOldFragment() {
        Fragment oldFragment = getSupportFragmentManager().findFragmentByTag(fragments[currentFragmentIndex]);
        try {
            if(oldFragment == null) {
                oldFragment = (Fragment) Class.forName(fragments[0]).newInstance();
            }
            replaceFragment(oldFragment);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Log.e(TAG, "Error in initializing the fragment", e);
        }
    }

    private void replaceFragment(Fragment fragment){
        shortcutForHearingfragment(fragment); //this is only used for testing

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.monitoringFragmentContainer, fragment, fragments[currentFragmentIndex]);
        transaction.commit();
    }

    private void shortcutForHearingfragment(Fragment newFragment) {
        /*******
         * TODO: [Testing Code] Remove this if no longer testing.
         * this is for the shortcut for the hearing fragment
         */
        final FrameLayout placeholderECA = (FrameLayout)findViewById(R.id.placeholderECA);
        Fragment hearingFragment = fragmentManager.findFragmentByTag(HearingMainFragment.class.getName());

        if(newFragment instanceof HearingMainFragment){
            placeholderECA.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HearingMainFragment hearingFragment = (HearingMainFragment) fragmentManager.findFragmentByTag(HearingMainFragment.class.getName());
                    hearingFragment.endTestShortCut();
                }
            });
        } else if(hearingFragment != null){
            placeholderECA.setClickable(false);
        }
    }

    private void integrateECA() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ecaFragment = (ECAFragment) fragmentManager.findFragmentByTag(ECAFragment.class.getName());
        if(ecaFragment == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            ecaFragment = new ECAFragment();
            transaction.add(R.id.placeholderECA, ecaFragment, ECAFragment.class.getName());
            transaction.commit();

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
