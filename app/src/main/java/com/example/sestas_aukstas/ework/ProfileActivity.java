package com.example.sestas_aukstas.ework;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import com.example.sestas_aukstas.ework.MainActivity;

/**
 * Created by Tadas on 4/15/2018.
 * Modified by Tadas on 4/15/2018
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
//    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    TextView tvProfileMail;
    TextView tvProfileName;
    Button btnLogout;
    //Button btnWorkStat;
    ImageView ivProfilePic;
    FirebaseAuth firebaseObj;
    FirebaseDatabase database;
    TextView tvTextTotal;
    TextView tvTimeTotal;

    SimpleDateFormat workDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
    long totalWorkT = 0;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    int intervalNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntervalOnStart();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getTotalTimeInMillis();
            }
        }, 3000);

        setContentView(R.layout.profile_page);
        getSupportActionBar().setTitle("Profilis");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeObj();
        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                totalTimePrint();
            }
        }, 10000);

        btnLogout.setOnClickListener(this);
        //btnWorkStat.setOnClickListener(this);
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

    public void showTotalTime(FirebaseUser currentUser){
        firebaseObj = FirebaseAuth.getInstance();
        String currentUserId = currentUser.getUid();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users").child(currentUserId).child("vardas").child("time_stamps").child("Total today");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("value is - ", value);
                tvTimeTotal = findViewById(R.id.tvTotal);
                tvTimeTotal.setText(value);
                tvTimeTotal.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Failed to read value", databaseError.toException());
                Toast.makeText(ProfileActivity.this, "Error: no total time detected in database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTotalTimeInMillis(){
        final String currentUser = mAuth.getCurrentUser().getUid();
        final DatabaseReference ref = firebaseDatabase.getReference();
        Date curDate = new Date();
        String date = dateFormat.format(curDate);
        totalWorkT = 0;
        Log.d("interval number = ", Integer.toString(intervalNum));
        for(int i = 1; i <= intervalNum; i++) {
            DatabaseReference inter = ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child(Integer.toString(i)).child("TotalInMillis");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        long x = (long) dataSnapshot.getValue();
                        totalWorkT +=x;
                        Log.i("database", Long.toString(x));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            inter.addListenerForSingleValueEvent(eventListener);
        }
    }

    public void getIntervalOnStart(){
        String currentUser = mAuth.getCurrentUser().getUid();
        Date curDate = new Date();
        final Integer[] a = {1};
        String date = dateFormat.format(curDate);
        DatabaseReference ref = firebaseDatabase.getReference();
        DatabaseReference inter = ref.child("users").child(currentUser).child("time_stamps").child(date.toString());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long x = dataSnapshot.getChildrenCount();
                Integer b = (int) (x);
                if(b >= 3){
                    a[0] = (int) (x-2);
                    Log.i("databaseProfile", Integer.toString(a[0]));
                }
                else if(b < 3) a[0] = 1;
                Log.i("databaseProfile", Integer.toString(a[0]));

                intervalNum = a[0];
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        inter.addListenerForSingleValueEvent(eventListener);
    }

    public void totalTimePrint(){
       // final String workDate = dateFormat.format(new Date());
        long elapsedSeconds = totalWorkT / 1000 % 60;
        long elapsedMinutes = totalWorkT / (60 * 1000) % 60;
        long elapsedHours = totalWorkT / (60 * 60 * 1000) % 24;
        //String workDate = dateFormat.format(new Date());

        String currentUser = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = firebaseDatabase.getReference();
        final String date = dateFormat.format(new Date());
        //  String elapsedTotal = String.format(Long.toString(elapsedHours) + ":" + Long.toString(elapsedMinutes) + ":" + Long.toString(elapsedSeconds));
        Log.i("PRINTAS", Long.toString(totalWorkT));
        String elapsedTotal = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);


        tvTimeTotal = findViewById(R.id.tvTotal);
        tvTimeTotal.setText(elapsedTotal);
        tvTimeTotal.setVisibility(View.VISIBLE);

        String elapsedH = Long.toString(elapsedHours);
        String elapsedM = Long.toString(elapsedMinutes);
        String elapsedS = Long.toString(elapsedSeconds);
        //elapsedH = String.format("%02d", elapsedH);
        //elapsedM = String.format("%02d", elapsedM);
        //elapsedS = String.format("%02d", elapsedS);
        String elapsed = elapsedH + ":" + elapsedM + ":" + elapsedS;
        ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child("Total today").setValue(elapsed);

    }

    public void initializeObj() {
        firebaseObj = FirebaseAuth.getInstance();
        tvProfileMail = findViewById(R.id.tvProfileMail);
        tvProfileMail.setText(firebaseObj.getCurrentUser().getEmail());
        tvProfileMail.setVisibility(View.VISIBLE);
        showUserName(firebaseObj.getCurrentUser());
        btnLogout = findViewById(R.id.btnLogout);
       // btnWorkStat = findViewById(R.id.btnWorkStat);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        tvTextTotal = findViewById(R.id.tvTextTotal);
        //showTotalTime(firebaseObj.getCurrentUser());

    }

    public void onClick(View v) {
        if (v == btnLogout) {
            //einama į prisijungimo langą
            //    finish();
            //    firebaseObj.signOut();
            Toast.makeText(ProfileActivity.this, "Sėkmingai atsijungta", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
//        if(v == btnWorkStat)
//        {
//            startActivity(new Intent(this, WorkActivity.class));
//        }

    }



}
