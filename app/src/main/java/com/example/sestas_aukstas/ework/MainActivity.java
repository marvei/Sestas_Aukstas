package com.example.sestas_aukstas.ework;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    FirebaseAuth firebaseObj;
    TextView nav_account;
    TextView nav_work;
    TextView nav_settings;
    TextView nav_logout;
    View nav_acc_divider;
    View nav_work_divider;
    View nav_settings_divider;
    View nav_logout_divider;
    TextView nav_mapTesting;
    View nav_map_divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initializeObj();
        nav_account.setOnClickListener(this);
        nav_work.setOnClickListener(this);
        nav_settings.setOnClickListener(this);
        nav_logout.setOnClickListener(this);
        nav_mapTesting.setOnClickListener(this);
        nav_account.setOnTouchListener(this);
        nav_work.setOnTouchListener(this);
        nav_settings.setOnTouchListener(this);
        nav_logout.setOnTouchListener(this);
        nav_mapTesting.setOnTouchListener(this);


    }

    public void initializeObj() {
        firebaseObj = firebaseObj.getInstance();
        firebaseObj = FirebaseAuth.getInstance();
        nav_account = findViewById(R.id.nav_account);
        nav_work = findViewById(R.id.nav_work);
        nav_settings = findViewById(R.id.nav_settings);
        nav_logout = findViewById(R.id.nav_logout);
        nav_mapTesting = findViewById(R.id.nav_mapTesting);
        nav_acc_divider = findViewById(R.id.nav_acc_divider);
        nav_work_divider = findViewById(R.id.nav_work_divider);
        nav_settings_divider = findViewById(R.id.nav_settings_divider);
        nav_logout_divider = findViewById(R.id.nav_logout_divider);
        nav_map_divider = findViewById(R.id.nav_map_divider);
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
        if(v == nav_mapTesting){
            goToMap();
        }
    }


    public void goToProfile(){
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }

    public void goToWork(){
        startActivity(new Intent(MainActivity.this, WorkActivity.class));
    }

    public void goToSettings(){
        startActivity(new Intent(MainActivity.this, ProfileSettingsActivity.class)); //create settings activity!!!!!!
    }

    public void logOut(){
        // finish();
        //  firebaseObj.signOut();
        Toast.makeText(MainActivity.this, "Sėkmingai atsijungta", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void goToMap(){startActivity(new Intent(MainActivity.this, MapActivity.class));}

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            // reset the background color here
            nav_acc_divider.setBackgroundColor(getResources().getColor(R.color.colorGray));
            nav_work_divider.setBackgroundColor(getResources().getColor(R.color.colorGray));
            nav_settings_divider.setBackgroundColor(getResources().getColor(R.color.colorGray));
            nav_logout_divider.setBackgroundColor(getResources().getColor(R.color.colorGray));
            nav_map_divider.setBackgroundColor(getResources().getColor(R.color.colorGray));

        }else{
            // Change the background color here
            if(v == nav_account)
                nav_acc_divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            if(v == nav_work)
                nav_work_divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            if(v == nav_settings)
                nav_settings_divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            if(v == nav_logout)
                nav_logout_divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            if(v == nav_mapTesting)
                nav_map_divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

        return false;
    }
}
