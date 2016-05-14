package com.geebeelicious.geebeelicious.monitoring;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.geebeelicious.geebeelicious.R;

import models.monitoring.Record;

/**
 * Created by MG.
 * The MonitoringMainActivity serves as the main activity for all the monitoring activities
 * Each test are executed through this activity
 */

public class MonitoringMainActivity extends ActionBarActivity {
    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_main);
        record = new Record();
    }

    public Record getRecord(){
        return record;
    }

    public void nextFragment(){
        
    }
}
