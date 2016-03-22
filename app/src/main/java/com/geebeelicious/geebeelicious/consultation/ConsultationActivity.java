package com.geebeelicious.geebeelicious.consultation;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import models.consultation.ConsultationHelper;

public class ConsultationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);

        Button yesButton = (Button) findViewById(R.id.YesButton);
        Button noButton = (Button) findViewById(R.id.NoButton);
        final TextView ECAText = (TextView) findViewById(R.id.placeholderECAText);
        final ConsultationHelper consultationHelper = new ConsultationHelper();

        ECAText.setText(consultationHelper.getFirstQuestion());

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nextQuestion = consultationHelper.getNextQuestion(true); //true because yes
                ECAText.setText(nextQuestion);
            }

        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nextQuestion = consultationHelper.getNextQuestion(false); //false because no
                ECAText.setText(nextQuestion);
            }
        });
    }
}
