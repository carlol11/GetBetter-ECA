package models.consultation;

/**
 * Created by mgmalana on 24/03/2016.
 */
public class Record {
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
    public final static String C_HEARING_RIGHT = "hearing)right";
    public final static String C_GROSS_MOTOR = "gross_motor";
    public final static String C_FINE_MOTOR_LEFT = "fine_motor_left";
    public final static String C_FINE_MOTOR_RIGHT = "fine_motor_right";
    public final static String C_FINE_MOTOR_HOLD = "fine_motor_hold";

    private int recordID;
    private int patient_id;
    private String dateCreated;
    private String height;
    private String weight;
    private String visualAcuityLeft;
    private String visualActuityRight;
    private String colorVision;
    private String hearingLeft;
    private String hearingRight;
    private String grossMotor;
    private String fineMotorLeft;
    private String fineMotorRight;
    private String fineMotorHold;

    public Record(int recordID, int patient_id, String dateCreated, String height, String weight, String visualAcuityLeft, String visualActuityRight, String colorVision, String hearingLeft, String hearingRight, String grossMotor, String fineMotorLeft, String fineMotorRight, String fineMotorHold) {
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
        this.fineMotorLeft = fineMotorLeft;
        this.fineMotorRight = fineMotorRight;
        this.fineMotorHold = fineMotorHold;
    }

    public Record(int patient_id, String dateCreated, String height, String weight, String visualAcuityLeft, String visualActuityRight, String colorVision, String hearingLeft, String hearingRight, String grossMotor, String fineMotorLeft, String fineMotorRight, String fineMotorHold) {
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
        this.fineMotorLeft = fineMotorLeft;
        this.fineMotorRight = fineMotorRight;
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

    public String getHeight() {
        return height;
    }

    public String getWeight() {
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

    public String getGrossMotor() {
        return grossMotor;
    }

    public String getFineMotorLeft() {
        return fineMotorLeft;
    }

    public String getFineMotorRight() {
        return fineMotorRight;
    }

    public String getFineMotorHold() {
        return fineMotorHold;
    }
}
