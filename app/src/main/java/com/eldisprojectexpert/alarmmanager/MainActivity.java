package com.eldisprojectexpert.alarmmanager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.eldisprojectexpert.alarmmanager.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String NOTIFICATION_CHANNEL_ID = "primary_notification_channel";
    ActivityMainBinding binding;
    String toastMessage;
    private NotificationManager notificationManager;
    public static final int NOTIFICATION_ID = 0;

    AlarmManager alarmManager;
    PendingIntent notifyPendingIntent;

    boolean isAlarmExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.toggleAlarm.setOnCheckedChangeListener(this);

        //init NotificationManager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotificationChannel();


        //init alarmmanager and fire pendingintent to receiver
        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        isAlarmExist = (PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        notifyPendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        binding.toggleAlarm.setChecked(isAlarmExist);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES; //90000L
        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;


        if (isChecked){
            toastMessage = getString(R.string.toast_message_alarm_on);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);
            }
        } else {
            toastMessage = getString(R.string.toast_message_alarm_off);
            if (alarmManager != null){
                alarmManager.cancel(notifyPendingIntent);
                notificationManager.cancelAll();
            }
        }

        Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();

    }

    private void createNotificationChannel(){
       notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Alarm Manager Notification", NotificationManager.IMPORTANCE_HIGH);
           notificationChannel.enableLights(true);
           notificationChannel.enableVibration(true);
           notificationChannel.setDescription("Notify every 15 m");

           notificationManager.createNotificationChannel(notificationChannel);
       }
    }
}