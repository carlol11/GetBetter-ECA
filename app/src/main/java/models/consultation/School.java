package models.consultation;

/**
 * Created by mgmalana on 24/03/2016.
 */
public class School {
    public static final String TABLE_NAME = "tbl_school";
    public static final String C_SCHOOLNAME = "name";
    public static final String C_SCHOOL_ID = "school_id";
    public static final String C_MUNICIPALITY = "municipality";

    private int schoolId;
    private String schoolName;
    private String municipality;

    public School(int schoolId, String schoolName, String municipality) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.municipality = municipality;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getMunicipality() {
        return municipality;
    }
}
