package models.colorvision;

/**
 * Created by Kate on 03/22/2016.
 * The ColorVisionResult stores
 * the results of the color vision
 * test. The class will contain
 * the score and the interpretation
 * of the exam for an eye.
 */
public class ColorVisionResult {

    private int score;
    private String interpretation;

    public ColorVisionResult(int score, String interpretation) {
        this.score = score;
        this.interpretation = interpretation;
    }

    public int getScore() {
        return score;
    }

    public String getInterpretation() {
        return interpretation;
    }
}
