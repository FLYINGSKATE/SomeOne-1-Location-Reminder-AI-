package com.someone.someone_v2;

///LAST WORKING COPY
///
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;


import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.Looper;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.TextView;
import android.widget.Toast;


import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;


import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, AIListener, LocationListener {

    public FloatingActionButton fab;
    private static final int TTS_REQUEST_CODE = 41;
    int backButtonCount = 0;
    TextView tv, hsay;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    private TextToSpeech tts;
    Bundle params;



    String OSGender;
    String userLang;
    String user_id;

    Intent intent;

    //MediaPlayer mediaPlayer;
    private AudioPlayer mAudioPlayer;
    BlobVisualizer mVisualizer;
    public String tempDestFile;

    //dialogflow
    AIService aiService;
    private Intent ttsInstallCheck;

    //Location
    private LocationManager locationManager;
    public Location currentLocation;
    private Geocoder geocoder;


    ///NEW LOCATION
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private String globalCommand;
    private boolean isAreaExist;
    private double saveTaskLocationLatitude;
    private double saveTaskLocationLongitude;
    private String globalAreaName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startActivity(new Intent(MainActivity.this,Main3Activity.class));
        tv = (TextView) findViewById(R.id.Welcome);
        hsay = (TextView) findViewById(R.id.hsay);
        globalAreaName=" no area";

        //Get Gender and language
        OSGender = ((HeartClass) this.getApplication()).getOSGender();
        userLang = ((HeartClass) this.getApplication()).getLanguage();
        user_id = ((HeartClass) this.getApplication()).getUser_id();




        ///Visualizer here
        mAudioPlayer = new AudioPlayer();
        fab=(FloatingActionButton)findViewById(R.id.floatingActionButton);
        fab.setEnabled(false);




        /////////TTS HERE

        ///Google dailogFlow listener
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest();
        }
        final AIConfiguration config = new AIConfiguration("53732c629a874d17a880a792d5808ce5",
                AIConfiguration.SupportedLanguages.DEFAULT,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(MainActivity.this, config);
        aiService.setListener(MainActivity.this);




        // check to TTS
        ttsInstallCheck = new Intent();
        ttsInstallCheck.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        ////

        startActivityForResult(ttsInstallCheck, TTS_REQUEST_CODE);

        //GETTING CURRENT LOCATION
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) this);

        startLocationUpdates();



    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

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

    @Override
    public void onLocationChanged(Location location) {
        // New location has now been determined
        currentLocation=location;
    }

    @Override
    public void onRestart() {
        ttsInstallCheck.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        startActivityForResult(ttsInstallCheck, TTS_REQUEST_CODE);


        super.onRestart();
    }


    @Override
    public void onBackPressed() {
        if (backButtonCount >= 1) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {

            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    public void onInit(int i) {
        Set<String> a = new HashSet<>();

        if (OSGender == "female") {
            a.add("female");//here you can give male if you want to select male voice.
        } else {
            a.add("male");
        }


        //Voice v=new Voice("en-us-x-sfg#female_2-local",new Locale("en","US"),400,200,true,a);
        Voice v;
        if (OSGender == "male" && userLang == "English") {
            v = new Voice("en-us-x-sfg#male_1-local", new Locale("en", "US"), 400, 200, true, a);
        } else if (OSGender == "male" && userLang == "Hindi") {
            v = new Voice("en-in-x-sfg#male_1-local", new Locale("en", "IN"), 400, 200, true, a);
        } else if (OSGender == "female" && userLang == "English") {
            v = new Voice("en-us-x-sfg#female_2-local", new Locale("en", "US"), 400, 200, true, a);
        } else {
            v = new Voice("en-in-x-sfg#female_1-local", new Locale("en", "IN"), 400, 200, true, a);
        }

        tts.setVoice(v);
        tts.setSpeechRate(1f);
        // int result = T2S.setLanguage(Locale.US);
        int result = tts.setVoice(v);
        if (result == tts.LANG_MISSING_DATA || result == tts.LANG_NOT_SUPPORTED) {
            Toast.makeText(getApplicationContext(), "The Language is not supported", Toast.LENGTH_LONG).show();
        }

        if (((HeartClass) this.getApplication()).isFirstTime()) {
            doSpeak("Overriding your device...");
            doSpeak("Assalamu alaikum , mien hoon aapki new personal assistant.");
            doSpeak("Kahiye mien aapki kaise seva kar sakti hu?");
            ((HeartClass) this.getApplication()).setFirstTime(false);


        } else {
            doSpeak("Overriding your device");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doSpeak(whatTime());
                    fab.setEnabled(true);
                }
            }, 3000);


        }


    }

    public void doSpeak(String text) {

        params = new Bundle();
        // use the actual text as the key to ID the utterance
        params.putString(text, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
        tts.speak(text, TextToSpeech.QUEUE_ADD, params, text);

        //set text view
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tv.setText(text);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.setEnabled(true);
            }
        }, 2000);
    }


    /**
     * This is the callback from the TTS engine check, if a TTS is installed we create a new TTS
     * instance (which in turn calls onInit), if not then we will create an intent to go off and
     * install a TTS engine
     *
     * @param requestCode int Request code returned from the check for TTS engine.
     * @param resultCode  int Result code returned from the check for TTS engine.
     * @param data        Intent Intent returned from the TTS check.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (TTS_REQUEST_CODE == requestCode) {
            if (TextToSpeech.Engine.CHECK_VOICE_DATA_PASS == resultCode) {
                tts = new TextToSpeech(this, this);
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                    @Override
                    public void onStart(final String utteranceId) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                /*
                                //Toast.makeText(InterviewScreen.this, utteranceId, Toast.LENGTH_LONG).show();
                                tv.setText(utteranceId);
                                String exStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                                Log.d("MainActivity", "exStoragePath : "+exStoragePath);
                                File appTmpPath = new File(exStoragePath + "/sounds/");
                                boolean isDirectoryCreated = appTmpPath.mkdirs();
                                Log.d("MainActivity", "directory "+appTmpPath+" is created : "+isDirectoryCreated);
                                String tempFilename = "tmpaudio.wav";
                                tempDestFile = appTmpPath.getAbsolutePath() + File.separator + tempFilename;
                                Log.d("MainActivity", "tempDestFile : "+tempDestFile);

                                if (utteranceId.equals("Kahiye mien aapki kaise seva kar sakti hu?")) {
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            aiService.startListening();
                                        }
                                    }, 3000);

                                }

                                 */
                            }
                        });

                    }

                    @Override
                    public void onError(String utteranceId) {
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (!(utteranceId.equals("Assalamu alaikum , mien hoon aapki new personal assistant.") || utteranceId.equals("Overriding your device..."))) {
                            aiService.startListening();
                        }
                        fab.setEnabled(true);

                    }
                });
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayingAudio();
        // Don't forget to shutdown!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        startGeoLocationAlarm();

        //startPlayingAudio(R.raw.finding_sound);
    }

    private void startGeoLocationAlarm() {
        alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, GetCurrentLocationUpdate.class);
        pendingIntent = PendingIntent.getService(this, 0, intent, 0);
        ///every 60 seconds
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                60*1000,
                pendingIntent);
        Toast.makeText(this,"Alarm Started",Toast.LENGTH_LONG).show();
    }

    private void stopGeoLocationAlarm(){
        alarmManager.cancel(pendingIntent);
    }

    private void startPlayingAudio(int resId) {
        mAudioPlayer.play(this, resId, new AudioPlayer.AudioPlayerEvent() {
            @Override
            public void onCompleted() {
                if (mVisualizer != null) {
                    //mVisualizer.hide();
                }

            }
        });
        int audioSessionId = mAudioPlayer.getAudioSessionId();
        if (audioSessionId != -1)
            mVisualizer.setAudioSessionId(audioSessionId);
    }

    private void stopPlayingAudio() {
        if (mAudioPlayer != null)
            mAudioPlayer.stop();
        if (mVisualizer != null)
            mVisualizer.release();
    }

    public void takeMetoDashBoard(View view) {
        startActivity(new Intent(MainActivity.this, DashBoard.class));
    }

    //DialogFlow api ai codes
    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {


                } else {

                }
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResult(AIResponse result) {
        Result result1 = result.getResult();
        String command = result1.getResolvedQuery();
        command=command.toLowerCase();
        hsay.setText(result1.getResolvedQuery()); //query




        /////////////////////////////////////TO ADD LOCATIONS via STT////////////////////////////////////////////////////////
        if(command.contains("add this location")||command.contains("add this place")||command.contains("i\'m at")){
            String place="";
            Toast.makeText(this,"Adding the place",Toast.LENGTH_LONG).show();
            if(command.contains("as")||command.contains("it\'s a")||command.contains("it is")){
                //GET PLACE NAME FROM STRING
                place=command.replace("add this location as","");
                place=place.replace("add this place as","");
                place=place.replace("add this location","");
                place=place.replace("add this place","");
                place=place.replace("i\'m at","");
            }
            else{
                //ADD FEATURE NAME OF CURRENT LOCATION AS PLACE NAME
                try{
                    List<Address> addressList = geocoder.getFromLocation(
                            currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        place = address.getFeatureName();
                    }

                }
                catch(Exception ae){
                    place="New Place";
                }


            }
            setCurrentLocation(currentLocation,place);
            doSpeak("New Location Added as  , "+place);
        }
        //TO ADD REMINDER
        else if (command.contains("remind")) {
            /////////////////////////////////////////TIME BASED REMINDER/////////////////////////////////////
            if (command.contains("at")) {

            }
            ////////////////////////////////////////LOCATION BASED REMINDER////////////////////////////////

            else if (command.contains("around") || command.contains("near")) {
                //cHECK IF AREA EXISTS
                checkIsAreaExist(command);
                if (isAreaExist) {
                    Toast.makeText(MainActivity.this, "Areas : " + globalAreaName, Toast.LENGTH_LONG).show();
                    String output = command;
                    output=output.replace(".","");
                    output = output.replace("remind me to", "");
                    output = output.replace("remind to", "");
                    output = output.replace("remind", "");
                    output = output.replace("when I\'m", "");
                    output = output.replace("when", "");
                    output = output.replace("near my", "");
                    output = output.replace("nearby my", "");
                    output = output.replace("nearby", "");
                    output=output.replace("i\'m near","");
                    output = output.replace(globalAreaName, "");
                    doSpeak("Yes I\'ll remind you to " + output + "When you are near " + globalAreaName);
                    saveTask(output, "", globalAreaName);
                    Toast.makeText(MainActivity.this, "TASK SAVED " + globalAreaName, Toast.LENGTH_LONG).show();
                }
                else{
                    doSpeak("Sorry sir cannot set reminder no location found"+globalAreaName);
                }


            }
        }
        else if(command.contains("Who am I")){
            doSpeak("You are "+((HeartClass) this.getApplication()).getUserName());
        }
        else if(command.contains("email")){
            doSpeak("Your Email Id is "+((HeartClass) this.getApplication()).getUser_id()+"@gmail.com");
        }
        else if(command.contains("sia cancel all my reminders")||command.contains("cancel all my reminders")||command.contains("cancel")){
            doSpeak("Okay , Sir Cancelling all your Reminders!");
            stopGeoLocationAlarm();



        }
        else if(command.contains("sia restart reminder")||command.contains("restart")||command.contains("sia reschedule my reminders")||command.contains("sia turn on reminders")||command.contains("reschedule")){
            startGeoLocationAlarm();
            doSpeak("Oh! Okay That needs a little refereshment wait...");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                }
            }, 3000);

        }

        else {
            doSpeak(result1.getFulfillment().getSpeech()); //answer
        }


    }


    //Fetch all the locations name from firebase and check if there is any such place in database and then only add reminders
    private boolean checkIsAreaExist(String command) {
        globalCommand=command;
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("USER DETAILS").child(user_id).child("Locations");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    String areaName = dataSnapshot1.getKey().toString();
                    if(globalCommand.contains(areaName)){
                        isAreaExist=true;
                        //Toast.makeText(getApplicationContext(),"Area does exists"+areaName+isAreaExist,Toast.LENGTH_LONG).show();
                        globalAreaName=areaName;

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return isAreaExist;
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        tv.setText("Hearing......");
        fab.setEnabled(false);
        Animation sgAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink_grow);
        sgAnimation.setRepeatCount(Animation.INFINITE);
        fab.startAnimation(sgAnimation);
    }

    @Override
    public void onListeningCanceled() {
        fab.clearAnimation();

    }

    @Override
    public void onListeningFinished() {
        tv.setText("Thinking....");
        //fab.clearAnimation();
    }

    public void listenToMe(View view) {
        aiService.startListening();
    }

    public void saveTask(final String titledoes, final String descdoes, final String datedoes) {
        //EXTRE WORK NEEDED HERE

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("USER DETAILS/"+user_id+"/Locations"+"/"+datedoes+"/l");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                saveTaskLocationLatitude=(Double)dataSnapshot.child("0").getValue();
                saveTaskLocationLongitude=(Double)dataSnapshot.child("1").getValue();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        //After saving task start a Foreground Service to fire geoQuery to monitor if you have entered the area or not
        //GeoQuery

        final double lat = saveTaskLocationLatitude;
        final double Long = saveTaskLocationLongitude;
        Intent geoServiceIntent =new Intent(this,GeoService2.class);
        geoServiceIntent.putExtra("latitude",lat);
        geoServiceIntent.putExtra("longitude",Long);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(this,"Started Foreground Services",Toast.LENGTH_LONG).show();
            startForegroundService(geoServiceIntent);
        }
        else{
            Toast.makeText(this,"Android Version greater than OREO can only use this Services",Toast.LENGTH_LONG).show();
        }
        ///////////////////////////////////SAVING TASKS IN FIREBASE DATABASE////////////////////////////////////////////////////////


        Integer doesNum = new Random().nextInt();
        final String keydoes = Integer.toString(doesNum);
        // insert data to database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("USER DETAILS").
                child(user_id).child("TodayList");
        DatabaseReference temp = reference.child("Task" + doesNum);
        temp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().child("titledoes").setValue(titledoes);
                dataSnapshot.getRef().child("descdoes").setValue(descdoes);
                dataSnapshot.getRef().child("datedoes").setValue(datedoes);
                dataSnapshot.getRef().child("keydoes").setValue(keydoes);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    ////////////////////////////////////////////////ADD LOCATION ON FIREBASE WHEN PROVIDED WITH ANY LOCATION AND NAME//////////////////////
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setCurrentLocation(Location locationNew, String place) {
        place=place.replace(".","");
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("USER DETAILS/"+user_id+"/Locations");
        GeoFire geoFire = new GeoFire(ref);
        //String place = tV.getText().toString();
        geoFire.setLocation(place, new GeoLocation(locationNew.getLatitude(), locationNew.getLongitude()));
        Toast.makeText(this,"Location added on Firebase",Toast.LENGTH_LONG).show();

        //Fetching location names and saving it in offline heartClass after that we will fetch it whenever require
        final List<String> areas = new ArrayList<String>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String areaName = dataSnapshot1.getKey().toString();
                    areas.add(areaName);
                }
                setHeartClassArea(areas);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private String whatTime(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            return "Good Morning";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            return "Good Afternoon";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            return "Good Evening";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            return "Good Night , I think you should sleep ..."+((HeartClass) this.getApplication()).getUserName();
        }
        else{
            return "Welcome , How are you ? "+user_id;
        }


    }


    private void setHeartClassArea(List<String> areas) {
        StringBuilder sb = new StringBuilder();
        for (String s : areas)
        {
            sb.append(s).append(",");
        }
        ((HeartClass)this.getApplication()).setAreas(sb.toString());
    }


}
