package com.veezlo.veelzodriver.Driver_Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.BaseURL;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.DataStorage.CampaginInstallationTotalTime;
import com.veezlo.veelzodriver.DataStorage.DriverActivtyStatus;
import com.veezlo.veelzodriver.DataStorage.StoreCampaignData;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.DataStorage.storecampaignID;
import com.veezlo.veelzodriver.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class ViewCampaignDetails extends AppCompatActivity {

    StoreCampaignData storeCampaignData;
    String campaignData="null";
    LinearLayout imagesLinearyout;
    CampaginInstallationTotalTime campaginInstallationTotalTime;
    DriverActivtyStatus driverActivtyStatus;
    storecampaignID camID;
    Store_User_Data_For_Local_User userData;
    ApiCall apiCall;
    TextView id,name,status,totalKM,timertextview;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_campaign_details);

        init();
        campaignData=storeCampaignData.getData();
        if(!campaignData.trim().equals("null"))
        {
            try {
                JSONObject object=new JSONObject(campaignData);
                JSONArray images=object.getJSONArray("images");
                JSONObject data=object.getJSONObject("data");

                Resources r = getResources();
                float px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        180,
                        r.getDisplayMetrics()
                );

                for (int i=0; i<images.length(); i++)
                {
                    JSONObject image=images.getJSONObject(i);

                    ImageView imageView=new ImageView(ViewCampaignDetails.this);
                    LinearLayout.LayoutParams imageheigh=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) px);
                    imageheigh.setMargins(0,0,0,25);
                    imageView.setLayoutParams(imageheigh);
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.requestLayout();

                    imagesLinearyout.addView(imageView);

                    Picasso.get().load(BaseURL.CompaignImagePath()+image.getString("url")).into(imageView);
                }

                id.setText(data.getString("id"));
                name.setText(data.getString("name"));
                status.setText(data.getString("status"));
                totalKM.setText(data.getString("totalKilometer")+" KM");

                int totalprice=Integer.parseInt(data.getString("totalExpenses"));
                int numberOfCar=Integer.parseInt(data.getString("numerOfcars"));
                int totalPrice= totalprice / numberOfCar;

            }
            catch (Exception e)
            {
                Toast.makeText(this, "Something went try again", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if(!camID.getData().trim().equals("null"))
            {
                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("id",camID.getData());
                hashMap.put("driver",userData.getData()[0]);
                apiCall.Insert2(hashMap, "getSingleCampaignData.php", new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject object=new JSONObject(result);
                            if(object.getString("status").trim().equals("1"))
                            {
                                JSONArray images=object.getJSONArray("images");
                                JSONObject data=object.getJSONObject("data");

                                Resources r = getResources();
                                float px = TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        180,
                                        r.getDisplayMetrics()
                                );

                                for (int i=0; i<images.length(); i++)
                                {
                                    JSONObject image=images.getJSONObject(i);

                                    ImageView imageView=new ImageView(ViewCampaignDetails.this);
                                    LinearLayout.LayoutParams imageheigh=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int) px);
                                    imageheigh.setMargins(0,0,0,25);
                                    imageView.setLayoutParams(imageheigh);
                                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    imageView.requestLayout();

                                    imagesLinearyout.addView(imageView);

                                    Picasso.get().load(BaseURL.CompaignImagePath()+image.getString("url")).into(imageView);
                                }

                                id.setText(data.getString("id"));
                                name.setText(data.getString("name"));
                                status.setText(data.getString("status"));
                                totalKM.setText(data.getString("totalKilometer")+" KM");

                                int totalprice=Integer.parseInt(data.getString("totalExpenses"));
                                int numberOfCar=Integer.parseInt(data.getString("numerOfcars"));
                                int totalPrice= totalprice / numberOfCar;

                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(ViewCampaignDetails.this, "Something went try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

    }

    private void init()
    {
        storeCampaignData=new StoreCampaignData(ViewCampaignDetails.this);
        imagesLinearyout=findViewById(R.id.imagesLinearyout);
        id=findViewById(R.id.id);
        name=findViewById(R.id.name);
        status=findViewById(R.id.status);
        totalKM=findViewById(R.id.totalKilometer);
        timertextview=findViewById(R.id.timertextview);
        campaginInstallationTotalTime=new CampaginInstallationTotalTime(ViewCampaignDetails.this);
        driverActivtyStatus=new DriverActivtyStatus(ViewCampaignDetails.this);
        camID=new storecampaignID(ViewCampaignDetails.this);
        userData=new Store_User_Data_For_Local_User(ViewCampaignDetails.this);
        apiCall=new ApiCall(ViewCampaignDetails.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int value;
        if(campaginInstallationTotalTime.getData() > 0)
        {
            value=campaginInstallationTotalTime.getData();
        }
        else {
            value=36000000;
        }

        countDownTimer= new CountDownTimer(value, 10000) {

            public void onTick(long millisUntilFinished) {

                campaginInstallationTotalTime.setData((int) millisUntilFinished);
                double second=(double) millisUntilFinished/1000;
                double hour= (double) ((second / 60) / 60);
                timertextview.setText("You have "+ String.valueOf(Math.round(hour)) +" hours remaining to complete installation");

                if (Math.round(hour) <= 3)
                {
                    timertextview.setTextColor(Color.RED);
                }
                else if(Math.round(hour) <= 5)
                {
                    timertextview.setTextColor(Color.BLUE);
                }
                else {
                    timertextview.setTextColor(Color.parseColor("#4CAF50"));
                }
            }

            public void onFinish() {
               driverActivtyStatus.setData(true);
               campaginInstallationTotalTime.setData(0);
               storeCampaignData.setData("null");
               startActivity(new Intent(getApplicationContext(),DriverHomeScreen.class));
                Toast.makeText(ViewCampaignDetails.this, "Your campagin order has been cancel because you did not complete the installation in the given time", Toast.LENGTH_SHORT).show();
            }
        }.start();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        countDownTimer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    // todo here
    public void StartCampaignButtonClick(View view) {

        try {
            HashMap<String,String> mm=new HashMap<>();
            mm.put("userID",userData.getData()[0]);
            JSONObject ob=new JSONObject(storeCampaignData.getData());
            JSONObject item=ob.getJSONObject("data");
            mm.put("campID",item.getString("id"));
            mm.put("state","3");
            apiCall.Insert(mm, "changeCamp_Assign_Status.php", new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject res=new JSONObject(result);
                        if (res.getString("status").trim().equals("1")){
                            Intent i = new Intent(getApplicationContext(), UploadCampaignImages.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("id",id.getText().toString());
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(ViewCampaignDetails.this, res.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e){
                        Toast.makeText(ViewCampaignDetails.this, "Error occurred try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        //Nothing To Do
    }
}