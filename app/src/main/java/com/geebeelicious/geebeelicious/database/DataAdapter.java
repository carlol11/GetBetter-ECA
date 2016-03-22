package com.geebeelicious.geebeelicious.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import models.consultation.ChiefComplaint;
import models.consultation.Impressions;
import models.consultation.Symptom;
import models.consultation.SymptomFamily;

public class DataAdapter {
    protected static final String TAG = "DataAdapter";

    private SQLiteDatabase getBetterDb;
    private DatabaseHelper getBetterDatabaseHelper;

    private static final String SYMPTOM_LIST = "tbl_symptom_list";
    private static final String SYMPTOM_FAMILY = "tbl_symptom_family";
    private static final String PATIENT_ANSWERS = "tbl_patient_answers";
    private static final String CASE_RECORDS_TABLE = "tbl_case_records";
    private static final String IMPRESSION_TABLE = "tbl_case_impression";
    private static final String IMPRESSION_TO_COMPLAINTS = "tbl_impressions_of_complaints";
    private static final String SYMPTOM_TO_IMPRESSION = "tbl_symptom_of_impression";
    private static final String SYMPTOM_TABLE = "tbl_symptom_list";


    public DataAdapter (Context context) {
        getBetterDatabaseHelper  = new DatabaseHelper(context);
    }

    public DataAdapter createDatabase() throws SQLException {

        try {
            getBetterDatabaseHelper.createDatabase();
        }catch (IOException ioe) {
            Log.e(TAG, ioe.toString() + "UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataAdapter openDatabaseForRead() throws SQLException {

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

    public DataAdapter openDatabaseForWrite() throws SQLException {

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
                    c.getInt(c.getColumnIndexOrThrow("symptom_family_id")));

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
                    c.getInt(c.getColumnIndexOrThrow("symptom_family_id")));

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
}
