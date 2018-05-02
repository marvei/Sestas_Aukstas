package com.example.sestas_aukstas.ework;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.common.GoogleApiAvailability;



public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvRegister;
    EditText usrMail;
    EditText usrPass;
    Button btnLogin;
    FirebaseAuth mAuth;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        if(isServicesOK()){
            initializeObj();
        }
        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }

    public boolean isServicesOK(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if(available == ConnectionResult.SUCCESS){
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(LoginActivity.this, "Google Services versija persena.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void initializeObj() {
        usrMail = findViewById(R.id.usrMail);
        usrPass = findViewById(R.id.usrPass);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        mAuth = FirebaseAuth.getInstance();
    }

    public boolean noEmptyFields(){
        String mail = usrMail.getText().toString();
        String pass = usrPass.getText().toString();

        if (mail.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Neteisingi prisijungimo duomenys", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }

    public void validateUser(){
        String mail = usrMail.getText().toString();
        String pass = usrPass.getText().toString();
        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("Vyksta prisijungimas");
        pd.show();

        mAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "Prisijungimas sėkmingas", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    boolean connected = false;
                    ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
                        connected = true;
                    }

                    if (connected){
                        Toast.makeText(LoginActivity.this, "Neteisingi prisijungimo duomenys", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Nėra interneto ryšio", Toast.LENGTH_SHORT).show();
                    }
                  //  Toast.makeText(LoginActivity.this, "Prisijungimas nepavyko", Toast.LENGTH_SHORT).show();

                    pd.cancel();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == tvRegister) {
            //einama į registracijos langą
            startActivity(new Intent(this, RegisterActivity.class));
        }

        if (v == btnLogin && noEmptyFields()){
            //jei prisijungimo laukai teisingi jungiamas vartotojas
            validateUser();
        }
    }
}
