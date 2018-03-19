package com.example.sestas_aukstas.ework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        initializeObj();
      btnBack.setOnClickListener(this);
    }

    public void initializeObj(){
        btnBack=findViewById(R.id.btnBack);
    }

    @Override
    public void onClick(View v) {
        if (v == btnBack){
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
    }
}
