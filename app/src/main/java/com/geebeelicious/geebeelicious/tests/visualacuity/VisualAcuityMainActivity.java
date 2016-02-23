package com.geebeelicious.geebeelicious.tests.visualacuity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.geebeelicious.geebeelicious.R;

import models.visualacuity.DistanceCalculator;
import models.visualacuity.ChartLine;

public class VisualAcuityMainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_acuity_main);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DistanceCalculator distanceCalculator = new DistanceCalculator();
        distanceCalculator.getUserDistance(this, (ImageView) findViewById(R.id.chartLine));
    }

}
