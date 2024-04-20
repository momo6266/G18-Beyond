package com.example.mad1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewMainAdapter adapter;
    private List<ReviewModel> dataList = new ArrayList<>();

    private ProgressBar progressBar;
    private String userID,bookingID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent intent = getIntent();
        userID = intent.getStringExtra("userId");


        recyclerView = findViewById(R.id.viewCompleteOrders);
        adapter = new ReviewMainAdapter(dataList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.progressBar);

        loadReviewOrders();

    }
    private void loadReviewOrders() {

        dataList.clear();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("booking_table");


        Query query = databaseReference.orderByChild("booking_status").equalTo("completed");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {

                    if (bookingSnapshot.child("booking_user").hasChild(userID)) {
                        String bookingID = bookingSnapshot.getKey();
                        String bookingDateTime = bookingSnapshot.child("booking_datetime").getValue(String.class);
                        String bookingDistance = bookingSnapshot.child("booking_distance").getValue(String.class);
                        String bookingStartLocation = bookingSnapshot.child("booking_startlocation").getValue(String.class);
                        String bookingEndLocation = bookingSnapshot.child("booking_endlocation").getValue(String.class);
                        String bookingTotalPrice = bookingSnapshot.child("booking_ttlprice").getValue(String.class);


                        ReviewModel reviewModel = new ReviewModel(bookingDistance, bookingTotalPrice, bookingDateTime, bookingStartLocation + " - " + bookingEndLocation, bookingID, userID);
                        dataList.add(reviewModel);
                    }
                }


                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.e("Firebase", "Error loading data", error.toException());


                progressBar.setVisibility(View.GONE);
            }
        });
    }


    public void onBackButtonClick(View view) {
        finish();
    }


}