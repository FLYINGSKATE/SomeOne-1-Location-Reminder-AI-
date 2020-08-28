package com.someone.someone_v2;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import io.paperdb.Paper;

public class Counter extends AppCompatActivity {

    private static final String TAG ="Counter" ;
    EditText edtTitle,edtDesc;
    DatePicker simpleDatePicker;
    TextView dateDetails;
    //somewhere in your code, init part
    Calendar then,c,now;
    RadioGroup rgCal;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        //Init Paper
        Paper.init(this);

        edtTitle = (EditText) findViewById(R.id.edtTitle);
        dateDetails = (TextView) findViewById(R.id.dateDetails);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        simpleDatePicker = (DatePicker) findViewById(R.id.simpleDatePicker);
        simpleDatePicker.setMinDate(System.currentTimeMillis() - 1000);

        c = Calendar.getInstance();
        now = setDate(c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR));


        Calendar today = Calendar.getInstance();

        simpleDatePicker.init(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view,
                                              int year, int monthOfYear, int dayOfMonth) {
                        dateDetails.setText(
                                "Year: " + year + "\n" +
                                        "Month of Year: " + (int)monthOfYear + 1+ "\n" +
                                        "Day of Month: " + dayOfMonth);

                    }
                });
        rgCal=(RadioGroup)findViewById(R.id.rgCal);
    }

    //method setting days months years - we ignore hours and minutes
    private String getLeftDays(Calendar then, Calendar now) {
        long leftMilis = then.getTimeInMillis() - now.getTimeInMillis();
        int seconds = (int) (leftMilis / 1000);
        Log.d(TAG, "seconds:" + seconds);
        int minutes = seconds / 60;
        Log.d(TAG, "minutes:" + minutes);
        int hours = minutes / 60;
        Log.d(TAG, "hours:" + hours);
        int days = hours / 24;
        Log.d(TAG, "days:" + days);
        int weeks = days / 7;
        Log.d(TAG, "weeks:" + weeks);

        //months.. another way calculating data due not equal amount of days per month
        Calendar temp = ((Calendar) then.clone());
        temp.add(Calendar.MONTH, -now.get(Calendar.MONTH));
        int months = temp.get(Calendar.MONTH);
        Log.d(TAG, "months:" + months);

        StringBuilder sb = new StringBuilder();
        String format = "%d Months %d Days  %d Weeks";
        String formatStr = String.format(format, months, days, weeks);

        String result = sb.append(formatStr).toString();
        Log.d(TAG, sb.toString());

        String newResult;
        int intermsof=rgCal.getCheckedRadioButtonId();
        switch(intermsof){
            case R.id.months:
                newResult=months+" to go";
                return newResult;
            case R.id.days:
                newResult=days+" to go";
                return newResult;
            case R.id.weeks:
                newResult=weeks+" to go";
                return newResult;
            default:
                return result;
        }
    }

    private Calendar setDate(int day, int month, int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Log.d(TAG, c.getTime().toString());
        return c;
    }



    public void applyWidget(View view) {

        int day = simpleDatePicker.getDayOfMonth();
        int month = simpleDatePicker.getMonth();
        int year = simpleDatePicker.getYear();

        then= setDate(day, month, year);
        String remDays=getLeftDays(then,now);



        dateDetails.setText(remDays);


        Paper.book().write("title",edtTitle.getText().toString());
        Paper.book().write("description",edtDesc.getText().toString());
        Paper.book().write("remDays",remDays);

        Toast.makeText(Counter.this,"Saved , Just Long tap on homescreen to your widget",Toast.LENGTH_LONG).show();


    }

    public void goBack2(View view) {
        startActivity(new Intent(this,DashBoard.class));
    }
}
