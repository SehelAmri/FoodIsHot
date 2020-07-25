package com.example.foodishot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class OrdersActivity extends AppCompatActivity{
TabLayout tabLayout;
ViewPager viewPager;
PagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_home);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        tabLayout = findViewById(R.id.tabWidget);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.previous_orders));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.pending_orders));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new com.example.foodishot.adapter.PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
       tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
           @Override
           public void onTabSelected(TabLayout.Tab tab) {
               viewPager.setCurrentItem(tab.getPosition());
           }

           @Override
           public void onTabUnselected(TabLayout.Tab tab) {

           }

           @Override
           public void onTabReselected(TabLayout.Tab tab) {

           }
       });

        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.restaurant_bn:
                        Intent intent_res_act = new Intent(OrdersActivity.this, Home.class);
                        intent_res_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_res_act);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.orders_bn:
                        break;
                    case R.id.account_bn:
                        Intent intent_acc_act = new Intent(OrdersActivity.this, AccountActivity.class);
                        intent_acc_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent_acc_act);
                        overridePendingTransition(0,0);
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
}
