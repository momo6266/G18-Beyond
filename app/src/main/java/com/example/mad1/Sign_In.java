package com.example.mad1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Sign_In extends AppCompatActivity {

    EditText etLoginEmail;
    EditText etLoginPassword;
    Button btnLogin;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        etLoginEmail = findViewById(R.id.EmailEditText);
        etLoginPassword = findViewById(R.id.passwordEditText);
        btnLogin = findViewById(R.id.SignInButton);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail = etLoginEmail.getText().toString();
                String userPassword = etLoginPassword.getText().toString();



                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user_table");


                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // This method will be called whenever data at the "user_table" node changes

                        try {
                            // Iterate through all children (users) under the "user_table" node
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                // Retrieve the value of the "email" and "password" fields for each user
                                String email = String.valueOf(userSnapshot.child("user_email").getValue());
                                String password = String.valueOf((userSnapshot.child("user_password").getValue()));

                                // Check if the retrieved email and password match the user input
                                if (userEmail.equals(email) && userPassword.equals(password)) {
                                    // Authentication successful
                                    // Get the userID from the snapshot key
                                    String userID = userSnapshot.getKey();

                                    // Create an intent to start MainActivity
                                    Intent intent = new Intent(Sign_In.this, MainActivity.class);

                                    // Pass the userID as an extra to the intent
                                    intent.putExtra("userId", userID);

                                    // Start MainActivity
                                    startActivity(intent);

                                    // Finish the current activity to prevent the user from going back to the Sign In page
                                    finish();

                                    // Exit the loop since we found a matching user
                                    return;
                                }
                            }
                        }
                        catch(Exception ex) {
                            builder = new AlertDialog.Builder(Sign_In.this);
                            builder.setTitle("Error")
                                    .setMessage(ex.toString())
                                    .setCancelable(false)
                                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }});
                            AlertDialog alert = builder.create();
                            alert.show();
                        }

                        // If no matching user is found, show an error message
                        Toast.makeText(Sign_In.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors (if any) that occur during the read operation
                        Log.e("TAG", "Error fetching data", databaseError.toException());
                    }
                });
            }
        });

    }
}