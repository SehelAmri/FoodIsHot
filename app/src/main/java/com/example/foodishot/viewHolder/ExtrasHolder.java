package com.example.foodishot.viewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodishot.R;

public class ExtrasHolder extends RecyclerView.ViewHolder {
    public TextView mName,mRequired,mUp_to;
    public RecyclerView rvMember;
    public ExtrasHolder(@NonNull View itemView) {
        super(itemView);
       mName = itemView.findViewById(R.id.ao_header);
       rvMember = itemView.findViewById(R.id.rv_member);
       mRequired = itemView.findViewById(R.id.ao_required);
       mUp_to = itemView.findViewById(R.id.ao_up_to);
    }
}
