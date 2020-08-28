package com.someone.someone_v2;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.squareup.picasso.Picasso;

public class profile extends AppCompatActivity {

    TextView userName,userId,myProfession;
    EditText about;
    ImageView profilePic;
    RadioGroup rgProf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myProfession=(TextView)findViewById(R.id.myProfession);
        myProfession.setText(((HeartClass)this.getApplication()).getProfession());

        userId=(TextView)findViewById(R.id.userIDTV);
        userId.setText(((HeartClass)this.getApplication()).getUser_id());

        userName=(TextView)findViewById(R.id.usernNameTV);
        userName.setText(((HeartClass)this.getApplication()).getUserName());

        profilePic=(ImageView)findViewById(R.id.profilePic);
        String imaguri=((HeartClass)this.getApplication()).getProfilepic();
        profilePic.setImageURI(Uri.parse(imaguri));

        about=(EditText)findViewById(R.id.edtAbout);
        about.setText(((HeartClass)this.getApplication()).getAbout());
        rgProf=(RadioGroup)findViewById(R.id.radioGroupWorkout);
        if(((HeartClass)this.getApplication()).isExercise()){
            rgProf.check(R.id.yesButton);
        }
        else{
            rgProf.check(R.id.noButton);
        }

        GoogleSignInAccount acct= GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(acct!=null) {
            //Toast.makeText(this,"Image URI : "+acct.getPhotoUrl().toString(),Toast.LENGTH_LONG).show();
            profilePic.setImageURI(acct.getPhotoUrl());
            Picasso.with(getApplicationContext())
                    .load(acct.getPhotoUrl())
                    .resize(50, 50)
                    .centerCrop()
                    .into(profilePic);
        }

    }

    public void savebtn(View view) {
        ((HeartClass)this.getApplication()).setAbout(about.getText().toString());
    }

    public void goBack(View view) {
        startActivity(new Intent(profile.this,DashBoard.class));
    }
}
