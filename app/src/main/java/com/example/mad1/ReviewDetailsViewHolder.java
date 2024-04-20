package com.example.mad1;


import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewDetailsViewHolder extends RecyclerView.ViewHolder {

    private TextView memberNameTextView;
    private RatingBar ratingBarUser;

    public ReviewDetailsViewHolder(@NonNull View itemView) {
        super(itemView);
        memberNameTextView = itemView.findViewById(R.id.UserName);
        ratingBarUser = itemView.findViewById(R.id.ratingBarUser);
    }

    public void bind(RDModel rdModel) {
        memberNameTextView.setText(rdModel.getNickname());
        ratingBarUser.setRating(rdModel.getRating());
    }
}
