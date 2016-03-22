package com.geebeelicious.geebeelicious.tests.finemotor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;


/*
* Activity for the Fine Motors Test
*/
public class FineMotorActivity extends Activity {

    private static final String TAG = "FineMotorsActivity";
    //Set the color for the start and end of the path
    private static final int START_COLOR = Color.WHITE; //update the instruction if you change this
    private static final int END_COLOR = Color.BLACK;  //update the instruction if you change this
    private static final int MAX_NUM_WRONG = 2;

    private ImageView imageViewPathToTrace;
    private TextView ECAtext;
    private MediaPlayer mp;
    private Button buttonYes;
    private Button buttonNo;

    private boolean isDominantHand = false; //current test the user is taking
    private boolean isTestOngoing = true;
    private boolean[] result = new boolean[3]; //result[i] is true if pass, false if fail

    //TODO: Change instructions to be more specific. since you know the dominant hand na. also the gender
    private String[] instructions = {"Using a finger of your non dominant hand, trace the path. Start from the white circle and go to the black circle",
        "Using the pen with your dominant hand, trace the path. Start from the white circle and go to the black circle",
        "Assistant, has he/she used the pen without difficulties?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_motor);

        ECAtext = (TextView) findViewById(R.id.placeholderECAText);
        imageViewPathToTrace = (ImageView) findViewById(R.id.imageViewPathToTrace);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.fine_motor_outside_path);
        buttonYes = (Button) findViewById(R.id.YesButton);
        buttonNo = (Button) findViewById(R.id.NoButton);

        mp.setLooping(true);
        imageViewPathToTrace.setOnTouchListener(image_Listener);
        ECAtext.setText(instructions[0]);
        buttonYes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                result[2] = true;
                sendResults();
            }
        });

        buttonNo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                result[2] = false;
                sendResults();
            }
        });

    }

    //TODO: Intent shound be sent through this method
    private synchronized void sendResults(){
        if(isTestOngoing){ //this is too avoid double clicking
            for(boolean b: result){
                String a = (b ? "pass" : "fail");
                Log.d(TAG, "Results: " + (b ? "pass" : "fail"));
            }
            isTestOngoing = false;
        }
    }

    private OnTouchListener image_Listener = new OnTouchListener(){
        private boolean hasStarted = false; //has user started
        private boolean wasOutside = false; //was user outside the path
        private int numWrongs = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Bitmap bitmap = ((BitmapDrawable) imageViewPathToTrace.getDrawable()).getBitmap();
            float eventX = event.getX();
            float eventY = event.getY();
            int[] xY = getBitMapCoordinates(bitmap, event.getX(), event.getY());
            int pixel = bitmap.getPixel(xY[0], xY[1]);

            if(hasStarted){ //if user have pressed start_color
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP: //go back to start if finger is lifted
                        doIfTouchIsUp();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(pixel == END_COLOR) { //if user done
                            doFinishPathSuccess(isDominantHand);
                        } else if(pixel == 0){ //if touch is outside path
                            doIfOutSideThePath();
                        } else { //if touch is within path
                            if (wasOutside){
                                if(mp.isPlaying()) {
                                    mp.pause();
                                }
                                wasOutside = false;
                            } else {
                                ECAtext.setText("Trace to black circle");
                            }

                            Log.d(TAG, "Touch event position: " + eventX + ", " + eventY + "\n" +
                                    "Pixel: " + pixel + " Number of wrongs: " + numWrongs);
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            } else {

                if(pixel == START_COLOR && event.getAction() == MotionEvent.ACTION_DOWN){
                    hasStarted = true;
                    return true;
                }
                return false;
            }

        }

        //called if user finished path successfully
        private synchronized void doFinishPathSuccess(boolean isDominant){
            if(mp.isPlaying()) {
                mp.pause();
            }
            if(!isDominant && hasStarted){
                ECAtext.setText("You've finished the path successfully!");
                doTestWithPen();
            } else if(isDominant && hasStarted) {
                ECAtext.setText("You've finished the path successfully!");
                askAssistantOfPen();
            }
        }

        //called if the user is outside the path
        private void doIfOutSideThePath(){
            if(!wasOutside){
                mp.start();
                wasOutside = true;
                numWrongs++;
            }
        }

        //called if the user lifts the touch while doing the path. user must go back to start
        private void doIfTouchIsUp(){
            hasStarted = false;
            ECAtext.setText("Don't lift your finger. Go back to start.");
            if(mp.isPlaying()) {
                mp.pause();
            }
        }

        //starts the 2nd test. resets the variables
        private void doTestWithPen(){
            hasStarted = false;
            result[0] = numWrongs <= MAX_NUM_WRONG;
            numWrongs = 0;
            ECAtext.setText(instructions[1]);
            isDominantHand = true;
        }

        //asks the assistant if the user was able to use the pen properly. saves the results too
        private void askAssistantOfPen(){
            LinearLayout linearLayoutAnswer = (LinearLayout) findViewById(R.id.linearLayoutAnswers);
            hasStarted = false;
            result[1] = numWrongs <= MAX_NUM_WRONG;
            ECAtext.setText(instructions[2]);
            linearLayoutAnswer.setVisibility(View.VISIBLE);
        }

        //returns the equivalent x and y coordinates of the bitmap given x and y coordinates of the touch event
        private int[] getBitMapCoordinates(Bitmap bitmap, float eventX, float eventY){
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
    };
}