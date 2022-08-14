package com.veezlo.veelzodriver.Driver_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.CheckInternetConnection;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.CurrentLocation.GPSLocation;

import com.veezlo.veelzodriver.DataStorage.DB_Helper;
import com.veezlo.veelzodriver.DataStorage.DriverActivtyStatus;
import com.veezlo.veelzodriver.DataStorage.KML;
import com.veezlo.veelzodriver.DataStorage.NotificationStatus;
import com.veezlo.veelzodriver.DataStorage.StoreCampaignData;
import com.veezlo.veelzodriver.DataStorage.StoreImageStatus;
import com.veezlo.veelzodriver.DataStorage.StoreTotalKM;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.DataStorage.storecampaignID;
import com.veezlo.veelzodriver.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.MalformedInputException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainCampaignPage extends AppCompatActivity implements OnMapReadyCallback {

    StoreCampaignData storeCampaignData;
    DriverActivtyStatus driverActivtyStatus;
    StoreTotalKM storeTotalKM;
    String campaignData = "null";
    TextView CampaignKM, youKm,km_in_picture;
    Store_User_Data_For_Local_User userData;
    ApiCall apiCall;
    StoreImageStatus imageStatus;
    GoogleMap map;
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private Boolean checklocationpermission = false;
    private static final float DEFAULT_ZOOM = 15f;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location location; // location
    LatLng oldLat, newLat, start;
    Marker marker = null;
    String lat = "0.0", longs = "0.0";
    Runnable runnable123,runnable12;
    Handler handler123;
    LocationRequest locationRequest;
    DB_Helper helper;
    JSONObject data;
    NotificationStatus notificationStatus;
    storecampaignID camID;
    boolean timeConstain=true;
    String allowkm,startTime,endTime;
    double localKm;
    FrameLayout fram_layout;
    KML kml;
    List<LatLng> listOfLocation;
    LinearLayout error_layout;
    Handler handler12;
    SharedPreferences prefs;
    Button gotobackground;
    boolean checkmode=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_campaign_page);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // to initialize
        init();
        km_in_picture=findViewById(R.id.km_in_picture);
        fram_layout=findViewById(R.id.fram_layout);
        // get map permission
        AskPermission();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (!prefs.getBoolean("Campaing_Active_Change_Status", false)) {
            // <---- run your one time code here
            try {
                JSONObject object = new JSONObject(storeCampaignData.getData());
                data = object.getJSONObject("data");

                HashMap<String,String> ms=new HashMap<>();
                ms.put("driverID",userData.getData()[0]);
                ms.put("campID",data.getString("id"));

                apiCall.Insert2(ms, "ChangeCampaignDriverState.php", new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                     if (result=="1"){
                         // the job is done successfully
                         // mark first time has ran.
                         SharedPreferences.Editor editor = prefs.edit();
                         editor.putBoolean("ChangeCampaignDriverState", true);
                         editor.commit();
                     }
                    }
                });

            } catch (Exception e) {
                Toast.makeText(this, "Error Occurred in Json Parsing", Toast.LENGTH_SHORT).show();
            }
        }

        //helper.updateKMValue(10,HelperFunctions.currentDate());

        // To check and get constrain data from server for ambassador
        DownloadAmbassadorConstrainData();
        //update campain location
        UpdateCampaginLocation();
        imageStatus=new StoreImageStatus(this);
        // Check time and km limit
        checkveryTwoMinute();

        // to check the tail status
        youKm.setText(storeTotalKM.getData());
        km_in_picture.setText(storeTotalKM.getData());
        campaignData = storeCampaignData.getData();
        if (!campaignData.trim().equals("null")) {
            try {
                JSONObject object = new JSONObject(campaignData);
                data = object.getJSONObject("data");
                CampaignKM.setText(data.getString("totalKilometer"));

            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
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
                            data = object.getJSONObject("data");
                            CampaignKM.setText(data.getString("totalKilometer"));

                            campaignData = result;
                                    storeCampaignData.setData(result);
                        }
                    }
                    catch (Exception e)
                    {

                    }
                }
            });
        }

            //#################### Code for map ############################
        if (HelperFunctions.CheckGPSStatus(MainCampaignPage.this)) {
            if (HelperFunctions.CheckGooglePlayServicesVersionOk(MainCampaignPage.this)) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        } else {
            HelperFunctions.ShowLocationAlert(MainCampaignPage.this);
        }


        //check time constaint
        // Performing action with Sqilte
        try {
            boolean cc=helper.insertDate(HelperFunctions.currentDate(),5);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error occured in sqlite DB", Toast.LENGTH_SHORT).show();
        }


        gotobackground=findViewById(R.id.gotobackground);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            PackageManager pm = MainCampaignPage.this.getPackageManager();
            if(pm.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)){
                gotobackground.setVisibility(View.VISIBLE);

                gotobackground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainCampaignPage.this.enterPictureInPictureMode();
                    }
                });
            }
            else{
                Toast.makeText(this, "This feature is not supported in you device", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            gotobackground.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPictureInPictureModeChanged (boolean isInPictureInPictureMode, Configuration newConfig) {
        if (isInPictureInPictureMode) {
            fram_layout.setVisibility(View.VISIBLE);
           error_layout.setVisibility(View.GONE);
           checkmode=false;
           gotobackground.setVisibility(View.GONE);
        } else {
           //error_layout.setVisibility(View.VISIBLE);
            fram_layout.setVisibility(View.GONE);
           gotobackground.setVisibility(View.VISIBLE);
           checkmode=true;
        }
    }


        @Override
    protected void onResume() {
        super.onResume();
        youKm.setText(storeTotalKM.getData());
            km_in_picture.setText(storeTotalKM.getData());
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeTotalKM.setData(youKm.getText().toString());
    }

    private void init() {
        storeCampaignData = new StoreCampaignData(MainCampaignPage.this);
        CampaignKM = findViewById(R.id.CampaignKM);
        youKm = findViewById(R.id.youKm);
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(MainCampaignPage.this);
        storeTotalKM=new StoreTotalKM(MainCampaignPage.this);
        helper=new DB_Helper(MainCampaignPage.this);
        driverActivtyStatus=new DriverActivtyStatus(MainCampaignPage.this);
        userData=new Store_User_Data_For_Local_User(MainCampaignPage.this);
        apiCall=new ApiCall(MainCampaignPage.this);
        notificationStatus=new NotificationStatus(MainCampaignPage.this);
        camID=new storecampaignID(MainCampaignPage.this);
        kml=new KML(MainCampaignPage.this);
        listOfLocation=new ArrayList<>();
        error_layout=findViewById(R.id.error_layout);
    }


    // Ask permission
    public void AskPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
        String[] permission1 = {Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(MainCampaignPage.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(MainCampaignPage.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checklocationpermission = true;
            } else {
                ActivityCompat.requestPermissions(MainCampaignPage.this, permission, LOCATION_PERMISSION_CODE);
                ActivityCompat.requestPermissions(MainCampaignPage.this, permission1, LOCATION_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(MainCampaignPage.this, permission, LOCATION_PERMISSION_CODE);
            ActivityCompat.requestPermissions(MainCampaignPage.this, permission1, LOCATION_PERMISSION_CODE);
        }
    }

    // result of permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

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


    //$$$$$$$$$$$$$$$$$$$$ When back from activity $$$$$$$$$$$$$$$$$$$$$$
    @Override
    protected void onRestart() {
        super.onRestart();
        //to restart the activity
        super.recreate();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setKMLLayerOnTheMap();
        if (checklocationpermission) {
            if (map != null) {
                getCurrentLocation();
                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location locat) {
                        if (locat != null) {
                            if (marker != null) {
                                marker.remove();
                            }
                            LatLng curlat = new LatLng(locat.getLatitude(), locat.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(curlat).title("Your Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                            marker = map.addMarker(options);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(curlat, DEFAULT_ZOOM));

                            boolean contain = PolyUtil.containsLocation(curlat, listOfLocation, true);
                            if(!contain)
                            {
                                if(checkmode)
                                {
                                    error_layout.setVisibility(View.VISIBLE);
                                }
                            }
                            else {
                                error_layout.setVisibility(View.GONE);
                            }

                        }
                    }
                });
            } else {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map = googleMap;

                        getCurrentLocation();
                        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange(Location locat) {
                                if (locat != null) {
                                    if (marker != null) {
                                        marker.remove();
                                    }
                                    LatLng curlat = new LatLng(locat.getLatitude(), locat.getLongitude());
                                    MarkerOptions options = new MarkerOptions().position(curlat).title("Your Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                                    marker = map.addMarker(options);
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(curlat, DEFAULT_ZOOM));

                                }
                            }
                        });

                    }
                });
            }

        } else {
            AskPermission();
        }
    }

    //##################### getting device current location #####################
    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainCampaignPage.this);
        try {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {

                        location = (Location) task.getResult();
                        lat = String.valueOf(location.getLatitude());
                        longs = String.valueOf(location.getLongitude());
                        oldLat = new LatLng(location.getLatitude(), location.getLongitude());

                        if(timeConstain)
                        {
                            MeasureDistance(oldLat);
                            createLocationRequest();
                        }

                        if (ActivityCompat.checkSelfPermission(MainCampaignPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainCampaignPage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        map.setMyLocationEnabled(true);
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(oldLat, DEFAULT_ZOOM));

                    } else {
                        getGPSLocation();
                    }
                }
            });
        } catch (Exception er) {
            getGPSLocation();
        }

    }


    public void getGPSLocation() {
        Toast.makeText(this, "Please wait, The system is getting current location this may take time", Toast.LENGTH_SHORT).show();
        GPSLocation finder;
        double longitude = 0.0, latitude = 0.0;
        finder = new GPSLocation(this);
        if (finder.canGetLocation()) {
            latitude = finder.getLatitude();
            longitude = finder.getLongitude();
            lat = String.valueOf(finder.getLatitude());
            longs = String.valueOf(finder.getLongitude());
            oldLat = new LatLng(latitude, longitude);

            if (oldLat != null) {
                MeasureDistance(oldLat);
                createLocationRequest();
            }

            MarkerOptions options = new MarkerOptions().position(oldLat).title("Your current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
            marker = map.addMarker(options);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(oldLat, DEFAULT_ZOOM));
        } else {
            finder.showSettingsAlert();
        }
    }

    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Mesauring the distan @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    // to measure the distance between two point
    private void MeasureDistance(final LatLng latLng) {
        start = latLng;
        handler123 = new Handler();
        runnable123 = new Runnable() {
            @Override
            public void run() {
                try {
                    if (start != null && newLat != null && start.longitude > 0 && start.longitude > 0) {
                        if (start.latitude == newLat.latitude && start.longitude == newLat.longitude) {
                            //  Toast.makeText(DriverHomeScreen.this, "Both are same", Toast.LENGTH_SHORT).show();
                            // ignore when both are same
                        } else {

                            boolean contain1 = PolyUtil.containsLocation(start, listOfLocation, true);
                            boolean contain2 = PolyUtil.containsLocation(newLat, listOfLocation, true);
                            if(contain1 && contain2)
                            {
                                double value = distance(start, newLat)/1000;
                                double total = Double.parseDouble(youKm.getText().toString()) + value;
                                storeTotalKM.setData(new DecimalFormat(".###").format((total)));
                                localKm+=value;
                                youKm.setText(storeTotalKM.getData());
                                km_in_picture.setText(storeTotalKM.getData());
                                start = newLat;
                                newLat = null;

                                //###################### Insert values into database for single day
                                double perDbValue=0;
                                Cursor cursor = helper.getDateandPerDayKM(HelperFunctions.currentDate());
                                if (cursor.moveToFirst()) {
                                    while (!cursor.isAfterLast()) {
                                        perDbValue=Double.parseDouble(cursor.getString(cursor.getColumnIndex("km")));
                                        cursor.moveToNext();
                                    }
                                }

                                double newvalue= value + perDbValue;
                                helper.updateKMValue(newvalue,HelperFunctions.currentDate());

                                //############### END

                                //to insert value for hot map
                                boolean ch =helper.setValues(String.valueOf(start.latitude),String.valueOf(start.longitude));
                                onCampaignComplete();

                            }
                            else {
                                //Toast.makeText(MainCampaignPage.this, "You will not get any credit because you are not in the Veezlo define boundry", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        //start=latLng;
                        if (ActivityCompat.checkSelfPermission(MainCampaignPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainCampaignPage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        Task<Location> t = fusedLocationProviderClient.getLastLocation();
                        if (t.isSuccessful() && t.getResult() != null)
                        {
                            start=new LatLng(t.getResult().getLatitude(),t.getResult().getLongitude());
                        }
                        else {
                            start=null;
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    Toast.makeText(MainCampaignPage.this, "Error occurred try again", Toast.LENGTH_SHORT).show();
                } finally {
                    //also call the same runnable to call it at regular interval
                    handler123.postDelayed(this, 30000); //180000
                }
            }
        };
        handler123.postDelayed(runnable123, 30000);
    }

    /*%55555555555555555555555555555555555555555555555*/

    void initLocation(Location loc)
    {
       newLat=new LatLng(loc.getLatitude(),loc.getLongitude());
    }

    protected void createLocationRequest() {
        try {
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainCampaignPage.this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest,new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location loc : locationResult.getLocations()) {
                        if(loc != null)
                        {
                            newLat= new LatLng(loc.getLatitude(),loc.getLongitude());
                            initLocation(loc);
                        }

                        //Toast.makeText(DriverHomeScreen.this, "Lat = "+loc.getLatitude()+"\n Long = "+loc.getLongitude(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, Looper.getMainLooper());
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Error Occured", Toast.LENGTH_SHORT).show();
        }
    }



    /*%55555555555555555555555555555555555555555555555*/


    // Calculating distance
    public double distance(LatLng start, LatLng end) {
        try {
            Location location1 = new Location("locationA");
            location1.setLatitude(start.latitude);
            location1.setLongitude(start.longitude);
            Location location2 = new Location("locationB");
            location2.setLatitude(end.latitude);
            location2.setLongitude(end.longitude);
            double dist = location1.distanceTo(location2);
            return Math.round(dist);
        } catch (Exception e) {

            e.printStackTrace();

        }
        return 0;
    }


    private void onCampaignComplete()  {

        Double ytotalKM=Double.parseDouble(storeTotalKM.getData());
        Double camKM=Double.parseDouble(CampaignKM.getText().toString());

        if(ytotalKM >= camKM) {
            try {
                if (CheckInternetConnection.Connection(MainCampaignPage.this)) {
                    Cursor cursor = helper.getHotmapData();

                    int balance = Integer.parseInt(data.getString("totalExpenses")) / Integer.parseInt(data.getString("numerOfcars"));
                    int newbalance=balance/2;
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userData.getData()[0]);
                    hashMap.put("name", "complete");
                    hashMap.put("balance", String.valueOf(newbalance));
                    hashMap.put("campID", data.getString("id"));
                    hashMap.put("hotmap", CursorToJson(cursor).toString());

                    apiCall.Insert2(hashMap, "driverRating.php", new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

                            try {
                                JSONObject object = new JSONObject(result);
                                if (object.getString("status").trim().equals("1")) {
                                    HashMap<String, String> newHashmap = new HashMap<>();
                                    newHashmap.put("campID", data.getString("id"));
                                    newHashmap.put("numOfCar", data.getString("numerOfcars"));
                                    newHashmap.put("userID",data.getString("userID"));
                                    apiCall.Insert2(newHashmap, "ToChangeCampaginStatus.php", new VolleyCallback() {
                                        @Override
                                        public void onSuccess(String result) {

                                            if (result.trim().equals("success")) {

                                            }
                                        }
                                    });
                                    handler123.removeCallbacksAndMessages(null);
                                    handler12.removeCallbacksAndMessages(null);
                                    locationRequest=null;
                                    startActivity(new Intent(getApplicationContext(), DriverHomeScreen.class));
                                    finish();
                                    storeCampaignData.setData("null");
                                    imageStatus.set(true);
                                    driverActivtyStatus.setData(true);
                                    storeTotalKM.setData("0");
                                    helper.ClearHeapmap();
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("ChangeCampaignDriverState", false);
                                    editor.commit();
                                    Toast.makeText(MainCampaignPage.this, "Campaign Completed Successfully", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(MainCampaignPage.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(MainCampaignPage.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            } catch (Exception e) {
                Toast.makeText(MainCampaignPage.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public JSONArray CursorToJson(Cursor cursor) {

        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("Basit", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;

    }

    // update campagin location
    private void UpdateCampaginLocation()
    {
      handler12 = new Handler();
         runnable12 = new Runnable() {
            @Override
            public void run() {
                try {

                    if (start != null) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userData.getData()[0]);
                        hashMap.put("campID",data.getString("id"));
                        hashMap.put("lat", String.valueOf(start.latitude));
                        hashMap.put("long", String.valueOf(start.longitude));
                        hashMap.put("km",String.valueOf(localKm));
                        hashMap.put("campaginLocation","true");

                        apiCall.Insert2(hashMap, "updateDriverLocation.php", new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                localKm=0;
                            }
                        });
                    }

                    onCampaignComplete();

                } catch (Exception e) {
                    // TODO: handle exception
                } finally {
                    //also call the same runnable to call it at regular interval
                    handler12.postDelayed(this, 60000); //180000
                }
            }
        };
        handler12.postDelayed(runnable12, 60000);
    }

    @Override
    protected void onStart() {
        super.onStart();
       if(!notificationStatus.getData())
       {
           AlertDialog.Builder builder=new AlertDialog.Builder(MainCampaignPage.this)
                   .setTitle("Your campaign is on hold")
                   .setMessage("Your advertiser is asking you for the campaign updates. Please check the notification section and provide the pictures of your car. After a successful response, your campaign will be automatically resumed.")
                   .setCancelable(false)
                   .setPositiveButton("Response", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           startActivity(new Intent(MainCampaignPage.this,NotificationForDriver.class));
                       }
                   })
                   .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           finish();
                      System.exit(0);
                       }
                   });
           builder.show();
       }
    }

    public static boolean isTimeBetweenTwoTime(String initialTime, String finalTime,
                                               String currentTime) throws ParseException {

        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) &&
                currentTime.matches(reg)) {
            boolean valid = false;
            //Start Time
            //all times are from java.util.Date
            Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            java.util.Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) ||
                    actualTime.compareTo(calendar1.getTime()) == 0) &&
                    actualTime.before(calendar2.getTime())) {
                valid = true;
                return valid;
            } else {
                return false;
            }
        }
        return false;
    }


    void checkveryTwoMinute()
    {
        final Handler handler = new Handler();
        runnable12 = new Runnable() {
            @Override
            public void run() {
                try {

                    try {
                        boolean check = isTimeBetweenTwoTime(startTime, endTime, HelperFunctions.getCurrentTimein24Hour());
                        if(!check)
                        {
                            timeConstain=false;
                            HelperFunctions.showTimeWarning(MainCampaignPage.this);
                            handler123.removeCallbacksAndMessages(null);
                            handler12.removeCallbacksAndMessages(null);
                            locationRequest=null;
                        }

                        Cursor cursor = helper.getDateandPerDayKM(HelperFunctions.currentDate());
                        double val=0;
                        if (cursor.moveToFirst()) {
                            while (!cursor.isAfterLast()) {
                                val=Double.parseDouble(cursor.getString(cursor.getColumnIndex("km")));
                                if(val >= Double.parseDouble(allowkm))
                                {
                                    HelperFunctions.KMLimitionAlert(MainCampaignPage.this);
                                    timeConstain=false;
                                }
                                cursor.moveToNext();
                            }
                        }


                    }
                    catch (Exception e)
                    {
                        Toast.makeText(MainCampaignPage.this, "Something went with time constrain try again", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                } finally {
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, 61000); //180000
                }
            }
        };
        handler.postDelayed(runnable12, 61000);
    }

    private void DownloadAmbassadorConstrainData()
    {
        apiCall.get("getAmbassadorConstrainData.php", new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject object=new JSONObject(result);
                    if(object.getString("status").trim().equals("1"))
                    {
                       JSONObject data=object.getJSONObject("data");
                       allowkm=data.getString("km");
                       startTime=data.getString("startTime");
                       endTime=data.getString("endTime");
                       initValues(allowkm,startTime,endTime);

                        //check time constaint
                        try {
                            boolean check = isTimeBetweenTwoTime(startTime, endTime, HelperFunctions.getCurrentTimein24Hour());
                            if(!check)
                            {
                                timeConstain=false;
                                HelperFunctions.showTimeWarning(MainCampaignPage.this);
                                handler123.removeCallbacksAndMessages(null);
                                handler12.removeCallbacksAndMessages(null);
                                locationRequest=null;
                            }


                            Cursor cursor = helper.getDateandPerDayKM(HelperFunctions.currentDate());
                            double val=0;
                            if (cursor.moveToFirst()) {
                                while (!cursor.isAfterLast()) {
                                    val=Double.parseDouble(cursor.getString(cursor.getColumnIndex("km")));
                                    if(val >= Double.parseDouble(allowkm))
                                    {
                                        HelperFunctions.KMLimitionAlert(MainCampaignPage.this);
                                        timeConstain=false;
                                    }
                                    cursor.moveToNext();
                                }
                            }

                        }
                        catch (Exception e)
                        {
                            Toast.makeText(MainCampaignPage.this, "Something went with time constrain try again", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        Toast.makeText(MainCampaignPage.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(MainCampaignPage.this, "Something went went with Json parsing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initValues(String allowkms,String startTimes,String endTimes)
    {
      allowkm=allowkms;
      startTime=startTimes;
      endTime=endTimes;
    }


    public void setKMLLayerOnTheMap()
    {
        /*Start KML FIle Logic*/
        if(kml.checkData())
        {
            try {
                JSONArray array=new JSONArray(kml.getData());
                for(int i=0; i<array.length(); i++)
                {
                    JSONObject object=array.getJSONObject(i);
                    listOfLocation.add(new LatLng(Double.parseDouble(object.getString("lat")),Double.parseDouble(object.getString("long"))));
                    if(map != null)
                    {
                        Polyline polygon = map.addPolyline(new PolylineOptions().addAll(listOfLocation).color(Color.RED));
                    }
                    else {
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(this);
                        Polyline polygon = map.addPolyline(new PolylineOptions().addAll(listOfLocation).color(Color.BLUE));
                    }

                }
            }
            catch (Exception e)
            {
                Toast.makeText(this, "Error occured in the parsing of Kml json data", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            HashMap<String,String> newhasp=new HashMap<>();
            newhasp.put("id",userData.getData()[5]);
            apiCall.Insert2(newhasp, "getCityKMLFile.php", new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    if(!result.trim().equals("fasle"))
                    {
                        kml.setData(result);
                        try {
                            JSONArray array=new JSONArray(result);
                            for(int i=0; i<array.length(); i++)
                            {
                                JSONObject object=array.getJSONObject(i);
                                listOfLocation.add(new LatLng(Double.parseDouble(object.getString("lat")),Double.parseDouble(object.getString("long"))));
                                if(map != null)
                                {
                                    Polyline polygon = map.addPolyline(new PolylineOptions().addAll(listOfLocation).color(Color.BLUE));
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(MainCampaignPage.this, "Error occured in the parsing of Kml json data", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        Toast.makeText(MainCampaignPage.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
        // END OF KML FILE
    }

    @Override
    public void onBackPressed() {
        //Nothing To Do
    }

    public void MainScreenHelp(View view) {
        startActivity(new Intent(getApplicationContext(), Help.class));
    }
}