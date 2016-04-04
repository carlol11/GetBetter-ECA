package models.hearing;

import android.content.Context;
import android.media.AudioTrack;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Kate on 03/11/2016.
 *
 * The HearingTest class functions
 * to help manage the proctoring of the
 * audiometry hearing test.
 *
 *  * The following class is based on the TestProctoring.java class created by
 * Reece Stevens (2014). The source code is available under the MIT License and
 * is published through a public GitHub repository:
 * https://github.com/ReeceStevens/ut_ewh_audiometer_2014/blob/master/app/src/main/java/ut/ewh/audiometrytest/TestProctoring.java
 */
public class HearingTest {

    final private int duration = 1;
    final private int sampleRate = 44100;
    final private int numSamples = duration * sampleRate;
    final private int volume = 32767;
    final private int[] testingFrequencies = {500, 1000, 2000};
    final private double mGain = 0.0044;
    final private double mAlpha = 0.9;

    private boolean isHeard = false;
    private boolean inLoop = true;
    private boolean isDone = false;

    private static boolean isRunning = true;
    private double[] thresholdsRight = {0, 0, 0};
    private double[] thresholdsLeft = {0, 0, 0};

    public HearingTest(){
        this.isHeard = false;
        this.inLoop = true;
        this.isDone = false;
        isRunning = true;
        thresholdsRight = new double[]{0, 0, 0};
        thresholdsLeft = new double[]{0, 0, 0};
    }

    private int getRandomGapDuration() {
        int time;
        double random = Math.random();
        if (random < 0.3) {
            time = 2000;
        } else if (random < 0.67 && random >= 0.3) {
            time = 2500;
        } else {
            time = 3000;
        }
        return time;
    }

    public double[] getCalibrationData(Context context){
        byte calibrationByteData[] = new byte[24];

        try{
            FileInputStream fis = context.openFileInput("HearingTestCalibrationPreferences");
            fis.read(calibrationByteData, 0, 24);
            fis.close();
        } catch(IOException e){
        }

        final double calibrationArray[] = new double[3];
        int counter = 0;
        for(int i = 0; i<calibrationArray.length; i++){
            byte tempByteBuffer[] = new byte[8];
            for(int j = 0; j<tempByteBuffer.length; j++){
                tempByteBuffer[j] = calibrationByteData[counter];
                counter++;
            }
            calibrationArray[i] = ByteBuffer.wrap(tempByteBuffer).getDouble();
        }
        return calibrationArray;
    }

    public void performTest(double[] calibrationArray) {
        SoundHelper soundHelper = new SoundHelper(numSamples, sampleRate);
        for (int e = 0; e < 2; e++) {
            for (int i = 0; i < testingFrequencies.length; i++) {
                int frequency = testingFrequencies[i];
                float increment = (float) (Math.PI) * frequency / sampleRate;
                int maxVolume = volume;
                int minVolume = 0;

                //Loop for each individual sample using binary search algorithm
                for (; ; ) {
                    int tempResponse = 0;
                    int actualVolume = (minVolume + maxVolume) / 2;
                    if ((maxVolume - minVolume) < 50) {
                        if(e == 0){
                            thresholdsRight[i] = actualVolume * calibrationArray[i];
                        }
                        else if(e == 1){
                            thresholdsLeft[i] = actualVolume * calibrationArray[i];
                        }else{

                        }
                        break;
                    } else {
                        for (int z = 0; z < 3; z++) {
                            isHeard = false;
                            if (!isRunning) {
                                return;
                            }

                            AudioTrack audioTrack = soundHelper.playSound(soundHelper.generateSound(increment, actualVolume), e);
                            try {
                                Thread.sleep(getRandomGapDuration());

                            } catch (InterruptedException ie) {

                            }
                            audioTrack.release();
                            if (isHeard) {
                                tempResponse++;
                            }
                            //Check if first two test were positive, skips the third to speed up the test
                            if (tempResponse >= 2) {
                                break;
                            }
                            //Check if first two were misses and skips the third
                            if (z == 1 && tempResponse == 0) {
                                break;
                            }
                        }
                        //If response was 2/3, register as heard
                        if (tempResponse >= 2) {
                            maxVolume = actualVolume;
                        } else {
                            minVolume = actualVolume;
                        }
                    } //Continue with test
                }
            }
            //Run
        }
        inLoop = false;
        isDone = true;
    }

    public void setHeard(){
        isHeard = true;
    }

    public boolean isHeard(){
        return isHeard;
    }

    public boolean isDone(){
        return isDone;
    }

    public boolean isInLoop(){
        return inLoop;
    }

    public void setIsNotRunning(){
        isRunning = false;
    }

    public boolean isRunning(){
        return isRunning;
    }

    private double getPureToneAverage(double[] testResults){
        double result = 0;
        for(double d : testResults){
            result += d;
        }
        return (result / testResults.length);
    }

    private String getResultsPerFrequency(double[] testResults){
        String result = "";

        for(int i = 0; i<testResults.length; i++){
            result+=(testingFrequencies[i] + " Hz: " + String.format("%.2f", testResults[i]) + " db HL\n");

        }
        return result;
    }

    private String interpretPureToneAverage(double result){
        if(result <= 20){
            return "Normal Hearing";
        } else if(result >= 21 && result <= 40){
            return "Mild Hearing Loss";
        } else if(result >=41 && result <= 55){
            return "Moderate Hearing Loss";
        } else if(result >= 56 && result <= 70){
            return "Moderately-Severe Hearing Loss";
        } else if(result >= 71 && result <= 90){
            return "Severe Hearing Loss";
        } else {
            return "Profound Hearing Loss";
        }
    }

    private String getPureToneAverageResults(double[] testResults){
        double ptaResult = getPureToneAverage(testResults);
        String result = "";
        result += "Pure Tone Average: " + String.format("%.2f",ptaResult) + " dB HL";
        result += "\nYou have " + interpretPureToneAverage(ptaResult) + ".";
        return result;
    }

    public String getResults(){
        String result = "";
        result += "Right Ear\n" + getResultsPerFrequency(thresholdsRight) + getPureToneAverageResults(thresholdsRight);
        result += "\n\nLeft Ear\n" + getResultsPerFrequency(thresholdsLeft) + getPureToneAverageResults(thresholdsLeft);
        return result;
    }

    public String getPureToneAverageInterpretation(String ear){
        double[] thresholds;
        if(ear.equals("Right")){
            thresholds = thresholdsRight;
        }else{
            thresholds = thresholdsLeft;
        }
        return interpretPureToneAverage(getPureToneAverage(thresholds));
    }





}


