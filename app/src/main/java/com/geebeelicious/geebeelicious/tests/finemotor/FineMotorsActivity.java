package com.geebeelicious.geebeelicious.tests.finemotor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;


/*
* Activity for the Fine Motors Test
*/
public class FineMotorsActivity extends Activity {
    //Set the color for the start and end of the path
    private static final int START_COLOR = Color.WHITE;
    private static final int END_COLOR = Color.BLACK;

    private ImageView imageViewPathToTrace;
    private TextView ECAtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fine_motors);
        ECAtext = (TextView) findViewById(R.id.placeholderECAText);
        imageViewPathToTrace = (ImageView) findViewById(R.id.imageViewPathToTrace);

        imageViewPathToTrace.setOnTouchListener(image_Listener);

    }

    private OnTouchListener image_Listener = new OnTouchListener(){
        private boolean haveStarted = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Bitmap bitmap = ((BitmapDrawable) imageViewPathToTrace.getDrawable()).getBitmap();
            float eventX = event.getX();
            float eventY = event.getY();
            int[] xY = getBitMapCoordinates(bitmap, event.getX(), event.getY());
            int pixel = bitmap.getPixel(xY[0], xY[1]);

            if(haveStarted){ //if user have pressed start_color
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if(pixel == END_COLOR) { //if user done
                            doFinishPathSuccess();
                        } else {
                            ECAtext.setText("Touch event position: " + eventX + ", " + eventY + "\n" +
                                    "Pixel: " + pixel);
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            } else {

                if(pixel == START_COLOR && event.getAction() == MotionEvent.ACTION_DOWN){
                    haveStarted = true;
                    return true;
                }
                return false;
            }

        }

        //called if user finished path successfully
        private void doFinishPathSuccess(){
            ECAtext.setText("You've finished the path successfully!");
        }
        
        //returns the equivalent x and y coordinates of the bitmap given x and y coordinates of the touch event
        private int[] getBitMapCoordinates(Bitmap bitmap, float eventX, float eventY){
            Matrix invertMatrix = new Matrix();
            float[] eventXY = new float[] {eventX, eventY};
            int x;
            int y;

            ((ImageView)imageViewPathToTrace).getImageMatrix().invert(invertMatrix);
            invertMatrix.mapPoints(eventXY);
            x = Integer.valueOf((int) eventXY[0]);
            y = Integer.valueOf((int) eventXY[1]);

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