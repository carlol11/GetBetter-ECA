package com.geebeelicious.geebeelicious.activities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
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
import com.geebeelicious.geebeelicious.fragments.MonitoringFragment;
import com.geebeelicious.geebeelicious.fragments.PatientPictureFragment;
import com.geebeelicious.geebeelicious.fragments.VaccinationFragment;
import com.geebeelicious.geebeelicious.fragments.VisualAcuityFragment;
import com.geebeelicious.geebeelicious.interfaces.ECAActivity;
import com.geebeelicious.geebeelicious.interfaces.MonitoringTestFragment;
import com.geebeelicious.geebeelicious.interfaces.OnMonitoringFragmentInteractionListener;
import com.geebeelicious.geebeelicious.fragments.FineMotorFragment;
import com.geebeelicious.geebeelicious.fragments.GrossMotorFragment;
import com.geebeelicious.geebeelicious.fragments.HearingMainFragment;

import com.geebeelicious.geebeelicious.models.consultation.Patient;
import com.geebeelicious.geebeelicious.models.monitoring.Record;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    private LinearLayout ecaLinearLayout;
    private FrameLayout ecaFragmentLayout;

    private String[] fragments;
    private int currentFragmentIndex;
    private FragmentManager fragmentManager;
    private Patient patient;

    private Typeface chalkFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_main);

        ECAText = (TextView) findViewById(R.id.placeholderECAText);
        resultsText = (TextView) findViewById(R.id.placeholderResults);
        TextView remarksText = (TextView) findViewById(R.id.questionMonitoringConsultationChoice);
        NAButton = (Button) findViewById(R.id.NAButton);
        ecaLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutECA);
        ecaFragmentLayout = (FrameLayout) findViewById(R.id.placeholderECA);

        chalkFont = Typeface.createFromAsset(getAssets(), "fonts/DJBChalkItUp.ttf");
        ECAText.setTypeface(chalkFont);
        resultsText.setTypeface(chalkFont);
        remarksText.setTypeface(chalkFont);
        NAButton.setTypeface(chalkFont);

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
    public void onBackPressed() {
        finish();
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
    public void doneFragment() { //gets called by the fragments when done
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragments[currentFragmentIndex]);

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
                    doTransitionWithoutResult((MonitoringTestFragment) nextFragment);
                } else {
                    replaceFragment(nextFragment);
                }

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

    private void clearTextViews() {
        ECAText.setText("");
        resultsText.setText("");
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
            saveButton.setTypeface(chalkFont);
            final EditText remarkText = (EditText) findViewById(R.id.remarkText);
            remarkText.setTypeface(chalkFont);

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
        if(!isFinishing()){
            transaction.commit();
        }
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

    private void maximizeECAFragment(){
        View parent = (View)ecaLinearLayout.getParent();
        final int mToHeight = parent.getHeight();
        final int mToWidth = parent.getWidth();
        ecaFragmentLayout.setLayoutParams(new LinearLayout.LayoutParams(mToWidth, mToHeight));
    }

    private void minimizeECAFragment(){
        ecaFragmentLayout.setLayoutParams(new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.activity_eca_small),
                getResources().getDimensionPixelSize(R.dimen.activity_eca_small)));
    }

    private void runTransition(final int time, final String ecaText, final Fragment nextFragment, final boolean isConcern) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CountDownTimer timer;

                maximizeECAFragment();
                if(isConcern){
                    ecaFragment.sendToECAToEmote(ECAFragment.Emotion.CONCERN, 1);
                }

                ecaFragment.sendToECAToSpeak(ecaText);

                timer = new CountDownTimer(time, 1000) { //timer for the transition
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        minimizeECAFragment();

                        if (isConcern){
                            ecaFragment.sendToECAToEmote(ECAFragment.Emotion.HAPPY, 2);
                        }

                        if(nextFragment != null){
                            replaceFragment(nextFragment);
                        } else {
                            finish();
                        }
                    }
                };
                timer.start();
            }
        });
    }

    private boolean doesNextHasIntro(Fragment nextFragment) {
        return nextFragment instanceof MonitoringTestFragment;
    }

    private void doTransitionWithResult(MonitoringTestFragment currentFragment, Fragment nextFragment) {
        String ecaText = tryGettingStringResource(currentFragment.getEndStringResource());
        int time = currentFragment.getEndTime();
        boolean isHappy = currentFragment.isEndEmotionHappy();

        currentFragment.hideFragmentMainView();

        if (doesNextHasIntro(nextFragment)) { //if next has intro
            ecaText +=" "+ tryGettingStringResource(((MonitoringTestFragment) nextFragment).getIntroStringResource());
            time += ((MonitoringTestFragment) nextFragment).getIntroTime();
        }

        runTransition(time, ecaText, nextFragment, isHappy);

    }

    private void doTransitionWithoutResult(MonitoringTestFragment nextFragment){
        String ecaText = tryGettingStringResource(nextFragment.getIntroStringResource());
        runTransition(nextFragment.getIntroTime(), ecaText, nextFragment, true);
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



    //TODO: Erase this if di na kelangan
//    private void resizeView(final View v, final int toHeight, final int toWidth) {
//        final int fromWidth = v.getWidth();
//        final int fromHeight = v.getHeight();
//        Animation animation = new Animation() {
//            @Override
//            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                float height = (toHeight - fromWidth) * interpolatedTime + fromWidth;
//                float width = (toWidth - fromHeight) * interpolatedTime + fromHeight; //also used by the childView
//                ViewGroup.LayoutParams q = v.getLayoutParams();
//
//                q.width = (int) width;
//                q.height = (int) height;
//
//                v.requestLayout();
//            }
//        };
//        animation.setDuration(500);
//        v.startAnimation(animation);
//    }

}
