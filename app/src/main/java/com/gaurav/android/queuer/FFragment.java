package com.gaurav.android.queuer;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Gaurav on 6/28/2017.
 */

public class FFragment extends Fragment {

    Spinner mSpinner;
    DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("queuer-3999c");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){



        View v = inflater.inflate(R.layout.form_fragment1,container,false); // inflating fragment view
        mSpinner = (Spinner) v.findViewById(R.id.area_spinner);     // obtaining reference to area spinner in form fragment 1

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long childrenCount = dataSnapshot.getChildrenCount();
                String[] areas = new String[(int) childrenCount];

                for (DataSnapshot locations : dataSnapshot.getChildren() ){
                        int i=0;
                        areas[++i] = locations.getKey();

                }
                if(areas != null){
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,areas);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinner.setAdapter(adapter);
                }else {
                    Toast.makeText(getActivity(),"Empty Location array",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addListenerForSingleValueEvent(valueEventListener);
        return v;
    }

}
