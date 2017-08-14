package com.gaurav.android.queuer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

/**
 * Created by Gaurav on 8/5/2017.
 */

public class login_activity extends AppCompatActivity implements View.OnClickListener {

    private EditText userPhone;
    private EditText verificationCodeEd;
    private FloatingActionButton next;
    private String verificationI;
    private TextView phoneCount;
    private CoordinatorLayout mCoordinatorLayout;
    private com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getPreferences(MODE_PRIVATE);
        boolean loginFlag = sharedPref.getBoolean("logedIn", false);
        if (loginFlag) {
            Log.d("STATE", "in loginFlag");
            Intent intent = new Intent(getApplicationContext(), Queuer.class);
            startActivity(intent);
        } else {
            Log.d("STATE", "not in loginFlag");
            setContentView(R.layout.activity_login);

            userPhone = (EditText) findViewById(R.id.user_phone);
            verificationCodeEd = (EditText) findViewById(R.id.verificationCode);
            next = (FloatingActionButton) findViewById(R.id.loginNext);
            phoneCount = (TextView) findViewById(R.id.phoneNocount);

            userPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    phoneCount.setText(editable.length() + "/10");
                }
            });
            next.setOnClickListener(this);
            callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                    Log.d("STATE", "in onVerificationCompleted");
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Log.d("STATE", "in onVerificationFailed");
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Log.d("STATE", "FirebaseAuthInvalidCredentialsException");
                        Toast.makeText(getApplicationContext(), "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    }

                    if (e instanceof FirebaseTooManyRequestsException) {
                        Log.d("STATE", "FirebaseTooManyRequestsException");
                        Toast.makeText(getApplicationContext(), "Too many request", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken tkn) {
                    Log.d("STATE", "in onCodeSent");
                    verificationI = verificationId;
                    userPhone.setVisibility(View.GONE);
                    phoneCount.setVisibility(View.GONE);
                    verificationCodeEd.setVisibility(View.VISIBLE);
                    i = 1;
                }
            };
        }


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        Log.d("STATE", "in signInWithPhoneAuthCredential");
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Sign up successful :)", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPrefs = getPreferences(MODE_PRIVATE);
                    sharedPrefs.edit().putBoolean("logedIn", true).commit();
                    Intent intent = new Intent(getApplicationContext(), Queuer.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Sign up failed.Retry :(", Toast.LENGTH_SHORT).show();
                    verificationCodeEd.setVisibility(View.GONE);
                    userPhone.setVisibility(View.VISIBLE);
                    i = 0;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Log.d("STATE", "in onClick");
        if (view.getId() == R.id.loginNext) {
            if (userPhone.getText().length() < 10) {
                Snackbar.make(findViewById(R.id.loginCoordinate), "Invalid Phone Number", Snackbar.LENGTH_SHORT).show();


            } else {
                if (i == 0) {
                    Log.d("STATE", "in i=0");
                    startPhoneNumberVerification(userPhone.getText());
                }
                if (i == 1) {
                    Log.d("STATE", "in i=1");
                    if (verificationCodeEd.getText() != null) {
                        String verificationCode = verificationCodeEd.getText().toString();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationI, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }
            }

        }
    }

    private void startPhoneNumberVerification(Editable phoneNo) {
        Log.d("STATE", "in startPhoneNumberVerification");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNo.toString(), 30, TimeUnit.SECONDS, login_activity.this, callbacks);
    }
}
