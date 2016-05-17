package com.geebeelicious.geebeelicious;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.interfaces.MonitoringFragmentInteraction;
import com.geebeelicious.geebeelicious.fragments.ColorVisionFragment;
import com.geebeelicious.geebeelicious.fragments.FineMotorFragment;
import com.geebeelicious.geebeelicious.fragments.GrossMotorFragment;
import com.geebeelicious.geebeelicious.fragments.HearingMainFragment;
import com.geebeelicious.geebeelicious.fragments.VisualAcuityFragment;

import models.consultation.Patient;
import models.monitoring.Record;

/**
 * Created by MG.
 * The MonitoringMainActivity serves as the main activity for all the monitoring activities
 * Each test are executed through this activity
 */

public class MonitoringMainActivity extends ActionBarActivity implements MonitoringFragmentInteraction {
    private final static String TAG = "MonitoringMainActivity";

    private Record record;

    private TextView ECAText;
    private TextView resultsText;

    private String[] fragments;
    private int currentFragmentIndex = 0;
    private FragmentManager fragmentManager;
    private Patient patient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_main);

        Bundle patientRecord = getIntent().getExtras();
        record = new Record();
        record.setDateCreated(patientRecord.getString("currentDate"));
        patient = patientRecord.getParcelable("patient");

        ECAText = (TextView) findViewById(R.id.placeholderECAText);
        resultsText = (TextView) findViewById(R.id.placeholderResults);

        //so that the fragments can be dynamically initialized
        fragments = new String[]{ //does not include the initial fragment
                VisualAcuityFragment.class.getName(),
                ColorVisionFragment.class.getName(),
                HearingMainFragment.class.getName(),
                GrossMotorFragment.class.getName(),
                FineMotorFragment.class.getName(),
        };

        fragmentManager = getSupportFragmentManager();
    }

    public Record getRecord(){
        return record;
    }

    @Override
    public void setInstructions(String instructions) {
        ECAText.setText(instructions);
    }

    @Override
    public void setResults(String results) {
        resultsText.append("\n" + results);
    }

    @Override
    public void doneFragment(){
        if(currentFragmentIndex == fragments.length){
            //TODO: send to database
        } else {

            try {
                Fragment newFragment = (Fragment) Class.forName(fragments[currentFragmentIndex]).newInstance();

                /*******
                 * TODO: [Testing Code] Remove this if no longer testing.
                 * this is for the shortcut for the hearing fragment
                 */
                final ImageView placeholderECA = (ImageView)findViewById(R.id.placeholderECA);
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


                /*
                 *
                 *  Testing code ends here
                 */




                FragmentTransaction transaction= fragmentManager.beginTransaction();
                transaction.replace(R.id.monitoringFragmentContainer, newFragment, fragments[currentFragmentIndex]);
                transaction.commit();

                currentFragmentIndex++;




            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                Log.e(TAG, "Error in initializing the fragment", e);
            }
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
}
