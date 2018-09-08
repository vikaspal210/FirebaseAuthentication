package com.example.cas.firebaseauthentication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {
    //Views variable
    Button btnGenerateOTP,btnVerifyOTP,btnSignOut;
    EditText etPhoneNumber, etOTP;
    private TextView mDetailTextView;
    private TextView mStatusTextView;

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
        updateUI(auth.getCurrentUser());

        //generating otp

        /*below lines will send an SMS to the provided phone number.
        As verifyPhoneNumber() is reentrant,
        it will not send another SMS on button click until the
        original request is timed out.*/
        btnGenerateOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    phoneNumber=etPhoneNumber.getText().toString();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            PhoneAuthActivity.this,
                            mCallbacks);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    Toast.makeText(PhoneAuthActivity.this, "Please Enter Phone No with country code first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //sign in Button onClick
        btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //get entered otp from EditText for verification
                    otp=etOTP.getText().toString();
                    //verify using otp, verificationCode; should be same for success
                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,otp);
                    //sign in with caught credentials
                    SigninWithPhone(credential);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                updateUI(auth.getCurrentUser());
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI(null);
                auth.signOut();
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
        btnSignOut=(Button)findViewById(R.id.sign_out_button);

        etPhoneNumber=(EditText)findViewById(R.id.edittext_phone_number);
        etOTP=(EditText)findViewById(R.id.edittext_otp);

        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
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
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = auth.getCurrentUser();
                updateUI(currentUser);
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

    private void signOut() {
        auth.signOut();
        updateUI(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentLoggedUser){
        if(currentLoggedUser!=null){
            mStatusTextView.setText(getString(R.string.google_status_fmt,currentLoggedUser.getPhoneNumber()));
            mDetailTextView.setText(getString(R.string.firebase_status_fmt,currentLoggedUser.getUid()));
            findViewById(R.id.login_linear_layout).setVisibility(View.GONE);
            findViewById(R.id.sign_out_linear_layout).setVisibility(View.VISIBLE);

        }else{
            mStatusTextView.setText("Signed Out");
            mDetailTextView.setText(null);
            findViewById(R.id.login_linear_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_linear_layout).setVisibility(View.GONE);
        }
    }

}
