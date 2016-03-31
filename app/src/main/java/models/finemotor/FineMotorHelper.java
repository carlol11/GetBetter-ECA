package models.finemotor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaPlayer;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.database.DataAdapter;

import java.sql.SQLException;
import java.util.Random;

import models.consultation.Record;

/**
 * Created by Mary Grace Malana on 25/03/2016.
 * serves as the helper class of FineMotorActivity
 * sets the conditions for each round of the test
 */
public class FineMotorHelper {

    private String TAG = "FineMotorHelper";

    private static final int MAX_NUM_WRONG = 2;

    private ImageView imageViewPathToTrace;
    private TextView ECAtext;

    private DataAdapter getBetterDb;
    private MediaPlayer mp;

    private boolean wasOutside = false; //was user outside the path
    private int numWrongs = 0;

    private boolean[] result = new boolean[3]; //result[i] is true if pass, false if fail

    private String[] instructions;

    public FineMotorHelper(Context context, ImageView imageViewPathToTrace, TextView ECAtext) {
        this.imageViewPathToTrace = imageViewPathToTrace;
        this.ECAtext = ECAtext;
        int pathNumber = getRandomPathDrawable();

        getBetterDb = new DataAdapter(context);

        mp = MediaPlayer.create(context, R.raw.fine_motor_outside_path);
        mp.setLooping(true);

        imageViewPathToTrace.setImageResource(pathNumber);
        instructions = getInstructions(pathNumber);
        setInstructions(0);
    }

    public void saveToDatabase(Record record){
        openDatabase();
        getBetterDb.insertRecord(record);

    }

    public boolean[] getResults(){
        return result;
    }

    public void setResult(int index, boolean isYes){
        result[index] = isYes;
    }

    //called if the user is outside the path
    public void doIfOutSideThePath(){
        ECAtext.setText(R.string.fine_motor_dont_lift);
        if(!wasOutside){
            mp.start();
            wasOutside = true;
            numWrongs++;
        }
    }
    //if touch is within path
    public void doIfWithinPath() {
        if (wasOutside){
            pauseMp();
            wasOutside = false;
        }
    }

    //starts the 2nd test. resets the variables
    public void doTestWithPen() {
        pauseMp();
        setInstructions(1);
        result[0] = numWrongs <= MAX_NUM_WRONG;
        numWrongs = 0;
    }

    public void doIfTouchIsUp() {
        pauseMp();
    }
    //asks the assistant if the user was able to use the pen properly. saves the results too
    public void askAssistantOfPen(){
        pauseMp();
        setInstructions(2);
        result[1] = numWrongs <= MAX_NUM_WRONG;
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

    public void refreshInstructions(int currentTest){
        setInstructions(currentTest);
    }

    private void setInstructions(int index){
        ECAtext.setText(instructions[index]);
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
                    "Using the pen with your dominant hand, trace the path. Start from the butterfly and go to the flowers",
                    "Assistant, has he/she used the pen without difficulties?"
                    };
                break;
        }
        return instructionList;

    }

    //opens the database for read
    private void openDatabase() {
        try {
            getBetterDb.createDatabase();
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //pause mediaplayer
    private void pauseMp(){
        if(mp.isPlaying()) {
            mp.pause();
        }
    }

}
