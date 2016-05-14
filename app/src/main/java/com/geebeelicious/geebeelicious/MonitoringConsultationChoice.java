package com.geebeelicious.geebeelicious;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.geebeelicious.geebeelicious.consultation.ConsultationActivity;
import com.geebeelicious.geebeelicious.monitoring.MonitoringMainActivity;
import com.geebeelicious.geebeelicious.tests.MonitoringActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.consultation.Patient;

/**
 * Created by Kate.
 * The MonitoringConsultationChoice class serves as the activity
 * allowing the user to choose between the monitoring or
 * consultation modules.
 */

public class MonitoringConsultationChoice extends ActionBarActivity {

    private DateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_consultation_choice);

        Button mButton = (Button)findViewById(R.id.monitoringButton);
        Button cButton = (Button)findViewById(R.id.consultationButton);
        final Patient patient = getIntent().getParcelableExtra("patient");

        dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle record = new Bundle();
                record.putParcelable("patient", patient);
                record.putString("currentDate", dateFormat.format(new Date()));
                Intent intent = new Intent(MonitoringConsultationChoice.this, MonitoringMainActivity.class);
                intent.putExtras(record);
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
    public void onBackPressed() {
        Intent intent = new Intent(MonitoringConsultationChoice.this, PatientListActivity.class);
        finish();
        startActivity(intent);
    }
}
