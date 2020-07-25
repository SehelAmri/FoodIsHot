package com.example.foodishot.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodishot.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView mName,mPrice,mType,mSubType,mNoRating,mRating,mDeliveryTime,closed;
    public ImageView imageView;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        mName = itemView.findViewById(R.id.name_restView);
        mPrice = itemView.findViewById(R.id.price_restView);
        mType = itemView.findViewById(R.id.type_restView);
        mNoRating = itemView.findViewById(R.id.ratingNo_restView);
        mRating =itemView.findViewById(R.id.rating_restView);
        imageView = itemView.findViewById(R.id.home_rest_image);
        mDeliveryTime = itemView.findViewById(R.id.delivery_time);
        closed = itemView.findViewById(R.id.closed);
        mSubType = itemView.findViewById(R.id.subtype_restView);
    }
}
