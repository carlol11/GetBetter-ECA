package models.consultation;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ConsultationHelper {
    private static String TAG = "ConsultationHelper";

    private boolean isConsultationDone;
    private boolean isAskingChiefComplaint;
    private int currentChiefComplaint;
    private ArrayList<ChiefComplaint> chiefComplaints;
    private ArrayList<ChiefComplaint> patientChiefComplaints;


    public ConsultationHelper() {
        isConsultationDone = false;
        isAskingChiefComplaint = true;
        currentChiefComplaint = 0;
        chiefComplaints = new ArrayList<ChiefComplaint>();
        patientChiefComplaints = new ArrayList<ChiefComplaint>();

        initializeQuestions();
    }




    public boolean isConsultationDone() {
        return isConsultationDone;
    }

    public String getNextQuestion(boolean isYes) {
        if(isAskingChiefComplaint){
            if(isYes){ //if isYes, adds to patient's chief complaints
                ChiefComplaint complaint = chiefComplaints.get(currentChiefComplaint);
                Log.d(TAG, "Added complaint with question '" + complaint.getQuestion() + "' to patient's chief complaint");
                patientChiefComplaints.add(complaint);
            }
            currentChiefComplaint++;
            if(currentChiefComplaint == chiefComplaints.size()){
                isAskingChiefComplaint = false; //to skip this parent conditional statement
                //TODO: Handle if all no for chief complain
            } else {
                return chiefComplaints.get(currentChiefComplaint).getQuestion();
            }
        }
        return "for impressions";
    }

    public String getFirstQuestion(){
        //TODO: have a checker if meron ba tlaaga chiefComplaints
        return chiefComplaints.get(0).getQuestion();
    }

    private void initializeQuestions(){
        //TODO: Transfer this questions if you think it's better. Fix questions, change wording
        chiefComplaints.add(new ChiefComplaint(1, "Do you have fever?"));
        chiefComplaints.add(new ChiefComplaint(2, "Are you experiencing any pain?"));
        chiefComplaints.add(new ChiefComplaint(3, "Do you have injury?"));
        chiefComplaints.add(new ChiefComplaint(4, "Do you have any skin problem?"));
        chiefComplaints.add(new ChiefComplaint(5, "Are you having breathing problems?"));
        chiefComplaints.add(new ChiefComplaint(6, "Are you having bowel movement problems?"));
        chiefComplaints.add(new ChiefComplaint(7, "Are you experiencing general unwellness?"));
    }

}
