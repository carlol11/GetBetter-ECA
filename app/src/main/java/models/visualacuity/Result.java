package models.visualacuity;

/**
 * Created by Kate on 02/24/2016.
 */
public class Result {

    private ChartLine resultLine;
    private String eye;

    public Result(String eye, ChartLine resultLine){
        this.eye = eye;
        this.resultLine = resultLine;
    }

    public String getEye(){
        if(eye.equals("Right")){
            return "R";
        }
        else {
            return "L";}
    }

    public String getVisualAcuity(){
        return resultLine.getVisualAcuity();
    }

    public String getLineNumber(){
        return Integer.toString(resultLine.getLineNumber());
    }


}
