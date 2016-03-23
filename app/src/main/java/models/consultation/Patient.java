package models.consultation;

/**
 * Created by mgmalana on 24/03/2016.
 */
public class Patient {
    private int patientID;
    private String firstName;
    private String lastName;
    private String birthday;
    private int gender; // 1 for MALE, 1 for FEMALE
    private int schoolId;
    private int handedness; //1 for RIGHT, 2 for LEFT

    public final static String C_PATIENT_ID = "patient_id";
    public final static String C_FIRST_NAME = "first_name";
    public final static String C_LAST_NAME = "last_name";
    public final static String C_BIRTHDAY = "birthday";
    public final static String C_GENDER = "gender";
    public final static String C_SCHOOL_ID = "school_id";
    public final static String C_HANDEDNESS = "handedness";
    public final static String FEMALE = "Female";
    public final static String MALE = "Male";


    public final static String TABLE_NAME = "tbl_patient";

    public Patient(int patientID, String firstName, String lastName, String birthday, int gender, int schoolId, int handedness) {
        this.patientID = patientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.gender = gender;
        this.schoolId = schoolId;
        this.handedness = handedness;
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

    public int isHandedness() {
        return handedness;
    }
}
