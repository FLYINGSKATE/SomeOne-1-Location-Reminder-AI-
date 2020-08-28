package com.someone.someone_v2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
public class GetCurrentLocationUpdate extends Service {

    private Location locationNew,tapMarkerLocation;
    private static final float DISPLACEMENT = 5000;
    private LocationRequest mLocationRequest;

    GeoFire geoFire;
    String taskList="NO TASKS";
    String user_id;
    DatabaseReference db1;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    public GetCurrentLocationUpdate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db1= FirebaseDatabase.getInstance().getReference("USER DETAILS/"+user_id+"/Locations");
        geoFire=new GeoFire(db1);
        user_id=((HeartClass)this.getApplicationContext()).getUser_id();
        //Get Reference to GeoQuery Database
        geoFire=new GeoFire(db1);

        Intent notificationIntent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);

        Notification notification=new NotificationCompat.Builder(this, HeartClass.CHANNEL_ID)
                .setContentTitle("Someone is monitoring your location")
                .setContentText("stop it by saying SIA to \"cancel all my reminders\"")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this,"OnStartCommand of GETCURRENTLOCATIONUPDATE Started",Toast.LENGTH_LONG).show();
        startLocationUpdates();
        return START_NOT_STICKY;
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
        locationNew=location;
        Toast.makeText(this,"CURRENT LOCATION ON :"+String.valueOf(location.getLatitude())+String.valueOf(location.getLongitude()),Toast.LENGTH_LONG).show();

        ////////GEOQUERY
        DatabaseReference db1= FirebaseDatabase.getInstance().getReference("USER DETAILS/"+user_id+"/Locations");
        geoFire=new GeoFire(db1);
        //////

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()),0.4f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //sendNotification("SomeOne|+1",String.format("User you have entered near your %s",key));
                //Toast.makeText(getApplicationContext(),"Sending broadcAST",Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(getApplicationContext(), BRC.class);
                intent2 .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2 .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                String taskList=getTaskList(key);
                intent2.putExtra("areaName",key);
                intent2.putExtra("value", taskList);
                //intent2.putExtra("value", String.format("User you have entered near your %s",key));
                sendBroadcast(intent2);
            }

            @Override
            public void onKeyExited(String key) {
                //sendNotification("SomeOne|+1",String.format("User you have exited from your %s boundary",key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //sendNotification("SomeOne|+1",String.format("User you have moving near your %s",key));
            }

            @Override
            public void onGeoQueryReady() {
                Toast.makeText(getApplicationContext(),"on query ready",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

                Toast.makeText(getApplicationContext(),"Database Error",Toast.LENGTH_LONG).show();
            }
        });
    }
    private String getTaskList(String key) {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("USER DETAILS/"+user_id+"/TodayList");
        final Query userQuery = rootRef.orderByChild("datedoes").equalTo(key);

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            public String taskList2="";
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String taskList2="";
                int i=0;
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    //String keys=datas.getKey();
                    i++;
                    taskList2=taskList2+datas.child("titledoes").getValue().toString()+"\n";
                    //String name=datas.child("name").getValue().toString();
                }
                taskList=taskList2;
                Toast.makeText(getApplicationContext(),taskList+"  "+taskList2,Toast.LENGTH_LONG).show();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return taskList;
    }
}
