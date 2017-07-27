package com.gaurav.android.queuer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;



public class FormActivity extends AppCompatActivity {


    private Toolbar mtoolbar;
    private ScrollView mScrollView;
    private Button mButton;
    private EditText mEditText1,mEditText2;
    private RadioGroup mGenderRadioGroup;
    private Random mRandom;
    PatientInfo data = new PatientInfo();
    private DatabaseReference ATNRef, PatInfoDB;
    private LinearLayoutCompat mProgressBar;
    Integer temp;
    private CardView toolBarCV;
    private TextView TBATNValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        mtoolbar =(Toolbar) findViewById(R.id.form_toolbar);
        setSupportActionBar(mtoolbar);

        //Setting toolbar CardView center to the bottom of green view background   |  getViewTreeObserver scan for layout change and Display metrics returns density and font of screen
//        toolBarCV=(CardView) findViewById(R.id.CV);
//        toolBarCV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)toolBarCV.getLayoutParams();
//                params.setMargins((int) ((16*displayMetrics.density)+0.5), (-toolBarCV.getHeight()/2),(int) ((16*displayMetrics.density)+0.5),(int) ((0*displayMetrics.density)+0.5));
//                toolBarCV.setLayoutParams(params);
//            }
//        });

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference ATN = mDatabase.getReference("AvailableTokenNumber");
        TBATNValue = (TextView)findViewById(R.id.ATNValue);

        ATN.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TBATNValue.setText(dataSnapshot.getValue().toString());
                Log.d("ATN", dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mScrollView = (ScrollView) findViewById(R.id.form_scrollview);
        mScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager mIMM = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mIMM.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                return false;
            }
        });

        mProgressBar = (LinearLayoutCompat) findViewById(R.id.TokenFetchingProgressBar);
        mButton = (Button) findViewById(R.id.formSubmitBt);
        mEditText1= (EditText) findViewById(R.id.enterName);
        mEditText2= (EditText) findViewById(R.id.enterContact);
        mGenderRadioGroup = (RadioGroup) findViewById(R.id.GenderRadioGroup);

        mGenderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.MRadioBt){
                    data.pGender = "M";
                }else {
                    data.pGender = "F";
                }
            }
        });

        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  -  H:m:s");
        final String mDateTime = simpleDateFormat.format(calendar.getTime());


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                data.pName = mEditText1.getText().toString();
                data.pContact = mEditText2.getText().toString();
                mRandom = new Random();
                data.pID=mRandom.nextInt(10000);
                data.tDateTime = mDateTime;
                if(data.pName.isEmpty() || data.pContact.isEmpty() || (data.pGender==null)){
                    Toast.makeText(getApplicationContext(),"Enter complete patient's detail",Toast.LENGTH_SHORT).show();
                }else {
                    mScrollView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);

                    ATNRef= FirebaseDatabase.getInstance().getReference("AvailableTokenNumber");

                    ATNRef.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(final MutableData mutableData) {

                             temp = mutableData.getValue(Integer.class);

                            if(temp != null && temp > 0){
                                mutableData.setValue(temp + 1);
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            if (b == true){
                                data.tokenNo = temp;
                                PatInfoDB = FirebaseDatabase.getInstance().getReference();
                                PatInfoDB.child("Patients").child(data.tokenNo.toString()).setValue(data);  //Storing object data to the firebase at Patients/tokenNo/
                                Toast.makeText(getApplicationContext(),"Token booked: " + data.tokenNo.toString() ,Toast.LENGTH_SHORT).show();
                                GetArrayList gal = (GetArrayList) getApplicationContext();    // GetArrayList declared as Global
                                gal.SaveInSP(data);  //Saving Object in Shared Preferences

                                Intent intent = new Intent(FormActivity.this, Queuer.class);
                                startActivity(intent);
                            }

                        }
                    });




                }
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return super.onTouchEvent(event);
    }

}
