package com.example.sestas_aukstas.ework;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.example.sestas_aukstas.ework.TrackingActivity;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Martynas on 3/21/2018.
 * Modified by Tadas on 4/15/2018.
 * Modified by Tadas on 4/24/2018.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, OnMapReadyCallback {

    FirebaseAuth firebaseObj;
    TextView nav_account;
    TextView nav_work;
    TextView nav_settings;
    TextView nav_logout;
    View nav_acc_divider;
    View nav_work_divider;
    View nav_settings_divider;
    View nav_logout_divider;
    TextView nav_mapTesting;
    View nav_map_divider;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    //Tracking
    private boolean mAlreadyStartedService = false;
    private GoogleMap mMap;
    Context context = this;
    Date workTimeStart;
    Date workTimeStop;
    Boolean workStarted = false;
    SimpleDateFormat workDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
    SimpleDateFormat timePraejo = new SimpleDateFormat("hh:mm:ss");;
    long totalWorkTime = 0;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    int intervalNumber = 1;

    // int intervalas;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initializeObj();
        nav_account.setOnClickListener(this);
        nav_work.setOnClickListener(this);
        nav_settings.setOnClickListener(this);
        nav_logout.setOnClickListener(this);
        nav_mapTesting.setOnClickListener(this);
        nav_account.setOnTouchListener(this);
        nav_work.setOnTouchListener(this);
        nav_settings.setOnTouchListener(this);
        nav_logout.setOnTouchListener(this);
        nav_mapTesting.setOnTouchListener(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationMonitoringService.EXTRA_LONGITUDE);


                        if (latitude != null && longitude != null) {
                            Double lat = Double.valueOf(latitude);
                            Double lon = Double.valueOf(longitude);
                            //mMsgView.setText(getString(R.string.msg_location_service_started) + "\n Latitude : " + latitude + "\n Longitude: " + longitude);
                            checkIfCurrentLocationInBounds(lat, lon);
                            Log.i("callingOnReceive: ", "Latitude: " + latitude + " Longitude: " + longitude);
                        }
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );


    }

    public void initializeObj() {
        firebaseObj = firebaseObj.getInstance();
        firebaseObj = FirebaseAuth.getInstance();
        nav_account = findViewById(R.id.nav_account);
        nav_work = findViewById(R.id.nav_work);
        nav_settings = findViewById(R.id.nav_settings);
        nav_logout = findViewById(R.id.nav_logout);
        nav_mapTesting = findViewById(R.id.nav_mapTesting);
        nav_acc_divider = findViewById(R.id.nav_acc_divider);
        nav_work_divider = findViewById(R.id.nav_work_divider);
        nav_settings_divider = findViewById(R.id.nav_settings_divider);
        nav_logout_divider = findViewById(R.id.nav_logout_divider);
        nav_map_divider = findViewById(R.id.nav_map_divider);
    }

    public void onClick(View v) {
        if (v == nav_account) {
            goToProfile();
        }
        if(v == nav_work)
        {
            goToWork();
        }
        if(v == nav_settings){
            goToSettings();
        }

        if (v == nav_logout) {
            logOut();
        }
        if(v == nav_mapTesting){
            goToMap();
        }
    }


    public void goToProfile(){
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
    }

    public void goToWork(){
        startActivity(new Intent(MainActivity.this, WorkActivity.class));
    }

    public void goToSettings(){
        startActivity(new Intent(MainActivity.this, ProfileSettingsActivity.class)); //create settings activity!!!!!!
    }

    public void logOut(){
        // finish();
        //  firebaseObj.signOut();
        Toast.makeText(MainActivity.this, "Sėkmingai atsijungta", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void goToMap(){startActivity(new Intent(MainActivity.this, TrackingActivity.class));}

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            // reset the background color here
            nav_acc_divider.setBackgroundColor(getResources().getColor(R.color.colorGray));
            nav_work_divider.setBackgroundColor(getResources().getColor(R.color.colorGray));
            nav_settings_divider.setBackgroundColor(getResources().getColor(R.color.colorGray));
            nav_logout_divider.setBackgroundColor(getResources().getColor(R.color.colorGray));
            nav_map_divider.setBackgroundColor(getResources().getColor(R.color.colorGray));

        }else{
            // Change the background color here
            if(v == nav_account)
                nav_acc_divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            if(v == nav_work)
                nav_work_divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            if(v == nav_settings)
                nav_settings_divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            if(v == nav_logout)
                nav_logout_divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            if(v == nav_mapTesting)
                nav_map_divider.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

        return false;
    }

    ////////////////////////////////////////TRACKING///////////////////////////////////////////
    private void checkLocationServices() {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
            });
            dialog.show();
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Žemėlapis paruoštas.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
    }


    @Override
    public void onResume() {
        super.onResume();

        startStep1();
    }

    public boolean pointInPolygon(LatLng point, Polygon polygon) {
        // ray casting alogrithm http://rosettacode.org/wiki/Ray-casting_algorithm
        int crossings = 0;
        List<LatLng> path = polygon.getPoints();
        path.remove(path.size() - 1); //remove the last point that is added automatically by getPoints()

        // for each edge
        for (int i = 0; i < path.size(); i++) {
            LatLng a = path.get(i);
            int j = i + 1;
            //to close the last edge, you have to take the first point of your polygon
            if (j >= path.size()) {
                j = 0;
            }
            LatLng b = path.get(j);
            if (rayCrossesSegment(point, a, b)) {
                crossings++;
            }
        }

        // odd number of crossings?
        return (crossings % 2 == 1);
    }

    public boolean rayCrossesSegment(LatLng point, LatLng a, LatLng b) {
        // Ray Casting algorithm checks, for each segment, if the point is 1) to the left of the segment and 2) not above nor below the segment. If these two conditions are met, it returns true
        double px = point.longitude,
                py = point.latitude,
                ax = a.longitude,
                ay = a.latitude,
                bx = b.longitude,
                by = b.latitude;
        if (ay > by) {
            ax = b.longitude;
            ay = b.latitude;
            bx = a.longitude;
            by = a.latitude;
        }
        // alter longitude to cater for 180 degree crossings
        if (px < 0 || ax < 0 || bx < 0) {
            px += 360;
            ax += 360;
            bx += 360;
        }
        // if the point has the same latitude as a or b, increase slightly py
        if (py == ay || py == by) py += 0.00000001;


        // if the point is above, below or to the right of the segment, it returns false
        if ((py > by || py < ay) || (px > Math.max(ax, bx))) {
            return false;
        }
        // if the point is not above, below or to the right and is to the left, return true
        else if (px < Math.min(ax, bx)) {
            return true;
        }
        // if the two above conditions are not met, you have to compare the slope of segment [a,b] (the red one here) and segment [a,p] (the blue one here) to see if your point is to the left of segment [a,b] or not
        else {
            double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Double.POSITIVE_INFINITY;
            double blue = (ax != px) ? ((py - ay) / (px - ax)) : Double.POSITIVE_INFINITY;
            return (blue >= red);
        }

    }


    public void checkIfCurrentLocationInBounds(Double latitude, Double longitude) {
        if(latitude != null && longitude != null && mMap != null) {
            Log.i("called", "checking");
            LatLng point = new LatLng(latitude, longitude);
            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                    .add(
                            new LatLng(54.905537, 23.965657),
                            new LatLng(54.905612, 23.966515),
                            new LatLng(54.905986, 23.966242),
                            new LatLng(54.905900, 23.965564)));
            if (pointInPolygon(point, polygon)) {
                //map_status.setText(R.string.inBounds);
                Log.i("checkIfInBounds", "done checking. In bounds");
                startWorking();
            } else {
                // map_status.setText(R.string.notInBounds);
                Log.i("checkIfInBounds", "done checking. Not in bounds");
                stopWorking();

            }
        }
        //else{getDeviceLocation();}
    }

    public void startWorking(){
        if(workStarted == false) {
            count = 0;
            workStarted = true;
            workTimeStart = new Date();
            String datetostr = workDateFormat.format(workTimeStart);
            String workDate = dateFormat.format(workTimeStart);
            String workTime = timeFormat.format(workTimeStart);
            // getCounterIfDateExists(workDate);
            // resetCounterIfNewDate(workDate);
            storeDataToDatabase(workDate, workTime, "start");
            Log.d("start = ", datetostr);
            Toast.makeText(MainActivity.this, "Darbas pradėtas.", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopWorking(){
        if(workStarted == true && count == 10) {
            workStarted = false;
            workTimeStop = new Date();
            String workDate = dateFormat.format(workTimeStop);
            String workTime = timeFormat.format(workTimeStop);
            // getCounterIfDateExists(workDate);
            // resetCounterIfNewDate(workDate);
            storeDataToDatabase(workDate, workTime, "stop");
            Log.d("stop = ", workDateFormat.format(workTimeStop));
            Toast.makeText(MainActivity.this, "Darbas baigtas.", Toast.LENGTH_SHORT).show();
            totalTimeToday();
        }
        count++;
    }

    public void stopWorkingOnExit(){
        //count++;
        if(workStarted == true) {
            workStarted = false;
            workTimeStop = new Date();
            String workDate = dateFormat.format(workTimeStop);
            String workTime = timeFormat.format(workTimeStop);
            // getCounterIfDateExists(workDate);
            // resetCounterIfNewDate(workDate);
            storeDataToDatabase(workDate, workTime, "stop");
            Log.d("stop = ", workDateFormat.format(workTimeStop));
            Toast.makeText(MainActivity.this, "Darbas baigtas.", Toast.LENGTH_SHORT).show();
            totalTimeToday();
        }

    }

    public void storeDataToDatabase(String date, String time, String caseOf){
        //  FirebaseDatabase database = FirebaseDatabase.getInstance();
        //   mAuth = FirebaseAuth.getInstance();
        String currentUser = mAuth.getCurrentUser().getUid();

        DatabaseReference ref = firebaseDatabase.getReference();
        //resetIntervalCounter(date, currentUser, ref);

        // intervalNumber = setIntervalCounter(date, currentUser, ref);

        switch(caseOf){
            case "start" :
                //getIntervals(currentUser, date);
                getTotalTimeInMillis();
                ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child("interval_counter").setValue(intervalNumber);
                ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child(Integer.toString(intervalNumber)).child("Start").setValue(time);
//                ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child("interval_counter").setValue(intervalNumber);
//                ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child("Total today").setValue(time);
                //getIntervals(currentUser, date);
                break;
            case "stop" :
                ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child(Integer.toString(intervalNumber)).child("Stop").setValue(time);
                ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child(Integer.toString(intervalNumber)).child("TotalInMillis").setValue(totalTimeStop());
                //intervalNumber++;
                intervalNumber++;
                break;
            case "total" :

                ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child("Total today").setValue(time);
                break;
            default :
                Toast.makeText(MainActivity.this, "Įvyko klaida", Toast.LENGTH_SHORT).show();
        }
    }

    public void getIntervalOnStart(){
        String currentUser = mAuth.getCurrentUser().getUid();
        Date curDate = new Date();
        final Integer[] a = {1};
        String date = dateFormat.format(curDate);
        DatabaseReference ref = firebaseDatabase.getReference();
        DatabaseReference inter = ref.child("users").child(currentUser).child("time_stamps").child(date.toString());
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long x = dataSnapshot.getChildrenCount();
                Integer b = (int) (x);
                if(b >= 3){
                    a[0] = (int) (x-1);

                    Log.i("database", Integer.toString(a[0]));
                }
                else if(b < 3) a[0] = 1;
                Log.i("database", Integer.toString(a[0]));

                intervalNumber = a[0];
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        inter.addListenerForSingleValueEvent(eventListener);
    }

    public void getTotalTimeInMillis(){
        final String currentUser = mAuth.getCurrentUser().getUid();
        final DatabaseReference ref = firebaseDatabase.getReference();
        Date curDate = new Date();
        String date = dateFormat.format(curDate);
        totalWorkTime = 0;

        for(int i = 1; i <= intervalNumber; i++) {
            DatabaseReference inter = ref.child("users").child(currentUser).child("time_stamps").child(date.toString()).child(Integer.toString(i)).child("TotalInMillis");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        long x = (long) dataSnapshot.getValue();
                        totalWorkTime+=x;
                        Log.i("database", Long.toString(x));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            inter.addListenerForSingleValueEvent(eventListener);
        }
    }

    public void totalTimePrint(){
        final String workDate = dateFormat.format(new Date());
                long elapsedSeconds = totalWorkTime / 1000 % 60;
                long elapsedMinutes = totalWorkTime / (60 * 1000) % 60;
                long elapsedHours = totalWorkTime / (60 * 60 * 1000) % 24;
                //String workDate = dateFormat.format(new Date());

                //  String elapsedTotal = String.format(Long.toString(elapsedHours) + ":" + Long.toString(elapsedMinutes) + ":" + Long.toString(elapsedSeconds));
                String elapsedTotal = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
                storeDataToDatabase(workDate, elapsedTotal, "total");

    }

    public void totalTimeToday(){



        //milliseconds
        long difference = workTimeStop.getTime() - workTimeStart.getTime();
        totalWorkTime+=difference;
        totalTimePrint();
    }

    public long totalTimeStop(){
        long difference = workTimeStop.getTime() - workTimeStart.getTime();
        return difference;
    }

    /**
     * Step 1: Check Google Play services
     */
    private void startStep1() {

        //Check whether this user has installed Google play service which is being used by Location updates.
        if (isGooglePlayServicesAvailable()) {

            //Passing null to indicate that it is executing for the first time.
            startStep2(null);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Step 2: Check & Prompt Internet connection
     */
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }


        if (dialog != null) {
            dialog.dismiss();
        }

        //Yes there is active internet connection. Next check Location is granted by user or not.

        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.

            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }

    /**
     * Show A Dialog with button to refresh the internet state.
     */
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        //Block the Application Execution until user grants the permissions
                        if (startStep2(dialog)) {

                            //Now make sure about location permission.
                            if (checkPermissions()) {

                                //Step 2: Start the Location Monitor Service
                                //Everything is there to start the service.

                                startStep3();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }

                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Step 3: Start the Location Monitor Service
     */
    private void startStep3() {

        //And it will be keep running until you close the entire application from task manager.
        //This method will executed only once.
        getIntervalOnStart();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mAlreadyStartedService) {
                    initMap();
                    //mMsgView.setText(R.string.msg_location_service_started);

                    //Start location sharing service to app server.........
                    Intent intent = new Intent(MainActivity.this, LocationMonitoringService.class);
                    startService(intent);

                    mAlreadyStartedService = true;
                    //Ends................................................
                }
            }
        }, 10000);

    }


    /**
     * Return the availability of GooglePlayServices
     */
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
            }
            return false;
        }
        return true;
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);

        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;

    }

    /**
     * Start permissions requests.
     */
    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);

        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION);


        // Provide an additional rationale to the img_user. This would happen if the img_user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the img_user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If img_user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startStep3();

            } else {
                // Permission denied.

                // Notify the img_user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the img_user for permission (device policy or "Never ask
                // again" prompts). Therefore, a img_user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    @Override
    public void onDestroy() {


        //Stop location sharing service to app server.........
        stopWorkingOnExit();
        totalTimePrint();
        stopService(new Intent(this, LocationMonitoringService.class));
        mAlreadyStartedService = false;

        //Ends................................................


        super.onDestroy();
    }
}
