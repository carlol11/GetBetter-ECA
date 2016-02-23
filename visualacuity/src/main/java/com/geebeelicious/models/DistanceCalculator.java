package com.geebeelicious.models;

import android.util.DisplayMetrics;
import android.content.Context;
import android.app.Activity;
import android.widget.ImageView;

import com.geebeelicious.visualacuity.MainActivity;

/**
 * Created by Kate on 02/23/2016.
 * The DistanceCalculator class determines
 * the size of the visual acuity chart to be used (maximum size depending on tablet)
 * and the required distance the user must be to perform the test.
 */
public class DistanceCalculator {

    private int displayHeight;
    private int displayWidth;
    private int chartHeight;
    private int chartWidth;

    public void getDisplaySize(ImageView imageView){
        displayHeight = imageView.getHeight();
        displayWidth = imageView.getWidth();
        System.out.println("height: " + displayHeight + " width: " + displayWidth);
    }

}
