package com.example.mad1;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Member;
import java.util.List;




public class ReviewDetailsAdapter extends RecyclerView.Adapter<ReviewDetailsAdapter.MemberViewHolder> {

    private List<RDModel> rdModelList;
    private int itemCount;

    public ReviewDetailsAdapter(List<RDModel> rdModelList, int itemCount) {
        this.rdModelList = rdModelList;
        this.itemCount = itemCount;
    }


    public ReviewDetailsAdapter(List<RDModel> rdModelList) {
        this.rdModelList = rdModelList;
    }

    public int getItemCount() {
        return rdModelList.size();
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_rating_user, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        RDModel rdModel = rdModelList.get(position);
        holder.userName.setText(rdModel.getNickname()); // Update to userName
        holder.bind(rdModel);
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private RatingBar ratingBar;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.UserName);
            ratingBar = itemView.findViewById(R.id.ratingBarUser);
        }

        public void bind(RDModel rdModel) {
            userName.setText(rdModel.getNickname());
            ratingBar.setRating(rdModel.getRating());
        }
    }
}

