package com.geebeelicious.geebeelicious.models.monitoring;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by mgmalana on 24/03/2016.
 * The Record class represents a health record
 * containing information from the monitoring module
 * performed by the patient.
 */
public class Record implements Parcelable {
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
    public final static String C_GROSS_MOTOR_REMARK = "gross_motor_remark";
    public final static String C_FINE_MOTOR_DOMINANT = "fine_motor_dominant";
    public final static String C_FINE_MOTOR_N_DOMINANT = "fine_motor_n_dominant";
    public final static String C_FINE_MOTOR_HOLD = "fine_motor_hold";
    public final static String C_VACCINATION = "vaccination";
    public final static String C_PATIENT_PICTURE = "patient_picture";

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
    private int grossMotor; //0 pass, 1 fail, 2 na
    private String grossMotorRemark;
    private int fineMotorDominant; //0 pass, 1 fail
    private int fineMotorNDominant; //0 pass, 1 fail
    private int fineMotorHold; //0 pass, 1 fail
    private byte[] vaccination;
    private byte[] patientPicture;

    public Record(){

    }

    public Record(int recordID, int patient_id, String dateCreated, double height, double weight,
                  String visualAcuityLeft, String visualActuityRight, String colorVision, String hearingLeft,
                  String hearingRight, int grossMotor, String grossMotorRemark, int fineMotorNDominant, int fineMotorDominant,
                  int fineMotorHold, byte[] vaccination, byte[] patientPicture) {
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
        this.grossMotorRemark = grossMotorRemark;
        this.fineMotorNDominant = fineMotorNDominant;
        this.fineMotorDominant = fineMotorDominant;
        this.fineMotorHold = fineMotorHold;
        this.vaccination = vaccination;
        this.patientPicture = patientPicture;
    }

    protected Record(Parcel in) {
        readParcel(in);
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

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

    public String getGrossMotorRemark() {
        return grossMotorRemark;
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

    public byte[] getVaccination(){
        return vaccination;
    }

    public byte[] getPatientPicture(){
        return patientPicture;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setVisualAcuityLeft(String visualAcuityLeft) {
        this.visualAcuityLeft = visualAcuityLeft;
    }

    public void setVisualActuityRight(String visualActuityRight) {
        this.visualActuityRight = visualActuityRight;
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

    public void setGrossMotor(int grossMotor) {
        this.grossMotor = grossMotor;
    }

    public void setGrossMotorRemark(String grossMotorRemark) {
        this.grossMotorRemark = grossMotorRemark;
    }

    public void setFineMotorDominant(int fineMotorDominant) {
        this.fineMotorDominant = fineMotorDominant;
    }

    public void setFineMotorNDominant(int fineMotorNDominant) {
        this.fineMotorNDominant = fineMotorNDominant;
    }

    public void setFineMotorHold(int fineMotorHold) {
        this.fineMotorHold = fineMotorHold;
    }

    public void setVaccination(byte[] vaccination){
        this.vaccination = vaccination;
    }

    public void setPatientPicture(byte[] patientPicture){
        this.patientPicture = patientPicture;
    }

    public void printRecord(){
        Log.d(TAG, getCompleteRecordInfo());
    }

    public String getCompleteRecordInfo(){
        return "recordID: " + recordID + ", patientID: " + patient_id + ", dateCreated: " + dateCreated +
                ", height: " + height + ", weight " + weight + ", visualAcuityLeft: " + visualAcuityLeft +
                ", visualAcuityRight: " + visualActuityRight + ", colorVision " + colorVision +
                ", hearingLeft: " + hearingLeft + ", hearingRight: " + hearingRight +
                ", grossMotor: " + grossMotor + ", grossMotorRemark: "+ grossMotorRemark +
                ", fineMotorDominant: " + fineMotorDominant + ", fineMotorNonDominant: " + fineMotorNDominant +
                ", fineMotorPen: " + fineMotorHold + ", vaccination: " + vaccination +
                ", patientPicture: " + patientPicture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recordID);
        dest.writeInt(patient_id);
        dest.writeString(dateCreated);
        dest.writeDouble(height);
        dest.writeDouble(weight);
        dest.writeString(visualAcuityLeft);
        dest.writeString(visualActuityRight);
        dest.writeString(colorVision);
        dest.writeString(hearingLeft);
        dest.writeString(hearingRight);
        dest.writeInt(grossMotor);
        dest.writeString(grossMotorRemark);
        dest.writeInt(fineMotorDominant);
        dest.writeInt(fineMotorNDominant);
        dest.writeInt(fineMotorHold);
        dest.writeByteArray(vaccination);
        dest.writeByteArray(patientPicture);
    }

    public void readParcel(Parcel in){
        recordID = in.readInt();
        patient_id = in.readInt();
        dateCreated = in.readString();
        height = in.readDouble();
        weight = in.readDouble();
        visualAcuityLeft = in.readString();
        visualActuityRight = in.readString();
        colorVision = in.readString();
        hearingLeft = in.readString();
        hearingRight = in.readString();
        grossMotor = in.readInt();
        grossMotorRemark = in.readString();
        fineMotorDominant = in.readInt();
        fineMotorNDominant = in.readInt();
        fineMotorHold = in.readInt();
        vaccination = in.createByteArray();
        patientPicture = in.createByteArray();
    }
}
