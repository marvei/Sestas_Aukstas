package com.example.sestas_aukstas.ework;


import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.view.View;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import static org.junit.Assert.*;
import android.test.UiThreadTest;

import com.example.sestas_aukstas.ework.ProfileActivity;
import com.example.sestas_aukstas.ework.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;


public class ProfileTest {
    ProfileActivity activity;
    TextView tvProfileMail;
    TextView tvProfileName;
    FirebaseAuth firebaseObj;
    FirebaseDatabase databaseObj = FirebaseDatabase.getInstance();

//    public ProfileTest() {
//        super(ProfileActivity.class);
//    }
//
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//
//        //setActivityInitialTouchMode(true);
//
//        activity = getActivity();
//        tvProfileMail = (TextView)activity.findViewById(R.id.tvProfileMail);
//        tvProfileName = (TextView)activity.findViewById(R.id.tvProfileName);
//    }

    @Test
    public void emailTest() {
        tvProfileMail = (TextView)activity.findViewById(R.id.tvProfileMail);
        String email = firebaseObj.getCurrentUser().getEmail();
        assertEquals(email, tvProfileMail.getText().toString());

    }

    @Test
    public void nameTest(){
        DatabaseReference databaseRef = databaseObj.getReference();
        String name = databaseRef.child("users").child(firebaseObj.getUid()).child("name").getKey();

        assertEquals(name, tvProfileName.getText().toString());
    }

}