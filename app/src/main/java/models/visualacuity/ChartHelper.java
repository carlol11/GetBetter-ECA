package models.visualacuity;

import android.widget.ImageView;

import com.geebeelicious.geebeelicious.R;


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
    private ChartLine result;
    private boolean isDone;
    private boolean isRightTested;
    private boolean isLeftTested;

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
        this.chartView = chartView;
        this.isRightTested = false;
        this.isLeftTested = false;
    }

    private ChartLine getPreviousLine(){
        return chart[currentLineNumber-1];
    }

    private ChartLine getCurrentLine(){
        return chart[currentLineNumber];
    }

    public void goToNextLine(){
        if(currentLineNumber<10){
            currentLineNumber++;
            displayChartLine();
        }
        else if(currentLineNumber==10 && result==null){
            setResult();
        }
    }

    private void displayChartLine(){
        chartView.setImageResource(getCurrentLine().getChartLineDrawable());
    }

    public void startTest(){
        result = null;
        isDone = false;
        currentLineNumber = 0;
        displayChartLine();
    }

    public boolean isDone(){
        return isDone;
    }

    public void setResult(){
        if(currentLineNumber==0 || currentLineNumber==10){
            result = getCurrentLine();
        }
        else{
            result = getPreviousLine();
        }
        isDone = true;
    }

    public ChartLine getResult(){
        return result;
    }

    public boolean isRightTested(){
        return isRightTested;
    }

    public boolean isLeftTested(){
        return isLeftTested;
    }

    public void setIsRightTested(){
        isRightTested = true;
    }

    public void setIsLeftTested(){
        isLeftTested = true;
    }

    public boolean isBothTested(){
        return isRightTested && isLeftTested;
    }















}
