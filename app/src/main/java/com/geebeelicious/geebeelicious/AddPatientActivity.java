package com.geebeelicious.geebeelicious;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import models.consultation.Patient;

public class AddPatientActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        String firstName = null;
        String lastName = null;
        String birthDate = null;
        String gender = null;
        String handedness = null;

        setQuestion(R.string.first_name);

    }

    private void setQuestion(int resID){
        TextView questionView = (TextView)findViewById(R.id.questionView);
        questionView.setText(resID);
    };

    private String getEditText(){
        EditText editText = (EditText)findViewById(R.id.newPatientStringInput);
        return editText.getText().toString();
    };

    private String getSelectedDate(){
        DatePicker datePicker = (DatePicker)findViewById(R.id.newPatientDatePicker);
        return (datePicker.getMonth() + "/" + datePicker.getDayOfMonth() + "/" + datePicker.getYear());
    }

    private String getRadioResult(){
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio);
        return String.valueOf(radioGroup.getCheckedRadioButtonId());

    }

}
