package models.hearing;

/**
 * Created by Kate on 03/22/2016.
 * The HearingResult class
 * contains the results per frequency,
 * the pure tone average, and the
 * pure tone average interpretation
 * for each ear for the Hearing Test.
 */

public class HearingResult {

    final private int[] testingFrequencies = {500, 1000, 2000};

    private String ear;
    private double[] thresholds = {0, 0, 0};
    private double pureToneAverage;
    private String pureToneAverageInterpretation;

    public HearingResult(String ear, double[] thresholds, double pureToneAverage, String pureToneAverageInterpretation) {
        this.ear = ear;
        this.thresholds = thresholds;
        this.pureToneAverage = pureToneAverage;
        this.pureToneAverageInterpretation = pureToneAverageInterpretation;
    }

    public int[] getTestingFrequencies() {
        return testingFrequencies;
    }

    public String getEar() {
        return ear;
    }

    public double[] getThresholds() {
        return thresholds;
    }

    public double getPureToneAverage() {
        return pureToneAverage;
    }

    public String getPureToneAverageInterpretation() {
        return pureToneAverageInterpretation;
    }
}
