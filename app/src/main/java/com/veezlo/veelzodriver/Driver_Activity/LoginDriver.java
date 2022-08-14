package com.veezlo.veelzodriver.Driver_Activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.Loading_AlertDialog;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.DataStorage.ApiKeY;
import com.veezlo.veelzodriver.DataStorage.DriverActivtyStatus;
import com.veezlo.veelzodriver.DataStorage.StoreCampaignData;
import com.veezlo.veelzodriver.DataStorage.StoreNotificationID;
import com.veezlo.veelzodriver.DataStorage.StoreTotalKM;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.R;


import org.json.JSONObject;

import java.util.HashMap;

public class LoginDriver extends AppCompatActivity {

    EditText mobile, pass1;
    String mobileValue, pass1Value;
    ApiCall apiCall;
    Store_User_Data_For_Local_User userData;
    Button submit;
    Loading_AlertDialog alert;
    StoreNotificationID storeNotificationID;
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private Boolean checklocationpermission = false;
    DriverActivtyStatus driverActivtyStatus;
    ApiKeY apiKeY;
    StoreTotalKM storeTotalKM;
    StoreCampaignData storeCampaignData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_driver);

        init();
        storeTotalKM=new StoreTotalKM(this);
        apiKeY=new ApiKeY(LoginDriver.this);
        storeCampaignData=new StoreCampaignData(this);

        // to initalize the views
        if(!HelperFunctions.CheckGPSStatus(LoginDriver.this))
        {
            HelperFunctions.ShowLocationAlert(LoginDriver.this);
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterNow();
            }
        });
        alert.Show();
        getificationID(new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                storeNotificationID.setData(result);
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if (!Settings.canDrawOverlays(this)) {

                AlertDialog.Builder alert=new AlertDialog.Builder(this)
                        .setTitle("Display Permission Required")
                        .setMessage("Allow us by give display permission to show you campaign request")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, 0);
                            }
                        })
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }
                        });
                alert.show();
            }
        }

    }


    // ######################## To initalize the views #############################
    private void init() {
        apiCall = new ApiCall(LoginDriver.this);
        mobile = findViewById(R.id.mobile);
        pass1 = findViewById(R.id.pass1);
        submit = findViewById(R.id.register);
        alert = new Loading_AlertDialog(LoginDriver.this);
        userData = new Store_User_Data_For_Local_User(LoginDriver.this);
        storeNotificationID = new StoreNotificationID(LoginDriver.this);
        driverActivtyStatus=new DriverActivtyStatus(LoginDriver.this);
    }

    // ######################## To get input fields values #############################
    private void getValues() {
        mobileValue = mobile.getText().toString();
        pass1Value = pass1.getText().toString();
    }

    // ######################## To get register new driver #############################

     /*
        0- User does not have any compaign
        1- redirect it to (Installation Center Screen)
        2- redirect it to (View Campaign Details Screen)
        3- redirect it to (Upload Campaign Images Screen)
        4- redirect it to (Waiting for installation center)
        5- redirect it to (Campaign Main Screen)
        */

    private void RegisterNow() {
        // to get values of the fields
        getValues();
        if (validate()) {
            final HashMap<String, String> map = new HashMap<>();
            map.put("mobile", mobileValue);
            map.put("pass", pass1Value);
            map.put("notID", storeNotificationID.getData());
            map.put("mac",HelperFunctions.getMacAddr());
            apiKeY.setData(storeNotificationID.getData());

                apiCall.Insert(map, "DriverLogin.php", new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {

                            JSONObject mainobj=new JSONObject(result);

                            if (mainobj.getString("status").trim().equals("1")){
                                JSONObject data=mainobj.getJSONObject("data");
                                if (mainobj.getString("camp_state").trim().equals("null")){
                                    JSONObject camp_assign_data=mainobj.getJSONObject("camp_assign_data");

                                    if(camp_assign_data.getString("mac").trim().equals(HelperFunctions.getMacAddr()))
                                    {
                                        userData.setData(data.getString("id"), data.getString("image"), data.getString("email"), data.getString("name"), mainobj.getString("type"),data.getString("cityID"),data.getString("ImagesStatus"));
                                        JSONObject camp_Data=mainobj.getJSONObject("camp_data");
                                        storeCampaignData.setData(camp_Data.toString());

                                        if (camp_assign_data.getString("campaign_state_status").trim().equals("1")){
                                            startActivity(new Intent(getApplicationContext(), InstallationCenter.class));
                                            finish();
                                        }
                                        else  if (camp_assign_data.getString("campaign_state_status").trim().equals("2")){
                                            startActivity(new Intent(getApplicationContext(), ViewCampaignDetails.class));
                                            finish();
                                        }
                                        else  if (camp_assign_data.getString("campaign_state_status").trim().equals("3")){
                                            Intent intent=new Intent(getApplicationContext(), UploadCampaignImages.class);
                                            intent.putExtra("state",false); // ture for (waiting for installation center && false for image uploading)
                                            startActivity(intent);
                                            finish();
                                        }
                                        else  if (camp_assign_data.getString("campaign_state_status").trim().equals("4")){
                                            Intent intent=new Intent(getApplicationContext(), UploadCampaignImages.class);
                                            intent.putExtra("state",true); // ture for (waiting for installation center && false for image uploading)
                                            startActivity(intent);
                                            finish();
                                        }
                                        else  if (camp_assign_data.getString("campaign_state_status").trim().equals("5")){
                                            storeTotalKM.setData(camp_assign_data.getString("km"));
                                            startActivity(new Intent(getApplicationContext(), MainCampaignPage.class));
                                            finish();
                                        }
                                        else{
                                            startActivity(new Intent(getApplicationContext(), DriverHomeScreen.class));
                                            finish();
                                            userData.setData(data.getString("id"), data.getString("image"), data.getString("email"), data.getString("name"), mainobj.getString("type"),data.getString("cityID"),data.getString("ImagesStatus"));
                                            pass1.getText().clear();
                                            mobile.getText().clear();
                                            driverActivtyStatus.setData(true);
                                        }
                                    }
                                    else{
                                        Loading_AlertDialog dialog=new Loading_AlertDialog(LoginDriver.this);
                                        dialog.Show();
                                        // submit request for driver new device added
                                        String os=System.getProperty("os.version"); // OS version
                                        String api = android.os.Build.VERSION.SDK;     // API Level
                                        String model = android.os.Build.MODEL;            // Model
                                        String product = android.os.Build.PRODUCT;
                                        String mac = HelperFunctions.getMacAddr();
                                        String date = HelperFunctions.currentDate();
                                        String time = HelperFunctions.getCurrentTime();
                                        String driverID = data.getString("id");
                                        String camID = camp_assign_data.getString("campaign_id");

                                        Toast.makeText(LoginDriver.this, "Two devices are not allowed to login at same time", Toast.LENGTH_SHORT).show();

                                        final HashMap<String,String> mmp=new HashMap<>();
                                        mmp.put("os",os);
                                        mmp.put("api",api);
                                        mmp.put("model",model);
                                        mmp.put("product",product);
                                        mmp.put("mac",mac);
                                        mmp.put("date",date);
                                        mmp.put("time",time);
                                        mmp.put("driverID",driverID);
                                        mmp.put("campID",camID);
                                        apiCall.Insert2(mmp, "Register_New_Device_Driver_Complain.php", new VolleyCallback() {
                                            @Override
                                            public void onSuccess(String result) {
                                                pass1.getText().clear();
                                              try {
                                                JSONObject object=new JSONObject(result);
                                                if(object.getString("status").trim().equals("1")){
                                                    Toast.makeText(LoginDriver.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    Toast.makeText(LoginDriver.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                                                }
                                              }
                                              catch (Exception e){
                                                  Toast.makeText(LoginDriver.this, "Error occurred in json parsing", Toast.LENGTH_SHORT).show();
                                              }
                                            }
                                        });
                                        dialog.Hide();
                                    }
                                }
                                else{
                                    startActivity(new Intent(getApplicationContext(), DriverHomeScreen.class));
                                    finish();
                                    userData.setData(data.getString("id"), data.getString("image"), data.getString("email"), data.getString("name"), mainobj.getString("type"),data.getString("cityID"),data.getString("ImagesStatus"));
                                    pass1.getText().clear();
                                    mobile.getText().clear();
                                    driverActivtyStatus.setData(true);
                                }
                            }
                            else {
                                pass1.getText().clear();
                                Toast.makeText(LoginDriver.this, mainobj.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(LoginDriver.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        } else {
            Toast.makeText(this, "Fill the form correctly", Toast.LENGTH_SHORT).show();
        }
    }

    // ######################## To validate inputs #############################
    private Boolean validate() {
        if (checkLenght(mobileValue) && checkLenght(pass1Value)) {
            return true;
        } else {
            return false;
        }
    }

    // ######################## To validate single input #############################
    private Boolean checkLenght(String value) {
        if (value.trim().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    // ######################## Use to close the activity #############################
    public void Close(View view) {
        finish();
    }

    private void getificationID(final VolleyCallback callback) {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            callback.onSuccess("null");
                            alert.Hide();
                            finish();
                            Toast.makeText(LoginDriver.this, "Please check you internet connection and try again", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get new Instance ID token
                        callback.onSuccess(task.getResult().getToken());
                        alert.Hide();
                       /* notificationID = task.getResult().getToken();
                        Log.e("Basit",task.getResult().getToken());*/
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
       if(checklocationpermission)
       {
           getLocation();
       }
       else {
           AskPermission();
       }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(checklocationpermission)
        {
            getLocation();
        }
        else {
            AskPermission();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if (!Settings.canDrawOverlays(this)) {

                AlertDialog.Builder alert=new AlertDialog.Builder(this)
                        .setTitle("Display Permission Required")
                        .setMessage("Allow us by give display permission to show you campaign request")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, 0);
                            }
                        })
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }
                        });
                alert.show();
            }
        }
    }

    // Ask permission
    public void AskPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
        String[] permission1 = {Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(LoginDriver.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(LoginDriver.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checklocationpermission = true;
            } else {
                ActivityCompat.requestPermissions(LoginDriver.this, permission, LOCATION_PERMISSION_CODE);
                ActivityCompat.requestPermissions(LoginDriver.this, permission1, LOCATION_PERMISSION_CODE);
                ActivityCompat.requestPermissions(LoginDriver.this,new String[]{Manifest.permission.ACTIVITY_RECOGNITION},LOCATION_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(LoginDriver.this, permission, LOCATION_PERMISSION_CODE);
            ActivityCompat.requestPermissions(LoginDriver.this, permission1, LOCATION_PERMISSION_CODE);
            ActivityCompat.requestPermissions(LoginDriver.this,new String[]{Manifest.permission.ACTIVITY_RECOGNITION},LOCATION_PERMISSION_CODE);
        }
    }

    // result of permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            switch (requestCode) {
                case LOCATION_PERMISSION_CODE: {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            checklocationpermission = false;
                            return;
                        }
                    }
                    checklocationpermission = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getLocation()
    {
        try {
            FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LoginDriver.this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            final Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if(locationTask.isSuccessful() && locationTask.getResult() != null)
                    {
                        Location location=locationTask.getResult();
                       // Toast.makeText(LoginDriver.this, "System get the location successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    public void Pre_Registration(View view) {
        startActivity(new Intent(getApplicationContext(),Driver_Pre_Registration.class));
    }

    public void BecomeAmbassador(View view) {
        startActivity(new Intent(getApplicationContext(),Signup.class));
    }
}