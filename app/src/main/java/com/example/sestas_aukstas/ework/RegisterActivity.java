package com.example.sestas_aukstas.ework;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private TextView tvBack;
    private EditText newMail, newPass, newPass2, newName;
    private Button btnSignUp;

  /*  DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUserRef = mRootRef.child("User");
    DatabaseReference mUserName = mUserRef.child("Name");
    DatabaseReference mEmailName = mUserRef.child("Email");
    DatabaseReference mPasswordName = mUserRef.child("Password"); */



   // private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        getSupportActionBar().setTitle("Registracija");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeObj();

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noEmptyFields() && passwordsMatch()){
                    String mail = newMail.getText().toString().trim();
                    String pass = newPass.getText().toString().trim();
                    final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pd.setMessage("Vyksta registracija");
                    pd.show();

                    mAuth.createUserWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                updateDatabase();
                                pd.dismiss();
                                finish();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                Toast.makeText(RegisterActivity.this, "Registracija sėkminga", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registracija nesėkminga", Toast.LENGTH_SHORT).show();
                                pd.cancel();
                            }
                        }
                    });
                }
            }
        });
    }

    public void initializeObj() {
      //  progressDialog = new ProgressDialog(this);
        tvBack = findViewById(R.id.tvBack);
        newMail = findViewById(R.id.newMail);
        newPass = findViewById(R.id.newPass);
        newPass2 = findViewById(R.id.newPass2);
        btnSignUp = findViewById(R.id.btnSignUp);
        newName = findViewById(R.id.newName);
        mAuth = FirebaseAuth.getInstance();

    }



    public boolean noEmptyFields() {
        String name = newName.getText().toString();
        String mail = newMail.getText().toString();
        String pass = newPass.getText().toString();
        String pass2 = newPass2.getText().toString();

        if (name.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Įveskite savo vardą", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mail.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Įveskite el. pašto adresą", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Įveskite slaptažodį", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (pass2.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Pakartokite slaptažodį", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

  /*  public void pushToDatabase(String name, String email, String pass) {
        mUserName.setValue(name);
        mEmailName.setValue(email);
        mPasswordName.setValue(pass);
    }*/


    public boolean passwordsMatch(){
        String pass = newPass.getText().toString();
        String passRepeat = newPass2.getText().toString();
        if(passRepeat.isEmpty()) {
            Toast.makeText(this, "Pakartokite įvestą slaptažodį", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(pass.equals(passRepeat))
            return true;
        Toast.makeText(this, "Neteisingai pakartotas slaptažodis", Toast.LENGTH_SHORT).show();
        return false;
    }

    /*private void uploadUsrData(){
        DatabaseReference dbRef = firebaseDatabase.getReference();
        String usrName = newName.getText().toString();
        String usrMail = newMail.getText().toString();
        UserData userData = new UserData(usrName, usrMail);
        dbRef.child("users").child(firebaseAuth.getUid()).setValue(userData).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Duomenų bazės klaida", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private void updateDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String currentUser = mAuth.getCurrentUser().getUid();


    //    User user = new User(newName.getText().toString(), newMail.getText().toString());

        DatabaseReference ref = database.getReference();

        ref.child("users").child(currentUser).child("vardas").setValue(newName.getText().toString());
        ref.child("users").child(currentUser).child("email").setValue(newMail.getText().toString());

    }

    /*public class User{
        public String username;
        public String email;

        public User(){

        }

        public User(String username, String email){
            this.username = username;
            this.email = email;
        }
    }*/

}
