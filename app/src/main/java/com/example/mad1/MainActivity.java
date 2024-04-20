package com.example.mad1;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActiveBookingsAdapter.OnItemClickListener{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String ACTIVE_STATUS = "active";
    private String userId="U2";
    private LinearLayout historyBtn, addBtn, reviewBtn, profileBtn;
    private RecyclerView activeBookingsRecyclerView;
    private ActiveBookingsAdapter activeBookingsAdapter;
    private List<BookingModel> activeBookingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        historyBtn = findViewById(R.id.historyBtn);
        addBtn = findViewById(R.id.addBtn);
        reviewBtn = findViewById(R.id.reviewBtn);
        profileBtn = findViewById(R.id.profileBtn);

        activeBookingsRecyclerView = findViewById(R.id.activeBookingsRecyclerView);
        activeBookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        activeBookingsAdapter = new ActiveBookingsAdapter(activeBookingsList, this);
        activeBookingsRecyclerView.setAdapter(activeBookingsAdapter);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        Button passengerButton = findViewById(R.id.button_passenger);
        passengerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchActiveBookingsForPassenger();
            }
        });

        Button driverButton = findViewById(R.id.button_driver);
        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchActiveBookingsForDriver();
            }
        });


        checkLocationPermission();
        setupButtonListeners();
        fetchActiveBookingsForPassenger();
    }

    public void onItemClick(BookingModel booking) {
        Intent intent = new Intent(MainActivity.this, JoinBookingActivity.class);
        intent.putExtra("bookingId", booking.getBookingId());
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void setupButtonListeners() {
        historyBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, BookingHistoryActivity.class).putExtra("userId", userId)));
        addBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, calcRideDistance.class).putExtra("userId", userId)));
        reviewBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ReviewActivity.class).putExtra("userId", userId)));
        profileBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class).putExtra("userId", userId)));
    }
    private void fetchActiveBookingsForPassenger() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("booking_table");
        Query query = databaseReference.orderByChild("booking_status").equalTo("active");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activeBookingsList.clear();
                for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                    // Check if the necessary fields are not null
                    if (bookingSnapshot.child("booking_status").getValue() != null &&
                            bookingSnapshot.child("booking_capacity").getValue() != null &&
                            bookingSnapshot.child("booking_user").getValue() != null &&
                            bookingSnapshot.child("booking_driver").getValue() != null) {

                        // Extract values from dataSnapshot
                        String status = bookingSnapshot.child("booking_status").getValue(String.class);
                        int capacity = Integer.parseInt(bookingSnapshot.child("booking_capacity").getValue(String.class));
                        int userCount = (int) bookingSnapshot.child("booking_user").getChildrenCount();
                        String driver = bookingSnapshot.child("booking_driver").getValue(String.class);

                        BookingModel booking = new BookingModel();
                        booking.setBookingId(bookingSnapshot.getKey());
                        booking.setBookingStartlocation(bookingSnapshot.child("booking_startlocation").getValue(String.class));
                        booking.setBookingEndlocation(bookingSnapshot.child("booking_endlocation").getValue(String.class));
                        booking.setBookingDatetime(bookingSnapshot.child("booking_datetime").getValue(String.class));
                        booking.setBookingDistance(bookingSnapshot.child("booking_distance").getValue(String.class));
                        booking.setBookingCapacity(bookingSnapshot.child("booking_capacity").getValue(String.class));
                        booking.setBookingTtlprice(bookingSnapshot.child("booking_ttlprice").getValue(String.class));
                        booking.setBookingLeader(bookingSnapshot.child("booking_leader").getValue(String.class));
                        booking.setBookingDriver(bookingSnapshot.child("booking_driver").getValue(String.class));
                        booking.setBookingStatus(status);
                        booking.setReviewStatus(bookingSnapshot.child("review_status").getValue(Boolean.class));

                        // Perform filtering logic
                        if ("active".equals(status)) {
                            // Check if booking is for passengers
                            if (capacity > userCount && !isUserBooked(bookingSnapshot, userId)) {
                                // Check if there's a driver assigned
                                if (driver != null && !driver.isEmpty()) {
                                    displayActiveBooking(booking);
                                } else {
                                    if ((capacity-userCount) > 1) {
                                        displayActiveBooking(booking);
                                    }
                                }
                            }
                        }
                    }
                }
                // Notify the adapter about the dataset change
                activeBookingsAdapter.notifyDataSetChanged();
                Log.d("MainActivity", "Active bookings list updated. Count: " + activeBookingsList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void fetchActiveBookingsForDriver() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("booking_table");
        Query query = databaseReference.orderByChild("booking_status").equalTo("active");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activeBookingsList.clear();
                for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                    // Check if the necessary fields are not null
                    if (bookingSnapshot.child("booking_status").getValue() != null &&
                            bookingSnapshot.child("booking_capacity").getValue() != null &&
                            bookingSnapshot.child("booking_user").getValue() != null &&
                            bookingSnapshot.child("booking_driver").getValue() != null) {

                        // Extract values from dataSnapshot
                        String status = bookingSnapshot.child("booking_status").getValue(String.class);
                        int capacity = Integer.parseInt(bookingSnapshot.child("booking_capacity").getValue(String.class));
                        int userCount = (int) bookingSnapshot.child("booking_user").getChildrenCount();
                        String driver = bookingSnapshot.child("booking_driver").getValue(String.class);

                        BookingModel booking = new BookingModel();
                        booking.setBookingId(bookingSnapshot.getKey());
                        booking.setBookingStartlocation(bookingSnapshot.child("booking_startlocation").getValue(String.class));
                        booking.setBookingEndlocation(bookingSnapshot.child("booking_endlocation").getValue(String.class));
                        booking.setBookingDatetime(bookingSnapshot.child("booking_datetime").getValue(String.class));
                        booking.setBookingDistance(bookingSnapshot.child("booking_distance").getValue(String.class));
                        booking.setBookingCapacity(bookingSnapshot.child("booking_capacity").getValue(String.class));
                        booking.setBookingTtlprice(bookingSnapshot.child("booking_ttlprice").getValue(String.class));
                        booking.setBookingLeader(bookingSnapshot.child("booking_leader").getValue(String.class));
                        booking.setBookingDriver(bookingSnapshot.child("booking_driver").getValue(String.class));
                        booking.setBookingStatus(status);
                        booking.setReviewStatus(bookingSnapshot.child("review_status").getValue(Boolean.class));

                        // Perform filtering logic
                        if ("active".equals(status)) {
                            // Check if booking is for drivers
                            if (!isUserBooked(bookingSnapshot, userId) &&
                                    (driver == null || driver.isEmpty())) {
                                displayActiveBooking(booking);
                            }
                        }
                    }
                }
                // Notify the adapter about the dataset change
                activeBookingsAdapter.notifyDataSetChanged();
                Log.d("MainActivity", "Active bookings list updated. Count: " + activeBookingsList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "Failed to read value.", databaseError.toException());
            }
        });
    }



    // Helper method to check if a user is already booked for a ride
    private boolean isUserBooked(DataSnapshot bookingSnapshot, String userId) {
        for (DataSnapshot userSnapshot : bookingSnapshot.child("booking_user").getChildren()) {
            if (userSnapshot.getKey().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    private void displayActiveBooking(BookingModel booking) {
        activeBookingsList.add(booking);
        activeBookingsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
