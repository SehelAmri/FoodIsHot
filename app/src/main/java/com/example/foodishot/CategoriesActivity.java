package com.example.foodishot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.foodishot.Model.Restaurants;
import com.example.foodishot.adapter.SearchFilterAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class CategoriesActivity extends AppCompatActivity {
Bundle bundle;
String ca_type,ca_user_city,ca_user_country,mins_set_hm,hours_set_hm;
FirebaseDatabase database;
DatabaseReference reference;
ArrayList<Restaurants> arrayList;
Double latitude,longitude;
String delivery_time;
int ca_image;
RecyclerView recyclerView;
ImageView cat_image,mRefresh;
SearchFilterAdapter searchFilterAdapter;
RecyclerView.LayoutManager layoutManager;
ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        database = FirebaseDatabase.getInstance();
        bundle = getIntent().getExtras();
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        mRefresh = findViewById(R.id.refresh_category);
        layoutManager = new LinearLayoutManager(CategoriesActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        cat_image = findViewById(R.id.categories_image);
        progressBar = findViewById(R.id.progressBar_home);
        mins_set_hm = getString(R.string.minutes_set_hm);
        hours_set_hm = getString(R.string.hours_set_hm);
        if(bundle != null){
            ca_type = bundle.getString("ca_type","Street");
            ca_image = bundle.getInt("ca_image",0);
            ca_user_city = bundle.getString("ca_user_city","");
            ca_user_country = bundle.getString("ca_user_country","");
            latitude = bundle.getDouble("ca_lat",0.0);
            longitude = bundle.getDouble("ca_long",0.0);
        }
        reference = database.getReference().child("Restaurants").child(ca_user_country).child(ca_user_city);
        cat_image.setImageResource(ca_image);
        queryBasedOnLocale();
        getSupportActionBar().setTitle(ca_type);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRefresh.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                queryBasedOnLocale();
            }
        });
    }
    private void queryBasedOnLocale() {
        progressBar.setVisibility(View.VISIBLE);
        if(reference != null && isNetworkAvailable()) {
            Query query;
            query = reference.orderByChild("Type")
                    .equalTo(ca_type);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        arrayList.clear();
                        for (DataSnapshot dss : dataSnapshot.getChildren()) {
                            final Restaurants restaurants = dss.getValue(Restaurants.class);
                            Location locationA = new Location("point A");

                            locationA.setLatitude(latitude);
                            locationA.setLongitude(longitude);

                            Location locationB = new Location("point B");

                            locationB.setLatitude(Double.parseDouble(restaurants.getLatitude()));
                            locationB.setLongitude(Double.parseDouble(restaurants.getLongitude()));

                            float distance = locationA.distanceTo(locationB);
                            int distance_km = Math.round(distance / 1000);
                            if (distance_km <= 5) {
                                if (distance_km <= 1) {
                                    delivery_time = "20 - 30 " + mins_set_hm;
                                } else if (distance_km > 1 && distance_km <= 3) {
                                    delivery_time = "25 - 40 " + mins_set_hm;
                                } else if (distance_km > 3 && distance_km <= 5) {
                                    delivery_time = "30 - 45 " + mins_set_hm;
                                }
                            } else if (distance_km > 5 && distance_km <= 10) {
                                delivery_time = "45 - 55 " + mins_set_hm;
                            } else if (distance_km > 10 && distance_km <= 15) {
                                delivery_time = "1:00 - 1:15 " + hours_set_hm;
                            } else if (distance_km > 15 && distance_km <= 20) {
                                delivery_time = "1:15 - 1:30 " + hours_set_hm;
                            } else if (distance_km > 20 && distance_km <= 25) {
                                delivery_time = "1:30 - 1:45 " + hours_set_hm;
                            } else if (distance_km > 25 && distance_km <= 30) {
                                delivery_time = "2:00 -2:15 " + hours_set_hm;
                            } else {
                                delivery_time = "10 - 20 " + mins_set_hm;
                            }

                            arrayList.add(new Restaurants(restaurants.getUnavailable(), restaurants.getOpen(), restaurants.getClose(), restaurants.getPrice(), restaurants.getType(), restaurants.getSubType(), restaurants.getRating(),
                                    restaurants.getNoRating(), restaurants.getName(), restaurants.getImage(), restaurants.getLongitude(),
                                    restaurants.getLatitude(), distance, delivery_time, dss.getKey()));
                        }

                       searchFilterAdapter = new SearchFilterAdapter(ca_user_country, ca_user_city, arrayList, getApplicationContext());
                        recyclerView.setAdapter(searchFilterAdapter);
                        searchFilterAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        AlertDialog alertDialog = new AlertDialog.Builder(CategoriesActivity.this)
                                .setTitle(R.string.no_restaurants_available)
                                .setMessage(R.string.no_restaurants_available_details)
                                .setPositiveButton(R.string.scOk, null)
                                .show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        mRefresh.setVisibility(View.VISIBLE);
                    }
                }, 2000);
                if (searchFilterAdapter != null) {
                    arrayList.clear();
                    searchFilterAdapter.notifyDataSetChanged();
                }
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
