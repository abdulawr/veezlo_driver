package com.veezlo.veelzodriver.Config;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.veezlo.veelzodriver.R;

import java.io.ByteArrayOutputStream;
import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HelperFunctions {

    public static boolean verify(String val){
        return val.trim().length() > 0 && !val.trim().equals("");
    }

    public static String currentDate()
    {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String formattedDate = df.format(c);
        return formattedDate;
    }


    public static String stringToImage(Bitmap bi)
    {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bi.compress(Bitmap.CompressFormat.JPEG,80,outputStream);
        byte[] imageByte=outputStream.toByteArray();
        String encodeimage= Base64.encodeToString(imageByte,Base64.DEFAULT);
        return  encodeimage;

    }

    public static Boolean CheckGPSStatus(Context context)
    {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            return false;
        }
        else {
            return true;
        }
    }

    public static void ShowLocationAlert(final Context context)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle("Location Services Problem")
                .setMessage("Please enable location services first otherwise the app will not work properly")
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("Close app", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
        builder.show();
    }

    // to check google services version
    public static  Boolean CheckGooglePlayServicesVersionOk(Activity context)
    {
        int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if(available == ConnectionResult.SUCCESS)
        {
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(context,available,9001);
            dialog.show();
            return true;
        }
        else
        {
            Toast.makeText(context, "You can`t make a map request", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }

        return false;
    }

    public static void ShowNotification(Context context,String title,String des,Class names)
    {
        try
        {
            int NOTIFICATION_ID = 234;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String CHANNEL_ID = "show_camp_request";
                CharSequence name = title;
                String Description = des;
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setDescription(Description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.RED);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mChannel.setShowBadge(false);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "show_camp_request")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(des)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.loading);
            Bitmap bitmap= BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.logo);
            builder.setLargeIcon(bitmap);

            if(names != null)
            {
                Intent intent = new Intent(context, names);
                intent.putExtra("title",title);
                intent.putExtra("message",des);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                builder.setContentIntent(pendingIntent);
            }

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
        catch (Exception e)
        {
            Log.e("Basit",e.getMessage());
        }

    }

    public static void NotificationSound(Context context)
    {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getCurrentTime()
    {
        Calendar c = Calendar .getInstance();
        System.out.println("Current time => "+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static void showTimeWarning(final Context context)
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context)
                .setTitle("Veezlo Alert")
                .setCancelable(false)
                .setMessage("Veezlo does not allow ambassadors to operate in the night time")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          System.exit(0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        alertDialog.show();
    }


    public static void KMLimitionAlert(final Context context)
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context)
                .setTitle("Veezlo Alert")
                .setMessage("You have completed your daily allowed campaign Kilometers")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        alertDialog.show();
    }


    public static String getCurrentTimein24Hour()
    {
        Calendar c = Calendar .getInstance();
        System.out.println("Current time => "+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif: all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "null";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b: macBytes) {
                    //res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }
}
