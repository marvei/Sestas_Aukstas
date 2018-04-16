package com.example.sestas_aukstas.ework;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    long totalWorkTime = 0;

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
                tvStart.setText(datetostr);
                btnWork.setText(R.string.workStop);
                btnWork.setBackgroundColor(Color.RED);
                Toast.makeText(WorkActivity.this, "Darbas pradėtas.", Toast.LENGTH_SHORT).show();
            }

            else
            {
                workStarted = false;
                workTimeStop = new Date();
                tvStop.setText(workDateFormat.format(workTimeStop));
                btnWork.setText(R.string.workStart);
                btnWork.setBackgroundColor(Color.GREEN);
                Toast.makeText(WorkActivity.this, "Darbas baigtas.", Toast.LENGTH_SHORT).show();

                totalTimeToday();
            }



        }

    }

    public void totalTimePrint(){
        long elapsedSeconds = totalWorkTime / 1000 % 60;
        long elapsedMinutes = totalWorkTime / (60 * 1000) % 60;
        long elapsedHours = totalWorkTime / (60 * 60 * 1000) % 24;
        tvTotal.setText(elapsedHours + " valandų, " + elapsedMinutes + " minučių, " + elapsedSeconds + " sekundžių.");
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
