package com.example.foodishot.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodishot.Home;
import com.example.foodishot.Model.Restaurants;
import com.example.foodishot.R;
import com.example.foodishot.RestaurantMenu;
import com.example.foodishot.viewHolder.MyViewHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchFilterAdapter extends RecyclerView.Adapter<MyViewHolder> {

    ArrayList<Restaurants> arrayList;
    Context c;
    Date currentTime,open_time,close_time,reformattedCurrentDate;
    String formattedCurrentDate;
    String current_user_city,current_user_country;
    public SearchFilterAdapter(String current_user_country,String current_user_city,ArrayList<Restaurants> arrayList,Context c){
this.arrayList = arrayList;
this.c = c;
this.current_user_city = current_user_city;
this.current_user_country = current_user_country;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_view,parent,false);
        MyViewHolder evh = new MyViewHolder(v);
        return evh;
    }
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        final Restaurants restaurants = arrayList.get(position);
        formattedCurrentDate = dateFormat.format(currentTime);
        try {
           open_time = dateFormat.parse(restaurants.getOpen());
           close_time = dateFormat.parse(restaurants.getClose());
           reformattedCurrentDate = dateFormat.parse(formattedCurrentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.mRating.setText(restaurants.getRating());
        holder.mNoRating.setText("("+restaurants.getNoRating()+" ratings)");
        holder.mPrice.setText(restaurants.getPrice());
        holder.mType.setText(restaurants.getType());
        holder.mSubType.setText(restaurants.getSubType());
        holder.mName.setText(restaurants.getName());
        holder.mDeliveryTime.setText(restaurants.getDelivery_time());
        Glide.with(c).load(restaurants.getImage()).into(holder.imageView);

        View.OnClickListener itemViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String restaurant_name = restaurants.getName();
                String restaurant_delivery_time = restaurants.getDelivery_time();
                Intent intent = new Intent(v.getContext(), RestaurantMenu.class);
                intent.putExtra("restaurant_name",restaurant_name);
                intent.putExtra("restaurant_image",restaurants.getImage());
                intent.putExtra("restaurant_price",restaurants.getPrice());
                intent.putExtra("restaurant_type",restaurants.getType());
                intent.putExtra("restaurant_rating",restaurants.getRating());
                intent.putExtra("restaurant_NoRating",restaurants.getNoRating());
                intent.putExtra("restaurant_latitude",restaurants.getLatitude());
                intent.putExtra("restaurant_longitude",restaurants.getLongitude());
                intent.putExtra("restaurant_delivery_time",restaurant_delivery_time);
                intent.putExtra("restaurant_user_city",current_user_city);
                intent.putExtra("restaurant_user_country",current_user_country);
                intent.putExtra("restaurant_key",restaurants.getKey());
                v.getContext().startActivity(intent);
            }
        };
        if(restaurants.getUnavailable() != null) {
            if (restaurants.getUnavailable().equals("Yes")) {
                holder.imageView.setColorFilter(Color.argb(122, 0, 0, 0), PorterDuff.Mode.DARKEN);
                holder.closed.setVisibility(View.VISIBLE);
                holder.closed.setText(R.string.unavailable_restaurant);
            }
        }else {
            if (open_time.after(close_time)) {
                if (reformattedCurrentDate.after(close_time) && reformattedCurrentDate.before(open_time)) {
                    holder.imageView.setColorFilter(Color.argb(122, 0, 0, 0), PorterDuff.Mode.DARKEN);
                    holder.closed.setVisibility(View.VISIBLE);
                } else {
                    holder.itemView.setOnClickListener(itemViewListener);
                }
            } else if (open_time.before(close_time)) {
                if ((reformattedCurrentDate.after(close_time) && reformattedCurrentDate.after(open_time)) ||
                        (reformattedCurrentDate.before(close_time) && reformattedCurrentDate.before(open_time))) {
                    holder.imageView.setColorFilter(Color.argb(122, 0, 0, 0), PorterDuff.Mode.DARKEN);
                    holder.closed.setVisibility(View.VISIBLE);
                } else {
                    holder.itemView.setOnClickListener(itemViewListener);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if(arrayList.size() < 100){
            return arrayList.size();
        }else {
            return 100;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
