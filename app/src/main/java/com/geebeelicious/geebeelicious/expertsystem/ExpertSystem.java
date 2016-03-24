package com.geebeelicious.geebeelicious.expertsystem;

import android.content.Context;
import android.util.Log;

import com.geebeelicious.geebeelicious.database.DataAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

import models.consultation.ChiefComplaint;
import models.consultation.Impressions;
import models.consultation.PatientAnswers;
import models.consultation.PositiveResults;
import models.consultation.Symptom;
import models.consultation.SymptomFamily;

/*
* The original code was created by Mike Dayupay 2015.
* For the purpose of integration, the code was modified.
* Modified by Mary Grace Malana 2015.
* The expert system gives specific questions depending on the input of the user
* It uses the class DataAdapter to connect with the database.
*/


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
    private boolean clickFlag;

    private DataAdapter getBetterDb;
    private SymptomFamily generalQuestion;

    private int caseRecordId;

    public ExpertSystem(Context context){
        ruledOutSymptomList = new ArrayList<>();
        ruledOutImpressionList = new ArrayList<>();
        plausibleImpressionList = new ArrayList<>();
        positiveSymptomList = new ArrayList<>();
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        initializeDatabase(context);

        clickFlag = true;

        //TODO: [URGENT] change this temp caseRecordId. should be fetched from the database
        caseRecordId = 0;
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

    private void updateAnsweredStatusSymptomFamily() {

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

    private void updateAnsweredStatusSymptomFamily(int answer) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getBetterDb.updateAnsweredStatusSymptomFamily(generalQuestion.getSymptomFamilyId(), answer);
    }

    private void getQuestions(int impressionId) {

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

    private String getQuestion() {
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

    private boolean isSymptomFamilyQuestionAnswered(int symptomFamilyId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean value = getBetterDb.symptomFamilyIsAnswered(symptomFamilyId);
        getBetterDb.closeDatabase();
        return value;
    }

    private void getGeneralQuestion (int symptomFamilyId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        generalQuestion = getBetterDb.getGeneralQuestion(symptomFamilyId);
        getBetterDb.closeDatabase();
    }

    public String getNextQuestion(boolean isYes) { //returns null if no more question


        if(isYes){
            if (flag) {
                positiveSymptomList.add(questions.get(currentSymptomIndex).getSymptomNameEnglish());
                positiveSymptomList = new ArrayList<>(new LinkedHashSet<>(positiveSymptomList));
                updateAnsweredFlagPositive(questions.get(currentSymptomIndex).getSymptomId());
                addToAnswers(new PatientAnswers(caseRecordId, questions.get(currentSymptomIndex).getSymptomId(), "Yes"));
            } else {
                updateAnsweredStatusSymptomFamily(1);
            }
        } else {
            if (flag) {
                ruledOutSymptomList.add(questions.get(currentSymptomIndex).getSymptomNameEnglish());
                ruledOutSymptomList = new ArrayList<>(new LinkedHashSet<>(ruledOutSymptomList));
                updateAnsweredFlagPositive(questions.get(currentSymptomIndex).getSymptomId());
                addToAnswers(new PatientAnswers(caseRecordId, questions.get(currentSymptomIndex).getSymptomId(), "No"));
            } else {
                updateAnsweredStatusSymptomFamily(0);
                updateAnsweredFlagPositive(questions.get(currentSymptomIndex).getSymptomId());
                ruledOutSymptomList.add(questions.get(currentSymptomIndex).getSymptomNameEnglish());
                ruledOutSymptomList = new ArrayList<>(new LinkedHashSet<>(ruledOutSymptomList));
                addToAnswers(new PatientAnswers(caseRecordId, questions.get(currentSymptomIndex).getSymptomId(), "No"));
            }
        }

        if(flag) {

            currentSymptomIndex++;

            if(currentSymptomIndex >= questions.size()) {

                checkForRuledOutImpression(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());
                currentSymptomIndex = 0;
                currentImpressionIndex++;


                if(currentImpressionIndex >= impressionsSymptoms.size()) {
                    currentImpressionIndex = impressionsSymptoms.size() - 1;

                    return null;
                } else {
                    getQuestions(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());

                    while (questions.size() == 0) {
                        currentImpressionIndex++;

                        if(currentImpressionIndex >= impressionsSymptoms.size()) {
                            currentImpressionIndex = impressionsSymptoms.size() - 1;

                            return null;
                        }
                        checkForRuledOutImpression(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());
                    }

                    return getQuestion();
                }

            } else {
                return getQuestion();
            }

        } else {
            if(!isSymptomFamilyPositive(questions.get(currentSymptomIndex).getSymptomFamilyId())){
                currentSymptomIndex++;
                if(currentSymptomIndex >= questions.size()) {

                    checkForRuledOutImpression(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());
                    currentSymptomIndex = 0;
                    currentImpressionIndex++;


                    if(currentImpressionIndex >= impressionsSymptoms.size()) {
                        currentImpressionIndex = impressionsSymptoms.size() - 1;

                        return null;
                    } else {
                        getQuestions(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());

                        while (questions.size() == 0) {
                            currentImpressionIndex++;
                            checkForRuledOutImpression(impressionsSymptoms.get(currentImpressionIndex).getImpressionId());

                            if(currentImpressionIndex >= impressionsSymptoms.size()) {
                                currentImpressionIndex = impressionsSymptoms.size() - 1;

                                return null;
                            }
                        }

                        return getQuestion();
                    }

                } else {
                    return getQuestion();
                }

            } else {
                return getQuestion();
            }

        }
    }

    private void updateAnsweredFlagPositive(int symptomId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getBetterDb.updateAnsweredFlagPositive(symptomId);
        getBetterDb.closeDatabase();
    }

    private void addToAnswers(PatientAnswers answer) {

        if(!answers.contains(answer)) {
            answers.add(answer);
        }

    }

    private void checkForRuledOutImpression(int impressionId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Log.d("Impression id", impressionId + "");
        ArrayList<String> hardSymptoms = getBetterDb.getHardSymptoms(impressionId);

//        for(String hard : hardSymptoms) {
//            Log.d("Hard symptoms: ", hard);
//        }

//        for(int i = 0; i < ruledOutSymptomList.size(); i++)
//            Log.d("ruled out", ruledOutSymptomList.get(i));

        if(ruledOutSymptomList.containsAll(hardSymptoms)) {
            ruledOutImpressionList.add(impressionsSymptoms.get(currentImpressionIndex).getImpression());
        } else {
            plausibleImpressionList.add(impressionsSymptoms.get(currentImpressionIndex).getImpression());
        }

        getBetterDb.closeDatabase();
    }

    private boolean isSymptomFamilyPositive (int symptomFamilyId) {

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean value = getBetterDb.symptomFamilyAnswerStatus(symptomFamilyId);
        getBetterDb.closeDatabase();
        return value;
    }
    //TODO: [REALLY NOT URGENT] You can refactor this. dont need to save the answers to database. just process it as it is. also remove the saveAnswersToDatabase
    public String getHPI(){
        String HPI = generateIntroductionSentence();
        ArrayList<PositiveResults> positiveSymptoms;

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        positiveSymptoms = getBetterDb.getPositiveSymptoms(answers);

        //TODO:[NOT URGENT] Use of 'his' it should be gender specific
        for(int i = 0; i < positiveSymptoms.size(); i++) {
            if (positiveSymptoms.get(i).getPositiveName() == "High Fever") {
                HPI += "His " + positiveSymptoms.get(i).getPositiveAnswerPhrase() + " ";
            } else {
                HPI += "He " + positiveSymptoms.get(i).getPositiveAnswerPhrase() + " ";
            }
        }
        return HPI;
    }

    private String generateIntroductionSentence () {

        String introductionSentence = "";
        //TODO: Should get from the database

        String patientGender = "Female";
        String patientName = "Elsa";
        int patientAge = 20;
        String attachComplaints = "being cold";

        introductionSentence = "A " + patientGender + " patient, " + patientName + ", who is " + patientAge + " years old, " +
                " is complaining about " + attachComplaints();


        return introductionSentence;
    }

    private String attachComplaints () {

        String complaints = "";

        String [] chiefComplaints = getChiefComplaints();

        //TODO: Have a default value. what if more than 3.
        switch (chiefComplaints.length) {

            case 1: complaints += " " + chiefComplaints[0] + ". ";
                break;
            case 2: complaints += " " + chiefComplaints[0] + " and " + chiefComplaints[1] + ". ";
                break;
            case 3: complaints += " " + chiefComplaints[0] + ", " + chiefComplaints[1] + ", and " + chiefComplaints[2] + ". ";
                break;
        }


        return complaints;
    }

    private String[] getChiefComplaints () {
        String [] chiefComplaints = new String[patientChiefComplaints.size()];

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < chiefComplaints.length; i++) {
            chiefComplaints[i] = getBetterDb.getChiefComplaints(patientChiefComplaints.get(i).getComplaintID());
        }

        getBetterDb.closeDatabase();

        return chiefComplaints;
    }
}
