package com.example.sestas_aukstas.ework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Enpoz on 4/15/2018.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tvProfileMail;
    TextView tvProfileName;
    Button btnLogout;
    Button btnWorkStat;
    ImageView ivProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        initializeObj();
        btnLogout.setOnClickListener(this);
        btnWorkStat.setOnClickListener(this);

    }

    public void initializeObj() {
        tvProfileMail = findViewById(R.id.tvProfileMail);
        tvProfileName = findViewById(R.id.tvProfileName);
        btnLogout = findViewById(R.id.btnLogout);
        btnWorkStat = findViewById(R.id.btnWorkStat);
        ivProfilePic = findViewById(R.id.ivProfilePic);
    }

    public void onClick(View v) {
        if (v == btnLogout) {
            //einama į prisijungimo langą
            startActivity(new Intent(this, LoginActivity.class));
        }
        if(v== btnWorkStat)
        {
            startActivity(new Intent(this, WorkActivity.class));
        }

    }



}
