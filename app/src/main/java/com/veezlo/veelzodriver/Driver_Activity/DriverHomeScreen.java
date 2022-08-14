package com.veezlo.veelzodriver.Driver_Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.BaseURL;
import com.veezlo.veelzodriver.Config.CheckInternetConnection;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.Loading_AlertDialog;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.CurrentLocation.GPSLocation;
import com.veezlo.veelzodriver.DataStorage.DriverTrailKmPerCity;
import com.veezlo.veelzodriver.DataStorage.KML;
import com.veezlo.veelzodriver.DataStorage.StoreTotalKM;
import com.veezlo.veelzodriver.DataStorage.StoreTrailStatus;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DriverHomeScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback {

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;
    Store_User_Data_For_Local_User userData;
    String[] data;
    ImageView profileImage;
    TextView profileName, profileEmail;
    GoogleMap map;
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private Boolean checklocationpermission = false;
    private static final float DEFAULT_ZOOM = 15f;
    Loading_AlertDialog dialog;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location location; // location
    LatLng oldLat, newLat, start;
    Marker marker = null;
    Geocoder geocoder;
    List<Address> addresses;
    String address, city, state, country, postalCode, knownName;
    ApiCall apiCall;
    String lat = "0.0", longs = "0.0";
    ImageView setcameratocurrentlocation;
    Button online, startTrail;
    LinearLayout trailLinearLayout;
    StoreTrailStatus storeTrailStatus;
    TextView startDate, endDate, status, kmTextview, speed;
    String endDates;
    LocationRequest locationRequest;
    double spd = 0;
    Button resumebutton;
    Runnable runnable123;
     Handler handler123;
     KML kml;
     Button gotobackground;
     StoreTotalKM storeTotalKM;
     List<LatLng> listOfLocation;
     LinearLayout error_layout;
     DriverTrailKmPerCity driverTrailKmPerCity;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // to initialize
        init();
        error_layout=findViewById(R.id.error_layout);

       if(userData.getData()[6].trim().equals("0")){
           Menu menu = navigationView.getMenu();
           menu.removeItem(R.id.c_profile);
       }



        // get map permission
        AskPermission();
        // to check the tail status
        if(storeTrailStatus.getData())
        {
          online.setVisibility(View.VISIBLE);
          startTrail.setVisibility(View.GONE);
          trailLinearLayout.setVisibility(View.GONE);
        }
        else {
            getTrailDetails();
        }
        // to start trail
        startDriverTrail();


        if (HelperFunctions.CheckGPSStatus(DriverHomeScreen.this)) {
            if (HelperFunctions.CheckGooglePlayServicesVersionOk(DriverHomeScreen.this)) {

            }
        } else {
            HelperFunctions.ShowLocationAlert(DriverHomeScreen.this);
        }

        dialog = new Loading_AlertDialog(DriverHomeScreen.this);
        apiCall = new ApiCall(DriverHomeScreen.this);

        // to move camera to curren location
        setcameratocurrentlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (map != null) {
                        LatLng cur = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(cur, DEFAULT_ZOOM));
                    }
                } catch (Exception e) {

                }
            }
        });

        // todo this method to be eanble in production
         updateLocation();

        // this click will work when driver click trail resume button
        resumebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              double totalDistanvalue=Double.parseDouble(kmTextview.getText().toString());
              if(totalDistanvalue > 0)
              {
                  HashMap<String,String> trailData=new HashMap<>();
                  trailData.put("id",userData.getData()[0]);
                  trailData.put("distance",String.valueOf(totalDistanvalue));

                  apiCall.Insert(trailData, "updateTrailKilometer.php", new VolleyCallback() {
                      @Override
                      public void onSuccess(String result) {
                          Toast.makeText(DriverHomeScreen.this, result, Toast.LENGTH_SHORT).show();
                      }
                  });
              }
              else {
                  Toast.makeText(DriverHomeScreen.this, "Total Km is zero", Toast.LENGTH_SHORT).show();
              }
            }
        });

