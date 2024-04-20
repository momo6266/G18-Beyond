package com.example.mad1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JoinBookingActivity extends AppCompatActivity {

    String userID, bookingID;

    TextView departTV,destinationTV,departTimeTV,distanceTV,totalCostTV,personalCostTV;
    Switch driverJoin_switch;

    Button confirm_button;

    float personalCost=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        userID = intent.getStringExtra("userId");
        bookingID = intent.getStringExtra("bookingId");


        departTV = findViewById(R.id.departTV);
        destinationTV = findViewById(R.id.destinationTV);
        departTimeTV = findViewById(R.id.departTimeTV);
        distanceTV = findViewById(R.id.distanceTV);
        totalCostTV = findViewById(R.id.totalCostTV);
        personalCostTV = findViewById(R.id.personalCostTV);

        driverJoin_switch = findViewById(R.id.driverJoin_switch);

        confirm_button = findViewById(R.id.confirm_button);

        loadBookingData();
        confirm_button.setOnClickListener(v -> joinRide(personalCost));
    }

    public void loadBookingData() {

        Log.d("bookingID", "bookingID: " + bookingID);
        DatabaseReference joinRef = FirebaseDatabase.getInstance().getReference().child("booking_table").child(bookingID);


        joinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String depart = dataSnapshot.child("booking_startlocation").getValue(String.class);
                    String dest = dataSnapshot.child("booking_endlocation").getValue(String.class);
                    String bookingDateTime = dataSnapshot.child("booking_datetime").getValue(String.class);
                    String distance = dataSnapshot.child("booking_distance").getValue(String.class);
                    String ttlCost = dataSnapshot.child("booking_ttlprice").getValue(String.class);

                    Log.d("depart", "depart: " + depart);
                    Log.d("dest", "dest: " + dest);
                    Log.d("bookingDateTime", "bookingDateTime: " + bookingDateTime);
                    Log.d("distance", "distance: " + distance);
                    Log.d("ttlCost", "ttlCost: " + ttlCost);

                    departTV.setText(depart);
                    destinationTV.setText(dest);
                    departTimeTV.setText(bookingDateTime);
                    distanceTV.setText(distance);
                    totalCostTV.setText(ttlCost);



                    // Calculate personal cost
                    long userNum =dataSnapshot.child("booking_user").getChildrenCount();
                    if (ttlCost != null) {
                        personalCost = Float.parseFloat(ttlCost.trim()) / (userNum + 1);
                        personalCostTV.setText(String.valueOf(personalCost));
                    } else {
                        Log.d("firebaseTag", "ttlCost is null");
                    }

                } else {
                    Log.d("firebaseTag", "No booking data found for bookingID: " + bookingID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.w("firebaseTag", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void joinRide(float personalCost) {
        DatabaseReference joinRef = FirebaseDatabase.getInstance().getReference().child("booking_table").child(bookingID);
        DatabaseReference bookingUserRef = joinRef.child("booking_user").child(userID);

        joinRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String driver = dataSnapshot.child("booking_driver").getValue(String.class);


                    for (DataSnapshot userSnapshot : dataSnapshot.child("booking_user").getChildren()) {
                        String userId = userSnapshot.getKey();
                        if (!userId.equals(driver)) {
                            DatabaseReference userPriceRef = joinRef.child("booking_user").child(userId).child("booking_user_price");
                            userPriceRef.setValue(String.valueOf(personalCost));
                        }
                    }


                    bookingUserRef.child("booking_user_price").setValue(String.valueOf(personalCost));

                    boolean isDriverJoin = driverJoin_switch.isChecked();
                    if (isDriverJoin) {

                        if (driver == null) {
                            dataSnapshot.getRef().child("booking_driver").setValue(userID);

                        } else {
                            Toast.makeText(JoinBookingActivity.this, "This booking already has a driver!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    Log.d("firebaseTag", "Submitted successfully.");
                } else {
                    Log.d("firebaseTag", "No booking data found for bookingID: " + bookingID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Log.w("firebaseTag", "Failed to read value.", databaseError.toException());
            }
        });

        Toast.makeText(JoinBookingActivity.this, "You have joined the booking", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(JoinBookingActivity.this, MainActivity.class);
        intent.putExtra("userId", userID);
        startActivity(intent);

    }


}