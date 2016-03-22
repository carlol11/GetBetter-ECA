package models.tests;

import java.util.Date;

import models.visualacuity.VisualAcuityResult;

/**
 * Created by Kate on 03/22/2016.
 * The Record class contains the
 * results of the different
 * health monitoring tests for
 * a single monitoring session
 */
public class Record {

    private String currentDate;
    private String visualAcuityRight;
    private String visualAcuityLeft;
    private String colorVision;
    private String hearingLeft;
    private String hearingRight;
    private String grossMotor;
    private String fineMotor;

    public Record() {
        currentDate = new Date().toString();
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public void setVisualAcuityRight(String visualAcuityRight) {
        this.visualAcuityRight = visualAcuityRight;
    }

    public void setVisualAcuityLeft(String visualAcuityLeft) {
        this.visualAcuityLeft = visualAcuityLeft;
    }

    public void setColorVision(String colorVision) {
        this.colorVision = colorVision;
    }

    public void setHearingLeft(String hearingLeft) {
        this.hearingLeft = hearingLeft;
    }

    public void setHearingRight(String hearingRight) {
        this.hearingRight = hearingRight;
    }

    public void setGrossMotor(String grossMotor) {
        this.grossMotor = grossMotor;
    }

    public void setFineMotor(String fineMotor) {
        this.fineMotor = fineMotor;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getVisualAcuityRight() {
        return visualAcuityRight;
    }

    public String getVisualAcuityLeft() {
        return visualAcuityLeft;
    }

    public String getColorVision() {
        return colorVision;
    }

    public String getHearingLeft() {
        return hearingLeft;
    }

    public String getHearingRight() {
        return hearingRight;
    }

    public String getGrossMotor() {
        return grossMotor;
    }

    public String getFineMotor() {
        return fineMotor;
    }
}
