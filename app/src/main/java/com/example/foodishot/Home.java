package com.example.foodishot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.foodishot.Model.Categories;
import com.example.foodishot.Model.Restaurants;
import com.example.foodishot.adapter.CategoriesAdapter;
import com.example.foodishot.adapter.SearchFilterAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Home extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView categories_recyclerView;
    RecyclerView.LayoutManager categories_layoutManager;
    String current_user_locale,current_user_city,current_user_country;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference reference;
    boolean inRange = false;
    SearchFilterAdapter searchFilterAdapter;
    Double latitude = 0.0;
    Double longitude = 0.0;
    String delivery_time;
    Geocoder coder;
    List<Address> address;
    boolean lower_price = false;
    boolean higher_price = false;
    boolean isRefreshing = false;
    boolean isSearching = false;
    ImageView filter,mRefresh;
    String mins_set_hm,hours_set_hm;
    ArrayList<Restaurants> arrayList;
    ArrayList<Categories> categories_list;
    SwipeRefreshLayout swipeRefreshLayout;

    public Button current_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        database = FirebaseDatabase.getInstance();
        recyclerView = findViewById(R.id.recycler_menu);
        categories_recyclerView = findViewById(R.id.recycler_categories);
        mRefresh = findViewById(R.id.refresh_home);
        sharedPreferences = getSharedPreferences("user_location", MODE_PRIVATE);
        categories_recyclerView.setHasFixedSize(true);
        recyclerView.setHasFixedSize(true);
        categories_layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        categories_recyclerView.setLayoutManager(categories_layoutManager);
        layoutManager = new LinearLayoutManager(Home.this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.progressBar_home);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        arrayList = new ArrayList<>();
        categories_list = new ArrayList<>();
        mins_set_hm = getString(R.string.minutes_set_hm);
        hours_set_hm = getString(R.string.hours_set_hm);
        final EditText homeSearchTxt = findViewById(R.id.home_mEdit);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_home);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);

        mRefresh.bringToFront();

        categories_list.add(new Categories("Mexican",R.drawable.sandwich));
        categories_list.add(new Categories("Italian",R.drawable.pizza));
        categories_list.add(new Categories("Fast food",R.drawable.hamburger));
        categories_list.add(new Categories("Chinese",R.drawable.ramen));
        categories_list.add(new Categories("Lebanese",R.drawable.shawarma));
        categories_list.add(new Categories("Seafood",R.drawable.lobster));
        categories_list.add(new Categories("Indian",R.drawable.indian_food));
        categories_list.add(new Categories("Café",R.drawable.coffee));
        categories_list.add(new Categories("Street",R.drawable.stand));

        filter = findViewById(R.id.filter);
        menuItem.setChecked(true);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[]  listOfFilter = {getString(R.string.lower_price_first),getString(R.string.higher_price_first),getString(R.string.string_filter_distance)};
                AlertDialog.Builder sort_price = new AlertDialog.Builder(Home.this)
                        .setTitle(R.string.sort_by);
                sort_price.setSingleChoiceItems(listOfFilter, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if(i == 0){
                           lower_price = true;
                           higher_price = false;
                           queryBasedOnLocale("$");
                        }else if(i == 1){
                            lower_price = true;
                            higher_price = true;
                            queryBasedOnLocale("$");
                        }else if(i == 2){
                            lower_price = false;
                            higher_price = false;
                            queryBasedOnLocale("");
                        }
                        dialog.dismiss();
                    }
                });
               AlertDialog mDialog = sort_price.create();
                mDialog.show();
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.restaurant_bn:
                        break;
                    case R.id.orders_bn:
                        Intent intent_order_act = new Intent(Home.this, OrdersActivity.class);
                        intent_order_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_order_act);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.account_bn:
                        Intent intent_acc_act = new Intent(Home.this, AccountActivity.class);
                        intent_acc_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_acc_act);
                        overridePendingTransition(0,0);
                        break;
                }
                return false;
            }
        });
        homeSearchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inRange) {
                    lower_price = false;
                    higher_price = false;
                    isSearching = true;
                    if (!s.toString().isEmpty()) {
                        queryBasedOnLocale(s.toString().toLowerCase());
                    } else {
                        queryBasedOnLocale("");
                    }
                }
            }
        });

        current_location = findViewById(R.id.current_location);
        current_user_locale = sharedPreferences.getString("current_user_locale", getResources().getString(R.string.get_curr_location));
        if (current_user_locale.contains(",")) {
            String[] current_user_parts = current_user_locale.split(",");
            String current_user_displayed_info_pt1 = current_user_parts[0];
            if (current_user_parts[1] != null) {
                String current_user_displayed_info_pt2 = current_user_parts[1];
                String current_user_displayed_info = current_user_displayed_info_pt1 + "," + current_user_displayed_info_pt2;
                current_location.setText(current_user_displayed_info);
            } else {
                String current_user_displayed_info = current_user_displayed_info_pt1;
                current_location.setText(current_user_displayed_info);
            }
        } else {
            current_location.setText(current_user_locale);
        }

        coder = new Geocoder(this);
        try {
            address = coder.getFromLocationName(current_user_locale, 5);
            if (address != null && address.size() > 0) {
                Address location = address.get(0);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                current_user_city =location.getLocality();
                current_user_country = location.getCountryName();
                reference = database.getReference().child("Restaurants").child(current_user_country).child(current_user_city);
                 inRange = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryBasedOnLocale("");
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(categories_list,current_user_country,current_user_city,latitude,longitude);
        categories_recyclerView.setAdapter(categoriesAdapter);

        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, LocationActivity.class);
                startActivity(intent);
            }
        });
