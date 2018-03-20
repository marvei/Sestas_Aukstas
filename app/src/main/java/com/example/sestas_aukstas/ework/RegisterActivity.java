package com.example.sestas_aukstas.ework;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvBack;
    EditText newMail, newPass, newPass2;
    Button btnSignUp;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        mAuth = FirebaseAuth.getInstance();

        initializeObj();
        tvBack.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    public void initializeObj(){
        progressDialog= new ProgressDialog(this);
        tvBack=findViewById(R.id.tvBack);
        newMail=findViewById(R.id.newMail);
        newPass=findViewById(R.id.newPass);
        newPass2=findViewById(R.id.newPass2);
        btnSignUp=findViewById(R.id.btnSignUp);

    }

    public void registerUser(){
        String email = newMail.getText().toString().trim();
        String password = newPass.getText().toString().trim();

       /* if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Įveskite el. pašto adresą", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Įveskite slaptažodį",Toast.LENGTH_SHORT).show();

        }
        progressDialog.setMessage("Registruojamas vartotojas...");
        progressDialog.show(); */

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                          //  Log.d(TAG, "createUserWithEmail:success");
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            Toast.makeText(RegisterActivity.this, "Registracija sėkminga", Toast.LENGTH_SHORT).show();
                         //   FirebaseUser user = mAuth.getCurrentUser();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                          //  Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // ...
                    }
                });
    }



    @Override
    public void onClick(View v) {
        if (v == tvBack){
            //grįžta atgal į prisijungimo ekraną
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
        if (v == btnSignUp){
           // if (newPass.getText().toString().trim() == newPass2.getText().toString().trim()){
                //jei slaptažodžiai sutampa, užregistruojamas vartotojas
                registerUser();
            }
       // }
    }
}
