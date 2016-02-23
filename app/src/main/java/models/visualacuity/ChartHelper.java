package models.visualacuity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.geebeelicious.geebeelicious.R;

import java.util.ArrayList;

/**
 * Created by Kate on 02/24/2016.
 * The ChartHelper class manages the
 * administration of the visual
 * acuity test using the visual acuity chart
 */
public class ChartHelper {

    private ChartLine[] chart;
    private int currentLineNumber;
    private ImageView chartView;

    public ChartHelper(ImageView chartView){
        chart = new ChartLine[11];
        chart[0] = new ChartLine(1, 20, 200, R.drawable.snellen_line_1);
        chart[1] = new ChartLine(2, 20, 100, R.drawable.snellen_line_2);
        chart[2] = new ChartLine(3, 20, 70, R.drawable.snellen_line_3);
        chart[3] = new ChartLine(4, 20, 50, R.drawable.snellen_line_4);
        chart[4] = new ChartLine(5, 20, 40, R.drawable.snellen_line_5);
        chart[5] = new ChartLine(6, 20, 30, R.drawable.snellen_line_6);
        chart[6] = new ChartLine(7, 20, 25, R.drawable.snellen_line_7);
        chart[7] = new ChartLine(8, 20, 20, R.drawable.snellen_line_8);
        chart[8] = new ChartLine(9, 20, 15, R.drawable.snellen_line_9);
        chart[9] = new ChartLine(10, 20, 10, R.drawable.snellen_line_10);
        chart[10] = new ChartLine(11, 20, 5, R.drawable.snellen_line_11);
        currentLineNumber = 0;
        this.chartView = chartView;
    }

    public ChartLine getCurrentLine(){
        return chart[currentLineNumber];
    }

    public void goToNextLine(){
        if(currentLineNumber<=9){
            currentLineNumber++;
        }
        else{
            currentLineNumber = 10;
        }
    }

    public void displayChartLine(){
        chartView.setImageResource(getCurrentLine().getChartLineDrawable());
    }

    public ChartLine testLine(boolean canRead){
        ChartLine result = null;
        if(canRead && currentLineNumber<=9){
            goToNextLine();
        }
        else{
            result = chart[currentLineNumber];
        }
        return result;
    }

    public void performTest(Context context){
        ChartLine result = null;
        while(currentLineNumber<=10 && result==null) {
            result = testLine(true);
        }
    }














}
