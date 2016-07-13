package com.geebeelicious.geebeelicious.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.geebeelicious.geebeelicious.interfaces.ECAActivity;
import com.geebeelicious.geebeelicious.models.consultation.Patient;

/**
 * Created by Kate.
 * The MonitoringConsultationChoice class serves as the activity
 * allowing the user to choose between the monitoring or
 * consultation modules.
 */

public class MonitoringConsultationChoice extends ECAActivity{
    private boolean hasSpoken;

    private DateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_consultation_choice);

        Button mButton = (Button)findViewById(R.id.monitoringButton);
        Button cButton = (Button)findViewById(R.id.consultationButton);
        TextView questionView = (TextView)findViewById(R.id.questionMonitoringConsultationChoice);

        Typeface chalkFont = Typeface.createFromAsset(getAssets(), "fonts/DJBChalkItUp.ttf");
        mButton.setTypeface(chalkFont);
        cButton.setTypeface(chalkFont);
        questionView.setTypeface(chalkFont);

        final Patient patient = getIntent().getParcelableExtra("patient");

        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        if(savedInstanceState == null){
            hasSpoken = false;
        } else {
            hasSpoken = savedInstanceState.getBoolean("hasSpoken");
        }

        integrateECA();

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonitoringConsultationChoice.this, MonitoringMainActivity.class);
                intent.putExtra("patient", patient);
                intent.putExtra("currentDate", dateFormat.format(new Date()));
                startActivity(intent);
            }
        });

        cButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonitoringConsultationChoice.this, ConsultationActivity.class);
                intent.putExtra("currentDate", dateFormat.format(new Date()));
                intent.putExtra("patient", patient);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            if(!hasSpoken){
                ecaFragment.sendToECAToSPeak(R.string.monitoring_consultation_choice);
                hasSpoken = true;
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasSpoken", hasSpoken);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MonitoringConsultationChoice.this, PatientListActivity.class);
        startActivity(intent);
        finish();
    }
}
