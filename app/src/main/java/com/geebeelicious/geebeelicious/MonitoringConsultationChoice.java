package com.geebeelicious.geebeelicious;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.geebeelicious.geebeelicious.tests.hearing.HearingCalibrationActivity;
import com.geebeelicious.geebeelicious.tests.visualacuity.VisualAcuityMainActivity;

import java.util.Date;

public class MonitoringConsultationChoice extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_consultation_choice);
        Button mButton = (Button)findViewById(R.id.monitoringButton);
        Button cButton = (Button)findViewById(R.id.consultationButton);

        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle record = new Bundle();
                record.putString("currentDate", new Date().toString());
                Intent intent = new Intent(MonitoringConsultationChoice.this, VisualAcuityMainActivity.class);
                intent.putExtras(record);
                startActivity(intent);
            }
        });

        cButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void calibrateHearing(View view){
        Intent intent = new Intent(MonitoringConsultationChoice.this, HearingCalibrationActivity.class);
        finish();
        startActivity(intent);
    }

}
