package com.someone.someone_v2;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewTask extends AppCompatActivity {

    TextView titlepage, addtitle, adddesc, adddate;
    EditText titledoes, descdoes, datedoes;
    Button btnSaveTask, btnCancel;
    DatabaseReference reference;
    Integer doesNum = new Random().nextInt();
    String keydoes = Integer.toString(doesNum);
    Spinner locSpinner;
    String user_id;

    RadioGroup rg;
    private List<String> areas2=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        rg=(RadioGroup)findViewById(R.id.radioGroup);
        locSpinner=(Spinner)findViewById(R.id.locSpinner);
        titlepage = findViewById(R.id.titlepage);

        addtitle = findViewById(R.id.addtitle);
        adddesc = findViewById(R.id.adddesc);
        adddate = findViewById(R.id.adddate);

        titledoes = findViewById(R.id.titledoes);
        descdoes = findViewById(R.id.descdoes);
        datedoes = findViewById(R.id.datedoes);

        btnSaveTask = findViewById(R.id.btnSaveTask);
        btnCancel = findViewById(R.id.btnCancel);

        user_id=((HeartClass)this.getApplication()).getUser_id();


        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert data to database
                if(titledoes.getText().toString()!=null||titledoes.getText().toString()==""){
                    reference = FirebaseDatabase.getInstance().getReference().child("USER DETAILS").
                            child(user_id).child("TodayList");
                    DatabaseReference temp=reference.child("Task"+doesNum);
                    temp.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            dataSnapshot.getRef().child("titledoes").setValue(titledoes.getText().toString());
                            dataSnapshot.getRef().child("descdoes").setValue(descdoes.getText().toString());
                            if(datedoes.getVisibility()==View.GONE){
                                dataSnapshot.getRef().child("datedoes").setValue(locSpinner.getSelectedItem().toString());
                            }
                            else{
                                dataSnapshot.getRef().child("datedoes").setValue(datedoes.getText().toString());
                            }
                            dataSnapshot.getRef().child("keydoes").setValue(keydoes);

                            Intent a = new Intent(NewTask.this,today_list.class);
                            startActivity(a);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

                else{
                    Toast.makeText(NewTask.this,user_id+" , I Cannot Add Empty Task",Toast.LENGTH_LONG).show();
                }

            }
        });




        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewTask.this,today_list.class));
            }
        });




        fillLocSpinner();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.locRB:
                        locSpinner.setVisibility(View.VISIBLE);
                        datedoes.setVisibility(View.GONE);

                        break;
                    case R.id.TimeRB:
                        locSpinner.setVisibility(View.GONE);
                        datedoes.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }
        });



        locSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = locSpinner.getSelectedItem().toString();
                if(text=="+ Add New Locations"){
                    startActivity(new Intent(NewTask.this,AddLocation.class));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






    }


    public void fillLocSpinner(){

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("USER DETAILS").child(user_id).child("Locations");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> areas = new ArrayList<String>();

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    String areaName = dataSnapshot1.getKey().toString();
                    areas.add(areaName);
                }

                setHeartClassArea(areas);
                locSpinner= (Spinner) findViewById(R.id.locSpinner);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(NewTask.this, R.layout.myspinneritem, areas);
                areasAdapter.add("+ Add New Locations");
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                locSpinner.setAdapter(areasAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setHeartClassArea(List<String> areas) {
        StringBuilder sb = new StringBuilder();
        for (String s : areas)
        {
            sb.append(s).append(",");
        }
        ((HeartClass)this.getApplication()).setAreas(sb.toString());
    }
}