//

swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
        lower_price = false;
        higher_price = false;
        isRefreshing = true;
        queryBasedOnLocale("");
swipeRefreshLayout.setRefreshing(false);
    }
});
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    try {
                        address = coder.getFromLocationName(current_user_locale, 5);
                        if (address != null && address.size() > 0) {
                            Address location = address.get(0);
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            current_user_city =location.getLocality();
                            current_user_country = location.getCountryName();
                            reference = database.getReference().child("Restaurants").child(current_user_country).child(current_user_city);
                            inRange = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    queryBasedOnLocale("");
                }else{
                    queryBasedOnLocale("");
                }

            }
        });
    }

    private void queryBasedOnLocale(String s) {
        Query query;
        String l,child_filtered;
        mRefresh.setVisibility(View.GONE);
        if (s == "" && isRefreshing == false) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if(lower_price){
            l = "$$$";
            child_filtered = "Price";
        }else{
            l = s + "\uf8ff";
            child_filtered = "Name_Sort";
        }
if(reference != null && isNetworkAvailable()) {
    query = reference.orderByChild(child_filtered)
            .startAt(s)
            .endAt(l);

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
                if (!lower_price) {
                    Collections.sort(arrayList, new CustomComparator());
                }
                if (higher_price) {
                    Collections.reverse(arrayList);
                }
                searchFilterAdapter = new SearchFilterAdapter(current_user_country, current_user_city, arrayList, getApplicationContext());
                recyclerView.setAdapter(searchFilterAdapter);
                searchFilterAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                if (!isSearching) {
                    new AlertDialog.Builder(Home.this)
                            .setTitle(R.string.Hmout_of_reach)
                            .setMessage(R.string.HmOut_of_reach_msg)
                            .setPositiveButton(R.string.scOk, null)
                            .show();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
       });
      }else{
    if(current_location.getText().equals(getResources().getString(R.string.get_curr_location))){
       new AlertDialog.Builder(Home.this)
                .setTitle(R.string.getDeliveryLocation)
                .setMessage(R.string.getDeliveryLocationDetails)
                .setPositiveButton(R.string.scOk, null)
                .show();
        progressBar.setVisibility(View.GONE);
    }else {
        progressBar.setVisibility(View.VISIBLE);
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
    public class CustomComparator implements Comparator<Restaurants> {
        @Override
        public int compare(Restaurants lhs, Restaurants rhs) {
            return Float.valueOf(lhs.getDistance()).compareTo(rhs.getDistance());
        }
    }
}
