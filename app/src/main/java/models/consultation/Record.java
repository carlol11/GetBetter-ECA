package models.consultation;

import android.util.Log;

/**
 * Created by mgmalana on 24/03/2016.
 * The Record class represents a health record
 * containing information from the monitoring module
 * performed by the patient.
 */
public class Record {
    public final static String TAG = "Record";

    public final static String TABLE_NAME = "tbl_record";

    public final static String C_RECORD_ID = "record_id";
    public final static String C_PATIENT_ID = "patient_id";
    public final static String C_DATE_CREATED = "date_created";
    public final static String C_HEIGHT = "height";
    public final static String C_WEIGHT = "weight";
    public final static String C_VISUAL_ACUITY_LEFT = "visual_acuity_left";
    public final static String C_VISUAL_ACUITY_RIGHT = "visual_acuity_right";
    public final static String C_COLOR_VISION = "color_vision";
    public final static String C_HEARING_LEFT = "hearing_left";
    public final static String C_HEARING_RIGHT = "hearing_right";
    public final static String C_GROSS_MOTOR = "gross_motor";
    public final static String C_FINE_MOTOR_DOMINANT = "fine_motor_dominant";
    public final static String C_FINE_MOTOR_N_DOMINANT = "fine_motor_n_dominant";
    public final static String C_FINE_MOTOR_HOLD = "fine_motor_hold";

    private int recordID;
    private int patient_id;
    private String dateCreated;
    private double height;
    private double weight;
    private String visualAcuityLeft;
    private String visualActuityRight;
    private String colorVision;
    private String hearingLeft;
    private String hearingRight;
    private int grossMotor;
    private int fineMotorDominant;
    private int fineMotorNDominant;
    private int fineMotorHold;

    public Record(int recordID, int patient_id, String dateCreated, double height, double weight, String visualAcuityLeft, String visualActuityRight, String colorVision, String hearingLeft, String hearingRight, int grossMotor, int fineMotorNDominant, int fineMotorDominant, int fineMotorHold) {
        this.recordID = recordID;
        this.patient_id = patient_id;
        this.dateCreated = dateCreated;
        this.height = height;
        this.weight = weight;
        this.visualAcuityLeft = visualAcuityLeft;
        this.visualActuityRight = visualActuityRight;
        this.colorVision = colorVision;
        this.hearingLeft = hearingLeft;
        this.hearingRight = hearingRight;
        this.grossMotor = grossMotor;
        this.fineMotorNDominant = fineMotorNDominant;
        this.fineMotorDominant = fineMotorDominant;
        this.fineMotorHold = fineMotorHold;
    }

    public Record(int patient_id, String dateCreated, double height, double weight, String visualAcuityLeft, String visualActuityRight, String colorVision, String hearingLeft, String hearingRight, int grossMotor, int fineMotorNDominant, int fineMotorDominant, int fineMotorHold) {
        this.patient_id = patient_id;
        this.dateCreated = dateCreated;
        this.height = height;
        this.weight = weight;
        this.visualAcuityLeft = visualAcuityLeft;
        this.visualActuityRight = visualActuityRight;
        this.colorVision = colorVision;
        this.hearingLeft = hearingLeft;
        this.hearingRight = hearingRight;
        this.grossMotor = grossMotor;
        this.fineMotorDominant = fineMotorDominant;
        this.fineMotorNDominant = fineMotorNDominant;
        this.fineMotorHold = fineMotorHold;
    }

    public int getRecordID() {
        return recordID;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getVisualAcuityLeft() {
        return visualAcuityLeft;
    }

    public String getVisualActuityRight() {
        return visualActuityRight;
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

    public int getGrossMotor() {
        return grossMotor;
    }

    public int getFineMotorDominant() {
        return fineMotorDominant;
    }

    public int getFineMotorNDominant() {
        return fineMotorNDominant;
    }

    public int getFineMotorHold() {
        return fineMotorHold;
    }

    public void printRecord(){
        Log.d(TAG, "recordID: " + recordID + ", patientID: " + patient_id + ", dateCreated: " + dateCreated +
            ", height: " + height + ", weight " + weight + ", visualAcuityLeft: " + visualAcuityLeft +
            ", visualAcuityRight: " + visualActuityRight + ", colorVision " + colorVision +
            ", hearingLeft: " + hearingLeft + ", hearingRight: " + hearingRight +
            ", grossMotor: " + grossMotor + ", fineMotorDominant: " + fineMotorDominant +
            ", fineMotorNonDominant: " + fineMotorNDominant + ", fineMotorPen: " + fineMotorHold);
    }
}
