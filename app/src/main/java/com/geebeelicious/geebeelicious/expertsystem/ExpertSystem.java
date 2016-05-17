package com.geebeelicious.geebeelicious.expertsystem;

import android.content.Context;
import android.util.Log;

import com.geebeelicious.geebeelicious.database.DatabaseAdapter;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;

import models.consultation.ChiefComplaint;
import models.consultation.HPI;
import models.consultation.Impressions;
import models.consultation.Patient;
import models.consultation.PatientAnswers;
import models.consultation.PositiveResults;
import models.consultation.Symptom;
import models.consultation.SymptomFamily;

/*
* The original code was created by Mike Dayupay 2015.
* For the purpose of integration, the code was modified by Mary Grace Malana (2015).
* The ExpertSystem class gives specific questions depending on the input of the user
* It uses the DatabaseAdapter class to connect with the database.
*/


public class ExpertSystem {
    private final static String TAG = "ExpertSystem";

    private ArrayList<Impressions> impressionsSymptoms;
    private ArrayList<String> ruledOutImpressionList;
    private ArrayList<String> plausibleImpressionList;
    private ArrayList<String> positiveSymptomList;
    private ArrayList<String> ruledOutSymptomList;
    private ArrayList<Symptom> questions;
    private ArrayList<PatientAnswers> answers;
    private ArrayList<ChiefComplaint> patientChiefComplaints;
    private Patient patient;

    private int currentImpressionIndex;
    private int currentSymptomIndex;
    private int symptomFamilyId;
    private int caseRecordId;

    private boolean flag;

    private DatabaseAdapter getBetterDb;
    private SymptomFamily generalQuestion;


    private DateFormat dateFormat;

    public ExpertSystem(Context context, Patient patient){
        ruledOutSymptomList = new ArrayList<>();
        ruledOutImpressionList = new ArrayList<>();
        plausibleImpressionList = new ArrayList<>();
        positiveSymptomList = new ArrayList<>();
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        this.patient = patient;
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");


        Log.d(TAG, patient.getFirstName());

        initializeDatabase(context);

        caseRecordId = 0; //just used for placeholder, since the old code saves the users' answers
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

    public void saveToDatabase(HPI hpi) {
        getBetterDb.insertHPI(hpi);
        getBetterDb.closeDatabase(); //closes the database
    }



    public ArrayList<ChiefComplaint> getChiefComplaintsQuestions(){
        ArrayList <ChiefComplaint> chiefComplaints = new ArrayList<>();

        chiefComplaints.add(new ChiefComplaint(1, "Do you have fever?"));
        chiefComplaints.add(new ChiefComplaint(2, "Are you experiencing any pain?"));
        chiefComplaints.add(new ChiefComplaint(3, "Do you have injury?"));
        chiefComplaints.add(new ChiefComplaint(4, "Do you have any skin problem?"));
        chiefComplaints.add(new ChiefComplaint(5, "Are you having breathing problems?"));
        chiefComplaints.add(new ChiefComplaint(6, "Are you having bowel movement problems?"));
        chiefComplaints.add(new ChiefComplaint(7, "Are you experiencing general unwellness?"));

        return chiefComplaints;
    }

    private void initializeDatabase (Context context) {

        getBetterDb = new DatabaseAdapter(context);

        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetDatabaseFlags() {
        getBetterDb.resetSymptomAnsweredFlag();
        getBetterDb.resetSymptomFamilyFlags();
    }

    private void initializeImpressionList () {

        impressionsSymptoms = new ArrayList<>();
        impressionsSymptoms.addAll(getBetterDb.getImpressions(patientChiefComplaints));

        impressionsSymptoms = new ArrayList<>(new LinkedHashSet<>(impressionsSymptoms)); // Remove duplicates

        for(int i = 0; i < impressionsSymptoms.size(); i++) {
            ArrayList<Symptom> symptoms = getBetterDb.getSymptoms(impressionsSymptoms.get(i).getImpressionId());
            impressionsSymptoms.get(i).setSymptoms(symptoms);
        }
    }

    private void updateAnsweredStatusSymptomFamily() {

        for(ChiefComplaint c: patientChiefComplaints){
            getBetterDb.updateAnsweredStatusSymptomFamily(c.getComplaintID());
        }
    }

    private void updateAnsweredStatusSymptomFamily(int answer) {
        getBetterDb.updateAnsweredStatusSymptomFamily(generalQuestion.getSymptomFamilyId(), answer);
    }

    private void getQuestions(int impressionId) {
        questions.clear();
        questions.addAll(getBetterDb.getQuestions(impressionId));
        //Log.d("questions size", questions.size() + "");
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
        boolean value = getBetterDb.symptomFamilyIsAnswered(symptomFamilyId);
        return value;
    }

    private void getGeneralQuestion (int symptomFamilyId) {
        generalQuestion = getBetterDb.getGeneralQuestion(symptomFamilyId);
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
        getBetterDb.updateAnsweredFlagPositive(symptomId);
    }

    private void addToAnswers(PatientAnswers answer) {

        if(!answers.contains(answer)) {
            answers.add(answer);
        }

    }

    private void checkForRuledOutImpression(int impressionId) {

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
    }

    private boolean isSymptomFamilyPositive (int symptomFamilyId) {

        boolean value = getBetterDb.symptomFamilyAnswerStatus(symptomFamilyId);
        return value;
    }

    public String getHPI(){
        String HPI = generateIntroductionSentence();
        ArrayList<PositiveResults> positiveSymptoms;

        positiveSymptoms = getBetterDb.getPositiveSymptoms(answers);

        //TODO:[NOT SCOPE] Use of 'his' it should be gender specific
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
        String patientGender = getGender(patient.getGender());
        String patientName = patient.getFirstName() + " " + patient.getLastName();
        int patientAge = getAge(patient.getBirthday());

        introductionSentence = "A " + patientGender + " patient, " + patientName + ", who is " + patientAge + " years old, " +
                " is complaining about " + attachComplaints();

        return introductionSentence;
    }

    private String attachComplaints () {

        String complaints = "";

        String [] chiefComplaints = getChiefComplaints();

        //TODO:[NOT SCOPE] Have a default value. what if more than 3.
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

        for(int i = 0; i < chiefComplaints.length; i++) {
            chiefComplaints[i] = getBetterDb.getChiefComplaints(patientChiefComplaints.get(i).getComplaintID());
        }

        return chiefComplaints;
    }

    private String getGender(int gender){
        return (gender == 0 ? "male" : "female");
    }

    //Returns the age depending on the birthdate
    private int getAge(String birthdayString){
        Calendar birthday = Calendar.getInstance();
        Calendar currentDate = Calendar.getInstance();

        try {
            birthday.setTime(dateFormat.parse(birthdayString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (int)((currentDate.getTimeInMillis() - birthday.getTimeInMillis()) / 31536000000L); //divides by number of milliseconds in a year
    }

}
