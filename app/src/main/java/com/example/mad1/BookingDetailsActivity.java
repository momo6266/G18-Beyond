package com.example.mad1;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookingDetailsActivity extends AppCompatActivity {

    private Button completeButton, cancelButton, userCancelButton;
    private String bookingId, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_history_details);

        bookingId = getIntent().getStringExtra("bookingId");
        userID = getIntent().getStringExtra("userId");
        boolean isCreator = getIntent().getBooleanExtra("isCreator", false);
        boolean isDriver = getIntent().getBooleanExtra("isDriver", false);

        completeButton = findViewById(R.id.button_complete);
        cancelButton = findViewById(R.id.button_cancel);
        userCancelButton = findViewById(R.id.button_usercancel);

        completeButton.setOnClickListener(v -> updateBookingStatus(bookingId, "completed"));
        cancelButton.setOnClickListener(v -> updateBookingStatus(bookingId, "cancelled"));
        userCancelButton.setOnClickListener(v -> removeUserFromBooking(bookingId));


        if (isCreator || isDriver) {
            completeButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            userCancelButton.setVisibility(View.GONE);
        } else {
            completeButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            userCancelButton.setVisibility(View.VISIBLE);
        }

        populateBookingDetails();
    }
    private void populateBookingDetails() {
        String creator = getIntent().getStringExtra("creator");
        String driver = getIntent().getStringExtra("driver");
        String date = getIntent().getStringExtra("date");
        String departure = getIntent().getStringExtra("departure");
        String destination = getIntent().getStringExtra("destination");
        String userSpecificPrice = getIntent().getStringExtra("price");

        TextView creatorTextView = findViewById(R.id.creator);
        creatorTextView.setText(creator);

        TextView driverTextView = findViewById(R.id.driver);
        driverTextView.setText(driver);

        TextView dateTextView = findViewById(R.id.date);
        dateTextView.setText(date);

        TextView departureTextView = findViewById(R.id.depart);
        departureTextView.setText(departure);

        TextView destinationTextView = findViewById(R.id.destiny);
        destinationTextView.setText(destination);

        TextView priceTextView = findViewById(R.id.price);
        priceTextView.setText("RM" + userSpecificPrice);
    }

    private void updateBookingStatus(String bookingId, String newStatus) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("booking_table").child(bookingId);
        bookingRef.child("booking_status").setValue(newStatus)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Update Status", "Booking status updated to " + newStatus);
                        finish(); // Close this activity and return
                    } else {
                        Log.e("Update Status", "Failed to update booking status", task.getException());
                    }
                });
    }

/*    private void removeUserFromBooking(String bookingId) {
        DatabaseReference bookingUserRef = FirebaseDatabase.getInstance().getReference("booking_table")
                .child(bookingId).child("booking_user").child(userID);
        bookingUserRef.removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Remove User", "User removed from booking");
                        finish(); // Close this activity and return
                    } else {
                        Log.e("Remove User", "Failed to remove user from booking", task.getException());
                    }
                });
    }*/

    private void removeUserFromBooking(String bookingId) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("booking_table").child(bookingId);

        bookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String driverId = dataSnapshot.child("booking_driver").getValue(String.class);
                    String ttlCost = dataSnapshot.child("booking_ttlprice").getValue(String.class);
                    if (ttlCost == null) {
                        Log.d("Remove User", "Total cost is null");
                        return;
                    }

                    float totalCost = Float.parseFloat(ttlCost);
                    long userCount = dataSnapshot.child("booking_user").getChildrenCount() - 1; // Exclude the driver

                    float personalCost = totalCost / userCount;

                    for (DataSnapshot userSnapshot : dataSnapshot.child("booking_user").getChildren()) {
                        String userId = userSnapshot.getKey();
                        if (!userId.equals(driverId)) {
                            DatabaseReference userPriceRef = bookingRef.child("booking_user").child(userId).child("booking_user_price");
                            userPriceRef.setValue(String.valueOf(personalCost));
                        }
                    }

                    DatabaseReference userRef = bookingRef.child("booking_user").child(userID);
                    userRef.removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("Remove User", "User removed from booking");
                                    finish(); // Close this activity and return
                                } else {
                                    Log.e("Remove User", "Failed to remove user from booking", task.getException());
                                }
                            });
                } else {
                    Log.d("Remove User", "No booking data found for bookingID: " + bookingId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("Remove User", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void onBackButtonClick(View view) {
        finish();
    }
}

