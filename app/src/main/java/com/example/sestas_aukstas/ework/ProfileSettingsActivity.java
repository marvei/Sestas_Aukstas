package com.example.sestas_aukstas.ework;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText newName, newEmail;
    private Button btnSave;
    private FirebaseAuth firebaseObj;
    private FirebaseDatabase database;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        initializeObj();
        btnSave.setOnClickListener(this);
    }

    public void initializeObj() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseObj = FirebaseAuth.getInstance();
        newName = findViewById(R.id.etNameUpdate);
        showUserName(firebaseObj.getCurrentUser());
        newEmail = findViewById(R.id.etEmailUpdate);
        newEmail.setText(firebaseObj.getCurrentUser().getEmail());
        btnSave = findViewById(R.id.btnSave);
    }

    public void onClick(View v) {
        if (v == btnSave) {
            if (noEmptyFields()){
                saveChanges(firebaseObj.getCurrentUser());
            }

        }
    }

    public void showUserName(FirebaseUser currentUser){
        firebaseObj = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users").child(currentUser.getUid()).child("vardas");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d("value is - ", value);
                newName = findViewById(R.id.etNameUpdate);
                newName.setText(value);
                newName.requestFocus();
                newName.setVisibility(View.VISIBLE);
                newName.requestFocus();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Failed to read value", databaseError.toException());
                Toast.makeText(ProfileSettingsActivity.this, "Error: no name detected in database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveChanges(FirebaseUser currentUser) {
        changesSuccessful = false;
        if (noEmptyFields()) {
            String name = newName.getText().toString();
            DatabaseReference myRef = database.getReference().child("users").child(currentUser.getUid()).child("vardas");
            try {

                myRef.setValue(name);
            } catch (Exception e) {
                Toast.makeText(ProfileSettingsActivity.this, "Vardo Pakeisti Nepavyko.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                finish();
            }

            final DatabaseReference myRefEmail = database.getReference().child("users").child(currentUser.getUid()).child("email");

            final String userEmailNew = newEmail.getText().toString();
            final String userEmailOld = firebaseUser.getEmail().toString();
                firebaseUser.updateEmail(userEmailNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            try{
                                myRefEmail.setValue(userEmailNew);
                                Toast.makeText(ProfileSettingsActivity.this, "Pakeitimai išsaugoti.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            catch (Exception e) {

                                firebaseUser.updateEmail(userEmailOld);
                                Toast.makeText(ProfileSettingsActivity.this, "El. Pašto Pakeisti Nepavyko.", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                finish();
                            }
                          // finish();
                        } else {
                            Toast.makeText(ProfileSettingsActivity.this, "El. Pašto Pakeisti Nepavyko", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

        }

    }

    public boolean isDataLoaded(EditText newName, EditText newEmail)
    {
        if(newName.getText() != null && newEmail.getText() != null)
        {
            newName.requestFocus();
            return true;
        }
        else return false;
    }

    private boolean noEmptyFields(){
        if (newName.getText().toString().isEmpty()){
            newName.setError(getResources().getString(R.string.emptyFieldMsg));
            newName.requestFocus();
            return false;
        }
        else if(newEmail.getText().toString().isEmpty()){
            newEmail.setError(getResources().getString(R.string.emptyFieldMsg));
            newEmail.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }
}
