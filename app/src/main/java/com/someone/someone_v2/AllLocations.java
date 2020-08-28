package com.someone.someone_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AllLocations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_locations);
    }

    public void addLocationAct(View view) {
        startActivity(new Intent(AllLocations.this,AddLocation.class));
    }
}
