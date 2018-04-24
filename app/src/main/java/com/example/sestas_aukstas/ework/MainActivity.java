package com.example.sestas_aukstas.ework;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Martynas on 3/21/2018.
 * Modified by Tadas on 4/15/2018.
 * Modified by Tadas on 4/24/2018.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FirebaseAuth firebaseObj;
    TextView nav_account;
    TextView nav_work;
    TextView nav_settings;
    TextView nav_logout;
    private LinearLayout navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initializeObj();
        nav_account.setOnClickListener(this);
        nav_work.setOnClickListener(this);
        nav_settings.setOnClickListener(this);
        nav_logout.setOnClickListener(this);

    }

    public void initializeObj() {
        firebaseObj = firebaseObj.getInstance();
        firebaseObj = FirebaseAuth.getInstance();
        nav_account = findViewById(R.id.nav_account);
        nav_work = findViewById(R.id.nav_work);
        nav_settings = findViewById(R.id.nav_settings);
        nav_logout = findViewById(R.id.nav_logout);
    }

    public void onClick(View v) {
        if (v == nav_account) {
            goToProfile();
        }
        if(v == nav_work)
        {
            goToWork();
        }
        if(v == nav_settings){
            goToSettings();
        }

        if (v == nav_logout) {
            logOut();
        }


    }

    public void goToProfile(){
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }

    public void goToWork(){
        startActivity(new Intent(MainActivity.this, WorkActivity.class));
    }

    public void goToSettings(){
        startActivity(new Intent(MainActivity.this, ProfileActivity.class)); //create settings activity!!!!!!
    }

    public void logOut(){
        // finish();
        //  firebaseObj.signOut();
        Toast.makeText(MainActivity.this, "Sėkmingai atsijungta", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

}
