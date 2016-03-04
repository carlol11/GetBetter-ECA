package models.colorvision;

/**
 * Created by Kate on 03/04/2016.
 */
public class IshiharaPlate {

    private String shape;
    private int style;
    private int ishiharaPlateDrawable;
    private boolean isAdded;

    public IshiharaPlate(String shape, int style, int ishiharaPlateDrawable){
        this.shape = shape;
        this.style = style;
        this.ishiharaPlateDrawable = ishiharaPlateDrawable;
        this.isAdded = false;
    }

    public String getShape() {
        return shape;
    }

    public int getStyle() {
        return style;
    }

    public boolean isVisibleToColorBlind() {
        if(style == 6){
            return true;
        }
        else{
            return false;
        }
    }

    public int getIshiharaPlateDrawable() {
        return ishiharaPlateDrawable;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(){
        isAdded = true;
    }
}
