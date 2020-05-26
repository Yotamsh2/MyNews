package com.yotamshoval.mynews;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationService extends Service {
    private static final String CHANNEL_ID = "1";
    private static final String TAG = "NotificationService";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dataRef = database.getReference("news");
    private List<UpdateItem> allItems = new ArrayList<>();
    private List<UpdateItem> sportsItems = new ArrayList<>();
    private List<UpdateItem> healthItems = new ArrayList<>();
    private List<UpdateItem> musicItems = new ArrayList<>();
    private List<UpdateItem> carsItems = new ArrayList<>();
    private String time, category;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "On Create");
        readDatabase();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getBundle(intent);
        createNotificationChannel();
        long t;
        if (time.equals("0.5"))
            t = 3;
        else if (time.equals("1"))
            t = 6;
        else t = 6*15;
        Log.d(TAG, "Start Delay of " + t + "*1000");
        int i = 0;
        while (i < 20) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Run method");
                    startNotification();
                }
            }, t * 10000);
            i++;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Notification", "on Destroy");
    }


    private void readDatabase() {
        //Read the database from firebase
        dataRef.child("updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allItems.clear();
                sportsItems.clear();
                carsItems.clear();
                healthItems.clear();
                musicItems.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UpdateItem item = snapshot.getValue(UpdateItem.class);
                        allItems.add(item);
                        if (item.getItemCategory().equals("Sports"))
                            sportsItems.add((item));
                        if (item.getItemCategory().equals("Health"))
                            healthItems.add((item));
                        if (item.getItemCategory().equals("Cars"))
                            carsItems.add((item));
                        if (item.getItemCategory().equals("Music"))
                            musicItems.add((item));
                    }
                }
                Log.d(TAG, "Read data");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getBundle(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            time = bundle.getString("time");
            category = bundle.getString("category");
        }
        Log.d(TAG, "Get bundle");

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
        List<UpdateItem> currentCategory = allItems;
        switch (category) {
            case "Sports":
                currentCategory = sportsItems;
                break;
            case "Health":
                currentCategory = healthItems;
                break;
            case "Cars":
                currentCategory = carsItems;
                break;
            case "Music":
                currentCategory = musicItems;
                break;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_news_white_24dp)
                .setContentTitle(category + " News: " + currentCategory.get(0).getItemName())
                .setContentText(currentCategory.get(0).getItemDescription())
                /*.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(currentCategory.get(0).getItemDescription()))*/
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
/*                .addAction(R.drawable.ic_add_alert_black_24dp, getString(R.string.snooze),
                        snoozePendingIntent)*/;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        //notificationManager.notify(1, builder.build());
        startForeground(1, builder.build());

        Log.d(TAG, "Start notification");

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
            channel.setVibrationPattern(new long[]{100, 50});
            channel.enableLights(true);
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Create Notification Channel");

        }
    }

}
