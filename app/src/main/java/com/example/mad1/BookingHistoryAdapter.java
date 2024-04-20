// BookingHistoryAdapter.java
package com.example.mad1;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

    private List<BookingModel> bookingList;
    private String currentUserId;

    public BookingHistoryAdapter(List<BookingModel> bookingList, String currentUserId) {
        this.bookingList = bookingList;
        this.currentUserId = currentUserId; // Set the current user ID
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_bookinghistory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingModel booking = bookingList.get(position);
        holder.locationTxt.setText(String.format("%s - %s", booking.getBookingStartlocation(), booking.getBookingEndlocation()));
        holder.priceTxt.setText("RM" + booking.getBookingTtlprice());
        holder.dateTimeTxt.setText(booking.getBookingDatetime());
        holder.distanceTxt.setText(booking.getBookingDistance());

        // Set OnClickListener to open BookingDetailsActivity
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), BookingDetailsActivity.class);
            intent.putExtra("bookingId", booking.getBookingId());
            intent.putExtra("userId", currentUserId);
            intent.putExtra("creator", booking.getLeaderNickname());
            intent.putExtra("driver", booking.getDriverNickname());
            intent.putExtra("date", booking.getBookingDatetime());
            intent.putExtra("departure", booking.getBookingStartlocation());
            intent.putExtra("destination", booking.getBookingEndlocation());
            intent.putExtra("price", booking.getBookingUserPrice());
            intent.putExtra("isCreator", currentUserId.equals(booking.getBookingLeader()));
            intent.putExtra("isDriver", currentUserId.equals(booking.getBookingDriver()));
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView locationTxt, priceTxt, dateTimeTxt, distanceTxt;

        public ViewHolder(View itemView) {
            super(itemView);
            locationTxt = itemView.findViewById(R.id.locationTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            dateTimeTxt = itemView.findViewById(R.id.dateTimeTxt);
            distanceTxt = itemView.findViewById(R.id.distanceTxt);
        }
    }
}
