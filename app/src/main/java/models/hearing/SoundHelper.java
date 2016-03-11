package models.hearing;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by Kate on 03/11/2016.
 */
public class SoundHelper {

    private int numSamples;
    private int sampleRate;

    public SoundHelper(int numSamples, int sampleRate){
        this.numSamples = numSamples;
    }

    public byte[] generateSound(float increment, int volume){
        float angle = 0;
        double sample[] = new double[numSamples];
        byte generatedSound[] = new byte[2 * numSamples];
        for(int i = 0; i<numSamples; i++){
            sample[i] = Math.sin(angle);
            angle+=increment;
        }

        int index = 0;
        for (final double dVal: sample){
            final short val = (short) ((dVal * volume)); //volume controlled by the value multiplied by dVal; max value is 32767
            generatedSound[index++] = (byte) (val & 0x00ff);
            generatedSound[index++] = (byte) ((val & 0xff00) >>> 8);
        }

        return generatedSound;
    }

    public AudioTrack playSound(byte[] generatedSound){
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, generatedSound.length, AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSound, 0, generatedSound.length);
        return audioTrack;
    }

    public AudioTrack playSound(byte[] generatedSound, int ear){
        AudioTrack audioTrack = playSound(generatedSound);
        if(ear == 0){
            audioTrack.setStereoVolume(0, AudioTrack.getMaxVolume());
        } else{
            audioTrack.setStereoVolume(AudioTrack.getMaxVolume(), 0);
        }
        audioTrack.play();
        return audioTrack;
    }

}
