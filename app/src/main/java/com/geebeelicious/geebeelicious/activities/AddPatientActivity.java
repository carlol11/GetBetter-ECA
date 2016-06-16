package com.geebeelicious.geebeelicious.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.database.DatabaseAdapter;

import java.sql.SQLException;
import java.util.Date;

import com.geebeelicious.geebeelicious.fragments.ECAFragment;
import com.geebeelicious.geebeelicious.models.consultation.Patient;

/**
 * Created by Kate.
 * The AddPatientActivity serves as the activity containing
 * functionality for adding new patients.
 */

public class AddPatientActivity extends ActionBarActivity implements  ECAFragment.OnFragmentInteractionListener{

    private String firstName = null;
    private String lastName = null;
    private String birthDate = null;
    private int gender;
    private int handedness;
    private Patient patient = null;

    private TextView questionView;
    private EditText editText;
    private RadioButton radioButton0;
    private RadioButton radioButton1;
    private RadioGroup radioGroup;
    private DatePicker datePicker;

    private int questionCounter = 0;
    private final int[] questions = {R.string.first_name, R.string.last_name, R.string.birthdate,
                                    R.string.gender, R.string.handedness};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        questionView = (TextView)findViewById(R.id.questionView);
        editText = (EditText)findViewById(R.id.newPatientStringInput);
        datePicker = (DatePicker)findViewById(R.id.newPatientDatePicker);
        datePicker.setMaxDate(new Date().getTime());
        radioButton0 = (RadioButton)findViewById(R.id.radioButton1);
        radioButton1 = (RadioButton)findViewById(R.id.radioButton2);
        radioGroup = (RadioGroup)findViewById(R.id.newPatientRadioChoice);

        setQuestion(questions[questionCounter]);
        editText.setVisibility(View.VISIBLE);

        //ECA Integration
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment ecaFragment = fragmentManager.findFragmentByTag(ECAFragment.class.getName());
        if(ecaFragment == null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            ecaFragment = new ECAFragment();
            transaction.add(R.id.placeholderECA, ecaFragment, ECAFragment.class.getName());
            transaction.commit();
        }


        Button cancelButton = (Button)findViewById(R.id.cancelNewPatientButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patient = null;
                questionCounter = 0;
                Intent intent = new Intent(AddPatientActivity.this, PatientListActivity.class);
                startActivity(intent);
            }
        });

        Button saveButton = (Button)findViewById(R.id.saveNewPatientButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(questionCounter){
                    case 0:
                        firstName = getEditText();
                        editText.setText("");
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
                        radioButton0.setText(R.string.male);
                        radioButton1.setText(R.string.female);
                        break;
                    case 3:
                        int selectedID = radioGroup.getCheckedRadioButtonId();
                        if(selectedID == radioButton0.getId()){
                            gender = 0;
                        } else if(selectedID == radioButton1.getId()){
                            gender = 1;
                        }
                        radioGroup.clearCheck();
                        radioButton0.setText(R.string.right_handed);
                        radioButton1.setText(R.string.left_handed);
                        radioButton0.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handedness = 0;
                            }
                        });
                        radioButton1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handedness = 1;
                            }
                        });
                        break;
                    case 4:
                        selectedID = radioGroup.getCheckedRadioButtonId();
                        if(selectedID == radioButton0.getId()){
                            handedness = 0;
                        } else if(selectedID == radioButton1.getId()){
                            handedness = 1;
                        }
                        radioGroup.setVisibility(View.GONE);
                        patient = new Patient(firstName, lastName, birthDate, gender, getIntent().getIntExtra("schoolID", 1), handedness);
                        String patientDetails = "First Name: " + patient.getFirstName() +
                                                "\nLast Name: " + patient.getLastName() +
                                                "\nBirthdate: " + patient.getBirthday() +
                                                "\nGender: " + patient.getGenderString() +
                                                "\nHandedness: " + patient.getHandednessString();
                        questionView.setText(patientDetails);
                        break;
                    case 5:
                        savePatientToDatabase(patient);
                        Intent intent = new Intent(AddPatientActivity.this, MonitoringConsultationChoice.class);
                        intent.putExtra("patient", patient);
                        startActivity(intent);
                    default:
                        break;
                }
                questionCounter++;
                if(questionCounter<5){
                    setQuestion(questions[questionCounter]);
                }
            }
        });

    }

    //Display question on screen based on resID parameter
    private void setQuestion(int resID){
        questionView.setText(resID);
    };

    //Return String format of contents of search field
    private String getEditText(){
        return editText.getText().toString();
    };

    //Return String format of selected date on Number Picker
    private String getSelectedDate(){
        return (datePicker.getMonth() + "/" + datePicker.getDayOfMonth() + "/" + datePicker.getYear());
    }

    private void savePatientToDatabase(Patient patient){
        DatabaseAdapter getBetterDb = new DatabaseAdapter(AddPatientActivity.this);
        try {
            getBetterDb.openDatabaseForRead();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getBetterDb.insertPatient(patient);

        getBetterDb.closeDatabase();
    }

    //TODO: Implement this
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
