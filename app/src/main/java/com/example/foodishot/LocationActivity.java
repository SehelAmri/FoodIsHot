package com.example.foodishot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.foodishot.adapter.PlaceAutoSuggestAdapter;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.lucasurbas.listitemview.ListItemView;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {
ListItemView list_item_check_home;
ListItemView list_item_check_work;
ListItemView list_item_check_other;
AutoCompleteTextView  showCurrLocale;
Boolean my_var;
SharedPreferences sharedPreferences;
Button save_locale;
ProgressBar progressBar;
AlertDialog.Builder alertDialog_locale_required;
AlertDialog.Builder alertDialog_incorrect_address;
AlertDialog.Builder alertDialog_permission_denied;
PlaceAutoSuggestAdapter placeAutoSuggestAdapter;
private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
boolean isChecked_home = false;
boolean isChecked_work = false;
boolean isChecked_other = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        sharedPreferences = getSharedPreferences("user_location",MODE_PRIVATE);
        getSupportActionBar().setTitle(R.string.address_title_actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressBar);
        placeAutoSuggestAdapter = new PlaceAutoSuggestAdapter(LocationActivity.this,android.R.layout.simple_list_item_1);

        final AutoCompleteTextView autoCompleteTextView = findViewById(R.id.searchLocation);
        autoCompleteTextView.setAdapter(placeAutoSuggestAdapter);
        list_item_check_home = findViewById(R.id.list_item_view_home);
        list_item_check_work = findViewById(R.id.list_item_view_work);
        list_item_check_other = findViewById(R.id.list_item_view_other);
        showCurrLocale = findViewById(R.id.searchLocation);
        save_locale = findViewById(R.id.save_locale);
       alertDialog_incorrect_address = new AlertDialog.Builder(LocationActivity.this)
                .setMessage(R.string.LAincorect_address)
                .setPositiveButton(R.string.scOk,null);
        alertDialog_locale_required = new AlertDialog.Builder(LocationActivity.this)
                .setMessage(R.string.WAlocation_required)
                .setPositiveButton(R.string.scOk,null);
       alertDialog_permission_denied = new AlertDialog.Builder(LocationActivity.this)
                .setMessage(R.string.permission_denied)
                .setPositiveButton(R.string.scOk,null);
        //

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                my_var = true;
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                my_var = false;
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
//
        list_item_check_home.setTitle(sharedPreferences.getString("home_user_locale","-"));
        list_item_check_work.setTitle(sharedPreferences.getString("work_user_locale","-"));
        list_item_check_other.setTitle(sharedPreferences.getString("other_user_locale","-"));

        View.OnClickListener assignLabel_locale =new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case(R.id.list_item_view_home):
                        if(!list_item_check_home.getTitle().equals("-")){
                            showCurrLocale.setText(list_item_check_home.getTitle());
                            my_var = true;
                        }
                        break;
                    case(R.id.list_item_view_work):
                        if(!list_item_check_work.getTitle().equals("-")) {
                            showCurrLocale.setText(list_item_check_work.getTitle());
                            my_var = true;
                        }
                        break;
                    case(R.id.list_item_view_other):
                        if(!list_item_check_other.getTitle().equals("-")) {
                            showCurrLocale.setText(list_item_check_other.getTitle());
                            my_var = true;
                        }
                        break;
                }
            }
        };
        list_item_check_home.setOnClickListener(assignLabel_locale);
        list_item_check_work.setOnClickListener(assignLabel_locale);
        list_item_check_other.setOnClickListener(assignLabel_locale);
        //
         list_item_check_home.setOnMenuItemClickListener( new ListItemView.OnMenuItemClickListener() {
             @Override
             public void onActionMenuItemSelected(MenuItem item) {
                 isChecked_home = !isChecked_home;
                 list_item_check_home.inflateMenu(isChecked_home?R.menu.check_menu:R.menu.uncheck_menu);
             }
         });
        list_item_check_work.setOnMenuItemClickListener( new ListItemView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                isChecked_work = !isChecked_work;
                list_item_check_work.inflateMenu(isChecked_work?R.menu.check_menu:R.menu.uncheck_menu);
            }
        });
        list_item_check_other.setOnMenuItemClickListener( new ListItemView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                isChecked_other = !isChecked_other;
                list_item_check_other.inflateMenu(isChecked_other?R.menu.check_menu:R.menu.uncheck_menu);
            }
        });
        //Check if input empty
        showCurrLocale.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneInput = showCurrLocale.getText().toString().trim();
                save_locale.setEnabled(!phoneInput.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //SAVE ONCLICK
        save_locale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(my_var) {
                    sharedPreferences.edit().putString("current_user_locale", showCurrLocale.getText().toString()).apply();
                    if (isChecked_home) {
                        sharedPreferences.edit().putString("home_user_locale", showCurrLocale.getText().toString()).apply();
                    }
                    if (isChecked_work) {
                        sharedPreferences.edit().putString("work_user_locale", showCurrLocale.getText().toString()).apply();
                    }
                    if (isChecked_other) {
                        sharedPreferences.edit().putString("other_user_locale", showCurrLocale.getText().toString()).apply();
                    }
                    return_home();
                }else{

                            alertDialog_incorrect_address.show();
                }
            }
        });
        //START CURRENT LOCATION SETUP
findViewById(R.id.list_item_view_current_location).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (isNetworkAvailable()) {
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LocationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            } else {
                final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest());
                builder.setAlwaysShow(true);
                builder.setNeedBle(true);
                SettingsClient client = LocationServices.getSettingsClient(LocationActivity.this);
                Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
                task.addOnSuccessListener(LocationActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        getCurrentLocation();
                    }
                });
                task.addOnFailureListener(LocationActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(LocationActivity.this,
                                        0x1);
                            } catch (IntentSender.SendIntentException en) {

                                en.printStackTrace();
                            }
                        }

                    }
                });
            }

        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(LocationActivity.this)
                    .setTitle(R.string.noConnectionToast)
                    .setPositiveButton(R.string.scOk,null)
                    .show();
        }
    }
});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else{
               alertDialog_permission_denied.show();
            }
        }
    }

    private void getCurrentLocation() {

        progressBar.setVisibility(View.VISIBLE);



        LocationServices.getFusedLocationProviderClient(LocationActivity.this)
                .requestLocationUpdates(createLocationRequest(),new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(LocationActivity.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0){
                         int latestLocationIndex = locationResult.getLocations().size() - 1;
                         double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                         double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            try {
                                Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                if(addresses != null && addresses.size()>0) {
                                    String address = addresses.get(0).getAddressLine(0);
                                    showCurrLocale.setText(address);
                                    my_var = true;
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, Looper.getMainLooper());
    }
    protected LocationRequest createLocationRequest() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==0x1) {

            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
             getCurrentLocation();
            } else {
                //User clicks No
              alertDialog_locale_required.show();
                  getCurrentLocation();
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
    public void return_home(){
Intent intent = new Intent(LocationActivity.this,Home.class);
intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
startActivity(intent);

    }
}
