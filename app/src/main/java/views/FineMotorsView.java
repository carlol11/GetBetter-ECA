package views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/*
* View class of FineMotorsActivity. Handles touch events
*/
public class FineMotorsView extends View {
    private Paint drawPaint;
    private Path drawPath;

    //to use to not repaint the whole view everytime
    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRectangle = new RectF();

    private static final float STROKE_WIDTH = 50f;
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

    public FineMotorsView(Context context, AttributeSet attrs) {
        super(context);
        drawPaint = new Paint();
        drawPath = new Path();

        init();
    }

    private void init(){
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(STROKE_WIDTH);
        drawPaint.setColor(Color.BLACK);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
    }
    //Event which the view draws itself
    @Override
    public void onDraw (Canvas canvas) {
        canvas.drawPath(drawPath, drawPaint);
    }

    //Event called on touch. Makes the view repaint itself
    @Override
    public boolean onTouchEvent (MotionEvent event) {
        //Get the coordinates of the touch event
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: // Set a new starting point
                drawPath.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;
            case MotionEvent.ACTION_MOVE: // Connect the points
            case MotionEvent.ACTION_UP:
                resetDirtyRectangle(eventX, eventY);

                int historySize = event.getHistorySize();
                // loop through the history of points to have a smoother line
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    drawPath.lineTo(historicalX, historicalY);
                }
                // connect the line to the touch point.
                drawPath.lineTo(eventX, eventY);
                break;
            default:
                return false;
        }
        invalidate((int) (dirtyRectangle.left - HALF_STROKE_WIDTH),
                (int) (dirtyRectangle.top - HALF_STROKE_WIDTH),
                (int) (dirtyRectangle.right + HALF_STROKE_WIDTH),
                (int) (dirtyRectangle.bottom + HALF_STROKE_WIDTH));

        lastTouchX = eventX;
        lastTouchY = eventY;
         //Forces the view to call onDraw
        return true;
    }

    // Resets the dirty rectangle
    private void resetDirtyRectangle(float eventX, float eventY) {
        dirtyRectangle.left = Math.min(lastTouchX, eventX);
        dirtyRectangle.right = Math.max(lastTouchX, eventX);
        dirtyRectangle.top = Math.min(lastTouchY, eventY);
        dirtyRectangle.bottom = Math.max(lastTouchY, eventY);
    }

     //Called when replaying history to ensure the dirty region includes all points.
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRectangle.left) {
            dirtyRectangle.left = historicalX;
        } else if (historicalX > dirtyRectangle.right) {
            dirtyRectangle.right = historicalX;
        }
        if (historicalY < dirtyRectangle.top) {
            dirtyRectangle.top = historicalY;
        } else if (historicalY > dirtyRectangle.bottom) {
            dirtyRectangle.bottom = historicalY;
        }
    }
}