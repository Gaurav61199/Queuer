package com.gaurav.android.queuer;

import android.app.Application;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Gaurav on 6/14/2017.
 */

public class Firebase extends Application {
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference ATNRef = mDatabase.getReference("AvailableTokenNumber");
    String tempATN;

    public String getATNValue(){
        ATNRef.addValueEventListener(mValueEventListener);
        return tempATN;
    }
    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            tempATN =(dataSnapshot.getValue().toString());
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}

