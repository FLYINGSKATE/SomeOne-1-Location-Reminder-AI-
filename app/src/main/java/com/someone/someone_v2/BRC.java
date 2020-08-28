package com.someone.someone_v2;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;



public class BRC extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String newMessage = intent.getExtras().getString("value");
        String areaName=intent.getExtras().getString("areaName");
        Toast.makeText(context, "USER ENTERED", Toast.LENGTH_SHORT).show();
        Intent intent2 = new Intent(context, PoofIAppear.class);
        intent2 .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2 .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent2.putExtra("value", newMessage);
        intent2.putExtra("areaName",areaName);
        if(newMessage==""||newMessage==null||newMessage=="No Tasks"||newMessage.isEmpty()||newMessage=="NO TASKS"){
            //Do Nothing
        }
        else{
            context.startActivity(intent2);
        }

    }
}