package com.geebeelicious.geebeelicious.models.colorvision;

/**
 * Created by Kate on 03/04/2016.
 * The IshiharaPlate represents a plate
 * to be used in the test. The class
 * represents the plate through information
 * about its shape, its style, and its
 * corresponding drawable.
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