// to show policy dialgo for first time
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            View view= LayoutInflater.from(getApplicationContext()).inflate(R.layout.policyalert,null);
            AlertDialog.Builder builder=new AlertDialog.Builder(DriverHomeScreen.this)
                    .setView(view);
            final Dialog dialog=builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            ImageView imageView=view.findViewById(R.id.closedialgo);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }


        //To Ask For display notification on Android 10
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            if (!Settings.canDrawOverlays(this)) {

                androidx.appcompat.app.AlertDialog.Builder alert=new androidx.appcompat.app.AlertDialog.Builder(this)
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


        gotobackground=findViewById(R.id.gotobackground);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            gotobackground.setVisibility(View.VISIBLE);

            gotobackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DriverHomeScreen.this.enterPictureInPictureMode();
                }
            });
        }
        else {
            gotobackground.setVisibility(View.GONE);
        }

    }

    /////////////////////////// Run when the map is ready //////////////////////////
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //To set KML Layer on the map
        setKMLLayerOnTheMap();
        if (checklocationpermission) {
            if(map != null)
            {
                getCurrentLocation();
                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location locat) {
                        if(locat != null)
                        {
                            if (marker != null) {
                                marker.remove();
                            }
                            LatLng curlat = new LatLng(locat.getLatitude(), locat.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(curlat).title("Your Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                            marker = map.addMarker(options);
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(curlat, DEFAULT_ZOOM));
                            //to check weather the driver is in range or boundry
                            boolean contain = PolyUtil.containsLocation(curlat, listOfLocation, true);
                            if(!contain)
                            {
                                error_layout.setVisibility(View.VISIBLE);
                            }
                            else {
                                error_layout.setVisibility(View.GONE);
                            }
                            
                            if(!storeTrailStatus.getData())
                            {
                                spd = locat.getSpeed() * 18 / 5;
                                speed.setText("Speed : " + Math.floor(spd) + " KM/H");
                            }

                            if(storeTrailStatus.getData())
                            {
                                newLat=new LatLng(locat.getLatitude(), locat.getLongitude());
                            }
                        }
                    }
                });
            }
            else {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        map=googleMap;

                        getCurrentLocation();
                        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange(Location locat) {
                                if(locat != null)
                                {
                                    if (marker != null) {
                                        marker.remove();
                                    }
                                    LatLng curlat = new LatLng(locat.getLatitude(), locat.getLongitude());
                                    MarkerOptions options = new MarkerOptions().position(curlat).title("Your Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                                    marker = map.addMarker(options);
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(curlat, DEFAULT_ZOOM));

                                    if(!storeTrailStatus.getData())
                                    {
                                        spd = locat.getSpeed() * 18 / 5;
                                        speed.setText("Speed : " + Math.floor(spd) + " KM/H");
                                    }
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

    //######################## getting device current Location ####################

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DriverHomeScreen.this);
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

                        if(!storeTrailStatus.getData())
                        {
                            MeasureDistance(oldLat);
                            createLocationRequest();
                        }

                        geocoder = new Geocoder(DriverHomeScreen.this, Locale.getDefault());
                        try {
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();
                            country = addresses.get(0).getCountryName();
                            postalCode = addresses.get(0).getPostalCode();
                            knownName = addresses.get(0).getFeatureName();


                            MarkerOptions options = new MarkerOptions().position(oldLat).title(address).icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                            marker = map.addMarker(options);
                            if (ActivityCompat.checkSelfPermission(DriverHomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverHomeScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            map.setMyLocationEnabled(true);
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(oldLat, DEFAULT_ZOOM));

                        } catch (Exception e) {

                            MarkerOptions options = new MarkerOptions().position(oldLat).title("Your Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                            marker = map.addMarker(options);
                            map.setMyLocationEnabled(true);
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(oldLat, DEFAULT_ZOOM));
                        }

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
       if (map != null)
       {

       }
       else {

       }
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
                if(!storeTrailStatus.getData())
                {
                    MeasureDistance(oldLat);
                    createLocationRequest();
                }
            }
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();

                // to update ui
                MarkerOptions options = new MarkerOptions().position(oldLat).title(address).icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                marker = map.addMarker(options);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(oldLat, DEFAULT_ZOOM));
                Toast.makeText(DriverHomeScreen.this, address, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                MarkerOptions options = new MarkerOptions().position(oldLat).title("Your current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.carmarker));
                marker = map.addMarker(options);
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(oldLat, DEFAULT_ZOOM));
            }
        } else {
            finder.showSettingsAlert();
        }
    }

    ////////////////  To initalize the view by ids //////////////////////
    private void init() {
        online = findViewById(R.id.online);
        startTrail = findViewById(R.id.startTrail);
        trailLinearLayout = findViewById(R.id.trailLinearLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimaryDark));
        toggle.syncState();
        drawer.addDrawerListener(toggle);
        navigationView.setNavigationItemSelectedListener(this);
        userData = new Store_User_Data_For_Local_User(DriverHomeScreen.this);
        data = userData.getData();
        apiCall = new ApiCall(DriverHomeScreen.this);
        speed = findViewById(R.id.speed);
        View headerItem = navigationView.getHeaderView(0);
        profileImage = headerItem.findViewById(R.id.profile_image);
        profileName = headerItem.findViewById(R.id.profilename);
        profileEmail = headerItem.findViewById(R.id.emailAddress);
        setcameratocurrentlocation = findViewById(R.id.setcameratocurrentlocation);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DriverHomeScreen.this);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        status = findViewById(R.id.status);
        kmTextview = findViewById(R.id.kmTextview);
        storeTrailStatus = new StoreTrailStatus(DriverHomeScreen.this);
        resumebutton=findViewById(R.id.resumebutton);
         storeTotalKM=new StoreTotalKM(DriverHomeScreen.this);
         kml=new KML(DriverHomeScreen.this);
         listOfLocation=new ArrayList<>();
        driverTrailKmPerCity=new DriverTrailKmPerCity(DriverHomeScreen.this);
    }

    // $$$$$$$$$$$$$$$$$$$$$$$$$$$$ THIS METHOD IS USED FOR NAV ITEM CLICK $$$$$$$$$$$$$$$$$$$$$$$$$$$
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();


         if (id == R.id.signout) {
            HashMap<String,String> logoutHashmap=new HashMap<>();
            logoutHashmap.put("id",userData.getData()[0]);
            logoutHashmap.put("type","driver");
           apiCall.Insert(logoutHashmap, "signOutDriver.php", new VolleyCallback() {
               @Override
               public void onSuccess(String result) {

                   userData.Clear();
                   kml.Clear();
                   storeTrailStatus.setData(false);
                   startActivity(new Intent(getApplicationContext(), LoginDriver.class));
                   finish();
               }
           });
        }

        else if (id == R.id.wallet) {
            startActivity(new Intent(getApplicationContext(), Wallet.class));
        }

         else if (id == R.id.c_profile) {
             startActivity(new Intent(getApplicationContext(), Complete_Profile.class));
         }

        else if (id == R.id.dashboard) {
            startActivity(new Intent(getApplicationContext(), Dashboard.class));
        }

        else if(id == R.id.help)
        {
            startActivity(new Intent(getApplicationContext(), Help.class));
        }

        else if(id == R.id.notification)
        {
            startActivity(new Intent(getApplicationContext(),NotificationForDriver.class));
        }
         else if(id == R.id.troubleshoot)
         {
            // clear the deveive cache
             clearApplicationData();
             startActivity(new Intent(getApplicationContext(),LoginDriver.class));
             Toast.makeText(this, "Troubleshoot run successfully.", Toast.LENGTH_SHORT).show();
         }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }
    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    // ############################# To show profile details
    @Override
    protected void onStart() {
        super.onStart();

        if (userData.checkData()) {
            String[] data = userData.getData();
            profileEmail.setText(data[2]);
            profileName.setText(data[3]);
            Picasso.get().load(BaseURL.DriverPath() + data[1]).into(profileImage);

        } else {
            startActivity(new Intent(getApplicationContext(), LoginDriver.class));
            finish();
            Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show();
        }
    }

    // Ask permission
    public void AskPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
        String[] permission1 = {Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(DriverHomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(DriverHomeScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checklocationpermission = true;
            } else {
                ActivityCompat.requestPermissions(DriverHomeScreen.this, permission, LOCATION_PERMISSION_CODE);
                ActivityCompat.requestPermissions(DriverHomeScreen.this, permission1, LOCATION_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(DriverHomeScreen.this, permission, LOCATION_PERMISSION_CODE);
            ActivityCompat.requestPermissions(DriverHomeScreen.this, permission1, LOCATION_PERMISSION_CODE);
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

    // to update driver location in background
    public void updateLocation() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    if (newLat != null) {

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userData.getData()[0]);
                        hashMap.put("lat", String.valueOf(newLat.latitude));
                        hashMap.put("long", String.valueOf(newLat.longitude));

                        apiCall.Insert2(hashMap, "updateDriverLocation.php", new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                // location updated successfull
                            }
                        });
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                } finally {
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, 610000); //480000
                }
            }
        };
        handler.postDelayed(runnable, 610000);
    }

    ////////////////////////////// GET TRAIL DETAILS ///////////////////////
    private void getTrailDetails() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userData.getData()[0]);
        hashMap.put("cityID", userData.getData()[5]);
        apiCall.Insert(hashMap, "getDriverTrailDetails.php", new VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                if (result.trim().equals("null")) {
                    startTrail.setVisibility(View.VISIBLE);
                    online.setVisibility(View.GONE);
                    trailLinearLayout.setVisibility(View.GONE);
                    storeTrailStatus.setData(false);
                } else if (result.trim().equals("done")) {
                    startTrail.setVisibility(View.GONE);
                    online.setVisibility(View.VISIBLE);
                    trailLinearLayout.setVisibility(View.GONE);
                    storeTrailStatus.setData(true);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        driverTrailKmPerCity.setData(Integer.parseInt(jsonObject.getString("km")));
                        if (jsonObject.getString("status").trim().equals("1")) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            startTrail.setVisibility(View.GONE);
                            online.setVisibility(View.GONE);
                            trailLinearLayout.setVisibility(View.VISIBLE);
                            startDate.setText(data.getString("startDate"));
                            endDate.setText(data.getString("endDate"));
                            status.setText("Trial");
                            storeTrailStatus.setData(false);

                            if(storeTotalKM.getData().trim().equals("0"))
                            {
                                storeTotalKM.setData(data.getString("totalKm"));
                                kmTextview.setText(storeTotalKM.getData());
                            }
                            // to check driver status
                           CheckDriverTailStatus();

                        } else {
                            Toast.makeText(DriverHomeScreen.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        finish();
                        Log.e("Basit",e.getMessage());
                        Toast.makeText(DriverHomeScreen.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    // to start driver trail
    public void startDriverTrail() {
        startTrail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeTrailStatus.setData(false);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", userData.getData()[0]);
                hashMap.put("start", HelperFunctions.currentDate());

                endDates = HelperFunctions.currentDate();
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(format.parse(endDates));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(Calendar.DATE, 7);
                endDates = format.format(calendar.getTime());

                hashMap.put("end", endDates);

                if (CheckInternetConnection.Connection(DriverHomeScreen.this)) {
                    apiCall.Insert(hashMap, "insertTrailData.php", new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                if (jsonObject.getString("status").trim().equals("1")) {
                                    startTrail.setVisibility(View.GONE);
                                    online.setVisibility(View.GONE);
                                    trailLinearLayout.setVisibility(View.VISIBLE);
                                    startDate.setText(HelperFunctions.currentDate());
                                    endDate.setText(endDates);
                                    kmTextview.setText("0");
                                    status.setText("Trail");
                                    storeTrailStatus.setData(false);
                                    Toast.makeText(DriverHomeScreen.this, "Trail start successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DriverHomeScreen.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(DriverHomeScreen.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(DriverHomeScreen.this, "Check you internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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
                                double total = Double.parseDouble(kmTextview.getText().toString()) + value;
                                storeTotalKM.setData(new DecimalFormat(".###").format((total)));
                                kmTextview.setText(storeTotalKM.getData());
                                start = newLat;
                                newLat = null;
                                CheckDriverTailStatus();
                            }
                            else {
                               // Toast.makeText(DriverHomeScreen.this, "You will not get any credit because you are not in the Veezlo define boundry", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } else {

                        //start=latLng;
                        if (ActivityCompat.checkSelfPermission(DriverHomeScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverHomeScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
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
                    Toast.makeText(DriverHomeScreen.this, "Error occurred try again", Toast.LENGTH_SHORT).show();
                } finally {
                    //also call the same runnable to call it at regular interval
                    handler123.postDelayed(this, 31000); //180000
                }
            }
        };
        handler123.postDelayed(runnable123, 31000);
    }

    /*%55555555555555555555555555555555555555555555555*/

    protected void createLocationRequest() {
        try {
             locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(DriverHomeScreen.this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
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
                        newLat = new LatLng(loc.getLatitude(),loc.getLongitude());
                            //Toast.makeText(DriverHomeScreen.this, "Lat = "+loc.getLatitude()+"\n Long = "+loc.getLongitude(), Toast.LENGTH_SHORT).show();
                    }
                }
            },Looper.getMainLooper());
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

    // to update the record when on back buttion is click
  /*  @Override
    public void onBackPressed() {
        boolean ch=true;
        if(storeTrailStatus.getData())
        {
            finish();
        }
        else if(ch) {
            Toast.makeText(this, "Please wait the system is updating record", Toast.LENGTH_SHORT).show();
            double totalDistanvalue=Double.parseDouble(kmTextview.getText().toString());
            ch=true;
            if(totalDistanvalue > 0)
            {
                HashMap<String,String> trailData=new HashMap<>();
                trailData.put("id",userData.getData()[0]);
                trailData.put("distance",String.valueOf(totalDistanvalue));

                apiCall.Insert(trailData, "updateTrailKilometer.php", new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(DriverHomeScreen.this, result, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        else {
            finish();
        }
    }*/

  //######################## Run when the trail is finish ######################################
    private void EndTheDriverTrail()
    {
        double totalDistanvalue=Double.parseDouble(kmTextview.getText().toString());
        HashMap<String,String> finishTrail=new HashMap<>();
        finishTrail.put("id",userData.getData()[0]);
        finishTrail.put("distance",String.valueOf(totalDistanvalue));

        apiCall.Insert(finishTrail, "finishDriverTrail.php", new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                if(result.trim().equals("success"))
                {
                    // todo to add notification
                    try {
                        storeTrailStatus.setData(true);
                        handler123.removeCallbacks(runnable123);
                        trailLinearLayout.setVisibility(View.GONE);
                        online.setVisibility(View.VISIBLE);
                        HelperFunctions.ShowNotification(DriverHomeScreen.this,"Driver trail success result","Your trail completed successfully.Now you will be able to receive user request",null);

                    }
                    catch (Exception e)
                    {
                        HelperFunctions.ShowNotification(DriverHomeScreen.this,"Driver trail success result","Your trail completed successfully.Now you will be able to receive user request",null);
                    }
                }
                else {
                    Toast.makeText(DriverHomeScreen.this, result, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //################ this function will be run when the trial is fail #################
    private void RestartTheDriverTrail(){
       trailLinearLayout.setVisibility(View.VISIBLE);
       online.setVisibility(View.GONE);
        storeTrailStatus.setData(false);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userData.getData()[0]);
        hashMap.put("start", HelperFunctions.currentDate());
        endDates = HelperFunctions.currentDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(format.parse(endDates));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DATE, 7);
        endDates = format.format(calendar.getTime());
        hashMap.put("end", endDates);

        if (CheckInternetConnection.Connection(DriverHomeScreen.this)) {
            apiCall.Insert(hashMap, "restartTrailData.php", new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                   if (result.trim().equals("success"))
                   {
                       speed.setText("0");
                       kmTextview.setText("0");
                       status.setText("Trail");
                       startDate.setText(HelperFunctions.currentDate());
                       endDate.setText(endDates);
                       HelperFunctions.ShowNotification(DriverHomeScreen.this,"Your trail drive result","Your trail has been restarted because you did not complete the required KM",null);

                   }
                   else {
                       Toast.makeText(DriverHomeScreen.this, result, Toast.LENGTH_SHORT).show();
                   }
                }
            });
        } else {
            Toast.makeText(DriverHomeScreen.this, "Check you internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    // to check the driver status
    private void CheckDriverTailStatus()
    {
        String [] currDate=HelperFunctions.currentDate().split("/");
        String [] enDate=endDate.getText().toString().split("-");

        double totaldistanceValue=Double.parseDouble(kmTextview.getText().toString());

        if(totaldistanceValue >= driverTrailKmPerCity.getData())
        {
            EndTheDriverTrail();
        }

        if(currDate[0].trim().equals(enDate[0]) && currDate[1].trim().equals(enDate[1]) && currDate[2].trim().equals(enDate[2]) )
        {
            // 0 for AM and 1 for PM
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-4:00"));
            Date currentLocalTime = cal.getTime();
            DateFormat date = new SimpleDateFormat("KK:mm");
            date.setTimeZone(TimeZone.getTimeZone("GMT-4:00"));
            int currentHour = cal.get(Calendar.HOUR);
            int cap=cal.get(Calendar.AM_PM);

            if(currentHour >= 6 && cap == 1)
            {
                if(totaldistanceValue >= driverTrailKmPerCity.getData())
                {
                    EndTheDriverTrail();
                }
                else {
                    RestartTheDriverTrail();
                }
            }
        }
        else if((currDate[0].trim().equals(enDate[0]) && currDate[1].trim().equals(enDate[1]) && Integer.parseInt(currDate[2]) >
                Integer.parseInt(enDate[2]) ))
        {
            if(totaldistanceValue >= driverTrailKmPerCity.getData())
            {
                EndTheDriverTrail();
            }
            else {
                RestartTheDriverTrail();
            }
        }
        else if((currDate[0].trim().equals(enDate[0]) && Integer.parseInt(currDate[1]) >
                Integer.parseInt(enDate[1]) && Integer.parseInt(currDate[2]) >=
                Integer.parseInt(enDate[2]) ))
        {
            if(totaldistanceValue >= driverTrailKmPerCity.getData())
            {
                EndTheDriverTrail();
            }
            else {
                RestartTheDriverTrail();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeTotalKM.setData(kmTextview.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        kmTextview.setText(storeTotalKM.getData());
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
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;

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
                            Toast.makeText(DriverHomeScreen.this, "Error occured in the parsing of Kml json data", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else {
                        Toast.makeText(DriverHomeScreen.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
        }
    }


}