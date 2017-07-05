package com.gaurav.android.queuer;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gaurav on 4/28/2017.
 */

public class PatientInfo  {
    public String pName=null;   // patient's name
    public String pContact=null;
    public String pGender;
    public int pID;
    public String tDateTime;   // today's date and time
    public Integer tokenNo;

    public PatientInfo(){
        // As per Firebase default constructor is required for calls to DataSnapshot.getValue(User.class)
    }
}
