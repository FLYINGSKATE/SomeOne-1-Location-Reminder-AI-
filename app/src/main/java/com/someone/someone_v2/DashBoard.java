package com.someone.someone_v2;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.someone.someone_v2.adapter.DashboardItemListAdapter;
import com.someone.someone_v2.model.DashBoardListDetails;
import com.someone.someone_v2.model.Model;

import java.util.ArrayList;


public class DashBoard extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Model> models;
    private DashboardItemListAdapter dashboardItemListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        listView=(ListView)findViewById(R.id.list_view);
        models= DashBoardListDetails.getItemList();
        dashboardItemListAdapter=new DashboardItemListAdapter(DashBoard.this,models);
        listView.setAdapter(dashboardItemListAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
    }

    //Dashboard Activity locater button
    public void dynamicMenuButton(View view){
        String btnName=((Button)view).getText().toString();
        switch(btnName){
            case "Today List":
                startActivity(new Intent(DashBoard.this,today_list.class));
                return;
            case "Add Locations":
                startActivity(new Intent(DashBoard.this,AddLocation.class));
                return;
            case "Counter":
                startActivity(new Intent(DashBoard.this,Counter.class));
                return;
            case "Profile":
                startActivity(new Intent(DashBoard.this,profile.class));
                return;
            default:
                return;

        }

    }

    public void goToBack(View view) {
        onBackPressed();
    }
}
