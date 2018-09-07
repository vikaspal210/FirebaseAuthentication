package com.example.cas.firebaseauthentication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {
    Button btnGenerateOTP,btnVerifyOTP;
    EditText etPhoneNumber, etOTP;

    private String phoneNumber,otp;

    //Global variable for verification state change
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    //FreebaseAuth variable
    private FirebaseAuth auth;

    //received verification code from otp
    private String verificationCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        //initializing views
        findViews();

        //setting onClickListener
        /*below lines will send an SMS to the provided phone number.
        As verifyPhoneNumber() is reentrant,
        it will not send another SMS on button click until the
        original request is timed out.*/
        btnGenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber=etPhoneNumber.getText().toString();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        PhoneAuthActivity.this,
                        mCallbacks);
            }
        });

    }

    //method to initialize views
    private void findViews() {
        btnGenerateOTP=(Button)findViewById(R.id.button_generate_otp);
        btnVerifyOTP=(Button)findViewById(R.id.button_verify_otp);

        etPhoneNumber=(EditText)findViewById(R.id.edittext_phone_number);
        etOTP=(EditText)findViewById(R.id.edittext_otp);
    }

    //firebase login method
    private void StartFirebaseLogin(){
        //get FirebaseAuth instance
        auth=FirebaseAuth.getInstance();

        //OnVerificationStateChangedCallbacks
        //We have to override onVerificationCompleted & onVerificationFailed
        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(PhoneAuthActivity.this, "verification completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneAuthActivity.this, "verification failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode=s;
                Toast.makeText(PhoneAuthActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
            }
        };

    }

}
