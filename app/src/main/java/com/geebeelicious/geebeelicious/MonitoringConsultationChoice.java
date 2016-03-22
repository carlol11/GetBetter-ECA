package com.geebeelicious.geebeelicious;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import android.view.View.OnClickListener;
import android.widget.Button;

public class MonitoringConsultationChoice extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_consultation_choice);

        Button mButton = (Button)findViewById(R.id.monitoringButton);
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}
