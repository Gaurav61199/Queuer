package com.gaurav.android.queuer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * Created by Gaurav on 7/7/2017.
 */

public class CSpinnerAdapter extends ArrayAdapter { // Customizing ArrayAdapter as layout inflated contains two textView with LinearLayout Group View
    private ArrayList<String> list;
    private ArrayList<String> subList;
    public CSpinnerAdapter(Context context, int resource, ArrayList<ArrayList<String>> objects) {
        super(context, resource, objects);  // passing resource is not necessary as getView() is going to be override n which super cass uses it
        list = objects.get(0);  // list arrayList is stored at position 0 of objects
        subList=objects.get(1); // subList arrayList is stored at position 1 of objects
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        if (row == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.spinner_row,parent,false);
        }
        TextView clinicName = (TextView) row.findViewById(R.id.clinicName);
        TextView clinicSpecial = (TextView) row.findViewById(R.id.Specializations);
        clinicName.setText(list.get(position));
        clinicSpecial.setText(subList.get(position));
        return row;
    }

    @Override
    public View getDropDownView(int position,View convertView,ViewGroup parent){ // getDropDownView is override so that dropdown also have custom layout
        return getView(position,convertView,parent);
    }

    @Override
    public int getCount(){
        return list.size();
    }
}
