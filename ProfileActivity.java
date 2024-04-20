package com.example.mad1;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView usernameTV;
    private TextView real_nameTV;
    private TextView mobile_noTV;
    private TextView emailTV;
    private TextView genderTV;
    private String userID;
    private ImageView profilePicIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        userID = intent.getStringExtra("userId");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user_table");



        usernameTV = findViewById(R.id.username);
        real_nameTV = findViewById(R.id.real_name);
        mobile_noTV = findViewById(R.id.mobile_no);
        emailTV = findViewById(R.id.email);
        genderTV = findViewById(R.id.gender);
        profilePicIV = findViewById(R.id.profilePic);

        loadProfile(userID);
    }

    private void loadProfile(String userId) {
        databaseReference = FirebaseDatabase.getInstance().getReference("user_table").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("user_nickname").getValue(String.class);
                    String realName = dataSnapshot.child("user_realname").getValue(String.class);
                    String mobileNo = formatPhoneNumber(dataSnapshot.child("user_phone").getValue(String.class));
                    String email = dataSnapshot.child("user_email").getValue(String.class);
                    String gender = dataSnapshot.child("user_gender").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("user_photoURL").getValue(String.class);

                    usernameTV.setText(username);
                    real_nameTV.setText(realName);
                    mobile_noTV.setText(mobileNo);
                    emailTV.setText(email);
                    genderTV.setText(gender);


                    Glide.with(ProfileActivity.this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.profile_placeholder)
                            .into(profilePicIV);

                    Toast.makeText(ProfileActivity.this, "Successfully Read", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "User Doesn't Exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to read", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onUploadProfileClick(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            uploadImage(selectedImageUri,userID);
        }
    }


    private void uploadImage(Uri imageUri,String userId) {
        String imageName = userId + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("user_profile_photo/" + imageName);


        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {

                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("user_table").child(userId).child("user_photoURL");

                        databaseRef.setValue(uri.toString())
                                .addOnSuccessListener(aVoid -> {

                                    Toast.makeText(ProfileActivity.this, "Photo uploaded and URL updated", Toast.LENGTH_SHORT).show();

                                    Glide.with(ProfileActivity.this)
                                            .load(uri)
                                            .placeholder(R.drawable.profile_placeholder)
                                            .into(profilePicIV);

                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(ProfileActivity.this, "Failed to update URL in database", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(exception -> {

                    Toast.makeText(ProfileActivity.this, "Failed to upload photo to Firebase Storage", Toast.LENGTH_SHORT).show();
                });
    }
    private String formatPhoneNumber(String phoneNumber) {

        String formattedPhoneNumber = "+60-" + phoneNumber;
        return formattedPhoneNumber;
    }

    public void onBackButtonClick(View view) {
        finish();
    }
}