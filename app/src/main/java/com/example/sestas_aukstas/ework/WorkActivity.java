package com.example.sestas_aukstas.ework;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Enpoz on 4/15/2018.
 */

public class WorkActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvWorkTimeStart;
    TextView tvWorkTimeStop;
    TextView tvWorkTimeTotal;
    TextView tvStart;
    TextView tvStop;
    TextView tvTotal;
    TextView tvWorkTimeToday;
    TextView tvToday;
    Button btnWork;
    Date workTimeStart;
    Date workTimeStop;
    Boolean workStarted = false;
    SimpleDateFormat workDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
    long totalWorkTime = 0;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    int intervalNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_page);
        initializeObj();
        btnWork.setOnClickListener(this);
    }

    public void initializeObj() {
        tvWorkTimeStart = findViewById(R.id.tvWorkTimeStart);
        tvWorkTimeStop = findViewById(R.id.tvWorkTimeStop);
        tvWorkTimeTotal = findViewById(R.id.tvWorkTimeTotal);
        btnWork = findViewById(R.id.btnWork);
        btnWork.setBackgroundColor(Color.GREEN);
        tvStart = findViewById(R.id.tvStart);
        tvStop = findViewById(R.id.tvStop);
        tvTotal = findViewById(R.id.tvTotal);
        tvWorkTimeToday = findViewById(R.id.tvWorkTimeToday);
        tvToday = findViewById(R.id.tvToday);
    }





    public void onClick(View v) {
        if (v == btnWork) {
            if(workStarted == false)
            {
                workStarted = true;
                workTimeStart = new Date();
                String datetostr = workDateFormat.format(workTimeStart);
                String workDate = dateFormat.format(workTimeStart);
                String workTime = timeFormat.format(workTimeStart);
                storeDataToDatabase(workDate, workTime, "start");
                Log.d("start = ", datetostr);
                tvStart.setText(datetostr);
                btnWork.setText(R.string.workStop);
                btnWork.setBackgroundColor(Color.RED);
                Toast.makeText(WorkActivity.this, "Darbas pradėtas.", Toast.LENGTH_SHORT).show();
            }

            else
            {
                workStarted = false;
                workTimeStop = new Date();
                String workDate = dateFormat.format(workTimeStop);
                String workTime = timeFormat.format(workTimeStop);
                tvStop.setText(workDateFormat.format(workTimeStop));
                storeDataToDatabase(workDate, workTime, "stop");
                Log.d("stop = ", workDateFormat.format(workTimeStop));
                btnWork.setText(R.string.workStart);
                btnWork.setBackgroundColor(Color.GREEN);
                Toast.makeText(WorkActivity.this, "Darbas baigtas.", Toast.LENGTH_SHORT).show();

                totalTimeToday();
            }



        }

    }

    public void storeDataToDatabase(String date, String time, String caseOf){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String currentUser = mAuth.getCurrentUser().getUid();

        DatabaseReference ref = database.getReference();

        switch(caseOf){
            case "start" :
                ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child(Integer.toString(intervalNumber)).child("Start").setValue(time);
                break;
            case "stop" :
                ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child(Integer.toString(intervalNumber)).child("Stop").setValue(time);
                intervalNumber++;
                break;
            case "total" :
                ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child("Total today").setValue(time);
                break;
            default :
                Toast.makeText(WorkActivity.this, "Įvyko klaida", Toast.LENGTH_SHORT).show();
        }
    }


    public void totalTimePrint(){
        long elapsedSeconds = totalWorkTime / 1000 % 60;
        long elapsedMinutes = totalWorkTime / (60 * 1000) % 60;
        long elapsedHours = totalWorkTime / (60 * 60 * 1000) % 24;
        tvTotal.setText(elapsedHours + " valandų, " + elapsedMinutes + " minučių, " + elapsedSeconds + " sekundžių.");
        String workDate = dateFormat.format(new Date());

      //  String elapsedTotal = String.format(Long.toString(elapsedHours) + ":" + Long.toString(elapsedMinutes) + ":" + Long.toString(elapsedSeconds));
        String elapsedTotal = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
        storeDataToDatabase(workDate, elapsedTotal, "total");
    }

    public void totalTimeToday(){
        //milliseconds
        long difference = workTimeStop.getTime() - workTimeStart.getTime();
        totalWorkTime+=difference;
        totalTimePrint();

        long elapsedSeconds = difference / 1000 % 60;
        long elapsedMinutes = difference / (60 * 1000) % 60;
        long elapsedHours = difference / (60 * 60 * 1000) % 24;



        tvToday.setText(elapsedHours + " valandų, " + elapsedMinutes + " minučių, " + elapsedSeconds + " sekundžių.");

    }
}
