package com.someone.someone_v2;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.os.Bundle;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class AddLocation extends FragmentActivity implements OnMapReadyCallback,
        LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GeoFire.CompletionListener {

    String user_id;
    private Button addLoc;
    private EditText tV;
    private Location locationNew,tapMarkerLocation;
    private static final float DISPLACEMENT = 5000;
    private LocationRequest mLocationRequest;
    private int locationRequestCode = 1000;
    private GoogleMap mMap;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */


    public Geocoder geocoder;
    private Marker currentMarker,anotherMarker;

    //GeoFire Fence

    DatabaseReference ref;
    GeoFire geoFire;
    private LatLng latLng;

    //Notification
    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private String locString;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startLocationUpdates();
        addLoc=(Button) findViewById(R.id.addLoc);
        tV=(EditText)findViewById(R.id.tV);

        geocoder=new Geocoder(this, Locale.getDefault());

        //GeoFire
        user_id=((HeartClass)this.getApplicationContext()).getUser_id();
        ref= FirebaseDatabase.getInstance().getReference("USER DETAILS/"+user_id+"/Locations");
        geoFire=new GeoFire(ref);

        mContext=getApplicationContext();

    }
    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);




        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }
    public void onLocationChanged(final Location location) {
        // New location has now been determined
        locString="New Place";
        try{
            List<Address> addressList = geocoder.getFromLocation(
                    latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                locString = address.getFeatureName();
            }

        }
        catch(Exception ae){
            locString="New Place";
        }
        Toast.makeText(this, locString, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if(currentMarker!=null){
            currentMarker.remove();
        }

        currentMarker=mMap.addMarker(new MarkerOptions().position(latLng).title("Ashraf\'s Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(400)
                .strokeColor(Color.GRAY)
                .fillColor(Color.argb(70,240, 89, 37))
                .strokeWidth(5.0f));
        addLoc.setEnabled(true);
        locationNew=location;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(checkPermissions()) {
            googleMap.setMyLocationEnabled(true);
            mMap = googleMap;
            MapStyleOptions mapStyleOptions;
            if(isDay()){
                mapStyleOptions=MapStyleOptions.loadRawResourceStyle(this,R.raw.google_style);
            }
            else{
                mapStyleOptions=MapStyleOptions.loadRawResourceStyle(this,R.raw.google_map_night);
            }
            //MapStyleOptions mapStyleOptions=MapStyleOptions.loadRawResourceStyle(this,R.raw.google_style);
            mMap.setMapStyle(mapStyleOptions);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    String locString="New Place";
                    try{
                        List<Address> addressList = geocoder.getFromLocation(
                                latLng.latitude, latLng.longitude, 1);
                        if (addressList != null && addressList.size() > 0) {
                            Address address = addressList.get(0);
                            locString = address.getFeatureName();
                        }

                    }
                    catch(Exception ae){
                        locString="New Place";
                    }

                    MarkerOptions marker = new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title(locString);

                    //mMap.animateCamera(locationC);
                    mMap.clear();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //CameraUpdate locationC = CameraUpdateFactory.newLatLngZoom(
                    //latLng, 15);

                    CameraPosition cameraPosition = new CameraPosition.Builder().
                            target(latLng).
                            tilt(45).
                            zoom(20).
                            bearing(0).
                            build();


                    mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(400)
                            .strokeColor(Color.GRAY)
                            .fillColor(Color.argb(70,240, 89, 37))
                            .strokeWidth(5.0f));
                    if((currentMarker!=null) && (anotherMarker!=null)){
                        currentMarker.remove();
                        anotherMarker.remove();
                    }
                    anotherMarker=mMap.addMarker(marker);


                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),2000,null);


                    mMap.setBuildingsEnabled(true);

                    //Convert LatLng to Location
                    Location location = new Location("Test");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);
                    location.setTime(new Date().getTime()); //Set time as current Date
                    tapMarkerLocation=location;
                    Toast.makeText(AddLocation.this,locString,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sendNotification(String title, String message) {
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(mContext , AddLocation.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(mContext,NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }


    private boolean checkPermissions() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            // already permission granted
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) { }


    @Override
    public void onComplete(String key, DatabaseError error) { }


    public void addLocation(View view) {

        geoFire=new GeoFire(ref);
        String place=tV.getText().toString();
        if(place==null){
            place=locString;
        }
        double lat,Long;
        if(tapMarkerLocation==null){
            geoFire.setLocation(place,new GeoLocation(locationNew.getLatitude(),locationNew.getLongitude()));
            lat=locationNew.getLatitude();
            Long=locationNew.getLongitude();

        }
        else{
            geoFire.setLocation(place,new GeoLocation(tapMarkerLocation.getLatitude(),tapMarkerLocation.getLongitude()));
            lat=tapMarkerLocation.getLatitude();
            Long=tapMarkerLocation.getLongitude();
        }



        //GeoQuery
        DatabaseReference db1=FirebaseDatabase.getInstance().getReference("USER DETAILS/"+user_id+"/Locations");
        geoFire=new GeoFire(db1);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat,Long),0.4f);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                sendNotification("SomeOne|+1",String.format("User you have entered near your %s",key));
                Intent intent2 = new Intent(AddLocation.this, BRC.class);
                intent2 .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2 .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent2.putExtra("value", String.format("No Tasks in %s",key));
                sendBroadcast(intent2);
            }

            @Override
            public void onKeyExited(String key) {
                sendNotification("SomeOne|+1",String.format("User you have exited from your %s boundary",key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                sendNotification("SomeOne|+1",String.format("User you have moving near your %s",key));
            }

            @Override
            public void onGeoQueryReady() { }

            @Override
            public void onGeoQueryError(DatabaseError error) { }
        });
    }

    private boolean isDay(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        /*
        if(timeOfDay >= 0 && timeOfDay < 12){
            Toast.makeText(this, "Good Morning", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            Toast.makeText(this, "Good Afternoon", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            Toast.makeText(this, "Good Evening", Toast.LENGTH_SHORT).show();
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            Toast.makeText(this, "Good Night", Toast.LENGTH_SHORT).show();
        }
        */
         if(timeOfDay>=0 && timeOfDay<16){
             return true;
         }
         else{
             return false;
         }

    }





}






