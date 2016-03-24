package models.consultation;

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
        initializeQuestions();
        expertSystem = new ExpertSystem(context, patient);
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
                return expertSystem.startExpertSystem(patientChiefComplaints);
                //TODO: Handle if all no for chief complain
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
        //TODO: have a checker if meron ba talaga chiefComplaints sa list. baka kasi will cause an error
        return chiefComplaints.get(0).getQuestion();
    }

    private void initializeQuestions(){
        //TODO: [NOT URGENT] Transfer this questions if you think it's better. Fix questions, change wording
        chiefComplaints.add(new ChiefComplaint(1, "Do you have fever?"));
        chiefComplaints.add(new ChiefComplaint(2, "Are you experiencing any pain?"));
        chiefComplaints.add(new ChiefComplaint(3, "Do you have injury?"));
        chiefComplaints.add(new ChiefComplaint(4, "Do you have any skin problem?"));
        chiefComplaints.add(new ChiefComplaint(5, "Are you having breathing problems?"));
        chiefComplaints.add(new ChiefComplaint(6, "Are you having bowel movement problems?"));
        chiefComplaints.add(new ChiefComplaint(7, "Are you experiencing general unwellness?"));
    }

    public String getHPI(){
        return expertSystem.getHPI();
    }

    public void saveToDatabase(String hpi){
        expertSystem.saveToDatabase(new HPI(patient.getPatientID(), hpi, dateConsultation));
    }
}
