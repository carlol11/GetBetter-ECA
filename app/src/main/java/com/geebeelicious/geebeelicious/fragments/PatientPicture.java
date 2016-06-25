package com.geebeelicious.geebeelicious.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.geebeelicious.geebeelicious.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientPicture extends Fragment {


    public PatientPicture() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_picture, container, false);
    }

}
