package com.geebeelicious.geebeelicious.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.geebeelicious.geebeelicious.models.consultation.ChiefComplaint;
import com.geebeelicious.geebeelicious.models.consultation.HPI;
import com.geebeelicious.geebeelicious.models.consultation.Impressions;
import com.geebeelicious.geebeelicious.models.consultation.Municipality;
import com.geebeelicious.geebeelicious.models.consultation.Patient;
import com.geebeelicious.geebeelicious.models.consultation.PatientAnswers;
import com.geebeelicious.geebeelicious.models.consultation.PositiveResults;
import com.geebeelicious.geebeelicious.models.monitoring.Record;
import com.geebeelicious.geebeelicious.models.consultation.School;
import com.geebeelicious.geebeelicious.models.consultation.Symptom;
import com.geebeelicious.geebeelicious.models.consultation.SymptomFamily;

/**
 * Created by Mary Grace Malana
 * The DatabaseAdapter class contains methods which serve as database queries.
 * The original class used as a basis for this current version was
 * created by Mike Dayupay for HPI Generation module of the GetBetter project.
 */

public class DatabaseAdapter {
    protected static final String TAG = "DatabaseAdapter";

    private SQLiteDatabase getBetterDb;
    private DatabaseHelper getBetterDatabaseHelper;

    private static final String SYMPTOM_LIST = "tbl_symptom_list";
    private static final String SYMPTOM_FAMILY = "tbl_symptom_family";

    public DatabaseAdapter(Context context) {
        getBetterDatabaseHelper  = new DatabaseHelper(context);
    }

    public DatabaseAdapter createDatabase() throws SQLException {

        try {
            getBetterDatabaseHelper.createDatabase();
        }catch (IOException ioe) {
            Log.e(TAG, ioe.toString() + "UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DatabaseAdapter openDatabaseForRead() throws SQLException {

        try {
            getBetterDatabaseHelper.openDatabase();
            getBetterDatabaseHelper.close();
            getBetterDb = getBetterDatabaseHelper.getReadableDatabase();
        }catch (SQLException sqle) {
            Log.e(TAG, "open >>" +sqle.toString());
            throw sqle;
        }
        return this;
    }

    public DatabaseAdapter openDatabaseForWrite() throws SQLException {

        try {
            getBetterDatabaseHelper.openDatabase();
            getBetterDatabaseHelper.close();
            getBetterDb = getBetterDatabaseHelper.getWritableDatabase();
        }catch (SQLException sqle) {
            Log.e(TAG, "open >>" +sqle.toString());
            throw sqle;
        }
        return this;
    }

    public void closeDatabase() {
        getBetterDatabaseHelper.close();
    }

    public void resetSymptomAnsweredFlag () {

        ContentValues values = new ContentValues();
        values.put("is_answered", 0);
        int count = getBetterDb.update(SYMPTOM_LIST, values, null, null);

        //Log.d("updated rows reset", count + "");
    }

    public void resetSymptomFamilyFlags() {

        ContentValues values = new ContentValues();
        values.put("answered_flag", 0);
        values.put("answer_status", 1);

        int count = getBetterDb.update(SYMPTOM_FAMILY, values, null, null);

        //Log.d("updated rows reset", count +"");
    }

    public ArrayList<Impressions> getImpressions (ArrayList<ChiefComplaint> chiefComplaints) {

        ArrayList<Impressions> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM tbl_case_impression AS i, tbl_impressions_of_complaints AS s " +
                "WHERE i._id = s.impression_id AND (");

        for(ChiefComplaint c: chiefComplaints){
            sql.append("s.complaint_id = ").append(c.getComplaintID()).append(" OR ");
        }

        sql.delete(sql.lastIndexOf(" OR "), sql.length());

        sql.append(")");
        Log.d(TAG, "SQL Statement: " + sql);

        Cursor c = getBetterDb.rawQuery(sql.toString(), null);

        while(c.moveToNext()) {
            Impressions impressions = new Impressions(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("medical_term")),
                    c.getString(c.getColumnIndexOrThrow("scientific_name")),
                    c.getString(c.getColumnIndexOrThrow("local_name")),
                    c.getString(c.getColumnIndexOrThrow("treatment_protocol")),
                    c.getString(c.getColumnIndexOrThrow("remarks")));

            results.add(impressions);
        }

        c.close();
        return results;
    }

