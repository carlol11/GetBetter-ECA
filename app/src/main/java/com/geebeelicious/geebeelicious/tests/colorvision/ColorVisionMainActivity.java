package com.geebeelicious.geebeelicious.tests.colorvision;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.geebeelicious.geebeelicious.R;

import models.colorvision.IshiharaHelper;
import models.colorvision.IshiharaPlate;

public class ColorVisionMainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_vision_main);

        final IshiharaHelper ishiharaHelper = new IshiharaHelper((ImageView) findViewById(R.id.ishiharaPlate));
        ishiharaHelper.startTest();
    }

}
