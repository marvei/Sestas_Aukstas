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

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Test;


public class ProfileTest extends ActivityInstrumentationTestCase2<MainActivity> {
    MainActivity activity;
    EditText firstNumber;
    EditText secondNumber;
    TextView addResult;
    Button btnAdd;
    TextView tvMail;
    FirebaseAuth firebaseObj;

    public ProfileTest() {
        super(MainActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(true);

        activity = getActivity();
//        firstNumber = (EditText)activity.findViewById(R.id.txtNumber1);
//        secondNumber = (EditText)activity.findViewById(R.id.txtNumber2);
//        addResult = (TextView)activity.findViewById(R.id.txtResult);
//        btnAdd = (Button) activity.findViewById(R.id.btnAdd);
        tvMail = (TextView)activity.findViewById(R.id.tvProfileMail);
    }

    @UiThreadTest
    public void testProfileMail() {
        String email = firebaseObj.getCurrentUser().getEmail();
        assertEquals(email, tvMail.getText().toString());

    }

    @Test
    public void testPM(){
        String email = firebaseObj.getCurrentUser().getEmail();
        assertEquals(email, tvMail.getText().toString());
    }

}