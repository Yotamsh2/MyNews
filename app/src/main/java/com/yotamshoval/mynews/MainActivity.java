package com.yotamshoval.mynews;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Time;
import java.util.List;
import java.util.Locale;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class MainActivity extends AppCompatActivity {

//    private static final String WEATHER_TAG = "weather";
    private static final String UPDATES_TAG = "updates";
    private static final String CHANNEL_ID = "1";
    private static final String ACTION_SNOOZE = "Action Snooze";
    private FloatingActionButton fab;
    private TextView timeSetTV;
    public String time;
    private Button cancelNotBtn;
    private RadioGroup radioGroup;

    //Ori
    final String WEATHER_FRAGMENT_TAG = "weather_fragment";
    private FusedLocationProviderClient fusedLocationProviderClient;
    TextView locationTv; //TO DELETE
    private int locationRequestCode = 1000;
    public double wayLatitude = 0.0, wayLongitude = 0.0; //passes to WeatherFragment
    public String town; //passes to WeatherFragment
    public String adminArea;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(1);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_news_white_24dp);
        timeSetTV = findViewById(R.id.timeSetTV);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final View dialogView = getLayoutInflater().inflate(R.layout.notification_dialog, null);

                final TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
                timePicker.setIs24HourView(true);

                final ImageButton iconSport = dialogView.findViewById(R.id.iconSports);
                final ImageButton iconCars = dialogView.findViewById(R.id.iconCars);
                final ImageButton iconHealth = dialogView.findViewById(R.id.iconHealth);
                final ImageButton iconMusic = dialogView.findViewById(R.id.iconMusic);
                final TextView categoryTV = dialogView.findViewById(R.id.categoryTV);
                radioGroup = dialogView.findViewById(R.id.radioGroup);
                cancelNotBtn = dialogView.findViewById(R.id.cancelNotBtn);
                final Intent intent = new Intent(MainActivity.this,NotificationService.class);

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

                    }
                });
                iconHealth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iconCars.setBackgroundColor(Color.WHITE);
                        iconHealth.setBackgroundColor(Color.LTGRAY);
                        iconMusic.setBackgroundColor(Color.WHITE);
                        iconSport.setBackgroundColor(Color.WHITE);
                        categoryTV.setText("Health");

                    }
                });
                iconMusic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iconCars.setBackgroundColor(Color.WHITE);
                        iconHealth.setBackgroundColor(Color.WHITE);
                        iconMusic.setBackgroundColor(Color.LTGRAY);
                        iconSport.setBackgroundColor(Color.WHITE);
                        categoryTV.setText("Music");

                    }
                });
                builder.setCancelable(true);

                builder.setView(dialogView).setPositiveButton("Set Notification", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        RadioButton timeChecked = dialogView.findViewById(radioGroup.getCheckedRadioButtonId());
                        String time = timeChecked.getTag().toString();
/*
                        if (time.equals("0.5"))
                            time = "0.5";
                        else if (time.equals("1 min"))
                            time = "1";
                        else if (time.equals("15 min"))
                            time = "15";
*/
                        if(categoryTV.getText().toString().equals("Choose category"))
                            categoryTV.setText("Sports");
                        intent.putExtra("time", time);
                        intent.putExtra("category",categoryTV.getText().toString());
                        startService(intent);
                        String cat = timeChecked.getText().toString()+" "+categoryTV.getText().toString();
                        timeSetTV.setText(cat);
                        timeSetTV.setVisibility(View.VISIBLE);


/*
                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();

                        if (hour < 10) {
                            if (minute < 10)
                                time = "0" + hour + ":0" + minute;
                            else time = "0" + hour + ":" + minute;
                        } else {
                            if (minute < 10)
                                time = hour + ":0" + minute;
                            else time = hour + ":" + minute;
                        }

                        timeSetTV.setText(time);
*/
/*
                        createNotificationChannel();
                        startNotification();
*/
                        //Toast.makeText(MainActivity.this, "Notification saved!", Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Notifications canceled", Toast.LENGTH_SHORT).show();
                        stopService(intent);
                        timeSetTV.setVisibility(View.GONE);


                    }
                }).show();


            }
        });

        //Ori
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
//                        locationTv.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude)); //shows longitude and latitde
                    }
                }
            }
        };

        // check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // reuqest for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            // already permission granted
            //get location here
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
//                    locationTv.setText(String.format(Locale.US, "%s -- %s", wayLatitude, wayLongitude));

                    //will show the city name with the cordinates gained
                    try {
                        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geo.getFromLocation(wayLatitude, wayLongitude, 10);
                        if (addresses.isEmpty()) {
//                            locationTv.setText("Waiting for Location");
                        } else {
                            if (addresses.size() > 0) {
//                                locationTv.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
//                                Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                                town = addresses.get(0).getLocality();
                                adminArea = addresses.get(0).getAdminArea();
                                Log.d("MainActivity town", "town initialized:  " + town + ", wayLatitude: " + wayLatitude);

                                //starting the Fragment with the needed params.
                                WeatherFragment weatherFragment = WeatherFragment.newInstance(town, adminArea, wayLatitude, wayLongitude);
                                getSupportFragmentManager().beginTransaction()
                                        .add(R.id.ads_container, weatherFragment, WEATHER_FRAGMENT_TAG)
//                                        .addToBackStack(null)
                                        .commit();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace(); // getFromLocation() may sometimes fail
                    }
                }
            });
        }
        //----------------------End of FusedLocation------------------------------------//


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.root_container, new UpdatesFragment(), UPDATES_TAG)
//                .add(R.id.ads_container, new AdvertisementFragment(), WEATHER_TAG).commit();

        transaction.add(R.id.root_container, new UpdatesFragment(), UPDATES_TAG).commit();
//                .add(R.id.ads_container, new WeatherFragment(), WEATHER_TAG)

    }

}
