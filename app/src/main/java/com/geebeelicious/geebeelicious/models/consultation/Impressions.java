package com.geebeelicious.geebeelicious.models.consultation;

import java.util.ArrayList;

/**
 * GetBetter. Created by Mike Dayupay on 9/21/15.
 */
public class Impressions {

    private int impressionId;
    private String impression;
    private String scientificName;
    private String localName;
    private String treatmentProtocol;
    private String remarks;
    private ArrayList<Symptom> symptoms;

    public Impressions(int impressionId, String impression, String scientificName, String localName,
                       String treatmentProtocol, String remarks) {
        this.impressionId = impressionId;
        this.impression = impression;
        this.scientificName = scientificName;
        this.localName = localName;
        this.treatmentProtocol = treatmentProtocol;
        this.remarks = remarks;
    }

    public int getImpressionId() {
        return impressionId;
    }

    public String getImpression() {
        return impression;
    }

    public void setImpression(String impression) {
        this.impression = impression;
    }

    public ArrayList<Symptom> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(ArrayList<Symptom> symptoms) {
        this.symptoms = symptoms;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getLocalName() {
        return localName;
    }

    public String getTreatmentProtocol() {
        return treatmentProtocol;
    }

    public String getRemarks() {
        return remarks;
    }

    public int hashCode() {

        return impressionId;
    }

    @Override
    public boolean equals(Object object) {

        boolean similar = false;

        if(object !=null && object instanceof Impressions) {
            similar = this.impressionId == ((Impressions)object).impressionId;
        }

        return similar;
    }
}
