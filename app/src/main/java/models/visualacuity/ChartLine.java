package models.visualacuity;


/**
 * Created by Kate on 02/23/2016.
 * The ChartLine class represents
 * a line on the Snellen Eye Chart
 */
public class ChartLine {

    private int lineNumber;
    private int actualDistance;
    private int expectedDistance;
    private int chartLineDrawable;

    public ChartLine(int lineNumber, int actualDistance, int expectedDistance, int chartLineDrawable) {
        this.lineNumber = lineNumber;
        this.actualDistance = actualDistance;
        this.expectedDistance = expectedDistance;
        this.chartLineDrawable = chartLineDrawable;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getActualDistance(){
        return actualDistance;
    }

    public int getExpectedDistance(){
        return expectedDistance;
    }

    public int getChartLineDrawable(){
        return chartLineDrawable;
    }

    public String getVisualAcuity() {
        return new String(actualDistance + "/" + expectedDistance);
    }



}
