package com.geebeelicious.models;

import android.util.DisplayMetrics;
import android.util.TypedValue;
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

    private void getDisplaySize(ImageView imageView){
        displayHeight = imageView.getHeight();
        displayWidth = imageView.getWidth();
        System.out.println("height: " + displayHeight);
    }

    public float getUserDistance(Context context, ImageView imageView){
        getDisplaySize(imageView);
        float height = convertPixelsToMillimeter(displayHeight, context.getResources().getDisplayMetrics().xdpi);
        float distance = (height/88) * 6;
        System.out.println("user distance: " + distance);
        return distance;
    }

    private float convertPixelsToMillimeter(int pixels, float dpi){
        float height = pixels / dpi * 25.4f;
        return height;
    }

    private float convertMetersToFeet(int meters){
        return meters * 3.28084f;
    }
}
