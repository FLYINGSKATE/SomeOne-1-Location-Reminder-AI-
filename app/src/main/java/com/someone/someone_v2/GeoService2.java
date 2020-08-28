package com.someone.someone_v2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class GeoService2 extends Service{

    GeoFire geoFire;
    String taskList="NO TASKS";
    String user_id;
    DatabaseReference db1;
    public GeoService2() {
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
                .setContentText("You can stop it by saying SIA to \"cancel my reminders\"")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();



        startForeground(1,notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final double lat=intent.getDoubleExtra("latitude",0.0);
        final double Long=intent.getDoubleExtra("longitude",0.0);
        //Toast.makeText(this,"GEO QUERYING ON :"+String.valueOf(lat)+String.valueOf(Long)+hola,Toast.LENGTH_LONG).show();

        ///GET ALL THE LAT,LNG,RADIUS FOR GEOQUERY FROM INTENT AS YOU HAVE STARTED
        startGeoQuery(lat,Long);
        return START_NOT_STICKY;
    }

    private void startGeoQuery(double lat,double Long) {
        //TRY REMOVING THIS TWO LINES
        DatabaseReference db1=FirebaseDatabase.getInstance().getReference("USER DETAILS/"+user_id+"/Locations");
        geoFire=new GeoFire(db1);
        //////

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat,Long),5f);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //sendNotification("SomeOne|+1",String.format("User you have entered near your %s",key));
                Toast.makeText(getApplicationContext(),"Sending broadcAST",Toast.LENGTH_LONG).show();
                Intent intent2 = new Intent(getApplicationContext(), BRC.class);
                intent2 .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2 .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                String taskList=getTaskList(key);
                intent2.putExtra("value", taskList);
                //intent2.putExtra("value", String.format("User you have entered near your %s",key));
                intent2.putExtra("areaName",key);
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
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    //String keys=datas.getKey();
                    taskList2=taskList2+datas.child("titledoes").getValue().toString();
                    //String name=datas.child("name").getValue().toString();
                }
                taskList=taskList2;

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return taskList;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
