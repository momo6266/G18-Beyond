package com.example.mad1;

import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewViewHolder extends RecyclerView.ViewHolder {
    public TableLayout tableLayout;

    public TextView distanceTxt, priceTxt, dateTimeTxt, locationTxt;

    public ReviewViewHolder(@NonNull View itemView) {
        super(itemView);
        tableLayout = itemView.findViewById(R.id.tableLayout);
        distanceTxt = itemView.findViewById(R.id.distanceTxt);
        priceTxt = itemView.findViewById(R.id.priceTxt);
        dateTimeTxt = itemView.findViewById(R.id.dateTimeTxt);
        locationTxt = itemView.findViewById(R.id.locationTxt);
    }
}