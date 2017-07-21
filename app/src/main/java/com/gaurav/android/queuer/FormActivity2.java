package com.gaurav.android.queuer;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by Gaurav on 6/28/2017.
 */

public class FormActivity2 extends AppCompatActivity implements FFragment.CommBridge{

    private SFragment mSFragment = new SFragment();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form2);
        FFragment fragment1 = new FFragment();
        if(fragment1 != null){
            FragmentManager mManager = getFragmentManager();
            FragmentTransaction mTransaction = mManager.beginTransaction();
            mTransaction.add(R.id.fragment_container,fragment1,"frag1");
            mTransaction.commit();
        }

    }

    @Override
    public void clinicPlaceName(String Clinic, String Place) {

        mSFragment.setF1Details(Clinic,Place);  // Details from FFragment is set in method setF1Details in SFragment
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,mSFragment).addToBackStack(null).commit();

    }
}
