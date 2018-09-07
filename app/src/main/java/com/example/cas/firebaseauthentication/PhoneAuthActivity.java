package com.example.cas.firebaseauthentication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {
    //Views variable
    Button btnGenerateOTP,btnVerifyOTP;
    EditText etPhoneNumber, etOTP;

    //otp, number variable
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

        //start firebase login
        StartFirebaseLogin();

        //generating otp

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

        //sign in Button onClick
        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get entered otp from EditText for verification
                otp=etOTP.getText().toString();
                //verify using otp, verificationCode; should be same for success
                PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,otp);
                //sign in with caught credentials
                SigninWithPhone(credential);
            }
        });

    }

    //Sign in with phone method
    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //make vidible sign out button , make invisible login layout
                            Toast.makeText(PhoneAuthActivity.this, "SignedIn, Correct OTP", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(PhoneAuthActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
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
    }//StartFirebaseLogin END

}
