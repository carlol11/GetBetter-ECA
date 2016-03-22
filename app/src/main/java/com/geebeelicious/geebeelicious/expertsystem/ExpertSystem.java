package com.geebeelicious.geebeelicious.expertsystem;

import android.content.Context;

import com.geebeelicious.geebeelicious.database.DataAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import models.consultation.ChiefComplaint;
import models.consultation.Impressions;
import models.consultation.PatientAnswers;
import models.consultation.Symptom;
import models.consultation.SymptomFamily;


public class ExpertSystem {
    private ArrayList<Impressions> impressionsSymptoms;
    private ArrayList<String> ruledOutImpressionList;
    private ArrayList<String> plausibleImpressionList;
    private ArrayList<String> positiveSymptomList;
    private ArrayList<String> ruledOutSymptomList;
    private ArrayList<Symptom> questions;
    private ArrayList<PatientAnswers> answers;
    private ArrayList<ChiefComplaint> patientChiefComplaints;

    private int currentImpressionIndex;
    private int currentSymptomIndex;
    private int symptomFamilyId;

    private boolean flag;

    private DataAdapter getBetterDb;
    private SymptomFamily generalQuestion;


    public ExpertSystem(Context context){
        ruledOutSymptomList = new ArrayList<>();
        ruledOutImpressionList = new ArrayList<>();
        plausibleImpressionList = new ArrayList<>();
        positiveSymptomList = new ArrayList<>();
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        initializeDatabase(context);
    }

    public String startExpertSystem(ArrayList<ChiefComplaint> patientChiefComplaints){
        this.patientChiefComplaints = patientChiefComplaints;

        resetDatabaseFlags();
        initializeImpressionList();
        updateAnsweredStatusSymptomFamily();

        currentImpressionIndex = 0;
        currentSymptomIndex = 0;
        getQuestions(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());

        return getQuestion();
    }

    private void initializeDatabase (Context context) {

        getBetterDb = new DataAdapter(context);

        try {
            getBetterDb.createDatabase();

            //TODO: Check if kelangan talaga ang openDatabaseForRead()
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetDatabaseFlags() {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getBetterDb.resetSymptomAnsweredFlag();
        getBetterDb.resetSymptomFamilyFlags();
        getBetterDb.closeDatabase();
    }

    private void initializeImpressionList () {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        impressionsSymptoms = new ArrayList<>();
        impressionsSymptoms.addAll(getBetterDb.getImpressions(patientChiefComplaints));

        impressionsSymptoms = new ArrayList<>(new LinkedHashSet<>(impressionsSymptoms)); // Remove duplicates

        for(int i = 0; i < impressionsSymptoms.size(); i++) {
            ArrayList<Symptom> symptoms = getBetterDb.getSymptoms(impressionsSymptoms.get(i).getImpressionId());
            impressionsSymptoms.get(i).setSymptoms(symptoms);
        }

        getBetterDb.closeDatabase();
    }

    public void updateAnsweredStatusSymptomFamily() {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(ChiefComplaint c: patientChiefComplaints){
            getBetterDb.updateAnsweredStatusSymptomFamily(c.getComplaintID());
        }

        getBetterDb.closeDatabase();

    }

    public void getQuestions(int impressionId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        questions.clear();
        questions.addAll(getBetterDb.getQuestions(impressionId));
        //Log.d("questions size", questions.size() + "");
        getBetterDb.closeDatabase();
    }

    public String getQuestion() {
        String questionAsked;

        if(isSymptomFamilyQuestionAnswered(questions.get(currentSymptomIndex).getSymptomFamilyId())) {
            questionAsked = questions.get(currentSymptomIndex).getQuestionEnglish();
            flag = true;
        } else {
            getGeneralQuestion(questions.get(currentSymptomIndex).getSymptomFamilyId());
            questionAsked = generalQuestion.getGeneralQuestionEnglish();
            symptomFamilyId = generalQuestion.getSymptomFamilyId();
            flag = false;
        }

        return questionAsked;
    }

    public boolean isSymptomFamilyQuestionAnswered(int symptomFamilyId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean value = getBetterDb.symptomFamilyIsAnswered(symptomFamilyId);
        getBetterDb.closeDatabase();
        return value;
    }

    public void getGeneralQuestion (int symptomFamilyId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        generalQuestion = getBetterDb.getGeneralQuestion(symptomFamilyId);
        getBetterDb.closeDatabase();
    }
}
