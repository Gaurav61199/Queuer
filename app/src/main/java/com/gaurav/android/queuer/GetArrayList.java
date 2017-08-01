package com.gaurav.android.queuer;


import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Gaurav on 5/5/2017.
 */
public class GetArrayList extends Application {   // extending Application makes class Global



    public SharedPreferences storingData,restoringData,deleteData ;

    public int getHighlight() {
        return highlight;
    }

    public void setHighlight(int highlight) {
        GetArrayList.highlight = highlight;
    }

    private static int highlight;  /* variable is declared as static as only one instance is required to be created to preserve
    the state of highlight else it get reset*/


    @Override
    public void onCreate(){
        super.onCreate();
        // Application class is instantiated before any other class and used for initialization of global state
        // Persistence should be set before any calls to database ref as per firebase, therefore it is the appropriate place
        FirebaseDatabase.getInstance().setPersistenceEnabled(true); //data is locally cached to maintain state offline and handle network interruption
    }

    public void SaveInSP(PatientInfo gsonData){
        storingData = getSharedPreferences("com.gaurav.queuer",MODE_PRIVATE);
        SharedPreferences.Editor storageEditor = storingData.edit();
        Gson gson = new Gson();// Gson is an API to convert Object data into Json data
        String json = gson.toJson(gsonData);
        storageEditor.putString(gsonData.tDateTime ,json);
/* TDateTime is used for naming Shared Preference file
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
            returnValue = gson.fromJson((String)listentry.getValue(),PatientInfo.class);
            SPArray.add(returnValue);
        }

        // as data from getAll() method and Map<> is inconsistent(not in order) it is needed to be sorted for proper sequence
        return sortSPArray(SPArray);
    }

    private ArrayList<PatientInfo> sortSPArray(ArrayList<PatientInfo> spArray) {

        for(int i=0 ; i< spArray.size();i++ ){
                for (int j =0 ; j< (spArray.size()- i-1);j++){
                    if (spArray.get(j).mCalendar.before(spArray.get(j+1).mCalendar)){   // before so that latest booking appears at top
                       PatientInfo temp = spArray.get(j); // sorting is done with respect to the time of booking
                        spArray.set(j,spArray.get(j+1));
                        spArray.set(j+1,temp);
                    }
                }
            }
        return spArray;
    }


    public void removeFromSP(Integer tokenNo, String tDateTime) {

        deleteData =getSharedPreferences("com.gaurav.queuer",MODE_PRIVATE);
        if (deleteData != null){
            deleteData.edit().remove(tDateTime).commit();
        }else{
            Log.d("STATE", "deleteData is  null");
        }

    }


}
