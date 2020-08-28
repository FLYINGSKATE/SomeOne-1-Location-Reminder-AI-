package com.someone.someone_v2;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import android.content.SharedPreferences;

import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class HeartClass extends Application {

    public static final String CHANNEL_ID = "geoServiceChannel";
    private static final String PREFS_NAME = "USERDETAILS_SOMEONE";
    SharedPreferences prefs;
    String user_id;
    String userName;
    boolean exercise;
    String about;
    String language;
    String profilepic;
    String OSGender;
    boolean isFirstTime;
    String areas;
    String profession;


    public String getAreas() {

        return prefs.getString("areas", null);

    }


    public void setAreas(String areas) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("areas");
        editor.putString("areas",areas);
        editor.apply();
        this.areas=areas;
    }

    public String getAbout() {
        return prefs.getString("about", "What\'s on your mind ?");
    }

    public void setAbout(String about) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("about");
        editor.putString("about",about);
        editor.apply();
        this.about = about;
    }

    public String getLanguage() {
        return prefs.getString("language", "hindi");
    }

    public void setLanguage(String language) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("language");
        editor.putString("language",language);
        editor.apply();
        this.language = language;
    }

    public String getProfilepic() {

        return prefs.getString("profilepic", "default_value");
    }

    public void setProfilepic(String profilepic) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("profilepic");
        editor.putString("profilepic",profilepic);
        editor.apply();
        this.profilepic = profilepic;
    }

    public String getOSGender() {
        return prefs.getString("osGender", "female");
    }

    public void setOSGender(String OSGender) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("osGender");
        editor.putString("osGender",OSGender);
        editor.apply();
        this.OSGender = OSGender;
    }


    public void setFirstTime(boolean firstTime) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("isFirstTime");
        editor.putBoolean("isFirstTime",firstTime);
        editor.apply();
        this.isFirstTime = firstTime;
    }

    public boolean isFirstTime() {
        return prefs.getBoolean("isFirstTime", true);
    }



    @Override
    public void onCreate() {
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        createNotificationChannel();
        super.onCreate();
    }

    public String getProfession() {
        //prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("profession", "default_value");

    }

    public void setProfession(String profession) {
        //prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("profession");
        editor.putString("profession",profession);
        editor.apply();
        this.profession = profession;
    }

    public String getUser_id() {
        //prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString("user_id", "default_value");
    }

    public void setUser_id(String user_id) {
        //prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("user_id");
        editor.putString("user_id", user_id);
        editor.apply();
        this.user_id = user_id;
    }


    public boolean isExercise() {
        //prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean("exercise", true);
    }

    public void setExercise(boolean exercise) {
        //prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("exercise");
        editor.putBoolean("exercise",exercise);
        editor.apply();
        this.exercise = exercise;

    }



    public String getUserName() {
        //prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Toast.makeText(getApplicationContext(),"USERNAME SAVED IN SHARED PREFERENCE : "+prefs.getString("userName", "default_value"),Toast.LENGTH_LONG).show();
        return prefs.getString("userName", "default_value");
    }

    public void setUserName(String userName) {
        //prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.putString("userName", userName);
        editor.apply();
        this.userName = userName;
    }



    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel serviceChannel=new NotificationChannel(
                    CHANNEL_ID,"EXAMPLE SERVICE CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(serviceChannel);
        }
    }


}
