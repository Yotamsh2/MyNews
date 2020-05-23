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
import android.widget.ImageButton;
import android.widget.ImageView;
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
                builder.setCancelable(false);

                builder.setView(dialogView).setPositiveButton("Set Notification", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                        createNotificationChannel();
                        startNotification();
                        //Toast.makeText(MainActivity.this, "Notification saved!", Toast.LENGTH_SHORT).show();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Changes not saved", Toast.LENGTH_SHORT).show();
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

    private void startNotification() {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

/*
        // Create action intent for the button
        Intent snoozeIntent = new Intent(this, MainActivity.class);
        snoozeIntent.setAction(ACTION_SNOOZE);
        snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);
*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_news_white_24dp)
                .setContentTitle("My News")
                .setContentText("Notification set to nclsaknl cknamn skdnla kdnlak aslkn akdn ladnas." + time)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Notification hour set to: " + time))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
/*                .addAction(R.drawable.ic_add_alert_black_24dp, getString(R.string.snooze),
                        snoozePendingIntent)*/;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setLightColor(Color.RED);
            channel.setVibrationPattern(new long[]{100, 200, 400, 500, 200, 300, 400});
            channel.enableLights(true);
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
