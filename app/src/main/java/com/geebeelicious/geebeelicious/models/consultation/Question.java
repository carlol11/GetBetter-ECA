package com.geebeelicious.geebeelicious.models.consultation;

/**
 * Created by mgmalana
 * Question class is used inside for getting the
 * question and the emotion of the question.
 */
public class Question {
    int emotion; //1 = happy1, 2 = happy2, 3 = happy3, 4 = concern1, 5 = concern2, 6 = concern3
    String questionstring;

    public Question(int emotion, String questionstring) {
        this.emotion = emotion;
        this.questionstring = questionstring;
    }

    public int getEmotion() {
        return emotion;
    }

    public String getQuestionstring() {
        return questionstring;
    }
}
