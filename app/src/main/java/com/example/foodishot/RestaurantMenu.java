package com.example.foodishot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodishot.Model.Menu;
import com.example.foodishot.adapter.MenuItemAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RestaurantMenu extends AppCompatActivity {
    Bundle bundle;
    ArrayList<Menu> menu_details;
    String restaurant_name,restaurant_type,restaurant_rating,restaurant_noRating,restaurant_image,restaurant_price,restaurant_key;
    String latitude,longitude,current_user_city,current_user_country;
    String restaurant_delivery_time = null;
    TextView mDeliveryTime,mRating,mName,mNoRating,mPrice,mType,mRestLocale;
    ProgressBar progressBar;
    ImageView imageView,menu_back;
    RecyclerView recyclerView;
    ViewStub viewStub;
    ValueEventListener listenTabChange;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu);
        progressBar = findViewById(R.id.progressBar_menu);
        progressBar.setVisibility(View.VISIBLE);
        viewStub = findViewById(R.id.stub_import);
        menu_details = new ArrayList<>();
        viewStub.inflate();
        mDeliveryTime = findViewById(R.id.menu_delivery_time);
        imageView = findViewById(R.id.menu_rest_image);
        menu_back = findViewById(R.id.menu_back);
        mName = findViewById(R.id.menu_rest_name);
        mType = findViewById(R.id.menu_rest_type);
        mRating = findViewById(R.id.menu_rest_rate);
        mNoRating = findViewById(R.id.menu_rest_noRate);
        mRestLocale = findViewById(R.id.restaurant_location);
        mPrice = findViewById(R.id.menu_rest_price);
        tabLayout = findViewById(R.id.menu_tabLayout);
        recyclerView = findViewById(R.id.recycler_restaurant_menu);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(RestaurantMenu.this);
        recyclerView.setLayoutManager(layoutManager);
         bundle = getIntent().getExtras();
        viewStub.setVisibility(View.GONE);
         menu_back.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         });
        if(bundle != null) {
            restaurant_name = bundle.getString("restaurant_name");
            restaurant_delivery_time = bundle.getString("restaurant_delivery_time");
            restaurant_price = bundle.getString("restaurant_price");
            restaurant_rating = bundle.getString("restaurant_rating");
            restaurant_noRating = bundle.getString("restaurant_NoRating");
            restaurant_image = bundle.getString("restaurant_image");
            restaurant_type = bundle.getString("restaurant_type");
            restaurant_key = bundle.getString("restaurant_key");
            current_user_city = bundle.getString("restaurant_user_city");
            current_user_country = bundle.getString("restaurant_user_country");
            latitude = bundle.getString("restaurant_latitude");
            longitude = bundle.getString("restaurant_longitude");
            mDeliveryTime.setText(restaurant_delivery_time);
            mType.setText(restaurant_type);
            mName.setText(restaurant_name);
            mPrice.setText(restaurant_price);
            mNoRating.setText("(" + restaurant_noRating + " ratings)");
            mRating.setText(restaurant_rating);
            Glide.with(RestaurantMenu.this).load(restaurant_image).into(imageView);
            if (latitude != null) {
                Geocoder geocoder = new Geocoder(RestaurantMenu.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(Double.parseDouble(latitude),Double.parseDouble(longitude), 1);
                    if(addresses != null && addresses.size()>0) {
                        String address = addresses.get(0).getAddressLine(0);
                        mRestLocale.setText(address);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        if(restaurant_key != null) {
            final DatabaseReference reference = database.getReference().child("Restaurants").child(current_user_country).child(current_user_city).child(restaurant_key).child("Menu");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot dss : dataSnapshot.getChildren()) {
                            tabLayout.addTab(tabLayout.newTab().setText(dss.getKey()));
                        }
                        TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                DatabaseReference reference_menu = database.getReference().child("Restaurants").child(current_user_country).child(current_user_city).child(restaurant_key).child("Menu").child((String) tab.getText());
                               reference_menu.addValueEventListener(listenTabChange_func(tab));
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {

                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {

                            }
                        };
                        onTabSelectedListener.onTabSelected(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
                        tabLayout.addOnTabSelectedListener(onTabSelectedListener);
                        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            progressBar.setVisibility(View.GONE);
            viewStub.setVisibility(View.VISIBLE);

        }
    }
public  ValueEventListener   listenTabChange_func (final TabLayout.Tab tab){
        listenTabChange = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            menu_details.clear();
            for (DataSnapshot dss_list : dataSnapshot.getChildren()) {
                Menu menu = dss_list.getValue(Menu.class);
                menu_details.add(new Menu(menu.getName(), menu.getType(), menu.getContains(), menu.getPrice(), restaurant_key, (String)tab.getText(),dss_list.getKey()));
            }
            MenuItemAdapter menuItemAdapter = new MenuItemAdapter(current_user_country,current_user_city,menu_details);
            recyclerView.setAdapter(menuItemAdapter);
            menuItemAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
return listenTabChange;
};
}
