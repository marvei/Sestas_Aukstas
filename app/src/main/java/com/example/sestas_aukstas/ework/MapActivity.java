package com.example.sestas_aukstas.ework;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.Manifest;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener,
        LocationListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    String locationProvider;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Location currentLocation = null;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int POLYGON_STROKE_WIDTH_PX = 5;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    TextView map_status;
    Context context = this;


    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkLocationServices();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map_status = findViewById(R.id.map_status);
        getLocationPermission();
        this.initializeLocationManager();
    }

    private void checkLocationServices(){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    startActivity(new Intent(MapActivity.this, MainActivity.class));
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        Log.i("called", "Activity --> onResume");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.locationManager.requestLocationUpdates(this.locationProvider, 10000, 1, this);

    }

    @Override
    protected void onPause() {

        super.onPause();

        Log.i("called", "Activity --> onPause");

        this.locationManager.removeUpdates(this);
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


    public void checkIfCurrentLocationInBounds() {
        Log.i("called", "checking");
        if(currentLocation != null && mMap != null) {
            LatLng point = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                    .add(
                            new LatLng(54.905632, 23.965836),
                            new LatLng(54.905882, 23.965836),
                            new LatLng(54.905887, 23.966082),
                            new LatLng(54.905670, 23.966252)));
            if (pointInPolygon(point, polygon)) {
                map_status.setText(R.string.inBounds);
                Log.i("called", "done checking");
            } else {
                map_status.setText(R.string.notInBounds);
                Log.i("called", "done checking");

            }
        }
        //else{getDeviceLocation();}
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Žemėlapis paruoštas.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            //getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

        }
        // Add polygons to indicate work area on the map.
        Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .add(
                        new LatLng(54.905632, 23.965836),
                        new LatLng(54.905882, 23.965836),
                        new LatLng(54.905887, 23.966082),
                        new LatLng(54.905670, 23.966252)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
        polygon.setTag("workArea");
        // Style the polygon.
        stylePolygon(polygon);
        // Position the map's camera to the work area
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(54.905759, 23.965989), 18));
        // Set listeners for click events.
        googleMap.setOnPolygonClickListener(this);


    }

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private void stylePolygon(Polygon polygon) {
        //   String type = "";
        // Get the data object stored with the polygon.
        // if (polygon.getTag() != null) {
        //      type = polygon.getTag().toString();
        //  }
        polygon.setStrokePattern(PATTERN_POLYGON_ALPHA);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(COLOR_GREEN_ARGB);
        polygon.setFillColor(COLOR_PURPLE_ARGB);
    }

    /**
     * Listens for clicks on a polygon.
     * @param polygon The polygon object that the user has clicked.
     */
    @Override
    public void onPolygonClick(Polygon polygon) {
        // Flip the values of the red, green, and blue components of the polygon's color.
        int color = polygon.getStrokeColor() ^ 0x00ffffff;
        polygon.setStrokeColor(color);
        color = polygon.getFillColor() ^ 0x00ffffff;
        polygon.setFillColor(color);

        Toast.makeText(this, "Area type " + polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: premission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: premission granted");
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void initializeLocationManager() {

        //get the location manager
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        //define the location manager criteria
        Criteria criteria = new Criteria();

        this.locationProvider = locationManager.getBestProvider(criteria, false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);

        //initialize the location
        if(location != null) {
            onLocationChanged(location);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("called", "changing location");
        currentLocation = location;
        checkIfCurrentLocationInBounds();
        Log.i("called", "onLocationChanged");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //checkIfCurrentLocationInBounds();
        //getDeviceLocation();
        checkIfCurrentLocationInBounds();
        Log.i("called", "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("called", "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("called", "onProviderDisabled");
        checkLocationServices();
    }
}
