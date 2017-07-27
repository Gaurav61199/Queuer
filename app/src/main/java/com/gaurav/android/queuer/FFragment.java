package com.gaurav.android.queuer;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * Created by Gaurav on 6/28/2017.
 */

public class FFragment extends Fragment implements AdapterView.OnItemSelectedListener, ValueEventListener, View.OnClickListener {

    private Spinner mAreaSpinner, mClinicSpinner;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference ClinicListRef, TokenRef;
    private Button backB, nextB;
    private CardView mCardView;
    private TextView ATNValue, OTNValue;
    private String AOPlace, AOClinic;
    private CommBridge commListener;
    private Toolbar mToolbar;
    private SharedPreferences saveCSpinnerList,saveASpinnerList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.form_fragment1, container, false); // inflating fragment view
        // obtaining references for various views from form fragment 1
        mToolbar = (Toolbar) getActivity().findViewById(R.id.form_toolbar);
        mToolbar.setTitle("DISCOVER CLINICS AND QUEUE STATUS");
        mToolbar.setTitleTextAppearance(getActivity(), R.style.FFagmentA);
        mToolbar.setSubtitle(null);

        mAreaSpinner = (Spinner) v.findViewById(R.id.area_spinner);
        mClinicSpinner = (Spinner) v.findViewById(R.id.clinic_spinner);
        backB = (Button) v.findViewById(R.id.f1Back);
        nextB = (Button) v.findViewById(R.id.f1Next);
        mCardView = (CardView) v.findViewById(R.id.AtnOtnDetCV);
        ATNValue = (TextView) v.findViewById(R.id.ATNValue);
        OTNValue = (TextView) v.findViewById(R.id.OTNValue);

        //setting Listeners
        rootRef.addValueEventListener(this);
        mAreaSpinner.setOnItemSelectedListener(this);
        mClinicSpinner.setOnItemSelectedListener(this);
        backB.setOnClickListener(this);
        nextB.setOnClickListener(this);

        return v;
    }

    // implemented methods
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView.getId() == mAreaSpinner.getId()) {  //if Area Spinner is selected
            ArrayList place = (ArrayList) adapterView.getItemAtPosition(0); // finding place which user selected
            AOPlace = place.get(i).toString();  // AOPlace is Available Ongoing Token of Place Selected

            //Saving Selected item in Shared Preference to avoid need to select next time in case user want to select the same
            saveASpinnerList = getActivity().getSharedPreferences("com.gaurav.queuer.areaspinner", Context.MODE_PRIVATE);
            saveASpinnerList.edit().putString("AreaPrevSelected", AOPlace).commit();


            ClinicListRef = rootRef.child(AOPlace).child("Clinics"); // setting reference for clinics available in the specific area
            ClinicListRef.addValueEventListener(this);
        }

        if (adapterView.getId() == mClinicSpinner.getId()) { // if clinic Spinner is selected
            ArrayList clinic = (ArrayList) adapterView.getItemAtPosition(0);
            AOClinic = clinic.get(i).toString();

            //Saving Selected item in Shared Preference to avoid need to select next time in case user want to select the same
            saveCSpinnerList = getActivity().getSharedPreferences("com.gaurav.queuer.clinicspinner", Context.MODE_PRIVATE);
            saveCSpinnerList.edit().putString("ClinicPrevSelected", AOClinic).commit();

            //getting ref to the token details of the place and clinic selected by user
            TokenRef = rootRef.child(AOPlace).child("Clinics").child(AOClinic);
            TokenRef.addValueEventListener(this);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getRef().equals(TokenRef)) {
            ATNValue.setText(dataSnapshot.child("AvailableTokenNumber").getValue().toString());
            OTNValue.setText(dataSnapshot.child("OngoingTokenNumber").getValue().toString());
            mCardView.setVisibility(View.VISIBLE);

        } else {
            ArrayList<String> list = new ArrayList<>();
            ArrayList<String> subList = new ArrayList<>(); // list to store doctor's specialization data
            for (DataSnapshot listData : dataSnapshot.getChildren()) {
                list.add(listData.getKey());
                if (dataSnapshot.getRef().equals(ClinicListRef)) { // if ref is clinic then we have to also get clinic medical specialization
                    subList.add(listData.child("Specializations").getValue().toString());
                }
                if (dataSnapshot.getRef().equals(rootRef)) {
                    subList.add(listData.child("Address").getValue().toString());
                }
            }
            ArrayList<ArrayList<String>> completeSpinList = new ArrayList<>();
            completeSpinList.add(list);
            completeSpinList.add(subList);
            CSpinnerAdapter adapter = new CSpinnerAdapter(getActivity(), R.layout.spinner_row, completeSpinList); // creating and initializing custom spinner adapter

            if (dataSnapshot.getRef().equals(rootRef)) {
                mAreaSpinner.setAdapter(adapter);
                //getting previously selected item from Shared Preferences and setting in a spinner as current selection
                retrieveSpinnerData("com.gaurav.queuer.areaspinner","AreaPrevSelected",mAreaSpinner); // method defined below

            } else {
                mClinicSpinner.setAdapter(adapter);
                //getting previously selected item from Shared Preferences and setting in a spinner as current selection
                retrieveSpinnerData("com.gaurav.queuer.clinicspinner","ClinicPrevSelected",mClinicSpinner);// method defined below

            }
        }

    }
    //method to retrieve data saved in shared preference to avoid selecting always if no changes is required in spinners
    private void retrieveSpinnerData(String prefFile, String id, Spinner spinner) {
        SharedPreferences sharedprefs = getActivity().getSharedPreferences(prefFile,Context.MODE_PRIVATE);
        String retrievedItem = sharedprefs.getString(id,"No value previously saved");
        ArrayList<String> temp = (ArrayList<String>) spinner.getAdapter().getItem(0);
        if (temp.contains(retrievedItem)){
            spinner.setSelection(temp.indexOf(retrievedItem));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.f1Next:
                commListener.clinicPlaceName(AOClinic, AOPlace);  //passing value to activity
                break;
            case R.id.f1Back:
                Intent intent = new Intent(getActivity(), Queuer.class);
                startActivity(intent);
                break;

        }

    }

    // creating interface for establishing communication between FFragment and SFragment fragment
    interface CommBridge {
        // this method is overridden in FormActivity2 class activity
        // this method doesn't contain body as it is declared in interface
        void clinicPlaceName(String Clinic, String Place);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CommBridge) {
            commListener = (CommBridge) context;
        }
    }
}
