package com.geebeelicious.geebeelicious.models.consultation;

import android.content.Context;
import android.util.Log;

import com.geebeelicious.geebeelicious.expertsystem.ExpertSystem;

import java.util.ArrayList;

/* Created by Mary Grace Malana on 3/22/2016
* The ConsultationHelper class serves to generate chief complaint
* questions, and retrieves succeeding questions from the ExpertSystem.
*/

public class ConsultationHelper {
    private static String TAG = "ConsultationHelper";

    private boolean isConsultationDone;
    private boolean isAskingChiefComplaint;
    private int currentChiefComplaint;
    private ArrayList<ChiefComplaint> chiefComplaints;
    private ArrayList<ChiefComplaint> patientChiefComplaints;
    private ExpertSystem expertSystem;
    private Patient patient;
    private String dateConsultation;

    public ConsultationHelper(Context context, Patient patient, String dateConsultation) {
        isConsultationDone = false;
        isAskingChiefComplaint = true;
        currentChiefComplaint = 0;
        chiefComplaints = new ArrayList<ChiefComplaint>();
        patientChiefComplaints = new ArrayList<ChiefComplaint>();
        this.patient = patient;
        this.dateConsultation = dateConsultation;
        expertSystem = new ExpertSystem(context, patient);

        chiefComplaints = expertSystem.getChiefComplaintsQuestions();
    }

    public boolean isConsultationDone() {
        return isConsultationDone;
    }

    public String getNextQuestion(boolean isYes) { //returns null if there's no more questions
        if(isAskingChiefComplaint){ //if asking about chief complaint
            if(isYes){ //if isYes, adds to patient's chief complaints
                ChiefComplaint complaint = chiefComplaints.get(currentChiefComplaint);
                Log.d(TAG, "Added complaint with question '" + complaint.getQuestion() + "' to patient's chief complaint");
                patientChiefComplaints.add(complaint);
            }
            currentChiefComplaint++;

            if(currentChiefComplaint == chiefComplaints.size()) { //checks if no more questions
                isAskingChiefComplaint = false; //to skip this parent conditional statement
                if(patientChiefComplaints.size() == 0)
                    return null;
                return expertSystem.startExpertSystem(patientChiefComplaints);
            } else {
                return chiefComplaints.get(currentChiefComplaint).getQuestion();
            }
        } else {
            String question = expertSystem.getNextQuestion(isYes);
            if(question == null) { //if there's no more question, consultation is done. also saves the answers
                isConsultationDone = true;
            }
            return question;
        }
    }


    public String getFirstQuestion(){
        return chiefComplaints.get(0).getQuestion();
    }

    public String getHPI(){
        return expertSystem.getHPI();
    }

    public void saveToDatabase(String hpi){
        expertSystem.saveToDatabase(new HPI(patient.getPatientID(), hpi, dateConsultation));
    }

    public boolean isTherePatientComplaints(){
        return patientChiefComplaints.size() > 0;
    }
}
