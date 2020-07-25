package com.example.foodishot.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodishot.AddToCart;
import com.example.foodishot.Model.AddOns;
import com.example.foodishot.Model.AddOnsSpecs;
import com.example.foodishot.R;
import com.example.foodishot.viewHolder.ExtrasHolder;
import com.example.foodishot.viewHolder.MenuItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExtrasAdapter extends RecyclerView.Adapter<ExtrasHolder> {
    private Activity activity;
    ArrayList<AddOns> addOns;
    DatabaseReference reference_add;
    int noRequired= 0;

    public ExtrasAdapter(ArrayList<AddOns> addOns,Activity activity,DatabaseReference reference_add) {
        this.addOns = addOns;
        this.activity = activity;
        this.reference_add = reference_add;
    }

    @NonNull
    @Override
    public ExtrasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_ons,parent,false);
       ExtrasHolder eh = new ExtrasHolder(v);
       return eh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ExtrasHolder holder, final int position) {
     final AddOns addOns_item = addOns.get(position);
     holder.mName.setText(addOns_item.getKey());
     if(addOns_item.getRequired() != null) {
         if (addOns_item.getRequired().equals("yes")) {
             holder.mRequired.setVisibility(View.VISIBLE);
             noRequired += 1;
         }
     }
     final ArrayList<AddOnsSpecs> addOnsSpecs =new ArrayList<>();
     LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
     holder.rvMember.setLayoutManager(layoutManager);
     if(addOns_item.getUp_to() != 0){
         holder.mUp_to.setVisibility(View.VISIBLE);
         holder.mUp_to.setText("(Choose up to "+addOns_item.getUp_to() + ")");
     }

     DatabaseReference reference_add_mod = reference_add.child(addOns_item.getKey());
     reference_add_mod.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot dss_child : dataSnapshot.getChildren()) {
                        if(dss_child.hasChildren()){
                            AddOnsSpecs addOnsSpecs_value = dss_child.getValue(AddOnsSpecs.class);
                            addOnsSpecs.add(new AddOnsSpecs(addOnsSpecs_value.getObj(), addOnsSpecs_value.getCash(),addOns_item.getRequired(),noRequired,position,addOns_item.getUp_to()));
                        }
                    }
                }
                CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(addOnsSpecs,activity);
                holder.rvMember.setAdapter(checkBoxAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return addOns.size();
    }
}
