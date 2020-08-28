package com.someone.someone_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.someone.someone_v2.adapter.DashboardItemListAdapter;
import com.someone.someone_v2.model.JournalListDetails;
import com.someone.someone_v2.model.Model;

import java.util.ArrayList;

public class JournalActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Model> models;
    private DashboardItemListAdapter dashboardItemListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        listView=(ListView)findViewById(R.id.list_view2);
        models= JournalListDetails.getItemList();
        dashboardItemListAdapter=new DashboardItemListAdapter(JournalActivity.this,models);
        listView.setAdapter(dashboardItemListAdapter);

    }

    //Dashboard Activity locater button
    public void dynamicMenuButton(View view){
        String btnName=((Button)view).getText().toString();
        switch(btnName){
            case "Today List":
                startActivity(new Intent(JournalActivity.this,today_list.class));
                return;
            case "Routine List":
                startActivity(new Intent(JournalActivity.this,AllLocations.class));
                return;
            case "Workout List":
                startActivity(new Intent(JournalActivity.this,AllLocations.class));
                return;
            case "Bucket List":
                startActivity(new Intent(JournalActivity.this,AllLocations.class));
                return;
            default:
                return;

        }

    }

}
