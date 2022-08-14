package com.veezlo.veelzodriver.Driver_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.CheckInternetConnection;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.DataStorage.StoreCampaignData;
import com.veezlo.veelzodriver.DataStorage.StoreTotalKM;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.R;

import org.json.JSONObject;

import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {

    Store_User_Data_For_Local_User user;
    ApiCall apiCall;
    HashMap<String,String> hashMap;
    StoreCampaignData storeCampaignData;
    StoreTotalKM storeTotalKM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        user=new Store_User_Data_For_Local_User(SplashScreen.this);
        apiCall=new ApiCall(this);
        storeCampaignData=new StoreCampaignData(this);
        storeTotalKM=new StoreTotalKM(this);
        hashMap=new HashMap<>();

        /*
        0- User does not have any compaign
        1- redirect it to (Installation Center Screen)
        2- redirect it to (View Campaign Details Screen)
        3- redirect it to (Upload Campaign Images Screen)
        4- redirect it to (Waiting for installation center)
        5- redirect it to (Campaign Main Screen)
        */

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

               if(user.checkData() && user.getData()[4].trim().equals("driver"))
                {
                    if(CheckInternetConnection.Connection(SplashScreen.this)){
                        hashMap.put("userID",user.getData()[0]);
                        apiCall.Insert(hashMap, "getDriverState_InCompaign.php", new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {

                                try{
                                    JSONObject object=new JSONObject(result);

                                    if (object.getString("status").trim().equals("1")){
                                       if(object.getString("camp_state").trim().equals("0")){
                                        // the use does not have any active campaign
                                           startActivity(new Intent(getApplicationContext(), DriverHomeScreen.class));
                                           finish();
                                       }
                                       else{
                                           // only run if the driver is in currently active campaign
                                         JSONObject data=object.getJSONObject("camp_assign_data");
                                         JSONObject camp_Data=object.getJSONObject("camp_data");
                                         storeCampaignData.setData(camp_Data.toString());
                                         if (data.getString("campaign_state_status").trim().equals("1")){
                                             startActivity(new Intent(getApplicationContext(), InstallationCenter.class));
                                             finish();
                                         }
                                         else  if (data.getString("campaign_state_status").trim().equals("2")){
                                             startActivity(new Intent(getApplicationContext(), ViewCampaignDetails.class));
                                             finish();
                                         }
                                         else  if (data.getString("campaign_state_status").trim().equals("3")){
                                             Intent intent=new Intent(getApplicationContext(), UploadCampaignImages.class);
                                             intent.putExtra("state",false); // ture for (waiting for installation center && false for image uploading)
                                             startActivity(intent);
                                             finish();
                                         }
                                         else  if (data.getString("campaign_state_status").trim().equals("4")){

                                             Intent intent=new Intent(getApplicationContext(), UploadCampaignImages.class);
                                             intent.putExtra("state",true); // ture for (waiting for installation center && false for image uploading)
                                             startActivity(intent);
                                             finish();
                                         }
                                         else  if (data.getString("campaign_state_status").trim().equals("5")){
                                             storeTotalKM.setData(data.getString("km"));
                                             startActivity(new Intent(getApplicationContext(), MainCampaignPage.class));
                                             finish();
                                         }

                                       }
                                    }
                                    else{
                                        finish();
                                        Toast.makeText(SplashScreen.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                        // User verification Failed
                                    }

                                }
                                catch (Exception e){
                                    Log.e("Basit",e.getMessage());
                                    Toast.makeText(SplashScreen.this, "Error occurred in Json parsing!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                    else{
                        // If the user does not have internet connection the app will be autometically close
                        Toast.makeText(SplashScreen.this, "Check you internet connection!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
                else {
                   startActivity(new Intent(getApplicationContext(),LoginDriver.class));
                    finish();
                }


            }
        },100);
    }

}