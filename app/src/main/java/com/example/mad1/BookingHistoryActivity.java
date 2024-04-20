package com.example.mad1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView textViewNoData;
    private String userID;
    private BookingHistoryAdapter adapter;
    private List<BookingModel> bookingList = new ArrayList<>();
    private ProgressBar progressBar;
    private Button activeButton, completedButton, cancelledButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userId");
        recyclerView = findViewById(R.id.viewHistory);
        progressBar = findViewById(R.id.progressBarHistory);
        activeButton = findViewById(R.id.active_booking_btn);
        completedButton = findViewById(R.id.complete_booking_btn);
        cancelledButton = findViewById(R.id.cancelled_booking_btn);
        textViewNoData = findViewById(R.id.textViewNoData);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingHistoryAdapter(bookingList, userID);
        recyclerView.setAdapter(adapter);

        activeButton.setOnClickListener(view -> loadBookingData("active", userID));
        completedButton.setOnClickListener(view -> loadBookingData("completed", userID));
        cancelledButton.setOnClickListener(view -> loadBookingData("cancelled", userID));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload booking data when the activity resumes
        loadBookingData("active", userID);
    }

    private void loadBookingData(String status, String userId) {
        progressBar.setVisibility(View.VISIBLE);
        textViewNoData.setVisibility(View.GONE);

        DatabaseReference bookingReference = FirebaseDatabase.getInstance().getReference("booking_table");
        Query bookingQuery = bookingReference.orderByChild("booking_status").equalTo(status);

        bookingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot bookingSnapshot) {
                bookingList.clear();
                adapter.notifyDataSetChanged();

                if (!bookingSnapshot.exists()) {
                    textViewNoData.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                int expectedCount = (int) bookingSnapshot.getChildrenCount();
                int[] fetchCounter = {0}; // Array to hold fetch counter

                for (DataSnapshot booking : bookingSnapshot.getChildren()) {
                    DataSnapshot bookingUserSnapshot = booking.child("booking_user").child(userId);
                    if (bookingUserSnapshot.exists()) {
                        BookingModel bookingModel = new BookingModel();
                        bookingModel.setBookingId(booking.getKey());
                        bookingModel.setBookingStartlocation(booking.child("booking_startlocation").getValue(String.class));
                        bookingModel.setBookingEndlocation(booking.child("booking_endlocation").getValue(String.class));
                        bookingModel.setBookingDatetime(booking.child("booking_datetime").getValue(String.class));
                        bookingModel.setBookingDistance(booking.child("booking_distance").getValue(String.class) + " KM");
                        bookingModel.setBookingCapacity(booking.child("booking_capacity").getValue(String.class));
                        bookingModel.setBookingTtlprice(booking.child("booking_ttlprice").getValue(String.class));
                        bookingModel.setBookingLeader(booking.child("booking_leader").getValue(String.class));
                        bookingModel.setBookingDriver(booking.child("booking_driver").getValue(String.class));
                        bookingModel.setBookingStatus(booking.child("booking_status").getValue(String.class));
                        bookingModel.setReviewStatus(booking.child("review_status").getValue(Boolean.class));

                        String userPrice = bookingUserSnapshot.child("booking_user_price").getValue(String.class);
                        bookingModel.setBookingUserPrice((userPrice == null || userPrice.isEmpty()) ? "-" : userPrice);

                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("user_table");
                        fetchUserNicknames(userReference, bookingModel, fetchCounter, expectedCount, bookingSnapshot);
                    } else {
                        synchronized (fetchCounter) {
                            fetchCounter[0]++;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DBError", "Error fetching booking data", databaseError.toException());
                textViewNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void fetchUserNicknames(DatabaseReference userRef, BookingModel model, int[] counter, int totalCount, DataSnapshot snapshot) {
        // Fetch leader nickname
        userRef.child(model.getBookingLeader()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                model.setLeaderNickname(dataSnapshot.exists() ? dataSnapshot.child("user_nickname").getValue(String.class) : "-");
                checkAndAdd(model, counter, totalCount, snapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                model.setLeaderNickname("-");
                checkAndAdd(model, counter, totalCount, snapshot);
            }
        });

        // Fetch driver nickname
        if (model.getBookingDriver() != null && !model.getBookingDriver().isEmpty()) {
            userRef.child(model.getBookingDriver()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    model.setDriverNickname(dataSnapshot.exists() ? dataSnapshot.child("user_nickname").getValue(String.class) : "-");
                    checkAndAdd(model, counter, totalCount, snapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    model.setDriverNickname("-");
                    checkAndAdd(model, counter, totalCount, snapshot);
                }
            });
        } else {
            model.setDriverNickname("-");
            checkAndAdd(model, counter, totalCount, snapshot);
        }
    }

    private synchronized void checkAndAdd(BookingModel model, int[] counter, int totalCount, DataSnapshot snapshot) {
        counter[0]++;
        if (!bookingList.contains(model)) {
            bookingList.add(model);
        }
        if (counter[0] >= totalCount) {
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }

    public void onBackButtonClick(View view) {
        finish();
    }
}

