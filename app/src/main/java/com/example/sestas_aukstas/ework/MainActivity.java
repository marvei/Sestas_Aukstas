package com.example.sestas_aukstas.ework;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Martynas on 3/21/2018.
 * Modified by Tadas on 4/15/2018.
 */

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseObj;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_account:
                        goToProfile();
                        return true;
                    case R.id.nav_settings:
                        goToSettings();
                        return true;
                    case R.id.nav_logout:
                        logOut();
                        return true;
                }

                return true;
            }
        });

    }

    public void goToProfile(){
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
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


    public void initializeObj() {
        firebaseObj = firebaseObj.getInstance();
    }

}
