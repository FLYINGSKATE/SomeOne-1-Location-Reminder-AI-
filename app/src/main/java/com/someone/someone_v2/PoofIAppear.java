package com.someone.someone_v2;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PoofIAppear extends AppCompatActivity  {


    String areaName;
    String user_id;
    MediaPlayer mp;
    int count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_id=((HeartClass)this.getApplicationContext()).getUser_id();
        //getWindow().setBackgroundDrawable(new ColorDrawable(0));
        String newMessage = getIntent().getExtras().getString("value","No Tasks");
        areaName=getIntent().getExtras().getString("areaName");


        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogStyle);
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.alert_dialogue_with_image_view, null);
        builder.setTitle("You have following Tasks in "+areaName)
                .setMessage(newMessage)
                .setCancelable(false)
                .setPositiveButton("DO IT NOW",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mp.stop();
                        deleteTasks();
                        dialog.cancel();
                        finish();
                    }
                })
                .setNegativeButton("LATER", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        mp.stop();
                        Toast.makeText(PoofIAppear.this,"Well , Good luck with that attitude",Toast.LENGTH_LONG).show();
                        dialog.cancel();
                        finish();
                    }
                });
        builder.setView(dialogLayout);
        AlertDialog alert = builder.create();
        alert.show();
        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(40);
        mp=MediaPlayer.create(this,R.raw.reminder);

        mp.start();
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(2500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(2500);
        }




    }

    public void deleteTasks(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("USER DETAILS/"+user_id+"/TodayList");
        final Query userQuery = rootRef.orderByChild("datedoes").equalTo(areaName);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot taskSnapshot: dataSnapshot.getChildren()) {
                    taskSnapshot.getRef().removeValue();
                }
                Toast.makeText(PoofIAppear.this,"All Task have been cleared from "+areaName,Toast.LENGTH_LONG).show();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PoofIAppear.this,"Error ccured , While Deleting tasks",Toast.LENGTH_LONG).show();
            }
        });

    }


}
