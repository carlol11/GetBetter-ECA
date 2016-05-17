package models.finemotor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaPlayer;

import android.widget.ImageView;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import java.util.Random;


/**
 * Created by Mary Grace Malana on 25/03/2016.
 * serves as the helper class of FineMotorActivity
 * sets the conditions for each round of the test
 */
public class FineMotorHelper {

    private String TAG = "FineMotorHelper";

    private static final int MAX_NUM_WRONG = 2;

    private ImageView imageViewPathToTrace;
    private MediaPlayer mp;

    private boolean wasOutside = false; //was user outside the path
    private int numWrongs = 0;

    private boolean[] result = new boolean[3]; //result[i] is true if pass, false if fail

    private String[] instructions;

    public FineMotorHelper(Context context, ImageView imageViewPathToTrace) {
        this.imageViewPathToTrace = imageViewPathToTrace;
        int pathNumber = getRandomPathDrawable();

        mp = MediaPlayer.create(context, R.raw.fine_motor_outside_path);
        mp.setLooping(true);

        imageViewPathToTrace.setImageResource(pathNumber);
        instructions = getInstructions(pathNumber);
    }

    public boolean[] getResults(){
        return result;
    }

    public void setResult(int index, boolean isYes){
        result[index] = isYes;
    }

    //called if the user is outside the path
    public void doIfOutSideThePath(){
        if(!wasOutside){
            mp.start();
            wasOutside = true;
            numWrongs++;
        }
    }
    //if touch is within path
    //returns true if was outside, else false;
    public boolean doIfWithinPath() {
        if (wasOutside){
            pauseMp();
            wasOutside = false;
            return true;
        }
        return false;
    }

    //starts the next test. resets the variables
    public String doNextTest(int currentTest){
        pauseMp();
        result[currentTest] = numWrongs <= MAX_NUM_WRONG;
        numWrongs = 0;
        return setInstructions(currentTest+1);
    }


    public String doIfTouchIsUp() {
        pauseMp();
        return "Don't lift your finger. Go back to start";
    }

    //returns the equivalent x and y coordinates of the bitmap given x and y coordinates of the touch event
    public int[] getBitMapCoordinates(Bitmap bitmap, float eventX, float eventY){
        Matrix invertMatrix = new Matrix();
        float[] eventXY = new float[] {eventX, eventY};
        int x;
        int y;

        ((ImageView)imageViewPathToTrace).getImageMatrix().invert(invertMatrix);
        invertMatrix.mapPoints(eventXY);
        x = (int) eventXY[0];
        y = (int) eventXY[1];

        if(x < 0){
            x = 0;
        }else if(x > bitmap.getWidth()-1) {
            x = bitmap.getWidth()-1;
        }

        if(y < 0){
            y = 0;
        }else if(y > bitmap.getHeight()-1) {
            y = bitmap.getHeight()-1;
        }
        return new int[] {x,y};
    }

    public String setInstructions(int index){
        return(instructions[index]);
    }

    private int getRandomPathDrawable(){
        int[] path = new int[]{R.drawable.path_to_trace_1, R.drawable.path_to_trace_2};
        Random random = new Random((int)System.nanoTime());
        return path[random.nextInt(path.length)];
    }

    //initialized the instructions depending on the chosen path
    private String[] getInstructions(int path) {
        String[] instructionList = null;
        switch(path){
            case R.drawable.path_to_trace_1: instructionList =  new String[]{"Using a finger of your non dominant hand, trace the path. Start from the butterfly and go to the flowers",
                    "Using the pen with your dominant hand, trace the path. Start from the butterfly and go to the flowers",
                    "Assistant, has he/she used the pen without difficulties?"
                    };
                break;
            case R.drawable.path_to_trace_2: instructionList =  new String[]{"Using a finger of your non dominant hand, trace the path. Start from the lion and go to his friends",
                    "Using the pen with your dominant hand, trace the path. Start from the lion and go to his friends",
                    "Assistant, has he/she used the pen without difficulties?"
                    };
                break;
        }
        return instructionList;

    }

    //pause mediaplayer
    private void pauseMp(){
        if(mp.isPlaying()) {
            mp.pause();
        }
    }

}
