package com.geebeelicious.geebeelicious.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import java.util.ArrayList;

import models.consultation.Patient;

/**
 * Created by Kate on 03/30/2016.
 */
public class PatientsAdapter extends ArrayAdapter<Patient> {

    public PatientsAdapter(Context context, ArrayList<Patient> patients) {
        super(context, 0, patients);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Patient patient = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_patient_list, parent, false);
        }

        TextView patientName = (TextView)convertView.findViewById(R.id.patient_name_list);
        TextView patientGender = (TextView)convertView.findViewById(R.id.patient_gender_list);
        TextView patientBirthDate = (TextView)convertView.findViewById(R.id.patient_birthdate_list);

        patientName.setText(patient.getFirstName() + " " + patient.getLastName());
        patientGender.setText(patient.getGenderString());
        patientBirthDate.setText(patient.getBirthday());

        return convertView;
    }
}
