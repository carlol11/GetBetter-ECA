package models.consultation;

/**
 * The ChiefComplaint class represents a chief complaint
 * in the expert system. It contains a complaintID and a
 * question that will be asked to the user.
 */

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
