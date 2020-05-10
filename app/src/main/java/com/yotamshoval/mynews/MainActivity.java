package com.yotamshoval.mynews;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Time;

public class MainActivity extends AppCompatActivity {

    private static final String WEATHER_TAG = "weather";
    private static final String UPDATES_TAG = "updates";
    private FloatingActionButton fab;
    private TextView timeSetTV;
    public String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_news_white_24dp);
        timeSetTV = findViewById(R.id.timeSetTV);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.notification_dialog, null);

                final TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
                timePicker.setIs24HourView(true);
/*
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                        time = i+":"+i1;
                    }
                });
*/

                final ImageButton iconSport = dialogView.findViewById(R.id.iconSports);
                final ImageButton iconCars = dialogView.findViewById(R.id.iconCars);
                final ImageButton iconHealth = dialogView.findViewById(R.id.iconHealth);
                final ImageButton iconMusic = dialogView.findViewById(R.id.iconMusic);
                final TextView categoryTV = dialogView.findViewById(R.id.categoryTV);


                iconCars.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iconCars.setBackgroundColor(Color.LTGRAY);
                        iconHealth.setBackgroundColor(Color.WHITE);
                        iconMusic.setBackgroundColor(Color.WHITE);
                        iconSport.setBackgroundColor(Color.WHITE);
                        categoryTV.setText("Cars");
                    }
                });
                iconSport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iconCars.setBackgroundColor(Color.WHITE);
                        iconHealth.setBackgroundColor(Color.WHITE);
                        iconMusic.setBackgroundColor(Color.WHITE);
                        iconSport.setBackgroundColor(Color.LTGRAY);
                        categoryTV.setText("Sports");

                    }});
                iconHealth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iconCars.setBackgroundColor(Color.WHITE);
                        iconHealth.setBackgroundColor(Color.LTGRAY);
                        iconMusic.setBackgroundColor(Color.WHITE);
                        iconSport.setBackgroundColor(Color.WHITE);
                        categoryTV.setText("Health");

                    }});
                iconMusic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iconCars.setBackgroundColor(Color.WHITE);
                        iconHealth.setBackgroundColor(Color.WHITE);
                        iconMusic.setBackgroundColor(Color.LTGRAY);
                        iconSport.setBackgroundColor(Color.WHITE);
                        categoryTV.setText("Music");

                    }});
                builder.setCancelable(false);

                builder.setView(dialogView).setPositiveButton("Set Notification", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();
                        String time;

                        if(hour<10){
                            if(minute<10)
                                time = "0"+hour+":0"+minute;
                            else time = "0"+hour+":"+minute;
                        }else {
                            if(minute<10)
                                time = hour+":0"+minute;
                            else time = hour+":"+minute;
                        }

                        timeSetTV.setText(time);
                        Toast.makeText(MainActivity.this, "Notification saved!", Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Changes not saved", Toast.LENGTH_SHORT).show();
                    }
                }).show();


            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.root_container,new UpdatesFragment(),UPDATES_TAG)
                .add(R.id.ads_container,new AdvertisementFragment(),WEATHER_TAG).commit();

    }
}
