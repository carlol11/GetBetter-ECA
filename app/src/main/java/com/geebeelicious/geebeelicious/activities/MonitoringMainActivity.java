package com.geebeelicious.geebeelicious.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.database.DatabaseAdapter;
import com.geebeelicious.geebeelicious.fragments.ColorVisionFragment;
import com.geebeelicious.geebeelicious.fragments.ECAFragment;
import com.geebeelicious.geebeelicious.fragments.FineMotorFragment;
import com.geebeelicious.geebeelicious.fragments.GrossMotorFragment;
import com.geebeelicious.geebeelicious.fragments.HearingMainFragment;
import com.geebeelicious.geebeelicious.fragments.MonitoringFragment;
import com.geebeelicious.geebeelicious.fragments.PatientPictureFragment;
import com.geebeelicious.geebeelicious.fragments.RemarksFragment;
import com.geebeelicious.geebeelicious.fragments.VaccinationFragment;
import com.geebeelicious.geebeelicious.fragments.VisualAcuityFragment;
import com.geebeelicious.geebeelicious.interfaces.ECAActivity;
import com.geebeelicious.geebeelicious.interfaces.MonitoringTestFragment;
import com.geebeelicious.geebeelicious.interfaces.OnMonitoringFragmentInteractionListener;
import com.geebeelicious.geebeelicious.models.consultation.Patient;
import com.geebeelicious.geebeelicious.models.monitoring.Record;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MG.
 * The MonitoringMainActivity serves as the main activity for all the monitoring activities
 * Each test are executed through this activity
 */

