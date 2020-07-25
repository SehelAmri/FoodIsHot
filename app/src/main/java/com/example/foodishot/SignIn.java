package com.example.foodishot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodishot.Model.User;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity {
    EditText edtEmail, edtPassword, edtPhone,edtName;
    Button btnSignUp;
    TextView txtTermsnPrivacy;
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        edtName = findViewById(R.id.name);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        edtPhone = findViewById(R.id.mPhoneNum);
        btnSignUp = findViewById(R.id.mRegister);
        txtTermsnPrivacy = findViewById(R.id.privacy_with_terms);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String phone = bundle.getString("phoneNum");
           edtPhone.setText(phone);
        }
        //Terms and condition and privacy policy

        String sAgreePt1 = getResources().getString(R.string.sAgreeTermsPt1);
        String sAgreePt2 = getResources().getString(R.string.sAgreeTermsPt2);

        txtTermsnPrivacy.setText(Html.fromHtml(sAgreePt1+" <font color='#F42900'>Privacy Policy</font color> "));
        txtTermsnPrivacy.append(Html.fromHtml(sAgreePt2+" <font color='#F42900'>Terms of Service</font color>"));

        //Handle Empty Field
        TextWatcher emptyField = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailInput = edtEmail.getText().toString().trim();
                String passInput = edtPassword.getText().toString().trim();
                String phoneInput = edtPhone.getText().toString().trim();

                btnSignUp.setClickable(!emailInput.isEmpty() && !passInput.isEmpty() && !phoneInput.isEmpty());
                btnSignUp.setEnabled(!emailInput.isEmpty() && !passInput.isEmpty() && !phoneInput.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        edtPhone.addTextChangedListener(emptyField);
        edtPassword.addTextChangedListener(emptyField);
        edtEmail.addTextChangedListener(emptyField);


        //Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if (validateEmail() && validatePhone() && validatePass()) {
                        final ProgressDialog mLoading = new ProgressDialog(SignIn.this);
                        mLoading.setMessage("Loading...");
                        mLoading.show();
                        table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                    mLoading.dismiss();
                                    edtPhone.setError(getResources().getString(R.string.alreadyExists));
                                } else {
                                    mLoading.dismiss();
                                    verifyPage();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }else {
                   AlertDialog alertDialog = new AlertDialog.Builder(SignIn.this)
                            .setTitle(R.string.noConnectionToast)
                            .setPositiveButton(R.string.scOk,null)
                            .show();
                }
            }

        });
    }
    private void verifyPage() {
        Intent intent = new Intent(this,SendCode.class);
        intent.putExtra("phoneNum",edtPhone.getText().toString());
        intent.putExtra("Name",edtName.getText().toString());
        intent.putExtra("Email",edtEmail.getText().toString());
        intent.putExtra("password",edtPassword.getText().toString());
        startActivity(intent);
    }
    private boolean validateEmail() {
        String emailInput = edtEmail.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            edtEmail.setError(getResources().getString(R.string.sValidEmail));
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePhone() {
        String phoneInput = edtPhone.getText().toString().trim();
        if (!Patterns.PHONE.matcher(phoneInput).matches()) {
            edtPhone.setError(getResources().getString(R.string.sValidPhone));
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePass() {
        String passInput = edtPassword.getText().toString().trim();
        Pattern PASSWORD = Pattern.compile("((?=.*[a-z]).{8,})");
        if (!PASSWORD.matcher(passInput).matches()) {
            edtPassword.setError(getResources().getString(R.string.sValidPass));
            return false;
        } else {
            return true;
        }
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
}
