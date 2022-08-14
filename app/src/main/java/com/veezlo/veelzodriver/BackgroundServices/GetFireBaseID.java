package com.veezlo.veelzodriver.BackgroundServices;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.VolleyCallback;

import com.veezlo.veelzodriver.DataStorage.DriverActivtyStatus;
import com.veezlo.veelzodriver.DataStorage.KML;
import com.veezlo.veelzodriver.DataStorage.StoreCampaignData;
import com.veezlo.veelzodriver.DataStorage.StoreTrailStatus;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.DataStorage.storecampaignID;
import com.veezlo.veelzodriver.Driver_Activity.CampaignRequestForDriver;
import com.veezlo.veelzodriver.Driver_Activity.DriverHomeScreen;
import com.veezlo.veelzodriver.Driver_Activity.InstallationCenter;
import com.veezlo.veelzodriver.Driver_Activity.LoginDriver;
import com.veezlo.veelzodriver.Driver_Activity.MainCampaignPage;
import com.veezlo.veelzodriver.Driver_Activity.NotificationForDriver;
import com.veezlo.veelzodriver.Driver_Activity.Push_Notification_Alert;
import com.veezlo.veelzodriver.Driver_Activity.SplashScreen;
import com.veezlo.veelzodriver.Driver_Activity.UploadCampaignImages;
import com.veezlo.veelzodriver.Driver_Activity.ViewCampaignDetails;
import com.veezlo.veelzodriver.R;

import org.json.JSONObject;

import java.util.HashMap;

public class GetFireBaseID extends FirebaseMessagingService {

    Store_User_Data_For_Local_User user;
    StoreTrailStatus storeTrailStatus;
    DriverActivtyStatus driverActivtyStatus;
    ApiCall apiCall;
    StoreCampaignData storeCampaignData;
    storecampaignID storeCamID;
    final public static String ONE_TIME = "onetime";
    KML kml;
    String campID;

    @Override
    public void onNewToken(String token) {
        //Log.e("Basit", "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        user=new Store_User_Data_For_Local_User(getApplicationContext());
        storeTrailStatus=new StoreTrailStatus(getApplicationContext());
        driverActivtyStatus=new DriverActivtyStatus(getApplicationContext());
        apiCall=new ApiCall(getApplicationContext());
        storeCampaignData=new StoreCampaignData(getApplicationContext());
        storeCamID=new storecampaignID(getApplicationContext());
        kml=new KML(getApplicationContext());

        if (remoteMessage.getData().size() > 0) {
            if(remoteMessage.getData().get("title").trim().equals("campaignRequest") && user.checkData() && user.getData()[4].equals("driver") && storeTrailStatus.getData()
            && driverActivtyStatus.getData())
            {
                Log.e("Basit",remoteMessage.getData().get("title"));
                storeCamID.setData(remoteMessage.getData().get("body"));
                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("id",remoteMessage.getData().get("body"));
                hashMap.put("driver",user.getData()[0]);
                apiCall.Insert2(hashMap, "getSingleCampaignData.php", new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        try{
                            JSONObject object=new JSONObject(result);
                            if(object.getString("status").trim().equals("1"))
                            {
                             HelperFunctions.NotificationSound(getApplicationContext());
                              Intent i = new Intent(getApplicationContext(), CampaignRequestForDriver.class);
                                i.putExtra("result",result);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        }
                        catch (Exception e)
                        {
                         Log.e("Basit",e.getMessage());
                        }
                    }
                });
            }
            else if(remoteMessage.getData().get("title").trim().equals("sendCampaignCancelationRequest") && user.checkData() &&
            driverActivtyStatus.getData() && storeCampaignData.getData().trim().equals("null"))
            {
                Intent i = new Intent(getApplicationContext(), DriverHomeScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
            else if(remoteMessage.getData().get("title").trim().equals("sendAskQuestionNotification") && user.checkData())
            {
                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("driverID",user.getData()[0]);
                hashMap.put("userID",remoteMessage.getData().get("body"));
                hashMap.put("date",HelperFunctions.currentDate());
                hashMap.put("status","pending");
                hashMap.put("campID",remoteMessage.getData().get("CampID"));

                apiCall.Insert2(hashMap, "InsertIntoUserAskQuestion.php", new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        if(result.trim().equals("success"))
                        {
                            HelperFunctions.NotificationSound(getApplicationContext());
                            HelperFunctions.ShowNotification(getApplicationContext(),"You have a new campaign update","Your Advertiser is requesting you to share the images of your car.",
                                    NotificationForDriver.class);

                            AlarmManager am=(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(getApplicationContext(), getBroadReceiveWhenResponeTimeExpire.class);
                            intent.putExtra(ONE_TIME, Boolean.FALSE);
                            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                            //After after 5 seconds
                            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 108000 , pi);
                        }
                    }
                });
            }

            else if(remoteMessage.getData().get("title").trim().equals("sendpushdrivernotification") && user.checkData())
            {
                Intent i = new Intent(getApplicationContext(), Push_Notification_Alert.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("title",remoteMessage.getData().get("heading"));
                i.putExtra("message",remoteMessage.getData().get("message"));
                startActivity(i);
            }

            else if(remoteMessage.getData().get("title").trim().equals("grouppushnotificationfromuAdmin") && user.checkData())
            {
               String subject=remoteMessage.getData().get("subject");
               String message= remoteMessage.getData().get("message");
                HelperFunctions.ShowNotification(getApplicationContext(),subject,message,Push_Notification_Alert.class);
            }

            else if(remoteMessage.getData().get("title").trim().equals("installationCenterStartCampaginForYOu") && user.checkData())
            {
                campID=remoteMessage.getData().get("campID");

                try {
                    HashMap<String,String> mm=new HashMap<>();
                    mm.put("userID",user.getData()[0]);
                    JSONObject ob=new JSONObject(storeCampaignData.getData());
                    JSONObject item=ob.getJSONObject("data");
                    mm.put("campID",item.getString("id"));
                    mm.put("state","5");
                    apiCall.Insert2(mm, "changeCamp_Assign_Status.php", new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject res=new JSONObject(result);
                                if (res.getString("status").trim().equals("1")){
                                        Intent i = new Intent(getApplicationContext(), MainCampaignPage.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.putExtra("id",campID);
                                        startActivity(i);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), res.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Error occurred try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                catch (Exception e){
                    Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                }

            }

            else if(remoteMessage.getData().get("title").trim().equals("driverisblocknownotification") && user.checkData())
            {
                HashMap<String,String> logoutHashmap=new HashMap<>();
                logoutHashmap.put("id",user.getData()[0]);
                logoutHashmap.put("type","driver");
                apiCall.Insert(logoutHashmap, "signOutDriver.php", new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        user.Clear();
                        kml.Clear();
                        storeTrailStatus.setData(false);
                        Intent i = new Intent(getApplicationContext(), LoginDriver.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                });
            }

            else if(remoteMessage.getData().get("title").trim().equals("restartcampaignNotIF"))
            {
             StoreTrailStatus trailStatus=new StoreTrailStatus(getApplicationContext());
             trailStatus.setData(false);
             Log.e("Basit","not receive skljsdf lkjsdf");
                Intent i = new Intent(getApplicationContext(), DriverHomeScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                Toast.makeText(this, "Your trail has been restart contact the company", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
