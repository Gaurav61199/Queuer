package com.gaurav.android.queuer;

import java.util.Calendar;

/**
 * Created by Gaurav on 4/28/2017.
 */

public class PatientInfo  {
    public String pName=null;   // patient's name
    public String pContact=null;
    public String pGender;
    public String tDateTime;   // today's date and time
    public Integer tokenNo;
    public String cName;   // clinic name
    public String cPlace;  // clinic place
    public Calendar mCalendar;
    public PatientInfo(){
        // As per Firebase default constructor is required for calls to DataSnapshot.getValue(User.class)
    }
}
