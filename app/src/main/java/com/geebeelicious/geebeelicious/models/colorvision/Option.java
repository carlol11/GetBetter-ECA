package com.geebeelicious.geebeelicious.models.colorvision;

/**
 * Created by Kate on 03/04/2016.
 * The Option class represents an option
 * from the multiple choice menu that
 * the user chooses answers from. The class
 * contains the shape of the Option, its
 * corresponding drawable resource.
 */
public class Option {

    private String shape;
    private int optionDrawable;
    private boolean isAdded;

    public Option(String shape, int optionDrawable){
        this.shape = shape;
        this.optionDrawable = optionDrawable;
        this.isAdded = false;
    }

    public String getShape() {
        return shape;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public int getOptionDrawable() {
        return optionDrawable;
    }

    public void setAdded(){
        isAdded = true;
    }

    public void resetAdded(){
        isAdded = false;
    }

}
