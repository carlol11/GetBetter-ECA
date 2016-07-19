package com.geebeelicious.geebeelicious.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.geebeelicious.geebeelicious.R;
import com.geebeelicious.geebeelicious.models.consultation.School;

import java.util.ArrayList;

/**
 * Created by Kate on 03/30/2016.
 * The SchoolsAdapter class extends the ArrayAdapter class to allow
 * a customized ListView to display School objects.
 */
public class SchoolsAdapter extends ArrayAdapter<School>{

    private Typeface chalkFont;

    public SchoolsAdapter(Context context, ArrayList<School> schools, Typeface chalkFont){
        super(context, 0, schools);
        this.chalkFont = chalkFont;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        School school = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_school_list, parent, false);
        }

        TextView schoolName = (TextView)convertView.findViewById(R.id.school_name_list);
        TextView schoolMunicipality = (TextView) convertView.findViewById(R.id.school_municipality_list);
        schoolName.setTypeface(chalkFont);
        schoolMunicipality.setTypeface(chalkFont);

        schoolName.setText(school.getSchoolName());
        schoolMunicipality.setText(school.getMunicipality());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        School school = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_school_list, parent, false);
        }

        TextView schoolName = (TextView)convertView.findViewById(R.id.school_name_list);
        TextView schoolMunicipality = (TextView) convertView.findViewById(R.id.school_municipality_list);
        schoolName.setTypeface(chalkFont);
        schoolMunicipality.setTypeface(chalkFont);

        schoolName.setText(school.getSchoolName());
        schoolMunicipality.setText(school.getMunicipality());

        return convertView;
    }

}
