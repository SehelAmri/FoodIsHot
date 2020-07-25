package com.example.foodishot.adapter;

import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodishot.CategoriesActivity;
import com.example.foodishot.Model.Categories;
import com.example.foodishot.R;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
ArrayList<Categories>categories;
    String current_user_country,current_user_city;
    Double latitude,longitude;
    public CategoriesAdapter(ArrayList<Categories> categories,String current_user_country,String current_user_city,Double latitude,Double longitude) {
        this.categories = categories;
        this.current_user_country = current_user_country;
        this.current_user_city = current_user_city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurants_categories,parent,false);
        CategoriesAdapter.ViewHolder cav= new CategoriesAdapter.ViewHolder(v);
        return cav;
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoriesAdapter.ViewHolder holder, final int position) {
holder.name.setText(categories.get(position).Name);
holder.image.setImageResource(categories.get(position).image);
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), CategoriesActivity.class);
        intent.putExtra("ca_type",categories.get(position).Name);
        intent.putExtra("ca_image",categories.get(position).image);
        intent.putExtra("ca_user_country",current_user_country);
        intent.putExtra("ca_user_city",current_user_city);
        intent.putExtra("ca_lat",latitude);
        intent.putExtra("ca_long",longitude);

        v.getContext().startActivity(intent);

    }
});
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
   TextView name;
   ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.categories_text);
            image = itemView.findViewById(R.id.categories_image);
        }
    }
}
