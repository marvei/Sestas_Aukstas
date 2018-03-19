package com.example.sestas_aukstas.ework;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvRegister;
    EditText txMail;
    EditText txPass;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        initializeObj();

        tvRegister.setOnClickListener(this);

    }

    public void initializeObj() {
        txMail = findViewById(R.id.txMail);
        txPass = findViewById(R.id.txPass);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        //firebaseObj = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v == tvRegister) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }
}
