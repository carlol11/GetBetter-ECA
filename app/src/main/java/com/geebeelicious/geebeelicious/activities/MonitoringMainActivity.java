package com.geebeelicious.geebeelicious.activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.database.DatabaseAdapter;
import com.geebeelicious.geebeelicious.fragments.ColorVisionFragment;
import com.geebeelicious.geebeelicious.fragments.MonitoringFragment;
import com.geebeelicious.geebeelicious.fragments.PatientPictureFragment;
import com.geebeelicious.geebeelicious.fragments.VaccinationFragment;
import com.geebeelicious.geebeelicious.fragments.VisualAcuityFragment;
import com.geebeelicious.geebeelicious.interfaces.ECAActivity;
import com.geebeelicious.geebeelicious.interfaces.OnMonitoringFragmentInteractionListener;
import com.geebeelicious.geebeelicious.fragments.FineMotorFragment;
import com.geebeelicious.geebeelicious.fragments.GrossMotorFragment;
import com.geebeelicious.geebeelicious.fragments.HearingMainFragment;

import com.geebeelicious.geebeelicious.models.consultation.Patient;
import com.geebeelicious.geebeelicious.models.monitoring.Record;

import java.sql.SQLException;

/**
 * Created by MG.
 * The MonitoringMainActivity serves as the main activity for all the monitoring activities
 * Each test are executed through this activity
 */

public class MonitoringMainActivity extends ECAActivity implements OnMonitoringFragmentInteractionListener, GrossMotorFragment.OnFragmentInteractionListener{
    private final static String TAG = "MonitoringMainActivity";
    private Record record;

    private TextView ECAText;
    private TextView resultsText;
    private Button NAButton;

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
        NAButton = (Button) findViewById(R.id.NAButton);

        //so that the fragments can be dynamically initialized
        fragments = new String[]{ //does not include the initial fragment
                MonitoringFragment.class.getName(),
                PatientPictureFragment.class.getName(),
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
        ecaFragment.sendToECAToSPeak(resID);
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
            finish();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ECAText.setText("Placeholder for Instructions");
                resultsText.setText("Placeholder for Results");
            }
        });
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
                replaceFragment(oldFragment);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Log.e(TAG, "Error in initializing the fragment", e);
        }
    }

    private void replaceFragment(final Fragment fragment){
        shortcutForHearingfragment(fragment); //this is only used for testing

        //onclick for NAButton for GrossMotor
        if(fragment instanceof GrossMotorFragment){
            Button saveButton = (Button) findViewById(R.id.saveButton);
            final EditText remarkText = (EditText) findViewById(R.id.remarkText);

            NAButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GrossMotorFragment)fragment).onNAButtonClick();
                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String remark = remarkText.getText().toString();
                    record.setGrossMotorRemark(remark);
                    setResults("Remarks:" + remark);
                    ((GrossMotorFragment)fragment).onRemarkSaveButtonClicked();
                }
            });

            remarkText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus){
                        hideKeyboard(v);
                    }
                }
            });
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.monitoringFragmentContainer, fragment, fragments[currentFragmentIndex]);
        transaction.commit();
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void shortcutForHearingfragment(Fragment newFragment) {
        /*******
         * TODO: [Testing Code] Remove this if no longer testing.
         * this is for the shortcut for the hearing fragment
         */
        final LinearLayout ecaLayout = (LinearLayout)findViewById(R.id.linearLayoutECA);
        Fragment hearingFragment = fragmentManager.findFragmentByTag(HearingMainFragment.class.getName());

        if(newFragment instanceof HearingMainFragment){
            ecaLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HearingMainFragment hearingFragment = (HearingMainFragment) fragmentManager.findFragmentByTag(HearingMainFragment.class.getName());
                    hearingFragment.endTestShortCut();
                }
            });
        } else if(hearingFragment != null){
            ecaLayout.setClickable(false);
        }
    }

    @Override
    public void onShowNAButton() {
        NAButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideNAButton() {
        NAButton.setVisibility(View.GONE);
    }

    @Override
    public void onShowRemarkLayout() {
        RelativeLayout remarkLayout = (RelativeLayout) findViewById(R.id.remarkLayout);
        remarkLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideRemarkLayout() {
        RelativeLayout remarkLayout = (RelativeLayout) findViewById(R.id.remarkLayout);
        remarkLayout.setVisibility(View.GONE);
    }
}
