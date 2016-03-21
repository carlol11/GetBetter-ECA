package models.grossmotor;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.geebeelicious.geebeelicious.R;

import java.util.Random;

/**
 * Created by Kate on 03/21/2016.
 */
public class MusicPlayer {

    private Context context;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private int[] music;

    public MusicPlayer(Context context){
        this.context = context;
        music = new int[3];
        music[0] = R.raw.gross_motor_1;
        music[1] = R.raw.gross_motor_1;
        music[2] = R.raw.gross_motor_1;
        //TODO: Download more music files

    }

    public void setRandomSong(int duration){
        Random random = new Random();
        int randomSong;
        boolean isFound = false;

        while(!isFound){
            randomSong = music[random.nextInt(2)];
            mediaPlayer = MediaPlayer.create(context, randomSong);
            System.out.println("duration: " + mediaPlayer.getDuration());
            if(mediaPlayer.getDuration()>=duration){
                isFound = true;
                break;
            }
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
    }

    public void playMusic(){
        mediaPlayer.start();
    }

    public void stopMusic(){
        System.out.println("Stopping");
        if(mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            System.out.println("Stopped");
        }
        //mediaPlayer.release();
        mediaPlayer = null;
    }







}
