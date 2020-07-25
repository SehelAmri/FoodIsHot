package com.example.foodishot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button mButton;
    EditText edtPhone;
    TextView signUpLink;
    String editedEdtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        edtPhone = findViewById(R.id.phoneNumber);
        final Button signUpbtn = findViewById(R.id.signUpbtn);
        mButton = findViewById(R.id.mButton);
        signUpLink = findViewById(R.id.signUpLink);

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPage();
            }
        });
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneInput = edtPhone.getText().toString().trim();
                signUpbtn.setEnabled(!phoneInput.isEmpty());
                signUpbtn.setClickable(!phoneInput.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnChangeLang();
            }
        });

        //INIT FIREBASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

            signUpbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((edtPhone.getText().toString().startsWith("0") || edtPhone.getText().toString().startsWith("+")) && edtPhone.getText().toString().length() > 9) {
                        if (isNetworkAvailable()) {
                            if (validatePhone()) {
                                final String edtPhoneStr = edtPhone.getText().toString();
                                if (edtPhone.getText().toString().startsWith("0")) {
                                    editedEdtPhone = "+255" + edtPhoneStr.substring(1);
                                } else if (edtPhone.getText().toString().startsWith("+")) {
                                    editedEdtPhone = "0" + edtPhoneStr.substring(4);
                                }
                                final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                                mDialog.setMessage("Loading...");
                                mDialog.show();
                                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child(edtPhoneStr).exists()) {
                                            mDialog.dismiss();
                                            verifyPage();
                                        } else if (dataSnapshot.child(editedEdtPhone).exists()) {
                                            mDialog.dismiss();
                                            verifyPage();
                                        } else {
                                            mDialog.dismiss();
                                            registerPage();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {
                           AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(R.string.noConnectionToast)
                                    .setPositiveButton(R.string.scOk,null)
                                    .show();
                        }
                    }else{
                   edtPhone.setError(getResources().getString(R.string.scIncorrectNumber));
                    }
                }
            });
    }
    private void registerPage() {
        Intent intent = new Intent(this,SignIn.class);
        intent.putExtra("phoneNum",edtPhone.getText().toString());
        startActivity(intent);
    }
    private void verifyPage() {
        Intent intent = new Intent(this,SendCode.class);
        intent.putExtra("phoneNum",edtPhone.getText().toString());
        startActivity(intent);
    }
    
    private void setOnChangeLang() {
       final String[]  listOfLang = {"English","Kiswahili"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle(R.string.selectLang);
mBuilder.setSingleChoiceItems(listOfLang, -1, new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int i) {
        if(i == 0){
            //English
            setLocale("en");
            recreate();
        }else if(i == 1){
            //Swahili
            setLocale("sw");
            recreate();
        }
        dialog.dismiss();
    }
});
AlertDialog mDialog = mBuilder.create();
mDialog.show();
    }

    private void setLocale(String lang) {
Locale locale = new Locale(lang);
Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,getBaseContext().getResources().getDisplayMetrics());
        //Save Data to Preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My Lang",lang);
        editor.apply();

    }
public void loadLocale(){
        SharedPreferences savedLang = getSharedPreferences("Settings",MODE_PRIVATE);
        String language = savedLang.getString("My Lang","");
        setLocale(language);
}
    private boolean validatePhone(){
        String phoneInput =  edtPhone.getText().toString().trim();
        if (!Patterns.PHONE.matcher(phoneInput).matches()){
            edtPhone.setError(getResources().getString(R.string.sValidPhone));
            return false;
        }else {
            return true;
        }}

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

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(this,Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
