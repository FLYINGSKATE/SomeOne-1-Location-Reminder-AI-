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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditTaskDetails extends AppCompatActivity {

    String user_id;
    Button btnUpdate,btnDelete;
    EditText titledoes, descdoes, datedoes;
    DatabaseReference reference,temp;
    private Spinner locSpinner;
    private RadioGroup rg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task_details);

        btnDelete=(Button)findViewById(R.id.btnDelete);
        btnUpdate=(Button)findViewById(R.id.btnSaveTask);

        titledoes=(EditText)findViewById(R.id.titledoes);
        descdoes=(EditText)findViewById(R.id.descdoes);
        datedoes=(EditText)findViewById(R.id.datedoes);
        
        locSpinner=(Spinner)findViewById(R.id.locSpinner);
        rg=(RadioGroup)findViewById(R.id.radioGroup);

        titledoes.setText(getIntent().getStringExtra("titledoes"));
        descdoes.setText(getIntent().getStringExtra("descdoes"));
        datedoes.setText(getIntent().getStringExtra("datedoes"));

        final String keydoes=getIntent().getStringExtra("keydoes");


        user_id=((HeartClass)this.getApplication()).getUser_id();
        reference = FirebaseDatabase.getInstance().getReference().child("USER DETAILS").
                child(user_id).child("TodayList");
        temp=reference.child("Task"+keydoes);

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
                    startActivity(new Intent(EditTaskDetails.this,AddLocation.class));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // insert data to database
                if(titledoes.getText().toString()!=null||titledoes.getText().toString()==""){

                    temp.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            dataSnapshot.getRef().child("titledoes").setValue(titledoes.getText().toString());
                            dataSnapshot.getRef().child("descdoes").setValue(descdoes.getText().toString());
                            dataSnapshot.getRef().child("datedoes").setValue(datedoes.getText().toString());
                            if(datedoes.getVisibility()==View.GONE){
                                dataSnapshot.getRef().child("datedoes").setValue(locSpinner.getSelectedItem().toString());
                            }
                            else{
                                dataSnapshot.getRef().child("datedoes").setValue(datedoes.getText().toString());
                            }
                            Intent a = new Intent(EditTaskDetails.this, DashBoard.class);
                            startActivity(a);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
                else{
                    Toast.makeText(EditTaskDetails.this,"Something went wrong try again",Toast.LENGTH_LONG).show();
                }
            }
        });





    btnDelete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            temp.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(EditTaskDetails.this,"DELETED SUCCESSFULLY",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(EditTaskDetails.this,DashBoard.class));
                    }
                    else{
                        Toast.makeText(EditTaskDetails.this,"CANNOT DELETE , FAILURE OCCUR",Toast.LENGTH_LONG).show();
                    }
                }
            });

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
                locSpinner= (Spinner) findViewById(R.id.locSpinner);
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(EditTaskDetails.this, R.layout.myspinneritem, areas);
                areasAdapter.add("+ Add New Locations");
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                locSpinner.setAdapter(areasAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
