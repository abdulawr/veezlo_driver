package com.veezlo.veelzodriver.Driver_Activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.CheckInternetConnection;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.Loading_AlertDialog;
import com.veezlo.veelzodriver.Config.VolleyCallback;

import com.veezlo.veelzodriver.DataStorage.DriverActivtyStatus;
import com.veezlo.veelzodriver.DataStorage.StoreCampaignData;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.R;

import org.json.JSONObject;

import java.util.HashMap;

public class CampaignRequestForDriver extends AppCompatActivity {

    ApiCall apiCall;
    Store_User_Data_For_Local_User userData;
    String campaignData;
    TextView title,totalKilometer;
    DriverActivtyStatus driverActivtyStatus;
    StoreCampaignData storeCampaignData;
    Loading_AlertDialog loading_alertDialog;
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_request_for_driver);

        init();

        mediaPlayer = MediaPlayer.create(this, R.raw.ring);
        try {
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
        catch (Exception e)
        {
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }


        try {
            campaignData=getIntent().getStringExtra("result");
            try {
                JSONObject object=new JSONObject(campaignData);
                JSONObject data=object.getJSONObject("data");
                title.setText(data.getString("name"));
                totalKilometer.setText(data.getString("totalKilometer")+" KM");

                HashMap<String,String> hMap=new HashMap<>();
                hMap.put("camID",data.getString("id"));
                hMap.put("numberOfCar",data.getString("numerOfcars"));
                apiCall.Insert2(hMap, "ChangeCampaignStatusToActive.php", new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                    }
                });
            }
            catch (Exception e)
            {
                finish();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e)
        {
            Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show();

        }
    }

    //########### to initalize the views #####################
    private void init()
    {
      apiCall=new ApiCall(CampaignRequestForDriver.this);
      userData=new Store_User_Data_For_Local_User(CampaignRequestForDriver.this);
      title=findViewById(R.id.title);
      driverActivtyStatus=new DriverActivtyStatus(CampaignRequestForDriver.this);
      totalKilometer=findViewById(R.id.totalKilometer);
      storeCampaignData=new StoreCampaignData(CampaignRequestForDriver.this);
      loading_alertDialog=new Loading_AlertDialog(CampaignRequestForDriver.this);
    }

   // ############# Run when the campaign request is rejected
    public void DeclineButtonClick(View view) {

        if (CheckInternetConnection.Connection(CampaignRequestForDriver.this))
        {
            HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("id",userData.getData()[0]);
            hashMap.put("name","reject");
            mediaPlayer.stop();
            apiCall.Insert(hashMap, "driverRating.php", new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try{
                        JSONObject object=new JSONObject(result);
                        if(object.getString("status").trim().equals("1"))
                        {
                           Intent intent=new Intent(getApplicationContext(),DriverHomeScreen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(CampaignRequestForDriver.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(CampaignRequestForDriver.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        else {
            Toast.makeText(this, "Please check you internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    // ######### Run when accept campagin button click #############
    public void AcceptCampaign(View view) {
        if (CheckInternetConnection.Connection(CampaignRequestForDriver.this))
        {
            loading_alertDialog.Show();
            mediaPlayer.stop();
            final HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("id",userData.getData()[0]);
            hashMap.put("name","accept");

            apiCall.Insert2(hashMap, "driverRating.php", new VolleyCallback() {
                @Override
                public void onSuccess(final String result) {
                   Log.e("Basit",result);
                    try{
                        JSONObject object=new JSONObject(result);
                        if(object.getString("status").trim().equals("1"))
                        {
                            JSONObject objectss=new JSONObject(campaignData);
                            JSONObject data=objectss.getJSONObject("data");

                            HashMap<String,String> newhashMap=new HashMap<>();
                            newhashMap.put("numberOfcars",data.getString("numerOfcars"));
                            newhashMap.put("CamID",data.getString("id"));
                            newhashMap.put("driverID",userData.getData()[0]);
                            String mac=HelperFunctions.getMacAddr();
                            if(!mac.trim().equals("null") && !mac.trim().equals("02:00:00:00:00:00"))
                            {
                                newhashMap.put("mac",mac);
                            }
                            else {
                                newhashMap.put("mac","null");
                            }
                            apiCall.Insert2(newhashMap, "CheckCampaginAssignData.php", new VolleyCallback() {
                                @Override
                                public void onSuccess(String results) {

                                    if (results.trim().equals("success"))
                                    {
                                        driverActivtyStatus.setData(false);
                                        storeCampaignData.setData(campaignData);
                                        Intent intent=new Intent(getApplicationContext(),InstallationCenter.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        driverActivtyStatus.setData(true);
                                        storeCampaignData.setData("null");
                                    }
                                }
                            });
                            // simple mean the driver is busy and can`t get the campagin request
                        }
                        else {
                            Toast.makeText(CampaignRequestForDriver.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e)
                    {
                        driverActivtyStatus.setData(true);
                        storeCampaignData.setData("null");
                        Toast.makeText(CampaignRequestForDriver.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                    }

                    loading_alertDialog.Hide();

                }
            });
        }
        else {
            Toast.makeText(this, "Please check you internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        //Nothing To Do
    }
}