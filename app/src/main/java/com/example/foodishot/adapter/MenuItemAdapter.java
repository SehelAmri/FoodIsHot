package com.example.foodishot.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodishot.AddToCart;
import com.example.foodishot.Model.Menu;
import com.example.foodishot.R;
import com.example.foodishot.RestaurantMenu;
import com.example.foodishot.viewHolder.MenuItem;
import com.example.foodishot.viewHolder.MyViewHolder;

import java.util.ArrayList;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItem> {
    ArrayList<Menu> menu;
    String current_user_country,current_user_city;
    public MenuItemAdapter(String current_user_country,String current_user_city,ArrayList<Menu> menu) {
        this.menu = menu;
        this.current_user_city = current_user_city;
        this.current_user_country =current_user_country;
    }

    @NonNull
    @Override
    public MenuItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_menu_details,parent,false);
       MenuItem evh = new MenuItem(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItem holder, int position) {
final Menu menu_model = menu.get(position);
holder.mName.setText(menu_model.getName());
if(menu_model.getContains().length() > 70){
    holder.mDetails.setText(menu_model.getContains().substring(0,67) + "...");
}else{
    holder.mDetails.setText(menu_model.getContains());
}
holder.mPrice.setText(menu_model.getPrice());
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), AddToCart.class);
        intent.putExtra("atc_name",menu_model.getName());
        intent.putExtra("atc_desc",menu_model.getContains());
        intent.putExtra("atc_price",menu_model.getPrice());
        intent.putExtra("atc_key",menu_model.getKey());
        intent.putExtra("atc_tab",menu_model.getTab());
        intent.putExtra("atc_menu_key",menu_model.getMenuKey());
        intent.putExtra("atc_user_country",current_user_country);
        intent.putExtra("atc_user_city",current_user_city);
        v.getContext().startActivity(intent);
    }
});
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }
}
