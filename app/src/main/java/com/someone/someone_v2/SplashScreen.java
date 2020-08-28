package com.someone.someone_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {
    String profession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        profession=((HeartClass)this.getApplication()).getProfession();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

                if((firebaseUser!=null)){

                    //Retereive data from firebase here
                    String emailID=firebaseUser.getEmail();
                    emailID=emailID.replace("@gmail.com","");
                    emailID=emailID.replace(".","");
                    emailID=emailID.replace("@","");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("USER DETAILS");
                    reference=reference.child(emailID);


                    Query query = reference.child("profession");
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String show=dataSnapshot.getValue(String.class);
                                Toast.makeText(SplashScreen.this,show,Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    if(!(profession.equals(null))){
                        Intent intent=new Intent(SplashScreen.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }
                else{
                    Intent i=new Intent(SplashScreen.this,InterviewScreen.class);
                    startActivity(i);
                    finish();
                }

            }
        },3000);


    }
}
