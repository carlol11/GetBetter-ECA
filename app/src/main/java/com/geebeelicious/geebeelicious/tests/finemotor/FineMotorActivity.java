package com.geebeelicious.geebeelicious.tests.finemotor;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.MonitoringConsultationChoice;
import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.database.DataAdapter;

import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import models.consultation.Patient;
import models.consultation.Record;


/*
* Activity for the Fine Motors Test
*/
public class FineMotorActivity extends Activity {

    Bundle record;

    private static final String TAG = "FineMotorActivity";
    //Set the color for the start and end of the path
    private static final int START_COLOR = Color.parseColor("#09BCD4");//update the instruction if you change this
    private static final int END_COLOR = Color.parseColor("#E71E63");  //update the instruction if you change this
    private static final int MAX_NUM_WRONG = 2;

    private ImageView imageViewPathToTrace;
    private TextView ECAtext;
    private MediaPlayer mp;
    private Button buttonYes;
    private Button buttonNo;

    private boolean isDominantHand = false; //current test the user is taking
    private boolean isTestOngoing = true;
    private boolean[] result = new boolean[3]; //result[i] is true if pass, false if fail

    private DataAdapter getBetterDb;

    private String[] instructions = {"Using a finger of your non dominant hand, trace the path. Start from the butterfly and go to the flowers",
        "Using the pen with your dominant hand, trace the path. Start from the butterfly and go to the flowers",
        "Assistant, has he/she used the pen without difficulties?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_motor);

        record = this.getIntent().getExtras();
        getBetterDb = new DataAdapter(this);

        ECAtext = (TextView) findViewById(R.id.placeholderECAText);
        imageViewPathToTrace = (ImageView) findViewById(R.id.imageViewPathToTrace);
        imageViewPathToTrace.setImageResource(getRandomPathDrawable());
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

    private int getRandomPathDrawable(){
        int[] path = new int[]{R.drawable.path_to_trace_1, R.drawable.path_to_trace_2};
        Random random = new Random((int)System.nanoTime());
        return path[random.nextInt(2)];
    }


    private synchronized void sendResults(){
        String resultString = "";
        String[] testString = {"nonDominantHand", "dominantHand", "usePen"};

        if(isTestOngoing){ //this is too avoid double clicking
            for(int i = 0; i < 3; i++){
                String temp =  (result[i] ? "Pass" : "Fail");
                resultString += testString[i] + ": " + temp + "\n";
                record.putString(testString[i], temp);
            }
            isTestOngoing = false;
        }
        TextView resultView = (TextView)findViewById(R.id.fineMotorResultsTV);
        resultView.setText(resultString);

        imageViewPathToTrace.setBackgroundColor(Color.WHITE);
        imageViewPathToTrace.setImageResource(R.drawable.wait_for_next_test);
        hideAnswerButtons();


        CountDownTimer timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            private int getIntResults(String result){
                switch(result){
                    case "Pass":
                        return 0;
                    case "Fail":
                        return 1;
                    default:
                        return 2;
                }
            }

            @Override
            public void onFinish() {
                openDatabase();
                Patient patient = record.getParcelable("patient");
                double height = 127;
                double weight =  25;

                int grossMotor = getIntResults(record.getString("grossMotor"));
                int nonDominantHand = getIntResults(record.getString("nonDominantHand"));
                int dominantHand = getIntResults(record.getString("dominantHand"));
                int usePen = getIntResults(record.getString("usePen"));

                //TODO: [NOT URGENT] Add activity asking for height and weight. change the placeholders
                getBetterDb.insertRecord(new Record(patient.getPatientID(),
                        record.getString("currentDate"), height, weight, record.getString("visualAcuityLeft"),
                        record.getString("visualAcuityRight"), record.getString("colorVision"),
                        record.getString("hearingLeft"), record.getString("hearingRight"),
                        grossMotor, nonDominantHand,
                        dominantHand, usePen));

                Intent intent = new Intent(FineMotorActivity.this, MonitoringConsultationChoice.class);
                finish();
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void openDatabase(){
        try {
            getBetterDb.createDatabase();
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hideAnswerButtons(){
        LinearLayout answers = (LinearLayout)findViewById(R.id.linearLayoutAnswers);
        for (int j = 0; j<answers.getChildCount(); j++){
            View view = answers.getChildAt(j);
            view.setEnabled(false);
            view.setVisibility(View.INVISIBLE);
        }
        answers.setVisibility(View.INVISIBLE);

    }

    private void showAnswerButtons(){
        LinearLayout answers = (LinearLayout)findViewById(R.id.linearLayoutAnswers);
        for (int j = 0; j<answers.getChildCount(); j++){
            View view = answers.getChildAt(j);
            view.setEnabled(true);
            view.setVisibility(View.VISIBLE);
        }
        answers.setVisibility(View.VISIBLE);

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
                                ECAtext.setText("Trace to flowers (pink circle)");
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

                if(pixel == START_COLOR && (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)){
                    hasStarted = true;
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN)
                    return true;
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
            showAnswerButtons();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FineMotorActivity.this, MonitoringConsultationChoice.class);
        finish();
        startActivity(intent);
    }
}