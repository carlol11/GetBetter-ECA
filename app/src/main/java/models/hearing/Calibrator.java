package models.hearing;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

/**
 * Created by Kate on 03/07/2016.
 * The Calibrator class functions to calibrate
 * the earphones used by the device. The
 * volume will be adjusted depending on the
 * device.
 *
 * The following class is based on the Calibration.java class created by
 * Reece Stevens (2014). The source code is available under the MIT License and
 * is published through a public GitHub repository:
 * https://github.com/ReeceStevens/ut_ewh_audiometer_2014/blob/master/app/src/main/java/ut/ewh/audiometrytest/Calibration.java
 */

public class Calibrator {
    final public int sampleRate = 44100;
    final public int numSamples = 4 * sampleRate;
    final public int bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

    private double[] inputSignalImaginary = new double[2048];
    private static boolean running = true;


    public int newBitReverse(int i){
        int a = 0;
        while(i!=0){
            a<<=1;
            a|=(i &1);
            i>>=1;
        }
        return a;
    }

    //FFT (Fast Fourier Transform) Analysis
    public double[] fftAnalysis(double[] inputReal, double[] inputImaginary){
        int n = 2048;
        int nu = 11;
        double[] bufferReal = new double[n];
        double[] shortenedReal = new double[n];

        //Shorten buffer data to a power of two
        for(int i = 0; i<n; i++){
            shortenedReal[i] = inputReal[i];
        }

        double[][] real = new double[nu+1][n];
        int counter = 2;
        for(int i=1;i<nu;i++){
            for(int j = 0; j<n; j++){
                real[i][j] = Math.cos(((double)2) * Math.PI * ((double)i) / ((double)counter));
            }
            counter *= 2;
        }

        double[][] imaginary = new double[nu+1][n];
        counter = 2;
        for(int i = 1; i<=nu; i++){
            for(int j=0; j<n; j++){
                imaginary[i][j] = -1 * Math.sin(((double)2) * Math.PI * ((double)i) / ((double)counter));
            }
            counter *=2;
        }

        //populate bufferReal with inputReal in bit-reversed order
        for(int k = 0; k<shortenedReal.length; k++){
            int reversedBit = newBitReverse(k);
            bufferReal[k] = shortenedReal[reversedBit];
        }

        //Begin Fast Fourier Transform (FFT)
        int step = 1;
        for(int level = 1; level<=nu; level++){
            int incrementValue = step * 2;
            for(int j = 0; j<step; j++){
                for(int i = j; i<n; i+=incrementValue){
                    double realCoefficient = real[level][j];
                    double imaginaryCoefficient = imaginary[level][j];
                    realCoefficient *= bufferReal[i+step];
                    imaginaryCoefficient *= bufferReal[i+step];
                    bufferReal[i+step] = bufferReal[i];
                    inputImaginary[i+step] = inputImaginary[i];
                    bufferReal[i] += realCoefficient;
                    inputImaginary[i] += imaginaryCoefficient;
                }
            }
            step *= 2;
        }

        double[] transformResult = new double[bufferReal.length];
        //Calculate magnitude of FFT coefficients
        for(int i = 0; i<bufferReal.length; i++){
            transformResult[i] = Math.sqrt(Math.pow(bufferReal[i], 2) + Math.pow(inputImaginary[i], 2));
        }

        return transformResult;
    }

    public double[] dbListen(int frequency){
        double rmsArray[] = new double[5];
        for(int i = 0; i<rmsArray.length; i++){
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            short[] buffer = new short[bufferSize];

            try{
                audioRecord.startRecording();
            }
            catch(IllegalStateException e){

            }

            //Convert buffer from short[] to double[]
            double[] inputSignal = new double[buffer.length];
            for(int j = 0; j < buffer.length; j++){
                inputSignal[j] = (double)buffer[j];
            }

            double[] outputSignal = fftAnalysis(inputSignal, inputSignalImaginary);

            //Select value from transform array corresponding to desired frequency
            int k = frequency * 2048 / sampleRate;
            rmsArray[i] = outputSignal[k];

            //FFT Decibel Calculation
            audioRecord.stop();
            audioRecord.release();
        }
        return rmsArray;
    }

    public static void stopThread(){
        running = false;
    }

    public static void startThread(){
        running = true;
    }

    public boolean isRunning(){
        return running;
    }

}
