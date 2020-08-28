package com.someone.someone_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pl.droidsonroids.gif.GifImageView;

public class DramaticLoading extends AppCompatActivity {

    LinearLayout dll;
    GifImageView findingGif;
    GifImageView load;
    MediaPlayer mp;
    ProgressBar simpleProgressBar;
    int progress=0;
    DatabaseReference databaseUserDetails;
    GoogleSignInAccount acct;
    String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dramatic_loading);
        dll = (LinearLayout) findViewById(R.id.bg);
        findingGif = (GifImageView) findViewById(R.id.findingGIF);
        load = (GifImageView) findViewById(R.id.load);
        simpleProgressBar =(ProgressBar)findViewById(R.id.simplePB);
        mp = MediaPlayer.create(DramaticLoading.this, R.raw.finding_sound);
        mp.start();
        acct= GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        emailId=acct.getEmail();
        databaseUserDetails= FirebaseDatabase.getInstance().getReference("USER DETAILS");
        addUSER();



    }


    @Override
    protected void onStart() {
        super.onStart();
        ColorDrawable[] colorDrawables = {new ColorDrawable(getResources().getColor(R.color.colorOrange)),
                new ColorDrawable(getResources().getColor(R.color.newOrange)), new ColorDrawable(getResources().getColor(R.color.newOrange))};
        TransitionDrawable transitionDrawable = new TransitionDrawable(colorDrawables);
        dll.setBackground(transitionDrawable);
        transitionDrawable.startTransition(3000);

        final Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                load.setImageDrawable(null);

            }
        }, 3000);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.newOrange));
        setProgressValue(progress);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findingGif.setVisibility(View.VISIBLE);

            }
        }, 5700);

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startActivity(new Intent(DramaticLoading.this,MainActivity.class));
            }
        });



    }

    private void setProgressValue(final int progress) {

        // set the progress
        simpleProgressBar.setProgress(progress);
        // thread is used to change the progress value
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setProgressValue(progress + 10);
            }
        });
        thread.start();
    }


    private void addUSER() {
        emailId=emailId.replace("@gmail.com","");
        emailId=emailId.replace(".","");
        emailId=emailId.replace("@","");
        ((HeartClass)this.getApplication()).setUser_id(emailId);
        UserDetails ud=new UserDetails(emailId,((HeartClass)this.getApplication()).getUserName(), ((HeartClass)this.getApplication()).isExercise(), ((HeartClass)this.getApplication()).getProfession());
        databaseUserDetails.child(emailId).setValue(ud);
    }
}
