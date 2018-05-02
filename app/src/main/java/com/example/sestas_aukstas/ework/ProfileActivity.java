package com.example.sestas_aukstas.ework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Tadas on 4/15/2018.
 * Modified by Tadas on 4/15/2018
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tvProfileMail;
    TextView tvProfileName;
    Button btnLogout;
    Button btnWorkStat;
    ImageView ivProfilePic;
    FirebaseAuth firebaseObj;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        getSupportActionBar().setTitle("Profilis");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeObj();
        btnLogout.setOnClickListener(this);
        btnWorkStat.setOnClickListener(this);
        //y u no work
    }

    public void showUserName(FirebaseUser currentUser){
        firebaseObj = FirebaseAuth.getInstance();
        String currentUserId = currentUser.getUid();
        Log.d("current user - ", currentUserId);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users").child(currentUserId).child("vardas");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("value is - ", value);
                tvProfileName = findViewById(R.id.tvProfileName);
                tvProfileName.setText(value);
                tvProfileName.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Failed to read value", databaseError.toException());
                Toast.makeText(ProfileActivity.this, "Error: no name detected in database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initializeObj() {
        firebaseObj = FirebaseAuth.getInstance();
        tvProfileMail = findViewById(R.id.tvProfileMail);
        tvProfileMail.setText(firebaseObj.getCurrentUser().getEmail());
        tvProfileMail.setVisibility(View.VISIBLE);
        showUserName(firebaseObj.getCurrentUser());
        btnLogout = findViewById(R.id.btnLogout);
        btnWorkStat = findViewById(R.id.btnWorkStat);
        ivProfilePic = findViewById(R.id.ivProfilePic);
    }

    public void onClick(View v) {
        if (v == btnLogout) {
            //einama į prisijungimo langą
            //    finish();
            //    firebaseObj.signOut();
            Toast.makeText(ProfileActivity.this, "Sėkmingai atsijungta", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(v == btnWorkStat)
        {
            startActivity(new Intent(this, WorkActivity.class));
        }

    }



}
