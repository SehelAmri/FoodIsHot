package com.example.foodishot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodishot.Model.AddOns;
import com.example.foodishot.Model.AddOnsSpecs;
import com.example.foodishot.adapter.ExtrasAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddToCart extends AppCompatActivity {
    Bundle bundle;
    String name,desc,price;
    ImageView back;
    TextView mName,mDesc,mAdd,mMinus,mCount;
    SharedPreferences sharedPreferences;
    Button mButton;
    ExtrasAdapter extrasAdapter;
    DatabaseReference reference_add;
    String restaurant_key,tab,menu_key,atc_user_country,atc_user_city;
    ArrayList<AddOns> addOns;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        sharedPreferences = getSharedPreferences("groupedState",MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        bundle = getIntent().getExtras();
        if(bundle != null){
            name = bundle.getString("atc_name");
            desc = bundle.getString("atc_desc");
            price = bundle.getString("atc_price");
            restaurant_key = bundle.getString("atc_key");
            tab = bundle.getString("atc_tab");
            atc_user_city = bundle.getString("atc_user_city");
            atc_user_country = bundle.getString("atc_user_country");
            menu_key = bundle.getString("atc_menu_key");
        }
        back = findViewById(R.id.atc_back);
        mName = findViewById(R.id.atc_name);
        mDesc = findViewById(R.id.atc_desc);
        mAdd = findViewById(R.id.atc_add);
        mMinus = findViewById(R.id.atc_minus);
        mCount = findViewById(R.id.atc_count);
        addOns = new ArrayList<>();
        mButton = findViewById(R.id.atc_btn);
        recyclerView = findViewById(R.id.atc_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddToCart.this);
        recyclerView.setLayoutManager(layoutManager);
        View.OnClickListener iterate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer mCountInt=Integer.parseInt(mCount.getText().toString());
               int atc_price = Integer.parseInt(price.replaceAll("[^0-9]", ""));
                switch (v.getId()){
                    case R.id.atc_add:
                      mCountInt += 1;
                      mCount.setText(String.valueOf(mCountInt));
                       mButton.setText(getString(R.string.add_to_order) + " " + String.format("%,d", atc_price * mCountInt) + " Tsh");
                        break;
                    case R.id.atc_minus:
                        if(mCountInt > 1) {
                            mCountInt -= 1;
                            mCount.setText(String.valueOf(mCountInt));
                            mButton.setText(getString(R.string.add_to_order) + " " + String.format("%,d", atc_price * mCountInt) + " Tsh");
                        }
                        break;
                }
                if(mCountInt == 1){
                    mMinus.setTextColor(getResources().getColor(R.color.searchBarBorder));
                }else{
                    mMinus.setTextColor(Color.BLACK);
                }
            }
        };
        mAdd.setOnClickListener(iterate);
        mMinus.setOnClickListener(iterate);
        mName.setText(name);
        mDesc.setText(desc);
        mButton.setText(getString(R.string.add_to_order) + " " + price);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference reference = database.getReference().child("Restaurants").child(atc_user_country).child(atc_user_city).child(restaurant_key).child("Menu").child(tab).child(menu_key).child("Extras");
       reference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               addOns.clear();
               if(dataSnapshot.hasChildren()) {
                   for (DataSnapshot dss : dataSnapshot.getChildren()) {
                           AddOns required = dss.getValue(AddOns.class);
                           addOns.add(new AddOns(dss.getKey(), required.getRequired(),required.getUp_to()));
                           reference_add = database.getReference().child("Restaurants").child(atc_user_country).child(atc_user_city).child(restaurant_key).child("Menu")
                                   .child(tab).child(menu_key).child("Extras");
                   }
                   extrasAdapter = new ExtrasAdapter(addOns,AddToCart.this,reference_add);
                   recyclerView.setAdapter(extrasAdapter);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }
}
