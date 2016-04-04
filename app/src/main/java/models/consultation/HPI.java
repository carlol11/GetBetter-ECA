package models.consultation;

/**
 * Created by mgmalana on 24/03/2016.
 * The HPI class represents a history of present illness (HPI).
 * It contains the HPI ID, the patient ID to which the HPI is for,
 * the text content of the HPI, and the date of creation of the HPI.
 */
public class HPI {
    public final static String TABLE_NAME = "tbl_hpi";

    public final static String C_HPI_ID = "hpi_id";
    public final static String C_PATIENT_ID = "patient_id";
    public final static String C_HPI_TEXT = "hpi";
    public final static String C_DATE_CREATED = "date_created";


    private int hpi_id;
    private int patientId;
    private String hpiText;
    private String dateCreated;

    public HPI(int hpi_id, int patientId, String hpiText, String dateCreated) {
        this.hpi_id = hpi_id;
        this.patientId = patientId;
        this.hpiText = hpiText;
        this.dateCreated = dateCreated;
    }

    public HPI(int patientId, String hpiText, String dateCreated) {
        this.patientId = patientId;
        this.hpiText = hpiText;
        this.dateCreated = dateCreated;
    }

    public int getHpi_id() {
        return hpi_id;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getHpiText() {
        return hpiText;
    }

    public String getDateCreated() {
        return dateCreated;
    }
}
