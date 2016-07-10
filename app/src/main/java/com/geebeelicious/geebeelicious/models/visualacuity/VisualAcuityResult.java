package com.geebeelicious.geebeelicious.models.visualacuity;

/**
 * Created by Kate on 02/24/2016.
 * The VisualAcuityResult class represents a result
 * of the visual acuity test performed.
 * It can store which eye was tested and
 * the test result (represented by a ChartLine)
 */
public class VisualAcuityResult {

    private ChartLine resultLine;
    private String eye;

    public VisualAcuityResult(String eye, ChartLine resultLine){
        this.eye = eye;
        this.resultLine = resultLine;
    }

    public String getEye(){
        if(eye.equals("Right")){
            return "Right";
        }
        else {
            return "Left";}
    }

    public String getVisualAcuity(){
        return resultLine.getVisualAcuity();
    }

    public String getLineNumber(){
        return Integer.toString(resultLine.getLineNumber());
    }


}
