package com.veezlo.veelzodriver.BackgroundServices;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.veezlo.veelzodriver.DataStorage.NotificationStatus;
import com.veezlo.veelzodriver.Driver_Activity.CampaignRequestForDriver;
import com.veezlo.veelzodriver.Driver_Activity.NotificationForDriver;

public class getBroadReceiveWhenResponeTimeExpire extends BroadcastReceiver {

    NotificationStatus notificationStatus;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationStatus=new NotificationStatus(context);

        notificationStatus.setData(false);
        Intent intents = new Intent(context, getBroadReceiveWhenResponeTimeExpire.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intents, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

        Intent intentone = new Intent(context.getApplicationContext(), NotificationForDriver.class);
        intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentone);


        Toast.makeText(context, "Time Out for response and your campaign is now stop response and campaign will start automatically", Toast.LENGTH_SHORT).show();

    }
}
