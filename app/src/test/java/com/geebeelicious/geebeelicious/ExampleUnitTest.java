package com.geebeelicious.geebeelicious;

import com.geebeelicious.geebeelicious.models.bmi.BMICalculator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void computeBMI() throws Exception {
        assertEquals(20f, BMICalculator.computeBMIMetric(158, 50), 0.1f);
    }

    @Test
    public void computeBMIPercentile() throws Exception {
        assertEquals(0, BMICalculator.getBMIResult(true, 2, 14));
        assertEquals(3, BMICalculator.getBMIResult(true, 2, 20));
        assertEquals(2, BMICalculator.getBMIResult(true, 2, 17.97371413f));
        assertEquals(1, BMICalculator.getBMIResult(true, 2, 17));
        assertEquals(4, BMICalculator.getBMIResult(true, 1, 17));
        assertEquals(4, BMICalculator.getBMIResult(true, 21, 17));
    }

    @Test
    public void computeBMIPercentileString() throws Exception {
        assertEquals("Underweight", BMICalculator.getBMIResultString(true, 2, 14));
        assertEquals("Obese", BMICalculator.getBMIResultString(true, 2, 20));
        assertEquals("Overweight", BMICalculator.getBMIResultString(true, 2, 17.97371413f));
        assertEquals("Normal", BMICalculator.getBMIResultString(true, 2, 17));
        assertEquals("N/A", BMICalculator.getBMIResultString(true, 1, 17));
    }
}