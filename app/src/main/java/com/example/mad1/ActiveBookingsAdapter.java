package com.example.mad1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActiveBookingsAdapter extends RecyclerView.Adapter<ActiveBookingsAdapter.ViewHolder> {

    private List<BookingModel> activeBookingsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BookingModel booking);
    }

    public ActiveBookingsAdapter(List<BookingModel> activeBookingsList, OnItemClickListener listener) {
        this.activeBookingsList = activeBookingsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingModel booking = activeBookingsList.get(position);
        if (booking != null) {
            holder.locationTxt.setText(booking.getBookingStartlocation() + " to " + booking.getBookingEndlocation());
            holder.priceTxt.setText("RM" + booking.getBookingTtlprice());
            holder.dateTimeTxt.setText(booking.getBookingDatetime());
            holder.distanceTxt.setText(booking.getBookingDistance() + " KM");

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(booking);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return activeBookingsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView locationTxt, priceTxt, dateTimeTxt, distanceTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            locationTxt = itemView.findViewById(R.id.locationTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            dateTimeTxt = itemView.findViewById(R.id.dateTimeTxt);
            distanceTxt = itemView.findViewById(R.id.distanceTxt);
        }
    }
}



