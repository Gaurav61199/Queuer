package com.gaurav.android.queuer;


import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Gaurav on 5/5/2017.
 */
public class GetArrayList extends Application {   // extending Application makes class Global

    public SharedPreferences storingData,restoringData,deleteData ;


    public void SaveInSP(PatientInfo gsonData){
        storingData = getSharedPreferences("com.gaurav.queuer",MODE_PRIVATE);
        SharedPreferences.Editor storageEditor = storingData.edit();
        Gson gson = new Gson();// Gson is an API to convert Object data into Json data
        String json = gson.toJson(gsonData);
        storageEditor.putString(gsonData.tokenNo.toString() +gsonData.tDateTime ,json);
/* tokenNo and TDateTime both is used for naming Shared Preference file to avoid
 to avoid conflicting of name in case the file in SP is not deleted and user booked the token on some other day with same token no*/
        storageEditor.commit();
    }

    public ArrayList<PatientInfo> GetFromSP() {
        ArrayList<PatientInfo> SPArray = new ArrayList<>();
        PatientInfo returnValue;
        Gson gson = new Gson();
        restoringData = getSharedPreferences("com.gaurav.queuer",MODE_PRIVATE);
        //Fetching the preferences is done directly on the SharedPreferences object, SharedPreferences.Editor is not required
        Map<String,?> entries = restoringData.getAll(); //throws exception
        //fetching all entries in Shared Preferences
        for (Map.Entry<String,?> listentry : entries.entrySet() ){
            returnValue = gson.fromJson((String) listentry.getValue(),PatientInfo.class);
            SPArray.add(returnValue);
        }
        return SPArray;
    }


    public void removeFromSP(Integer tokenNo, String tDateTime) {

        deleteData =getSharedPreferences("com.gaurav.queuer",MODE_PRIVATE);
        if (deleteData != null){
            deleteData.edit().remove(tokenNo.toString() + tDateTime).commit();
            Log.d("STATE", "deleteData is not null");
        }else{
            Log.d("STATE", "deleteData is  null");
        }

    }
}
