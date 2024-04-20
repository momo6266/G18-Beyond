package com.example.mad1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ReviewDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewDetailsAdapter adapter;

    private ReviewDetailsWriteAdapter adapterWrite;
    private List<RDModel> reviewDetailsList = new ArrayList<>();

    private List<String> reviewWriteDetailsList = new ArrayList<>();

    ProgressBar progressBar;
    Button buttonSubmitReview;

    RatingBar ratingBarRide;


    boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");
        String bookingID = intent.getStringExtra("bookingID");



        buttonSubmitReview = findViewById(R.id.buttonSubmitReview);
        progressBar = findViewById(R.id.progressBarDetails);
        ratingBarRide = findViewById(R.id.ratingBarRide);



        recyclerView = findViewById(R.id.viewOrderUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        checkReviewStatus(userID, bookingID);
    }

    public class Review {
        private float rating;

        public Review() {

        }

        public Review(float rating) {
            this.rating = rating;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

    }

    private void submitRatingToDatabase(String userID, String bookingID, String memberId, float rating) {
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("review_table")
                .child(userID).child(bookingID).child("user_ratings").child(memberId);

        reviewsRef.setValue(rating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(ReviewDetailsActivity.this, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ReviewDetailsActivity.this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void letUserReview(String userID, String bookingID) {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("booking_table").child(bookingID).child("booking_user");

        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                reviewWriteDetailsList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String memberId = userSnapshot.getKey();

                    if (!userID.equals(memberId)) {
                        reviewWriteDetailsList.add(memberId);
                    }
                }

                if (!reviewWriteDetailsList.isEmpty()) {
                    adapterWrite = new ReviewDetailsWriteAdapter(reviewWriteDetailsList, userID, bookingID);
                    recyclerView.setAdapter(adapterWrite);
                    adapterWrite.notifyDataSetChanged();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void loadReviewData(String userID, String bookingID) {
        ratingBarRide.setIsIndicator(true);
        buttonSubmitReview.setVisibility(View.INVISIBLE);

        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("review_table")
                .child(userID).child(bookingID).child("user_ratings");

        reviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                reviewDetailsList.clear();


                for (DataSnapshot memberSnapshot : dataSnapshot.getChildren()) {
                    final String memberId = memberSnapshot.getKey();


                    final Float rating = memberSnapshot.getValue(Float.class);


                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("user_table").child(memberId).child("user_nickname");
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String nickname = snapshot.getValue(String.class);
                            Log.d("Nickname", "Nickname: " + nickname);

                            RDModel rdModel = new RDModel(memberId, nickname, rating);

                            reviewDetailsList.add(rdModel);

                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Log.e("Firebase", "Error loading user nickname", error.toException());
                        }
                    });
                }


                adapter.notifyDataSetChanged();


                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("Firebase", "Error loading review data", databaseError.toException());


                progressBar.setVisibility(View.GONE);
            }
        });

        DatabaseReference reviewsRef2 = FirebaseDatabase.getInstance().getReference("review_table")
                .child(userID).child(bookingID).child("review_ride_rating");

        reviewsRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Float rating = dataSnapshot.getValue(Float.class);
                    if (rating != null) {

                        ratingBarRide.setRating(rating);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.e("Firebase", "Error loading ride rating", databaseError.toException());
            }
        });

    }


    private void checkReviewStatus(String userID, String bookingID) {
        DatabaseReference reviewStatusRef = FirebaseDatabase.getInstance().getReference("booking_table")
                .child(bookingID).child("review_status");

        reviewStatusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("ReviewStatus", "DataSnapshot: " + dataSnapshot.getValue());
                if (dataSnapshot.exists()) {
                    Boolean reviewStatus = dataSnapshot.getValue(Boolean.class);
                    status = reviewStatus != null && reviewStatus;
                } else {
                    status = false;
                }
                Log.d("ReviewStatus", "status: " + status);


                if (status) {
                    Log.d("truetag", "statusreal ");
                    adapter = new ReviewDetailsAdapter(reviewDetailsList);
                    recyclerView.setAdapter(adapter);
                    loadReviewData(userID, bookingID);
                } else {
                    Log.d("falsetag", "statusfalse ");
                    adapterWrite = new ReviewDetailsWriteAdapter(reviewWriteDetailsList, userID, bookingID);
                    recyclerView.setAdapter(adapterWrite);
                    letUserReview(userID, bookingID);

                    buttonSubmitReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ratingBarRide.getRating() > 0.4f) {

                                boolean allRatingsProvided = true;
                                for (Float rating : adapterWrite.getAllRatings()) {
                                    if (rating < 0.5f) {
                                        allRatingsProvided = false;
                                        break;
                                    }
                                }


                                if (allRatingsProvided) {
                                    float ratingRide = ratingBarRide.getRating();

                                    DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("review_table").child(userID).child(bookingID).child("review_ride_rating");

                                    reviewsRef.setValue(ratingRide)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Toast.makeText(ReviewDetailsActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                                                    buttonSubmitReview.setVisibility(View.INVISIBLE);
                                                    status = true;

                                                    DatabaseReference reviewsRef1 = FirebaseDatabase.getInstance().getReference("booking_table").child(bookingID).child("review_status");
                                                    reviewsRef1.setValue(status);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ReviewDetailsActivity.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                                                }
                                            });



                                    List<Float> ratings = adapterWrite.getAllRatings();


                                    for (int i = 0; i < ratings.size(); i++) {
                                        float rating = ratings.get(i);
                                        String memberId = reviewWriteDetailsList.get(i);
                                        submitRatingToDatabase(userID, bookingID, memberId, rating);
                                    }
                                } else {

                                    Toast.makeText(ReviewDetailsActivity.this, "Please rate all options", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                Toast.makeText(ReviewDetailsActivity.this, "Please provide a rating", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                status = false;
            }
        });
    }


    public void onBackButtonClick(View view) {
        finish();
    }
}