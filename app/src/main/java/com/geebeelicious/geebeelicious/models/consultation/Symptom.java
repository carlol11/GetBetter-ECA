package com.geebeelicious.geebeelicious.models.consultation;

/**
 * GetBetter. Created by Mike Dayupay on 10/17/15.
 */
public class Symptom {

    private int symptomId;
    private String symptomNameEnglish;
    private String symptomNameTagalog;
    private String questionEnglish;
    private String questionTagalog;
    private String responsesEnglish;
    private String responsesTagalog;
    private int symptomFamilyId;

    public Symptom(int symptomId, String symptomNameEnglish, String symptomNameTagalog,
                   String questionEnglish, String questionTagalog, String responsesEnglish,
                   String responsesTagalog, int symptomFamilyId) {

        this.symptomId = symptomId;
        this.symptomNameEnglish = symptomNameEnglish;
        this.symptomNameTagalog = symptomNameTagalog;
        this.questionEnglish = questionEnglish;
        this.questionTagalog = questionTagalog;
        this.responsesEnglish = responsesEnglish;
        this.responsesTagalog = responsesTagalog;
        this.symptomFamilyId = symptomFamilyId;
    }

    public Symptom(int symptomId, String symptomNameEnglish) {

        this.symptomId = symptomId;
        this.symptomNameEnglish = symptomNameEnglish;
    }

    public int getSymptomId() {
        return symptomId;
    }

    public void setSymptomId(int symptomId) {
        this.symptomId = symptomId;
    }

    public String getSymptomNameEnglish() {
        return symptomNameEnglish;
    }

    public void setSymptomNameEnglish(String symptomNameEnglish) {
        this.symptomNameEnglish = symptomNameEnglish;
    }

    public String getSymptomNameTagalog() {
        return symptomNameTagalog;
    }

    public void setSymptomNameTagalog(String symptomNameTagalog) {
        this.symptomNameTagalog = symptomNameTagalog;
    }

    public String getQuestionEnglish() {
        return questionEnglish;
    }

    public void setQuestionEnglish(String questionEnglish) {
        this.questionEnglish = questionEnglish;
    }

    public String getQuestionTagalog() {
        return questionTagalog;
    }

    public void setQuestionTagalog(String questionTagalog) {
        this.questionTagalog = questionTagalog;
    }

    public String getResponsesEnglish() {
        return responsesEnglish;
    }

    public void setResponsesEnglish(String responsesEnglish) {
        this.responsesEnglish = responsesEnglish;
    }

    public String getResponsesTagalog() {
        return responsesTagalog;
    }

    public void setResponsesTagalog(String responsesTagalog) {
        this.responsesTagalog = responsesTagalog;
    }

    public int getSymptomFamilyId() {
        return symptomFamilyId;
    }

    public void setSymptomFamilyId(int symptomFamilyId) {
        this.symptomFamilyId = symptomFamilyId;
    }
}
