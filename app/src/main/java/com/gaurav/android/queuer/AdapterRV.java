package com.gaurav.android.queuer;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * Created by Gaurav on 4/28/2017.
 */

public class AdapterRV extends RecyclerView.Adapter<AdapterRV.ViewHolder> {

    public ArrayList<PatientInfo> mDetails = new ArrayList<>();
    Context context;
    LayoutInflater inflater;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();




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
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final PatientInfo pi = mDetails.get(position);
        DatabaseReference OTN = rootRef.child(pi.cPlace).child("Clinics").child(pi.cName).child("OngoingTokenNumber");
        OTN.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.maujoodatoken.setText("Ongoing Token Number" + ":   " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.naam.setText(pi.pName + " - " + pi.pGender);
        holder.dinank.setText(pi.tDateTime);
        holder.kram.setText(pi.tokenNo + "");
        holder.Chikitshalaya.setText(pi.cName);

        GetArrayList gal = new GetArrayList();
        if (position == 0 && (gal.getHighlight() == 1)) {
            ObjectAnimator animator = ObjectAnimator.ofObject(holder.InfoArea, "backgroundColor", new ArgbEvaluator(), Color.argb(100, 29, 233, 182), Color.TRANSPARENT);
            animator.setDuration(4000);
            animator.start();
            gal.setHighlight(0);
        }


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
                        rootRef.child(pi.cPlace).child("Clinics").child(pi.cName).child("Patients").child(pi.tokenNo.toString()).removeValue();
                       // PatInfoDB.child("Patients").child(pi.tokenNo.toString()).removeValue();
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
        public TextView dinank;
        public TextView kram;
        public ImageButton cancelBook;
        public LinearLayoutCompat InfoArea;
        public TextView Chikitshalaya;
        public TextView maujoodatoken;

        public ViewHolder(View itemView) {
            super(itemView);
            maujoodatoken = (TextView) itemView.findViewById(R.id.RVListClinicOTN);
            Chikitshalaya = (TextView) itemView.findViewById(R.id.RVListClinicName);
            naam = (TextView) itemView.findViewById(R.id.namefield);
            dinank = (TextView) itemView.findViewById(R.id.datefield);
            kram = (TextView)itemView.findViewById(R.id.tokennofield);
            cancelBook = (ImageButton)itemView.findViewById(R.id.bookTokenDelete);
            InfoArea = (LinearLayoutCompat) itemView.findViewById(R.id.contentArea);
        }

    }

}
