package com.gaurav.android.queuer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


/**
 * Created by Gaurav on 6/28/2017.
 */

public class FFragment extends Fragment implements AdapterView.OnItemSelectedListener, ValueEventListener{

    Spinner mAreaSpinner,mClinicSpinner;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference ClinicListRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.form_fragment1,container,false); // inflating fragment view
        mAreaSpinner = (Spinner) v.findViewById(R.id.area_spinner);     // obtaining reference to area spinner in form fragment 1
        mClinicSpinner = (Spinner) v.findViewById(R.id.clinic_spinner);
        rootRef.addValueEventListener(this);
        mAreaSpinner.setOnItemSelectedListener(this);
        mClinicSpinner.setOnItemSelectedListener(this);
        return v;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if(adapterView.getId() == mAreaSpinner.getId()){  //if Area Spinner is selected
            ArrayList place = (ArrayList) adapterView.getItemAtPosition(0); // finding place which user selected
            ClinicListRef = rootRef.child(place.get(i).toString()).child("Clinics"); // setting reference for clinics available in the specific area
            ClinicListRef.addValueEventListener(this);
        }

        if(adapterView.getId() == mClinicSpinner.getId()){ // if clinic Spinner is selected

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> subList = new ArrayList<>(); // list to store doctor's specialization data
        for (DataSnapshot listData : dataSnapshot.getChildren()){
            list.add(listData.getKey());
            if(dataSnapshot.getRef().equals(ClinicListRef)){ // if ref is clinic then we have to also get clinic medical specialization
                subList.add(listData.child("Specializations").getValue().toString());
            }
            if(dataSnapshot.getRef().equals(rootRef)){
                subList.add(listData.child("Address").getValue().toString());
            }
        }
        ArrayList<ArrayList<String>> completeSpinList = new ArrayList<>();
        completeSpinList.add(list);
        completeSpinList.add(subList);
        CSpinnerAdapter adapter = new CSpinnerAdapter(getActivity(),R.layout.spinner_row,completeSpinList); // creating and initializing custom spinner adapter

        if (dataSnapshot.getRef().equals(rootRef)){
            mAreaSpinner.setAdapter(adapter);
        }else{
            mClinicSpinner.setAdapter(adapter);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
