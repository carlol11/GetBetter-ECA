package com.geebeelicious.geebeelicious;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import models.consultation.Patient;

public class AddPatientActivity extends ActionBarActivity {

    private String firstName = null;
    private String lastName = null;
    private String birthDate = null;
    private String gender = null;
    private String handedness = null;
    private TextView questionView;
    private EditText editText;
    private RadioGroup radioGroup;
    private DatePicker datePicker;
    private int questionCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        questionView = (TextView)findViewById(R.id.questionView);
        editText = (EditText)findViewById(R.id.newPatientStringInput);
        datePicker = (DatePicker)findViewById(R.id.newPatientDatePicker);
        radioGroup = (RadioGroup)findViewById(R.id.radio);

        int[] questions = {R.string.first_name, R.string.last_name, R.string.birthdate,
                            R.string.gender, R.string.handedness};

        setQuestion(questions[questionCounter]);
        editText.setVisibility(View.VISIBLE);

        Button saveButton = (Button)findViewById(R.id.saveNewPatientButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(questionCounter){
                    case 0:
                        firstName = getEditText();
                        break;
                    case 1:
                        lastName = getEditText();
                        editText.setVisibility(View.GONE);
                        datePicker.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        birthDate = getSelectedDate();
                        datePicker.setVisibility(View.GONE);
                        radioGroup.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        gender = getRadioResult();
                        break;
                    case 4:
                        handedness = getRadioResult();
                        radioGroup.setVisibility(View.GONE);
                        editText.setVisibility(View.VISIBLE);
                        break;
                    default:
                        firstName = getEditText();
                        break;
                }
                questionCounter++;
                if(questionCounter>4){
                    //TODO: [URGENT, DB] write to database
                }
            }
        });

    }

    private void setQuestion(int resID){
        questionView.setText(resID);
    };

    private String getEditText(){
        return editText.getText().toString();
    };

    private String getSelectedDate(){
        return (datePicker.getMonth() + "/" + datePicker.getDayOfMonth() + "/" + datePicker.getYear());
    }

    private String getRadioResult(){
        return String.valueOf(radioGroup.getCheckedRadioButtonId());
    }

}
