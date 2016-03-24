package models.consultation;

public class ChiefComplaint {
    private int complaintID;
    private String question;

    public ChiefComplaint(int complaintID, String question) {
        this.complaintID = complaintID;
        this.question = question;
    }

    public int getComplaintID() {
        return complaintID;
    }

    public String getQuestion() {
        return question;
    }
}
