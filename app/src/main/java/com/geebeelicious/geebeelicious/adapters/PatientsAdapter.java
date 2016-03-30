package com.geebeelicious.geebeelicious.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import models.consultation.Patient;

/**
 * Created by Kate on 03/30/2016.
 */
public class PatientsAdapter extends ArrayAdapter<Patient> {

    private List<Patient> patientList = null;
    private ArrayList<Patient> arrayPatientList;


    public PatientsAdapter(Context context, ArrayList<Patient> patients) {
        super(context, 0, patients);
        this.patientList = patients;
        this.arrayPatientList = new ArrayList<>();
        this.arrayPatientList.addAll(patientList);
    }

    @Override
    public int getCount() {
        return patientList.size();
    }

    @Override
    public Patient getItem(int position) {
        return patientList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

    public void filter(String searchText){
        searchText = searchText.toLowerCase();
        patientList.clear();
        if(searchText.length()==0){
            patientList.addAll(arrayPatientList);
        }
        else{
            for(Patient p : arrayPatientList){
                if(p.getFirstName().toLowerCase().contains(searchText) ||
                    p.getLastName().toLowerCase().contains(searchText)){
                    patientList.add(p);
                }
            }
        }
        notifyDataSetChanged();

    }
//
//    charText = charText.toLowerCase(Locale.getDefault());
//    worldpopulationlist.clear();
//    if (charText.length() == 0) {
//        worldpopulationlist.addAll(arraylist);
//    }
//    else
//    {
//        for (WorldPopulation wp : arraylist)
//        {
//            if (wp.getCountry().toLowerCase(Locale.getDefault()).contains(charText))
//            {
//                worldpopulationlist.add(wp);
//            }
//        }
//    }
//    notifyDataSetChanged();
//

}
