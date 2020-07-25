package com.example.foodishot.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodishot.Model.AddOnsSpecs;
import com.example.foodishot.Model.User;
import com.example.foodishot.R;
import com.example.foodishot.viewHolder.ExtrasHolder;
import com.lucasurbas.listitemview.ListItemView;

import java.util.ArrayList;

public class CheckBoxAdapter extends RecyclerView.Adapter<CheckBoxAdapter.ViewHolder> {
    ArrayList<AddOnsSpecs> arrayListMember;
    Activity activity;
    Bundle bundle;
    int noChecked,groupedPos,groupedNoRequired,atc_price_total,atc_price_int,mCountInt,countChecked,mUp_to;
    String atc_price;
    Boolean checkBoxIterator;
    SharedPreferences sharedPreferences;
   ArrayList<CheckBox> cba = new ArrayList<>();
    public CheckBoxAdapter(ArrayList<AddOnsSpecs> arrayListMember, Activity activity) {
        this.arrayListMember = arrayListMember;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_view,parent,false);
        CheckBoxAdapter.ViewHolder eh = new CheckBoxAdapter.ViewHolder(v);
        return eh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        sharedPreferences = activity.getSharedPreferences("groupedState", Context.MODE_PRIVATE);
        holder.mCheck.setText(arrayListMember.get(position).getObj());
        cba.add(holder.mCheck);
        groupedNoRequired = arrayListMember.get(position).getNoRequired();
        groupedPos = arrayListMember.get(position).getPosition();
        mUp_to = arrayListMember.get(position).getUp_to();
        mCountInt=Integer.parseInt(holder.mCount.getText().toString());
        bundle = activity.getIntent().getExtras();
        countChecked = 0;
        if(bundle != null) {
            atc_price = bundle.getString("atc_price");
        }
        atc_price_int = Integer.parseInt(atc_price.replaceAll("[^0-9]", ""));
        atc_price_total = atc_price_int;
        noChecked = 0;
        //
        holder.mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mUp_to > 0) {
                    int countChecked = 0;
                    for (CheckBox cb : cba) {
                        cb.setEnabled(true);
                        if (cb.isChecked()) countChecked++;
                    }
                    //your variable
                    if (mUp_to <= countChecked) {
                        for (CheckBox cb : cba) {
                            if (!cb.isChecked()) cb.setEnabled(false);
                        }
                    }
                }
            }
        });
        //
        if(arrayListMember.get(position).getRequired() != null) {
            if (arrayListMember.get(position).getRequired().equals("yes")) {
                holder.order_btn.setEnabled(false);
                holder.mCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.mCheck.isChecked()){
                            checkBoxIterator = true;
                            noChecked +=1;
                            sharedPreferences.edit().putBoolean(""+groupedPos,true).apply();
                            for(int i=0;i<groupedNoRequired;i++){
                                checkBoxIterator &= sharedPreferences.getBoolean(""+i,false);
                            }

                            if(checkBoxIterator){
                                holder.order_btn.setEnabled(true);
                            }
                        }else if(!holder.mCheck.isChecked() ){
                            noChecked -=1;
                            sharedPreferences.edit().putBoolean(""+groupedPos,false).apply();
                            if(noChecked == 0){
                                holder.order_btn.setEnabled(false);
                            }
                        }
                    }
                });
            }
        }
        if(arrayListMember.get(position).getCash() != null) {
            holder.mText.setText("+" + arrayListMember.get(position).getCash());
            holder.mCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String price = holder.order_btn.getText().toString();
                    String array_list_string = arrayListMember.get(position).getCash();
                    int array_list_cash = Integer.parseInt(array_list_string.replaceAll("[^0-9]", ""));
                    int price_int = Integer.parseInt(price.replaceAll("[^0-9]", ""));
                    if (holder.mCheck.isChecked()) {
                        int mCountIntAdded = mCountInt * array_list_cash;
                        atc_price_total += array_list_cash;
                        int price_added = price_int + mCountIntAdded;
                        holder.order_btn.setText(activity.getString(R.string.add_to_order) + " " + String.format("%,d", price_added) + " Tsh");
                    }else if(!holder.mCheck.isChecked()){
                        int mCountIntAdded = mCountInt * array_list_cash;
                        atc_price_total -= array_list_cash;
                        int price_added = price_int - mCountIntAdded;
                        holder.order_btn.setText(activity.getString(R.string.add_to_order) + " " + String.format("%,d", price_added) + " Tsh");
                    }

                }
            });
        }
        View.OnClickListener iterate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.atc_add:
                        mCountInt += 1;
                        holder.mCount.setText(String.valueOf(mCountInt));
                        holder.order_btn.setText(activity.getString(R.string.add_to_order) + " " + String.valueOf(String.format("%,d",atc_price_total * mCountInt)) + " Tsh");
                        break;
                    case R.id.atc_minus:
                        if(mCountInt > 1) {
                            mCountInt -= 1;
                            holder.mCount.setText(String.valueOf(mCountInt));
                            holder.order_btn.setText(activity.getString(R.string.add_to_order) + " " + String.valueOf(String.format("%,d", atc_price_total * mCountInt)) + " Tsh");
                        }
                        break;
                }
                if(mCountInt == 1){
                    holder.mMinus.setTextColor(activity.getResources().getColor(R.color.searchBarBorder));
                }else{
                    holder.mMinus.setTextColor(Color.BLACK);
                }
            }
        };
        holder.mAdd.setOnClickListener(iterate);
        holder.mMinus.setOnClickListener(iterate);
    }

    @Override
    public int getItemCount() {
        return arrayListMember.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox mCheck;
        public TextView mText,mAdd,mMinus,mCount;
        Button order_btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCheck = itemView.findViewById(R.id.ao_checkbox);
            mText = itemView.findViewById(R.id.ao_cash);
            mAdd = activity.findViewById(R.id.atc_add);
            mMinus = activity.findViewById(R.id.atc_minus);
            order_btn = activity.findViewById(R.id.atc_btn);
            mCount = activity.findViewById(R.id.atc_count);
        }
    }
}