    public ArrayList<Symptom> getSymptoms(int impressionId) {

        ArrayList<Symptom> results = new ArrayList<>();
        String sql = "SELECT * FROM tbl_symptom_list AS s, tbl_symptom_of_impression AS i " +
                "WHERE i.impression_id = " + impressionId + " AND s._id = i.symptom_id";

        Cursor c = getBetterDb.rawQuery(sql, null);
        while(c.moveToNext()) {
            Symptom symptom = new Symptom(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_english")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("question_english")),
                    c.getString(c.getColumnIndexOrThrow("question_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("responses_english")),
                    c.getString(c.getColumnIndexOrThrow("responses_tagalog")),
                    c.getInt(c.getColumnIndexOrThrow("symptom_family_id")),
                    c.getInt(c.getColumnIndexOrThrow("emotion")));

            results.add(symptom);
        }

        c.close();
        return results;
    }

    public void updateAnsweredStatusSymptomFamily(int chiefComplaintId) {

        ContentValues values = new ContentValues();
        values.put("answered_flag", 1);
        values.put("answer_status", 1);

        int count = getBetterDb.update(SYMPTOM_FAMILY, values, "related_chief_complaint_id = " + chiefComplaintId, null);

        //Log.d("updated rows symptom family flags", count + "");
    }

    public ArrayList<Symptom> getQuestions(int impressionId) {

        ArrayList<Symptom> results = new ArrayList<>();
        String sql = "SELECT * FROM tbl_symptom_list AS s, tbl_symptom_of_impression AS i " +
                "WHERE i.impression_id = " + impressionId + " AND s._id = i.symptom_id AND s.is_answered = 0";

        Cursor c = getBetterDb.rawQuery(sql, null);
        Log.d("query count", c.getCount() + "");
        while(c.moveToNext()) {
            Symptom symptom = new Symptom(c.getInt(c.getColumnIndexOrThrow("_id")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_english")),
                    c.getString(c.getColumnIndexOrThrow("symptom_name_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("question_english")),
                    c.getString(c.getColumnIndexOrThrow("question_tagalog")),
                    c.getString(c.getColumnIndexOrThrow("responses_english")),
                    c.getString(c.getColumnIndexOrThrow("responses_tagalog")),
                    c.getInt(c.getColumnIndexOrThrow("symptom_family_id")),
                    c.getInt(c.getColumnIndexOrThrow("emotion")));

            results.add(symptom);
        }

        c.close();
        return results;
    }

    public boolean symptomFamilyIsAnswered (int symptomFamilyId) {

        Log.d("id", symptomFamilyId + "");
        String sql = "SELECT answered_flag FROM tbl_symptom_family WHERE _id = " + symptomFamilyId;

        Cursor c = getBetterDb.rawQuery(sql, null);
        c.moveToFirst();


        if(c.getCount() == 0) {
            c.close();
            return true;
        } else {
            if (c.getInt(c.getColumnIndexOrThrow("answered_flag")) == 1) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        }
    }

    public SymptomFamily getGeneralQuestion (int symptomFamilyId) {

        String sql = "SELECT * FROM tbl_symptom_family WHERE _id = " + symptomFamilyId;

        Cursor c = getBetterDb.rawQuery(sql, null);
        c.moveToFirst();

        SymptomFamily generalQuestion;
        generalQuestion = new SymptomFamily(c.getInt(c.getColumnIndexOrThrow("_id")),
                c.getString(c.getColumnIndexOrThrow("symptom_family_name_english")),
                c.getString(c.getColumnIndexOrThrow("symptom_family_name_tagalog")),
                c.getString(c.getColumnIndexOrThrow("general_question_english")),
                c.getString(c.getColumnIndexOrThrow("responses_english")),
                c.getInt(c.getColumnIndexOrThrow("related_chief_complaint_id")));

        c.close();
        return generalQuestion;
    }

    public void updateAnsweredFlagPositive(int symptomId) {

        ContentValues values = new ContentValues();
        values.put("is_answered", 1);
        int count = getBetterDb.update(SYMPTOM_LIST, values, "_id = " + symptomId, null);

        //Log.d("update rows flag positive", count + "");
    }

    public void updateAnsweredStatusSymptomFamily(int symptomFamilyId, int answer) {

        String sql = "UPDATE tbl_symptom_family SET answered_flag = 1, answer_status = " + answer +
                " WHERE _id = " + symptomFamilyId;

        getBetterDb.execSQL(sql);
    }

    public ArrayList<String> getHardSymptoms (int impressionId) {

        ArrayList<String> results = new ArrayList<>();

        String sql = "SELECT s.symptom_name_english AS symptom_name_english FROM tbl_symptom_list AS s, " +
                "tbl_symptom_of_impression AS i WHERE i.impression_id = " + impressionId +
                " AND i.hard_symptom = 1 AND i.symptom_id = s._id";
        Cursor c = getBetterDb.rawQuery(sql, null);


        while(c.moveToNext()) {
            results.add(c.getString(c.getColumnIndexOrThrow("symptom_name_english")));
        }

        c.close();
        return results;
    }

    public boolean symptomFamilyAnswerStatus (int symptomFamilyId) {

        Log.d("symptom family id", symptomFamilyId + "");
        String sql = "SELECT answer_status FROM tbl_symptom_family WHERE _id = " + symptomFamilyId;

        Cursor c = getBetterDb.rawQuery(sql, null);
        c.moveToFirst();

        if(c.getCount() == 0) {
            c.close();
            return false;
        } else {
            if (c.getInt(c.getColumnIndexOrThrow("answer_status")) == 1) {
                c.close();
                return true;
            } else {
                c.close();
                return false;
            }
        }
    }

    public String getChiefComplaints(int chiefComplaintIds) {

        String result = "";
        String sql = "SELECT chief_complaint_english FROM tbl_chief_complaint WHERE _id = " + chiefComplaintIds;
        Cursor c = getBetterDb.rawQuery(sql, null);

        c.moveToFirst();
        result = c.getString(c.getColumnIndexOrThrow("chief_complaint_english"));

        //Log.d("result", result);
        c.close();
        return result;
    }

    public ArrayList<PositiveResults> getPositiveSymptoms (ArrayList<PatientAnswers> patientAnswers) {
        ArrayList<PositiveResults> results = new ArrayList<>();
        String delim = "";

        StringBuilder sql = new StringBuilder("Select symptom_name_english AS positiveSymptom, answer_phrase AS answerPhrase" +
                " FROM tbl_symptom_list WHERE ");

        for(PatientAnswers answer: patientAnswers){
            sql.append(delim).append("_id = ").append(answer.getSymptomId());
            delim = " OR ";
        }

        Log.d(TAG, "SQL Statement: " + sql);
        Cursor c = getBetterDb.rawQuery(sql.toString(), null);

        while(c.moveToNext()) {
            PositiveResults positive = new PositiveResults(c.getString(c.getColumnIndexOrThrow("positiveSymptom")),
                    c.getString(c.getColumnIndexOrThrow("answerPhrase")));

            results.add(positive);
        }

        c.close();
        return results;
    }

/*
 * The succeeding code are not part of the original code created by Mike Dayupay
 */

    public void insertPatient(Patient patient){
        ContentValues values = new ContentValues();
        int row;

        values.put(Patient.C_FIRST_NAME, patient.getFirstName());
        values.put(Patient.C_LAST_NAME, patient.getLastName());
        values.put(Patient.C_BIRTHDAY, patient.getBirthday());
        values.put(Patient.C_GENDER, patient.getGender());
        values.put(Patient.C_SCHOOL_ID, patient.getSchoolId());
        values.put(Patient.C_HANDEDNESS, patient.getHandedness());

        row = (int) getBetterDb.insert(Patient.TABLE_NAME, null, values);
        Log.d(TAG, "insertPatient Result: " + row);
    }

    public void insertRecord(Record record){
        ContentValues values = new ContentValues();
        int row;

        values.put(Record.C_PATIENT_ID, record.getPatient_id());
        values.put(Record.C_DATE_CREATED, record.getDateCreated());
        values.put(Record.C_HEIGHT, record.getHeight());
        values.put(Record.C_WEIGHT, record.getWeight());
        values.put(Record.C_VISUAL_ACUITY_LEFT, record.getVisualAcuityLeft());
        values.put(Record.C_VISUAL_ACUITY_RIGHT, record.getVisualActuityRight());
        values.put(Record.C_COLOR_VISION, record.getColorVision());
        values.put(Record.C_HEARING_LEFT, record.getHearingLeft());
        values.put(Record.C_HEARING_RIGHT, record.getHearingRight());
        values.put(Record.C_GROSS_MOTOR, record.getGrossMotor());
        values.put(Record.C_GROSS_MOTOR_REMARK, record.getGrossMotorRemark());
        values.put(Record.C_FINE_MOTOR_DOMINANT, record.getFineMotorDominant());
        values.put(Record.C_FINE_MOTOR_N_DOMINANT, record.getFineMotorNDominant());
        values.put(Record.C_FINE_MOTOR_HOLD, record.getFineMotorHold());
        values.put(Record.C_VACCINATION, record.getVaccination());
        values.put(Record.C_VACCINATION, record.getPatientPicture());

        row = (int) getBetterDb.insert(Record.TABLE_NAME, null, values);
        Log.d(TAG, "insertRecord Result: " + row);
    }

    public void insertHPI(HPI hpi){
        ContentValues values = new ContentValues();
        int row;

        values.put(HPI.C_PATIENT_ID, hpi.getPatientId());
        values.put(HPI.C_DATE_CREATED, hpi.getDateCreated());
        values.put(HPI.C_HPI_TEXT, hpi.getHpiText());

        row = (int) getBetterDb.insert(HPI.TABLE_NAME, null, values);
        Log.d(TAG, "insertHPI Result: " + row);
    }

    //Return a list of possible patients matching the given first name, last name, gender, birthday, and school
    public ArrayList<Patient> getPossiblePatients(String firstName, String lastName, int gender, String birthday, int schoolId){
        ArrayList<Patient> patients = new ArrayList<>();
        Cursor c = getBetterDb.query(Patient.TABLE_NAME, null, Patient.C_FIRST_NAME + " = ? AND "
                + Patient.C_LAST_NAME + " = ? AND " + Patient.C_GENDER + " = "+ gender+" AND "
                + Patient.C_BIRTHDAY + " = ? AND " + Patient.C_SCHOOL_ID + " = " + schoolId,
                new String[]{firstName, lastName, birthday}, null, null, null, null);

        if(c.moveToFirst()){
            do{
                    patients.add(new Patient(c.getInt(c.getColumnIndex(Patient.C_PATIENT_ID)),
                            c.getString(c.getColumnIndex(Patient.C_FIRST_NAME)),
                            c.getString(c.getColumnIndex(Patient.C_LAST_NAME)),
                            c.getString(c.getColumnIndex(Patient.C_BIRTHDAY)),
                            c.getInt(c.getColumnIndex(Patient.C_GENDER)),
                            c.getInt(c.getColumnIndex(Patient.C_SCHOOL_ID)),
                            c.getInt(c.getColumnIndex(Patient.C_HANDEDNESS))));
            }while(c.moveToNext());
        }
        c.close();
        return patients;
    }

    //Return a list of all schools
    public ArrayList<School> getAllSchools(){
        ArrayList<School> schools = new ArrayList<>();
        Cursor c = getBetterDb.rawQuery("SELECT "+ School.C_SCHOOL_ID + ", s." + School.C_SCHOOLNAME + ", m." +
                Municipality.C_NAME + " AS municipalityName FROM " + School.TABLE_NAME + " AS s, " + Municipality.TABLE_NAME +
                " AS m WHERE s." + School.C_MUNICIPALITY + " = m." + Municipality.C_MUNICIPALITY_ID, null);
        if(c.moveToFirst()){
            do{
                schools.add(new School(c.getInt(c.getColumnIndex(School.C_SCHOOL_ID)), c.getString(c.getColumnIndex(School.C_SCHOOLNAME)),
                        c.getString(c.getColumnIndex("municipalityName"))));
            }while(c.moveToNext());
        }
        c.close();
        return schools;
    }

    //Return a list of all records associated with a given patient
    public ArrayList<Record> getRecords(int patientId){
        ArrayList<Record> records = new ArrayList<>();
        Cursor c = getBetterDb.query(Record.TABLE_NAME, null, Record.C_PATIENT_ID + " = " + patientId, null, null, null, null, null);

        if(c.moveToFirst()){
            do{
                Record record = new Record(c.getInt(c.getColumnIndex(Record.C_RECORD_ID)), c.getInt(c.getColumnIndex(Record.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(Record.C_DATE_CREATED)), c.getDouble(c.getColumnIndex(Record.C_HEIGHT)),
                        c.getDouble(c.getColumnIndex(Record.C_WEIGHT)), c.getString(c.getColumnIndex(Record.C_VISUAL_ACUITY_LEFT)),
                        c.getString(c.getColumnIndex(Record.C_VISUAL_ACUITY_RIGHT)), c.getString(c.getColumnIndex(Record.C_COLOR_VISION)),
                        c.getString(c.getColumnIndex(Record.C_HEARING_LEFT)), c.getString(c.getColumnIndex(Record.C_HEARING_RIGHT)),
                        c.getInt(c.getColumnIndex(Record.C_GROSS_MOTOR)), c.getString(c.getColumnIndex(Record.C_GROSS_MOTOR_REMARK)),
                        c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_N_DOMINANT)), c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_DOMINANT)),
                        c.getInt(c.getColumnIndex(Record.C_FINE_MOTOR_HOLD)), c.getBlob(c.getColumnIndex(Record.C_VACCINATION)),
                        c.getBlob(c.getColumnIndex(Record.C_PATIENT_PICTURE)));

                record.printRecord();
                records.add(record);

            }while(c.moveToNext());
        }
        c.close();
        return records;
    }

    //Return a list of all the generated HPIs associated with a given patients
    public ArrayList<HPI> getHPIs(int patientId){
        ArrayList<HPI> HPIs = new ArrayList<>();
        Cursor c = getBetterDb.query(HPI.TABLE_NAME, null, HPI.C_PATIENT_ID + " = " + patientId, null, null, null, null, null);

        if(c.moveToFirst()){
            do{
                HPIs.add(new HPI(c.getInt(c.getColumnIndex(HPI.C_HPI_ID)), c.getInt(c.getColumnIndex(HPI.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(HPI.C_HPI_TEXT)), c.getString(c.getColumnIndex(HPI.C_DATE_CREATED))));
            }while(c.moveToNext());
        }
        c.close();
        return HPIs;
    }

    //Return a list of patients from a given school
    public ArrayList<Patient> getPatientsFromSchool(int schoolID){
        ArrayList<Patient> patients = new ArrayList<>();
        Cursor c = getBetterDb.query(Patient.TABLE_NAME, null, Patient.C_SCHOOL_ID + " = " + schoolID, null, null, null, Patient.C_LAST_NAME +" ASC");
        if(c.moveToFirst()){
            do{
                patients.add(new Patient(c.getInt(c.getColumnIndex(Patient.C_PATIENT_ID)),
                        c.getString(c.getColumnIndex(Patient.C_FIRST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_LAST_NAME)),
                        c.getString(c.getColumnIndex(Patient.C_BIRTHDAY)),
                        c.getInt(c.getColumnIndex(Patient.C_GENDER)),
                        c.getInt(c.getColumnIndex(Patient.C_SCHOOL_ID)),
                        c.getInt(c.getColumnIndex(Patient.C_HANDEDNESS))));
            }while(c.moveToNext());
        }
        c.close();

        return patients;
    }
}