public class MonitoringMainActivity extends ECAActivity implements OnMonitoringFragmentInteractionListener,
        RemarksFragment.OnFragmentInteractionListener{
    private final static String TAG = "MonitoringMainActivity";
    private Record record;

    private TextView ecaText;
    private TextView resultsText;
    private FrameLayout ecaFragmentLayout;
    private Button readyButton;
    private TextView ecaTransitionText;

    private String[] fragments;
    private int currentFragmentIndex;
    private FragmentManager fragmentManager;
    private Patient patient;

    private Typeface chalkFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_main);

        ecaText = (TextView) findViewById(R.id.placeholderECAText);
        resultsText = (TextView) findViewById(R.id.placeholderResults);
        ecaFragmentLayout = (FrameLayout) findViewById(R.id.placeholderECA);
        readyButton = (Button) findViewById(R.id.readyButton);
        ecaTransitionText = (TextView) findViewById(R.id.ecaTransitionTextView);

        chalkFont = Typeface.createFromAsset(getAssets(), "fonts/DJBChalkItUp.ttf");
        ecaText.setTypeface(chalkFont);
        resultsText.setTypeface(chalkFont);
        readyButton.setTypeface(chalkFont);
        ecaTransitionText.setTypeface(chalkFont);

        resultsText.setMovementMethod(new ScrollingMovementMethod());

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
                RemarksFragment.class.getName()
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
    public void onBackPressed() {
        Fragment currentFragment = fragmentManager.findFragmentByTag(fragments[currentFragmentIndex]);

        if (currentFragment instanceof GrossMotorFragment){
            ((GrossMotorFragment) currentFragment).onBackPressed();
        } else if (currentFragment instanceof HearingMainFragment) {
            ((HearingMainFragment) currentFragment).onBackPressed();
        }

        finish();
    }

    @Override
    public Record getRecord(){
        return record;
    }

    @Override
    public void setInstructions(String instructions) {
        ecaText.setText(instructions);
        ecaFragment.sendToECAToSpeak(instructions);
    }

    @Override
    public void setInstructions(int resID) {
        ecaText.setText(resID);
        ecaFragment.sendToECAToSPeak(resID);
    }

    @Override
    public void setResults(String results) {
        resultsText.append("\n" + results);
    }

    @Override
    public void doneFragment() { //gets called by the fragments when done
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment currentFragment = fragmentManager.findFragmentByTag(fragments[currentFragmentIndex]);

                if(currentFragmentIndex + 1 >= fragments.length){ //if last fragment
                    endActivity(currentFragment);
                } else {
                    clearTextViews();
                    try {
                        currentFragmentIndex++;
                        Fragment nextFragment = (Fragment) Class.forName(fragments[currentFragmentIndex]).newInstance();
                        if (currentFragment instanceof MonitoringTestFragment){ //if the current has intro
                            doTransitionWithResult((MonitoringTestFragment) currentFragment, nextFragment);
                        } else if (doesNextHasIntro(nextFragment)){ //if the next has intro
                            runTransition(100, "", nextFragment, true);
                        } else {
                            replaceFragment(nextFragment);
                        }

                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        Log.e(TAG, "Error in initializing the fragment", e);
                    }
                }
            }
        });
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

    @Override
    public int getAge() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date dateOfBirth = dateFormat.parse(patient.getBirthday());
            Calendar dob = Calendar.getInstance();
            dob.setTime(dateOfBirth);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR))
                age--;
            Log.d(TAG, "Patient age: " + age);
            return age;
        } catch (ParseException e) {
            Log.d(TAG, "Error in reading birthday", e);
        }
        return 0;
    }

    @Override
    public boolean isGirl() {
        return patient.getGender() == 1;
    }

    @Override
    public void showTransitionTextLayout() {
        ((View)ecaTransitionText.getParent()).setVisibility(View.VISIBLE);
    }

    @Override
    public void appendTransitionIntructions(String instructions) {
        instructions = " " + instructions + " " + getString(R.string.monitoring_ready);
        ecaTransitionText.append(instructions);
        ecaFragment.sendToECAToSpeak(ecaTransitionText.getText().toString());
    }

    private void clearTextViews() {
        ecaText.setText("");
        resultsText.setText("");
    }

    private void initializeOldFragment() {
        Fragment oldFragment = fragmentManager.findFragmentByTag(fragments[currentFragmentIndex]);
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

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.monitoringFragmentContainer, fragment, fragments[currentFragmentIndex]);
        if(!isFinishing()){
            transaction.commit();
        }
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

    private void maximizeToFullScreenECAFragment(){
        LinearLayout ecaLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutECA);

        View parent = (View)ecaLinearLayout.getParent();
        final int mToHeight = parent.getHeight();
        final int mToWidth = parent.getWidth();
        ecaFragmentLayout.setLayoutParams(new LinearLayout.LayoutParams(mToWidth, mToHeight));
    }

    private void maximizeToBigECAFragment(){
        LinearLayout ecaLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutECA);

        View parent = (View)ecaLinearLayout.getParent();
        final int mToHeight = parent.getHeight();
        ecaFragmentLayout.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.activity_eca_big),
                mToHeight));
    }

    private void minimizeECAFragment(){
        ecaFragmentLayout.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.activity_eca_small),
                getResources().getDimensionPixelSize(R.dimen.activity_eca_small)));
    }

    private void runTransition(int time, String ecaText, final Fragment nextFragment, final boolean isHappy) {
        CountDownTimer timer;

        if (doesNextHasIntro(nextFragment)) { //if next is a monitoring test and has intro
            maximizeToBigECAFragment();
        } else {
            maximizeToFullScreenECAFragment();
        }

        if(!isHappy){
            ecaFragment.sendToECAToEmote(ECAFragment.Emotion.CONCERN, 1);
        }

        ecaFragment.sendToECAToSpeak(ecaText);

        timer = new CountDownTimer(time, 10000) { //timer for the transition
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (!isHappy){
                    ecaFragment.sendToECAToEmote(ECAFragment.Emotion.HAPPY, 2);
                }

                if(nextFragment != null){
                    if (doesNextHasIntro(nextFragment)) { //if next is a monitoring test and has intro
                        final LinearLayout ecaTransitionTextLayout = (LinearLayout) findViewById(R.id.ecaTextTransitionLayout);
                        String ecaIntroText = getString(((MonitoringTestFragment) nextFragment).getIntro());

                        if (((MonitoringTestFragment)nextFragment).hasEarlyInstruction()){ //has an early instruction
                            ecaTransitionText.setText(ecaIntroText);
                            replaceFragment(nextFragment);
                            readyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                ecaTransitionTextLayout.setVisibility(View.GONE);
                                minimizeECAFragment();
                                }
                            });
                        } else {
                            ecaIntroText += " " + getString(R.string.monitoring_ready);
                            ecaTransitionText.setText(ecaIntroText);
                            ecaFragment.sendToECAToSpeak(ecaIntroText);
                            ecaTransitionTextLayout.setVisibility(View.VISIBLE);
                            readyButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ecaTransitionTextLayout.setVisibility(View.GONE);
                                    transitionToNextFragment(nextFragment);
                                }
                            });
                        }
                    } else {
                        transitionToNextFragment(nextFragment);
                    }
                } else {
                    finish();
                }
            }
        };
        timer.start();

    }

    private void transitionToNextFragment(Fragment nextFragment){
        minimizeECAFragment();
        replaceFragment(nextFragment);
    }

    private boolean doesNextHasIntro(Fragment nextFragment) {
        return nextFragment instanceof MonitoringTestFragment;
    }

    private void doTransitionWithResult(MonitoringTestFragment currentFragment, Fragment nextFragment) {
        String ecaText = tryGettingStringResource(currentFragment.getEndStringResource());
        int time = currentFragment.getEndTime();
        boolean isHappy = currentFragment.isEndEmotionHappy();

        currentFragment.hideFragmentMainView();
        runTransition(time, ecaText, nextFragment, isHappy);

    }

    private void endActivity(Fragment currentFragment) {
        DatabaseAdapter db = new DatabaseAdapter(this);
        String ecaText = "";
        int time = 0;
        try {
            db.openDatabaseForRead();
            record.printRecord();

            db.insertRecord(record);
        } catch (SQLException e) {
            Log.e(TAG, "Database error");
        }

        if(currentFragment instanceof MonitoringTestFragment){
            ecaText = tryGettingStringResource(((MonitoringTestFragment) currentFragment).getEndStringResource());
            time = ((MonitoringTestFragment) currentFragment).getEndTime();
        }
        ecaText += " " + getString(R.string.monitoring_end);
        time += 3000;
        runTransition(time, ecaText, null, true);
    }

    private String tryGettingStringResource(int res){
        try {
            return getString(res);
        } catch (Resources.NotFoundException e){
            Log.e(TAG, "Resource not found", e);
        }
        return "";
    }

    @Override
    public void onDoneRemarks(String remarkString, byte[] remarkAudio) {
        record.setRemarksString(remarkString);
        record.setRemarksAudio(remarkAudio);
        doneFragment();
    }

    @Override
    public void onDoneRemarks() {
        doneFragment();
    }

    @Override
    public void setRemarksQuestion() {
        Fragment currentFragment = fragmentManager.findFragmentByTag(fragments[currentFragmentIndex]);

        if (currentFragment instanceof RemarksFragment){
            int question = R.string.remarks_monitoring;

            ((RemarksFragment) currentFragment).setRemarkQuestion(question);
            ecaFragment.sendToECAToSPeak(question);

        }
    }
}
