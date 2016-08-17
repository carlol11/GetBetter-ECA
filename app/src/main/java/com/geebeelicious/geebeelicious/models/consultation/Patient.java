package com.geebeelicious.geebeelicious.models.consultation;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mgmalana on 24/03/2016.
 * The Patient class represents a Patient and implements Parcelable
 * to allow for the object to be passed between activities.
 * The class contains patient information.
 */
public class Patient implements Parcelable {
    private int patientID;
    private String firstName;
    private String lastName;
    private String birthday;
    private int gender; // 0 for MALE, 1 for FEMALE
    private int schoolId;
    private int handedness; //0 for RIGHT, 1 for LEFT
    private String remarksString;
    private byte[] remarksAudio;

    public final static String C_PATIENT_ID = "patient_id";
    public final static String C_FIRST_NAME = "first_name";
    public final static String C_LAST_NAME = "last_name";
    public final static String C_BIRTHDAY = "birthday";
    public final static String C_GENDER = "gender";
    public final static String C_SCHOOL_ID = "school_id";
    public final static String C_HANDEDNESS = "handedness";
    public final static String C_REMARKS_STRING = "remarks_string";
    public final static String C_REMARKS_AUDIO = "remarks_audio";

    public final static String FEMALE = "Female";
    public final static String MALE = "Male";

    public final static String TABLE_NAME = "tbl_patient";

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     */
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Patient createFromParcel(Parcel in) {
                    return new Patient(in);
                }

                public Patient[] newArray(int size) {
                    return new Patient[size];
                }
            };




    public Patient(int patientID, String firstName, String lastName, String birthday, int gender, int schoolId, int handedness, String remarksString, byte[] remarksAudio) {
        this.patientID = patientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.gender = gender;
        this.schoolId = schoolId;
        this.handedness = handedness;
        this.remarksString = remarksString;
        this.remarksAudio = remarksAudio;
    }

    public Patient(String firstName, String lastName, String birthday, int gender, int schoolId, int handedness, String remarksString, byte[] remarksAudio) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.gender = gender;
        this.schoolId = schoolId;
        this.handedness = handedness;
        this.remarksString = remarksString;
        this.remarksAudio = remarksAudio;
    }

    public Patient(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) { //write each field into the parcel
        dest.writeInt(patientID);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(birthday);
        dest.writeInt(gender);
        dest.writeInt(schoolId);
        dest.writeInt(handedness);
        dest.writeString(remarksString);
        dest.writeByteArray(remarksAudio);
    }

    public void readFromParcel(Parcel in){ //read back each field in the order that it was written to the parcel
        patientID = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        birthday = in.readString();
        gender = in.readInt();
        schoolId = in.readInt();
        handedness = in.readInt();
        remarksString = in.readString();
        remarksAudio = in.createByteArray();
    }

    public int getPatientID() {
        return patientID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getGender() {
        return gender;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public int getHandedness() {
        return handedness;
    }

    public String getGenderString(){
        if(gender == 0){
            return "Male";
        } else{
            return "Female";
        }
    }

    public String getHandednessString(){
        if(handedness == 0){
            return "Right-Handed";
        }else{
            return "Left-Handed";
        }
    }

    public String getRemarksString() {
        return remarksString;
    }

    public byte[] getRemarksAudio() {
        return remarksAudio;
    }
}
