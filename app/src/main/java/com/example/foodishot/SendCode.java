package com.example.foodishot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodishot.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import okio.Timeout;

public class SendCode extends AppCompatActivity {
FirebaseAuth mAuth;
String codeSent,phone;
EditText codeInput;
int noOfTimesClicked = 0;
int enableVerifier = 0;
Button resendCode,phoneVerified;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_code);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        final Bundle bundle = getIntent().getExtras();
        final TextView resendTimer = findViewById(R.id.resendTimer);

        String scEnterCodePt1 = getResources().getString(R.string.scEnterCodePt1);
        TextView scMessageView = findViewById(R.id.scWaitforMessage);
        resendCode = findViewById(R.id.resendCode);
        phoneVerified = findViewById(R.id.phoneVerified);
        codeInput = findViewById(R.id.editTextCode);

        //disable button
        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneInput = codeInput.getText().toString().trim();
                if(enableVerifier == 0) {
                    phoneVerified.setEnabled(!phoneInput.isEmpty());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // Timer
        final CountDownTimer countDownTimer = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                resendTimer.setText(getResources().getString(R.string.scTimer) +" "+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                resendCode.setEnabled(true);
                resendTimer.setText("");
            }
        };

        if(bundle != null){
            String Subphone = bundle.getString("phoneNum");
            if(Subphone.startsWith("0")){
                phone = "+255"+Subphone.substring(1);
            }else{
                phone = Subphone;
            }
            scMessageView.setText(Html.fromHtml(scEnterCodePt1+" <b>"+phone+"</b>"));
            sendVerificationCode(phone);
        }
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    noOfTimesClicked += 1;
                    if (noOfTimesClicked <= 3) {
                        if (bundle != null) {
                            sendVerificationCode(phone);
                            Toast.makeText(SendCode.this, codeSent, Toast.LENGTH_LONG);
                        }
                        resendCode.setEnabled(false);
                        countDownTimer.start();
                    } else {
                        resendTimer.setText(R.string.scMaxRequest);
                    }
                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(SendCode.this)
                            .setTitle(R.string.noConnectionToast)
                            .setPositiveButton(R.string.scOk,null)
                            .show();
                }
            }
        });
        phoneVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    verifySignInCode();
                }else {
                    AlertDialog alertDialog = new AlertDialog.Builder(SendCode.this)
                            .setTitle(R.string.noConnectionToast)
                            .setPositiveButton(R.string.scOk,null)
                            .show();
                }
            }
        });
        //Init firebase
       mAuth = FirebaseAuth.getInstance();
    }


    private void verifySignInCode() {
        String code = codeInput.getText().toString();
        if(codeSent != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
            signInWithPhoneAuthCredential(credential);
        }
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                         locationActivity_intent();
                         signInUser();
                        } else {

                            }
                    }
                });
    }
    private void sendVerificationCode(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,60, TimeUnit.SECONDS,this,mCallbacks);
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
         
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            enableVerifier = 1;
           phoneVerified.setEnabled(false);
            AlertDialog.Builder alertDialog =new AlertDialog.Builder(SendCode.this);
            alertDialog.setMessage(R.string.scIncorrectNumber);
            alertDialog.setPositiveButton(R.string.scOk,null);
            alertDialog.create().show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }

    };
    private void locationActivity_intent(){
        Intent intent = new Intent(SendCode.this,LocationActivity.class);
        startActivity(intent);
        finish();
    }
    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        }catch (NullPointerException e){
            return false;
        }

    }
    private void signInUser(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        final Bundle bundle = getIntent().getExtras();
        table_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(bundle != null) {
                    String pullName, pullEmail, pullPassword, pullPhone,editedEdtPhone = "0";
                    pullPhone = bundle.getString("phoneNum");
                    if(pullPhone.startsWith("0")){
                        editedEdtPhone ="+255"+pullPhone.substring(1);
                    }else if(pullPhone.startsWith("+")){
                        editedEdtPhone ="0"+pullPhone.substring(4);
                    }
                    if (!dataSnapshot.child(pullPhone).exists() && !dataSnapshot.child(editedEdtPhone).exists() ) {
                        pullName = bundle.getString("Name");
                        pullEmail = bundle.getString("Email");
                        pullPassword = bundle.getString("password");
                        User user = new User(pullName, pullEmail, pullPassword);
                        table_user.child(pullPhone).setValue(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
