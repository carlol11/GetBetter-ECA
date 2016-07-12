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
public class EyeChartsAdapter extends ArrayAdapter<String>{

    private Typeface chalkFont;

    public EyeChartsAdapter(Context context, ArrayList<String> chartNames, Typeface chalkFont){
        super(context, 0, chartNames);
        this.chalkFont = chalkFont;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String chart = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_eyechart_list, parent, false);
        }

        TextView chartName = (TextView)convertView.findViewById(R.id.eyechartlistTV);
        chartName.setTypeface(chalkFont);
        chartName.setText(chart);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        String chart = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_eyechart_list, parent, false);
        }

        TextView chartName = (TextView)convertView.findViewById(R.id.eyechartlistTV);
        chartName.setTypeface(chalkFont);
        chartName.setText(chart);

        return convertView;
    }

}
