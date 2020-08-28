package com.someone.someone_v2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;


public class InterviewScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO =2 ;
    private TextToSpeech tts;
    Bundle params;
    TextView textView,hText;

    SpeechRecognizer sr;
    String profession;
    boolean exercise;

    public GifImageView g1;
    Intent intent;

    private int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    // This code can be any value you want, its just a checksum.
    private static final int TTS_REQUEST_CODE = 1234;
    private String OSGender;
    private String userLang;

    /**
     * Called when the activity is first created.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_screen);
        textView=(TextView)findViewById(R.id.tvCareBot);
        LinearLayout ll=(LinearLayout)findViewById(R.id.InterViewL);

        hText=(TextView)findViewById(R.id.tvHumanSays);

        // Intent to listen to user vocal input and return the result to the same activity.
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Use a language model based on free-form speech recognition.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getApplicationContext().getPackageName());

        // Add custom listeners.
        sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();


        checkSelfPermission(Manifest.permission.RECORD_AUDIO);
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant


        }

        ///
        mAuth=FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        g1=(GifImageView)findViewById(R.id.firstgif);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseUser!=null){
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant

                        return;
                    }

                    sr.setRecognitionListener(new RecognitionListener() {
                        @Override
                        public void onBeginningOfSpeech() {

                        }

                        @Override
                        public void onBufferReceived(byte[] buffer) {

                        }

                        @Override
                        public void onEndOfSpeech() {


                        }

                        @Override
                        public void onError(int errorCode) {
                            String errorMessage = getErrorText(errorCode);
                            hText.setText(errorMessage);

                        }

                        @Override
                        public void onEvent(int arg0, Bundle arg1) {
                        }

                        @Override
                        public void onPartialResults(Bundle arg0) {
                        }

                        @Override
                        public void onReadyForSpeech(Bundle arg0) {
                        }

                        @Override
                        public void onResults(Bundle results) {

                            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                            String text = "";
                            for (String result : matches)
                                text += result + "\n";

                            hText.setText(matches.get(0));
                        }

                        @Override
                        public void onRmsChanged(float rmsdB) {
                        }

                    });


                }
                else{
                    signIn();
                }

            }
        });
        // check to TTS
        Intent ttsInstallCheck = new Intent();
        ttsInstallCheck.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        ////

        startActivityForResult(ttsInstallCheck, TTS_REQUEST_CODE);
    }

    /**
     * TextToSpeech.OnInitListener callback is called when the TTS engine has initialised.
     */
    public void onInit(int i)
    {
        Set<String> a=new HashSet<>();
        a.add("male");//here you can give male if you want to select male voice.
        //Voice v=new Voice("en-us-x-sfg#female_2-local",new Locale("en","US"),400,200,true,a);
        Voice v=new Voice("en-us-x-sfg#male_1-local",new Locale("en","IN"),400,200,true,a);
        tts.setVoice(v);
        tts.setSpeechRate(1f);
        // int result = T2S.setLanguage(Locale.US);
        int result = tts.setVoice(v);
        if(result==tts.LANG_MISSING_DATA||result==tts.LANG_NOT_SUPPORTED){
            Toast.makeText(getApplicationContext(),"The Language is not supported",Toast.LENGTH_LONG).show();
        }
        if(firebaseUser!=null){
            Conversation();
        }
        else{
            doSpeak("Someone is overriding your system");
            doSpeak("TAP ANYWHERE TO SIGN IN");
        }

    }

    private void doSpeak(String text)
    {

        params=new Bundle();
        // use the actual text as the key to ID the utterance
        params.putString(text,TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID);
        tts.speak(text, TextToSpeech.QUEUE_ADD, params,text);
        g1.setVisibility(View.VISIBLE);

    }




    /**
     * This is the callback from the TTS engine check, if a TTS is installed we create a new TTS
     * instance (which in turn calls onInit), if not then we will create an intent to go off and
     * install a TTS engine
     *
     * @param requestCode
     *            int Request code returned from the check for TTS engine.
     * @param resultCode
     *            int Result code returned from the check for TTS engine.
     * @param data
     *            Intent Intent returned from the TTS check.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (TTS_REQUEST_CODE == requestCode)
        {
            if (TextToSpeech.Engine.CHECK_VOICE_DATA_PASS == resultCode)
            {
                tts = new TextToSpeech(this, this);
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
                {

                    @Override
                    public void onStart(final String utteranceId)
                    {
                        InterviewScreen.this.runOnUiThread(new Runnable()
                        {
                            public void run()
                            {
                                //Toast.makeText(InterviewScreen.this, utteranceId, Toast.LENGTH_LONG).show();
                                textView.setText(utteranceId);
                                if(utteranceId.equals("Are you a student or Professor or a Co-orporate worker?")){
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getProfessionBySpeech();
                                        }
                                    }, 3000);

                                }
                                else if((utteranceId.contains("Am I right"))){
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            getYesorNo();
                                        }
                                    }, 2000);
                                }
                                else if(utteranceId.contains("Do you exercise ? Gym , Homeworkout etc ?")){
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setExercise();
                                        }
                                    }, 2500);
                                }
                                else if(utteranceId.contains("Do you want a female or male assistant?")){
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setOSGender();
                                        }
                                    }, 2500);
                                }
                                else if(utteranceId.contains("What language would you prefer for your Assistant ?")){
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setUserLanguage();
                                        }
                                    }, 2500);

                                }

                            }
                        });

                    }

                    @Override
                    public void onError(String utteranceId)
                    {
                    }

                    @Override
                    public void onDone(String utteranceId)
                    {
                    }
                });
            }
            else
            {
                // not installed
            }
        }


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(InterviewScreen.this, "Login Failed", Toast.LENGTH_LONG).show();
                // ...
            }
        }
    }

    private void setUserLanguage() {
        sr.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { }

            @Override
            public void onBeginningOfSpeech() { }

            @Override
            public void onRmsChanged(float rmsdB) { }

            @Override
            public void onBufferReceived(byte[] buffer) { }

            @Override
            public void onEndOfSpeech() { }

            @Override
            public void onError(int error) { }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                hText.setText(matches.get(0));
                if(matches.get(0).contains("Hindi")||matches.get(0).contains("hindi")||matches.get(0).contains("Hindi")||matches.get(0).contains("Hindi Bhasha")){
                    userLang="Hindi";
                }
                else if(matches.get(0).contains("English")||matches.get(0).contains("english")||matches.get(0).contains("angrezi")){
                    userLang="English";
                }
                else{
                    userLang="Hindi";
                }
                doSpeak("Do you want a female or male assistant?");

            }

            @Override
            public void onPartialResults(Bundle partialResults) { }

            @Override
            public void onEvent(int eventType, Bundle params) { }
        });
        sr.startListening(intent);

    }

    private void setOSGender() {
        sr.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { }

            @Override
            public void onBeginningOfSpeech() { }

            @Override
            public void onRmsChanged(float rmsdB) { }

            @Override
            public void onBufferReceived(byte[] buffer) { }

            @Override
            public void onEndOfSpeech() { }

            @Override
            public void onError(int error) { }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                hText.setText(matches.get(0));
                if(matches.get(0).contains("Female")||matches.get(0).contains("female")||matches.get(0).contains("girl")||matches.get(0).contains("Girl")||matches.get(0).contains("Lady")||matches.get(0).contains("Woman")||matches.get(0).contains("Ladki")||matches.get(0).contains("ladki")||matches.get(0).contains("aurat")||matches.get(0).contains("ladies")){
                    OSGender="female";
                    beginJulia();
                }
                else if(matches.get(0).contains("Male")||matches.get(0).contains("male")||matches.get(0).contains("man")||matches.get(0).contains("Man")||matches.get(0).contains("Boy")||matches.get(0).contains("boy")||matches.get(0).contains("guy")){
                    OSGender="male";
                    beginJulia();

                }
                else{
                    OSGender="female";
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) { }

            @Override
            public void onEvent(int eventType, Bundle params) { }
        });
        sr.startListening(intent);

    }

    /**
     * Be kind, once you've finished with the TTS engine, shut it down so other applications can use
     * it without us interfering with it :)
     */
    @Override
    public void onDestroy()
    {
        // Don't forget to shutdown!
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    ///Google Sign-in options methods
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(InterviewScreen.this,"Unable to login with google",Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // ...
                    }


                    private void updateUI(FirebaseUser user) {
                        GoogleSignInAccount acct=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        if(acct!=null){
                            Toast.makeText(InterviewScreen.this,"Login as "+acct.getGivenName(),Toast.LENGTH_LONG).show();
                            setUsername(acct.getDisplayName());
                            setProfilePic(acct.getPhotoUrl());
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                            textView.setText("Loading...Please wait / Reopen the app");
                            Conversation();
                        }
                    }
                });

    }

    private void setProfilePic(Uri photoUrl) {
        ((HeartClass)this.getApplication()).setProfilepic(photoUrl.toString());
    }

    public void setUsername(String Uname){
        ((HeartClass)this.getApplication()).setUserName(Uname);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (sr != null) {
            sr.destroy();
        }
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }


    //Permission for microphone
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }

    }

    public void Conversation(){
        doSpeak("Please wait...");
        doSpeak("Hello , "+((HeartClass)this.getApplication()).getUserName());
        doSpeak("Welcome to our unique personal AI assistant service.");
        doSpeak("Before we begin I would like you to answer some personal question.");
        doSpeak("This will help us to find SomeOne that meets your need");
        doSpeak("Are you a student or Professor or a Co-orporate worker?");
    }

    public void getProfessionBySpeech(){
        sr.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { }

            @Override
            public void onBeginningOfSpeech() { }

            @Override
            public void onRmsChanged(float rmsdB) { }

            @Override
            public void onBufferReceived(byte[] buffer) { }

            @Override
            public void onEndOfSpeech() { }

            @Override
            public void onError(int error) { }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                hText.setText(matches.get(0));
                if(matches.get(0).contains("Student")||matches.get(0).contains("student")){
                    profession="Student";
                }
                else if(matches.get(0).contains("professor")||matches.get(0).contains("Professor")){
                    profession="Professor";
                }
                else if(matches.get(0).contains("Co-orporate worker")||matches.get(0).contains("Co-orporate worker")||matches.get(0).contains("worker")||matches.get(0).contains("corporate")){
                    profession="Co-orporate worker";
                }
                else{
                    profession=matches.get(0);
                }
                doSpeak("So you are a "+profession +"? Am I right ?");
            }

            @Override
            public void onPartialResults(Bundle partialResults) { }

            @Override
            public void onEvent(int eventType, Bundle params) { }
        });
        sr.startListening(intent);
    }

    public void getYesorNo(){
        sr.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { }

            @Override
            public void onBeginningOfSpeech() { }

            @Override
            public void onRmsChanged(float rmsdB) { }

            @Override
            public void onBufferReceived(byte[] buffer) { }

            @Override
            public void onEndOfSpeech() { }

            @Override
            public void onError(int error) { }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                hText.setText(matches.get(0));
                if(matches.get(0).contains("YES")||matches.get(0).contains("Yes")||matches.get(0).contains("yeah")||matches.get(0).contains("yes")||matches.get(0).contains("right")||matches.get(0).contains("Right")||matches.get(0).contains("correct")||matches.get(0).contains("Correct")||matches.get(0).contains("absolutely")||matches.get(0).contains("perfect")){
                    doSpeak("Do you exercise ? Gym , Homeworkout etc ?");
                }
                else{
                    profession="";
                    doSpeak("Speak again what is your profession? Student , professor , corporate worker or other");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getProfessionBySpeech();
                        }
                    }, 3000);

                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) { }

            @Override
            public void onEvent(int eventType, Bundle params) { }
        });
        sr.startListening(intent);
    }
    public void setExercise(){
        sr.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { }

            @Override
            public void onBeginningOfSpeech() { }

            @Override
            public void onRmsChanged(float rmsdB) { }

            @Override
            public void onBufferReceived(byte[] buffer) { }

            @Override
            public void onEndOfSpeech() { }

            @Override
            public void onError(int error) { }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                hText.setText(matches.get(0));
                if(matches.get(0).contains("YES")||matches.get(0).contains("Yes")||matches.get(0).contains("yes")||matches.get(0).contains("yeah")||matches.get(0).contains("right")||matches.get(0).contains("Right")||matches.get(0).contains("home workout")||matches.get(0).contains("gym")||matches.get(0).contains("regularly")||matches.get(0).contains("obviously")){
                    exercise=true;

                }
                else if(matches.get(0).contains("haha")||matches.get(0).contains("No")||matches.get(0).contains("no")){
                    exercise=false;

                }
                else{
                    doSpeak("I mean...do you exercise ? ");
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setExercise();
                        }
                    }, 2000);
                }
                doSpeak("What language would you prefer for your Assistant ?");

            }

            @Override
            public void onPartialResults(Bundle partialResults) { }

            @Override
            public void onEvent(int eventType, Bundle params) { }
        });
        sr.startListening(intent);
    }
    public void beginJulia(){
        ((HeartClass)this.getApplication()).setExercise(exercise);
        ((HeartClass)this.getApplication()).setProfession(profession);
        ((HeartClass)this.getApplication()).setOSGender(OSGender);
        ((HeartClass)this.getApplication()).setLanguage(userLang);
        doSpeak("Please wait we are finding a personal assistant for you....");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(InterviewScreen.this,DramaticLoading.class));
                finish();
            }
        }, 4000);

    }




}

