package com.geebeelicious.geebeelicious.models.consultation;

/**
 * Created by mgmalana
 * Question class is used inside for getting the
 * question and the emotion of the question.
 */
public class Question {
    int emotion;
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
