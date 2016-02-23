package com.geebeelicious.geebeelicious.tests.visualacuity;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.geebeelicious.geebeelicious.R;

import models.visualacuity.ChartHelper;
import models.visualacuity.ChartLine;
import models.visualacuity.DistanceCalculator;

public class VisualAcuityMainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_acuity_main);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ImageView chartView = (ImageView) findViewById(R.id.chartLine);
        DistanceCalculator distanceCalculator = new DistanceCalculator();
        distanceCalculator.getUserDistance(this, chartView);

        ChartHelper chartHelper = new ChartHelper(chartView);
    }
    



}
