package com.example.mad1;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewDetailsWriteViewHolder extends RecyclerView.ViewHolder {

    private TextView memberNameTextView;
    private RatingBar ratingBarUser;

    public ReviewDetailsWriteViewHolder(@NonNull View itemView) {
        super(itemView);

        memberNameTextView = itemView.findViewById(R.id.UserName);
        ratingBarUser = itemView.findViewById(R.id.ratingBarUser);

        ratingBarUser.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });
    }

    public void bind(String memberId) {

        if (memberNameTextView != null) {
            memberNameTextView.setText(memberId);
        }
    }
}
