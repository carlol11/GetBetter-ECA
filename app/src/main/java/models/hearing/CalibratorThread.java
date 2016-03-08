package models.hearing;

import android.content.Context;
import android.media.AudioTrack;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Kate on 03/08/2016.
 *
 * The CalibratorThread class allows for
 * multithreading to be performed with the
 * Calibrator class.
 *
 * The following class is based on the Calibration.java class created by
 * Reece Stevens (2014). The source code is available under the MIT License and
 * is published through a public GitHub repository:
 * https://github.com/ReeceStevens/ut_ewh_audiometer_2014/blob/master/app/src/main/java/ut/ewh/audiometrytest/Calibration.java
 */
public class CalibratorThread extends Thread {

    final private int frequencies[] = {500, 1000, 3000, 4000, 6000, 8000};
    final private int volume = 30000;
    final private double gain = 0.0044;
    final private double alpha = 0.9;
    final public double[] dbHLCorrectionCoefficients = {13.5, 7.5, 11.5, 12, 16, 15.5};

    private Calibrator calibrator;
    private double calibrationArray[];
    private Context context;

    public CalibratorThread(Context context){
        calibrator = new Calibrator();
        calibrationArray = new double[frequencies.length];
        this.context = context;
    }

    public void run(){
        calibrator.startThread();
        System.out.println("Start Thread");
        for(int i = 0; i<frequencies.length; i++){
            int frequency = frequencies[i];
            final float increment = (float) (Math.PI) * frequency / calibrator.sampleRate;

            AudioTrack audioTrack = calibrator.playSound(calibrator.generateSound(increment, volume));
            System.out.println("Played sound");

            if(!calibrator.isRunning()){
                return;
            }

            System.out.println("HELLO");

            double backgroundRms[] = calibrator.dbListen(frequency);
            audioTrack.play();
            double soundRms[] = calibrator.dbListen(frequency);

            System.out.println("listened");

            double resultingRms[] = new double[5];
            double resultingDb[] = new double[5];

            for(int j = 0; j< resultingRms.length; j++){
                resultingRms[j] = soundRms[j]/backgroundRms[j];
                resultingDb[j] = 20 * Math.log10(resultingRms[j]) + 70;
                resultingDb[j] -= dbHLCorrectionCoefficients[i];
            }

            double rmsSum = 0;
            for (double rms: resultingRms){
                if(rms > 0){
                    rmsSum += rms;
                }
            }

            double dbAverage = 0;
            for(double db : resultingDb){
                dbAverage += db;
            }
            dbAverage /= resultingDb.length;

            calibrationArray[i] = dbAverage / volume;

            if(!calibrator.isRunning()){
                return;
            }

            try{
                Thread.sleep(1000);
            } catch(InterruptedException e){

            }

            audioTrack.release();
            System.out.println("Frequency tested");
        }

        int counter = 0;
        byte calibrationByteArray[] = new byte[calibrationArray.length * 8];
        for(double calibration : calibrationArray){
            byte tempByteArray[] = new byte[8];
            ByteBuffer.wrap(tempByteArray).putDouble(calibration);
            for(byte b : tempByteArray){
                calibrationByteArray[counter] = b;
                counter++;
            }
        }

        try{
            FileOutputStream fos = context.openFileOutput("HearingTestCalibrationPreferences", Context.MODE_PRIVATE);
            try{
                fos.write(calibrationByteArray);
                fos.close();
            } catch(IOException ioe){

            }
        } catch(FileNotFoundException fe){

        }

        System.out.println("CALIBRATION DONE");
    }

    public void stopThread(){
        calibrator.stopThread();
    }
}
