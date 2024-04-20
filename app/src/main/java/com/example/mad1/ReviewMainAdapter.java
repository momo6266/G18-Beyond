package com.example.mad1;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import com.bumptech.glide.Glide;

import java.util.List;

public class ReviewMainAdapter extends RecyclerView.Adapter<ReviewViewHolder> {
    private List<ReviewModel> dataList;
    private Context context;
    private int selectedPosition = -1;
    private int lastSelectedPosition = -1;

    public ReviewMainAdapter(List<ReviewModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_order_in_rating, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel item = dataList.get(position);


        holder.distanceTxt.setText(item.getDistance() + " km");
        holder.priceTxt.setText("RM " + item.getPrice());
        holder.dateTimeTxt.setText(item.getDateTime());
        holder.locationTxt.setText(item.getLocation());

        if (!holder.distanceTxt.getText().toString().endsWith("km")) {
            holder.distanceTxt.append(" km");
        }

        if (!holder.priceTxt.getText().toString().startsWith("RM")) {
            holder.priceTxt.setText("RM " + holder.priceTxt.getText());
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookingId = item.getBookingId();
                String userId = item.getUserID();
                Intent intent = new Intent(context, ReviewDetailsActivity.class);
                intent.putExtra("bookingID", bookingId);
                intent.putExtra("userID", userId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
