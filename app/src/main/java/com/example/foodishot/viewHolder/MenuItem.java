package com.example.foodishot.viewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodishot.R;

public class MenuItem extends RecyclerView.ViewHolder {
    public TextView mName,mPrice,mDetails;
    public MenuItem(@NonNull View itemView) {
        super(itemView);
        mName = itemView.findViewById(R.id.rm_name);
        mDetails = itemView.findViewById(R.id.rm_details);
        mPrice = itemView.findViewById(R.id.rm_price);
    }
}
