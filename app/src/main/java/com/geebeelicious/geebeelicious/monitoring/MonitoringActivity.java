package com.geebeelicious.geebeelicious.monitoring;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.tests.visualacuity.VisualAcuityMainActivity;

import org.w3c.dom.Text;


public class MonitoringActivity extends ActionBarActivity {

    private TextView questionView;
    private NumberPicker numberPicker;
    private TextView unitView;

    private Bundle record;

    private final int[] questions = {R.string.height, R.string.weight};
    private final int[] questionUnit = {R.string.centimeters, R.string.kilograms};
    private final int numberOfQuestions = 2;
    private int questionsCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);

        questionView = (TextView)findViewById(R.id.questionView);
        unitView = (TextView)findViewById(R.id.unitView);
        numberPicker = (NumberPicker)findViewById(R.id.monitoringNumberPicker);
        numberPicker.setMinValue(0);

        questionView.setText(questions[questionsCounter]);
        unitView.setText(questionUnit[questionsCounter]);
        numberPicker.setMaxValue(250);

        Button saveButton = (Button)findViewById(R.id.saveAnswerButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record = getIntent().getExtras();
                switch(questionsCounter){
                    case 0:
                        record.putDouble("height", (double)numberPicker.getValue());
                        numberPicker.setMaxValue(100);
                        break;
                    case 1:
                        record.putDouble("weight", (double)numberPicker.getValue());
                        break;
                }
                questionsCounter++;
                if(questionsCounter < numberOfQuestions){
                    questionView.setText(questions[questionsCounter]);
                    unitView.setText(questionUnit[questionsCounter]);
                }else{
                    endMonitoring();
                }
            }
        });
    }

    private void endMonitoring(){
        RelativeLayout monitoringView = (RelativeLayout)findViewById(R.id.monitoringQuestionView);
        monitoringView.setVisibility(View.GONE);
        Button saveButton = (Button)findViewById(R.id.saveAnswerButton);
        saveButton.setVisibility(View.INVISIBLE);
        ImageView imageView = (ImageView)findViewById(R.id.monitoringIV);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.wait_for_next_test);

        CountDownTimer timer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(MonitoringActivity.this, VisualAcuityMainActivity.class);
                intent.putExtras(record);
                finish();
                startActivity(intent);
            }
        };
        timer.start();
    }

}
