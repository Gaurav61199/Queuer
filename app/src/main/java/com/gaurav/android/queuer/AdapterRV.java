package com.gaurav.android.queuer;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Created by Gaurav on 4/28/2017.
 */

public class AdapterRV extends RecyclerView.Adapter<AdapterRV.ViewHolder> {

    public ArrayList<PatientInfo> mDetails = new ArrayList<>();
    Context context;
    LayoutInflater inflater;
    private DatabaseReference PatInfoDB;


    public AdapterRV(Context context, ArrayList<PatientInfo> info) {
        mDetails = info;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.single_row_rv, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PatientInfo pi = mDetails.get(position);
        holder.naam.setText(pi.pName + " - " + pi.pGender);
        holder.kramank.setText(pi.pContact);
        holder.pehchaanpatra.setText(pi.pID + "");
        holder.dinank.setText(pi.tDateTime);
        holder.kram.setText(pi.tokenNo + "");
        holder.cancelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.cancel_book_dialog);
                Button buttonY = (Button)dialog.findViewById(R.id.YesCancelBt);
                Button buttonN = (Button)dialog.findViewById(R.id.NoCancelBt);
                dialog.show();
                buttonY.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // deleting the details from Firebase Database
                        PatInfoDB = FirebaseDatabase.getInstance().getReference();
                        PatInfoDB.child("Patients").child(pi.tokenNo.toString()).removeValue();
                        // deleting the details from Shared  Preference
                        GetArrayList mGetArrayList = (GetArrayList) context.getApplicationContext();
                        mGetArrayList.removeFromSP(pi.tokenNo, pi.tDateTime);
                        // deleting the details from recyclerview
                        mDetails.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                buttonN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetails == null ? 0 : mDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView naam;
        public TextView kramank;
        public TextView pehchaanpatra;
        public TextView dinank;
        public TextView kram;
        public ImageButton cancelBook;


        public ViewHolder(View itemView) {
            super(itemView);

            naam = (TextView) itemView.findViewById(R.id.namefield);
            kramank = (TextView) itemView.findViewById(R.id.contactfield);
            pehchaanpatra = (TextView) itemView.findViewById(R.id.idfield);
            dinank = (TextView) itemView.findViewById(R.id.datefield);
            kram = (TextView)itemView.findViewById(R.id.tokennofield);
            cancelBook = (ImageButton)itemView.findViewById(R.id.bookTokenDelete);
        }

    }



}
