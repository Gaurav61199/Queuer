package com.gaurav.android.queuer;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Gaurav on 6/28/2017.
 */

public class SFragment extends Fragment implements View.OnClickListener {
    private EditText mEditText1, mEditText2;
    private RadioGroup mGenderRadioGroup;
    private Button cancelBt, bookBt,backBt;
    private String mDateTime;
    private DatabaseReference ATNRef, PatInfoDB, rootRef;
    private Integer temp;
    private Toolbar mToolbar;
    private Calendar mCalendar;
    private LinearLayoutCompat progressview;
    private RelativeLayout f2cont;
    PatientInfo data = new PatientInfo();
    String lClinic, lPlace;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.form_fragment2, container, false);
        // obtaining references for various views from form fragment 2
        mToolbar=(Toolbar) getActivity().findViewById(R.id.form_toolbar);
        mToolbar.setTitle(lClinic);
        mToolbar.setSubtitle(lPlace);
        mToolbar.setSubtitleTextColor(Color.WHITE);
        cancelBt = (Button) v.findViewById(R.id.quitBooking);
        bookBt = (Button) v.findViewById(R.id.bookBt);
        backBt=(Button)v.findViewById(R.id.f2backBt);
        mEditText1 = (EditText) v.findViewById(R.id.enterName);
        mEditText2 = (EditText) v.findViewById(R.id.enterContact);
        mGenderRadioGroup = (RadioGroup) v.findViewById(R.id.GenderRadioGroup);
        progressview = (LinearLayoutCompat) v.findViewById(R.id.TokenFetchingProgressBar);
        f2cont = (RelativeLayout) v.findViewById(R.id.f2Container);
        rootRef = FirebaseDatabase.getInstance().getReference();



        //setting Listeners
        mGenderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.MRadioBt) {
                    data.pGender = "M";
                } else {
                    data.pGender = "F";
                }
            }
        });

        bookBt.setOnClickListener(this);
        cancelBt.setOnClickListener(this);
        backBt.setOnClickListener(this);

        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  -  H:m:s");
        mDateTime = simpleDateFormat.format(calendar.getTime());
        mCalendar  =simpleDateFormat.getCalendar();

        return v;
    }

    public void setF1Details(String clinic, String place) {
        lClinic = clinic;
        lPlace = place;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bookBt:

                data.pName = mEditText1.getText().toString();
                data.pContact = mEditText2.getText().toString();
                data.cName = lClinic;
                data.cPlace = lPlace;
                data.tDateTime = mDateTime;
                data.mCalendar = mCalendar;

                if (data.pName.isEmpty() || data.pContact.isEmpty() || (data.pGender == null)) {
                    Toast.makeText(getActivity(), "Enter complete patient's detail", Toast.LENGTH_SHORT).show();
                } else {
                    f2cont.setVisibility(View.GONE);
                    progressview.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), lPlace + lClinic + "", Toast.LENGTH_SHORT).show();
                    ATNRef = rootRef.child(lPlace).child("Clinics").child(lClinic).child("AvailableTokenNumber");

                    ATNRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(final MutableData mutableData) {

                            temp = mutableData.getValue(Integer.class);

                            if (temp != null && temp > 0) {
                                mutableData.setValue(temp + 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            if (b == true) {
                                data.tokenNo = temp;
                                PatInfoDB = rootRef.child(lPlace).child("Clinics").child(lClinic);
                                PatInfoDB.child("Patients").child(data.tokenNo.toString()).setValue(data);  //Storing object data to the firebase at Patients/tokenNo/
                                Toast.makeText(getActivity(), "Token booked: " + data.tokenNo.toString(), Toast.LENGTH_SHORT).show();
                                GetArrayList gal = (GetArrayList) getActivity().getApplicationContext();    // GetArrayList declared as Global
                                gal.SaveInSP(data);  //Saving Object in Shared Preferences
                                gal.setHighlight(1);
                                //gal.save(data);
                                Intent intent = new Intent(getActivity(), Queuer.class);
                                startActivity(intent);
                            }

                        }
                    });
                }
                break;
            case R.id.quitBooking:
                Intent intent = new Intent(getActivity(), Queuer.class);
                startActivity(intent);
                break;
            case R.id.f2backBt:
                getFragmentManager().popBackStackImmediate();
                break;
        }

    }

}
