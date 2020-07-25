package com.example.foodishot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodishot.Model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucasurbas.listitemview.ListItemView;

import java.util.Locale;

public class AccountActivity extends AppCompatActivity {
String user_phone_num;
String user_name;
ProgressBar progressBar;
ImageView mRefresh;
TextView account_name,account_num;
ListItemView sign_out,set_en,set_sw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        loadLocale();
        user_phone_num = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        account_name = findViewById(R.id.act_account_name);
        mRefresh = findViewById(R.id.refresh_account);
        progressBar = findViewById(R.id.progressBar_home);
        account_num = findViewById(R.id.act_account_no);
        sign_out = findViewById(R.id.sign_out);
        set_en = findViewById(R.id.set_en);
        set_sw = findViewById(R.id.set_sw);

        View.OnClickListener set_lang = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_res_act = new Intent(AccountActivity.this, Home.class);
                switch (v.getId()) {
                    case R.id.set_en:
                        setLocale("en");
                        startActivity(intent_res_act);
                        break;
                    case R.id.set_sw:
                        setLocale("sw");
                        startActivity(intent_res_act);
                        break;
                }
            }
        };
        set_en.setOnClickListener(set_lang);
        set_sw.setOnClickListener(set_lang);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefresh.setVisibility(View.GONE);
                userInfo();
            }
        });
        userInfo();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_home);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.restaurant_bn:
                        Intent intent_res_act = new Intent(AccountActivity.this, Home.class);
                        intent_res_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_res_act);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.orders_bn:
                        Intent intent_order_act = new Intent(AccountActivity.this, OrdersActivity.class);
                        intent_order_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_order_act);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.account_bn:
                        break;
                }
                return false;
            }
        });
    }
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
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
    public void userInfo(){
        progressBar.setVisibility(View.VISIBLE);
        if (isNetworkAvailable()){
            FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        String v2_user_phone_num = null;
                        if (user_phone_num.startsWith("0")) {
                            v2_user_phone_num = "+255" + user_phone_num.substring(1);
                        } else if (user_phone_num.startsWith("+")) {
                            v2_user_phone_num = "0" + user_phone_num.substring(4);
                        }
                        if (dataSnapshot.child(user_phone_num).exists()) {
                            User user = dataSnapshot.child(user_phone_num).getValue(User.class);
                            user_name = user.getName();
                        } else if (dataSnapshot.child(v2_user_phone_num).exists()) {
                            User user = dataSnapshot.child(v2_user_phone_num).getValue(User.class);
                            user_name = user.getName();
                            account_name.setText(user_name);
                            account_num.setText(user_phone_num);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    mRefresh.setVisibility(View.VISIBLE);
                }
            },2000);
        }
    };
    public void loadLocale(){
        SharedPreferences savedLang = getSharedPreferences("Settings",MODE_PRIVATE);
        String language = savedLang.getString("My Lang","");
        setLocale(language);
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
