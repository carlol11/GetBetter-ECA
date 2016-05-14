package com.geebeelicious.geebeelicious.monitoring;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import models.monitoring.Record;

/**
 * Created by MG.
 * The MonitoringMainActivity serves as the main activity for all the monitoring activities
 * Each test are executed through this activity
 */

public class MonitoringMainActivity extends ActionBarActivity implements MonitoringFragmentInteraction{
    private Record record;

    private TextView ECAText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring_main);
        record = new Record();
        ECAText = (TextView) findViewById(R.id.placeholderECAText);
    }

    public Record getRecord(){
        return record;
    }

    @Override
    public void setInstructions(String instructions) {
        ECAText.setText(instructions);
    }

    @Override
    public void doneFragment(){
        //TODO:
    }
}
