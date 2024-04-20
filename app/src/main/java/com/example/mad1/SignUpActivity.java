package com.example.mad1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    Button signupBtn;
    RadioButton maleRB, femaleRB;
    EditText usernameET, nicknameET, phoneET, emailET, passwordET;

    //Variables used in logical conditioning
    String usernameET2, nicknameET2, phoneET2, emailET2, passwordET2;
    boolean ismale, isfemale;
    AlertDialog.Builder builder;

    private FirebaseDatabase db;
    private DatabaseReference df;
    UserModel user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        signupBtn = findViewById(R.id.signupBTN);
        usernameET = findViewById(R.id.usernameET);
        nicknameET = findViewById(R.id.nicknameET);
        maleRB = findViewById(R.id.maleRB);
        femaleRB = findViewById(R.id.femaleRB);
        phoneET = findViewById(R.id.phoneET);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);

        db = FirebaseDatabase.getInstance();
        df = db.getReference().child("user_table");
        /*UUID uuid = UUID.randomUUID();
        uniqueId = uuid.toString();*/

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Invalid")
                .setMessage("Please fill in all the field")
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }});
        AlertDialog alert = builder.create();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameET2 = usernameET.getText().toString();
                nicknameET2 = nicknameET.getText().toString();
                ismale = maleRB.isChecked();
                isfemale = femaleRB.isChecked();
                phoneET2 = phoneET.getText().toString();
                emailET2 =  emailET.getText().toString();
                passwordET2 = passwordET.getText().toString();


                if(usernameET2 == "" ||  nicknameET2 == "" || phoneET2 == "" ||
                        emailET2 == "" || passwordET2 == "" ||
                        (ismale == false && isfemale == false)) {
                    alert.show();
                }
                else {
                    insertUserData();
                }
            }
        });

    }
    private void insertUserData() {
        try {
            if (ismale == true)
                user = new UserModel(emailET2, "M", nicknameET2, passwordET2, phoneET2, "null", usernameET2);
            else
                user = new UserModel(emailET2, "F", nicknameET2, passwordET2, phoneET2, "null", usernameET2);

            //df.push().setValue(user);
            df.push().setValue(user);

            Toast.makeText(SignUpActivity.this, "Sign Up successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, Sign_In.class));
        }
        catch (Exception ex) {
            builder = new AlertDialog.Builder(this);
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
    }





}