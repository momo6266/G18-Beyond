package com.example.mad1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewDetailsWriteAdapter extends RecyclerView.Adapter<ReviewDetailsWriteAdapter.MemberViewHolder> {

    private List<String> reviewWriteDetailsList;
    private String userID;
    private String bookingID;
    private List<Float> ratingsList;

    public ReviewDetailsWriteAdapter(List<String> reviewWriteDetailsList, String userID, String bookingID) {
        this.reviewWriteDetailsList = reviewWriteDetailsList;
        this.userID = userID;
        this.bookingID = bookingID;
        this.ratingsList = new ArrayList<>(Collections.nCopies(reviewWriteDetailsList.size(), 0.0f));
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_rating_user, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        String memberId = reviewWriteDetailsList.get(position);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user_table").child(memberId).child("user_nickname");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nickname = snapshot.getValue(String.class);
                holder.userNameTextView.setText(nickname);

                holder.ratingBar.setRating(ratingsList.get(holder.getAdapterPosition()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error loading user nickname", error.toException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewWriteDetailsList.size();
    }

    public List<Float> getAllRatings() {
        return ratingsList;
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {

        private RatingBar ratingBar;
        private TextView userNameTextView;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBarUser);
            userNameTextView = itemView.findViewById(R.id.UserName);


            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (rating < 1.0f) {

                        ratingBar.setRating(1.0f);
                    } else {

                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            ratingsList.set(position, rating);
                        }
                    }
                }
            });
        }
    }

    public boolean areAllRatingsSubmitted() {
        for (Float rating : ratingsList) {
            if (rating == 0.0f) {
                return false;
            }
        }
        return true;
    }
}
