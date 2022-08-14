package com.veezlo.veelzodriver.Driver_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.CurrentLocation.GPSLocation;

import com.veezlo.veelzodriver.DataStorage.DriverActivtyStatus;
import com.veezlo.veelzodriver.DataStorage.StoreCampaignData;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class InstallationCenter extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    ApiCall apiCall;
    StoreCampaignData storeCampaignData;
    DriverActivtyStatus driverActivtyStatus;
    LatLng endLocation = null,startLocation=null;
    TextView contact;
    GoogleMap map;
    private static final int LOCATION_PERMISSION_CODE = 1234;
    private Boolean checklocationpermission = false;
    private static final float DEFAULT_ZOOM = 15f;
    Marker marker = null;
    Geocoder geocoder;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location location = null;
    double longitude = 0.0, latitude = 0.0;
    JSONObject locationObj=null;
    private List<Polyline> polylines = null;
    TextView distance,address;
    List<Address> addresses;
    Button tryAgainButton;
    String apiKey;
    Store_User_Data_For_Local_User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_installation_center);

        // to initialize
        userData=new Store_User_Data_For_Local_User(this);
        init();

        // get map permission
        AskPermission();

        if (HelperFunctions.CheckGPSStatus(InstallationCenter.this)) {
            if (HelperFunctions.CheckGooglePlayServicesVersionOk(InstallationCenter.this)) {
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }
        } else {
            HelperFunctions.ShowLocationAlert(InstallationCenter.this);
        }

       if (!storeCampaignData.getData().trim().equals("null")) {
           HashMap<String,String> hashMap=new HashMap<>();
           hashMap.put("cityID",userData.getData()[5]);

            apiCall.Insert(hashMap,"getInstallationCenter.php", new VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    try {
                        tryAgainButton.setVisibility(View.GONE);
                         locationObj = new JSONObject(result);
                        contact.setText("Contact : "+locationObj.getString("mobile"));
                        address.setText("Address: "+locationObj.getString("address"));
                        apiKey=locationObj.getString("key");
                        if (locationObj.getString("status").trim().equals("1")) {
                            endLocation = new LatLng(Double.parseDouble(locationObj.getString("lat")), Double.parseDouble(locationObj.getString("long")));
                            AssignObject(endLocation);
                            MakeRouteToInstallationCenter(startLocation,endLocation);
                            initApiKey(locationObj.getString("key"));
                        } else {
                            tryAgainButton.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        tryAgainButton.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
           startActivity(new Intent(getApplicationContext(), DriverHomeScreen.class));
            storeCampaignData.setData("null");
            driverActivtyStatus.setData(true);
            Toast.makeText(this, "Something went", Toast.LENGTH_SHORT).show();
            finish();
        }
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> hashMap=new HashMap<>();
                hashMap.put("cityID",userData.getData()[5]);
                apiCall.Insert(hashMap,"getInstallationCenter.php", new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            tryAgainButton.setVisibility(View.GONE);
                             locationObj = new JSONObject(result);
                            if (locationObj.getString("status").trim().equals("1")) {
                                endLocation = new LatLng(Double.parseDouble(locationObj.getString("lat")), Double.parseDouble(locationObj.getString("long")));
                                AssignObject(endLocation);
                                MakeRouteToInstallationCenter(startLocation,endLocation);
                            } else {
                                tryAgainButton.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            tryAgainButton.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        });

    }

    //############# Initalize the view by id`s ########################
    private void init() {
        apiCall = new ApiCall(InstallationCenter.this);
        storeCampaignData = new StoreCampaignData(InstallationCenter.this);
        driverActivtyStatus = new DriverActivtyStatus(InstallationCenter.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(InstallationCenter.this);
        distance=findViewById(R.id.distance);
        address=findViewById(R.id.address);
        tryAgainButton=findViewById(R.id.tryAgainButton);
        contact=findViewById(R.id.contact);


    }

    // ############### run when the map is ready ################
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (checklocationpermission) {
            getCurrentLocation();
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location locat) {
                    if (locat != null) {
                        if (marker != null) {
                            marker.remove();
                        }
                        LatLng curlat = new LatLng(locat.getLatitude(), locat.getLongitude());
                        MarkerOptions options = new MarkerOptions().position(curlat).title("Your Current Location");
                        marker = map.addMarker(options);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(curlat, DEFAULT_ZOOM));
                        AssignObjectStart(curlat);

                    }
                }
            });
        } else {
            AskPermission();
        }

    }

    private void AssignObject(LatLng end) {
        endLocation = end;
    }

    private void AssignObjectStart(LatLng start) {
        startLocation = start;
    }

    private void initApiKey(String key)
    {
        apiKey=key;
    }


    //######################## getting device current Location ####################

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(InstallationCenter.this);
        try {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {

                        location = (Location) task.getResult();
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                       // Log.e("Basit","Start == "+String.valueOf(latLng.latitude) + " ===== "+String.valueOf(latLng.longitude));
                        MarkerOptions options = new MarkerOptions().position(latLng).title("Your Current Location");
                        marker = map.addMarker(options);
                        if (ActivityCompat.checkSelfPermission(InstallationCenter.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(InstallationCenter.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        map.setMyLocationEnabled(true);
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));

                        // to create route

                        AssignObjectStart(latLng);
                       // Log.e("Basit","Start == "+String.valueOf(latLng.latitude) + " ===== "+String.valueOf(latLng.longitude));
                    } else {
                        getGPSLocation();
                        if(latitude > 0 && longitude > 0)
                        {
                            AssignObjectStart(new LatLng(latitude,longitude));
                        }
                    }
                }
            });
        } catch (Exception er) {
            getGPSLocation();
            if(latitude > 0 && longitude > 0)
            {
                AssignObjectStart(new LatLng(latitude,longitude));
            }
        }

    }


    public void getGPSLocation() {
        Toast.makeText(this, "Please wait, The system is getting current location this may take time", Toast.LENGTH_SHORT).show();
        GPSLocation finder;
        finder = new GPSLocation(this);
        if (finder.canGetLocation()) {
            latitude = finder.getLatitude();
            longitude = finder.getLongitude();
            LatLng oldLat = new LatLng(latitude, longitude);
            MarkerOptions options = new MarkerOptions().position(oldLat).title("Your current location");
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



    // Ask permission
    public void AskPermission() {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};
        String[] permission1 = {Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(InstallationCenter.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(InstallationCenter.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checklocationpermission = true;
            } else {
                ActivityCompat.requestPermissions(InstallationCenter.this, permission, LOCATION_PERMISSION_CODE);
                ActivityCompat.requestPermissions(InstallationCenter.this, permission1, LOCATION_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(InstallationCenter.this, permission, LOCATION_PERMISSION_CODE);
            ActivityCompat.requestPermissions(InstallationCenter.this, permission1, LOCATION_PERMISSION_CODE);
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

    // to make Route to the installation center
    private void MakeRouteToInstallationCenter(LatLng start,LatLng end)
    {
        startLocation=start;
        endLocation=end;
        if(startLocation != null && endLocation != null && startLocation.longitude > 0 && startLocation.latitude > 0)
        {
            Findroutes(startLocation,endLocation);
            distanceAndAddress(startLocation,endLocation);
        }
        else {
            if (ActivityCompat.checkSelfPermission(InstallationCenter.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(InstallationCenter.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Task<Location> t = fusedLocationProviderClient.getLastLocation();
            if (t.isSuccessful() && t.getResult() != null)
            {
                startLocation=new LatLng(t.getResult().getLatitude(),t.getResult().getLongitude());
            }
            else {
                startLocation=null;
            }

            Findroutes(startLocation,endLocation);
            distanceAndAddress(startLocation,endLocation);
        }

      /*  Log.e("Basit","Start == "+String.valueOf(startLocation.latitude) + " ===== "+String.valueOf(startLocation.longitude));
        Log.e("Basit","End == "+String.valueOf(endLocation.latitude) + " ===== "+String.valueOf(endLocation.longitude));*/
    }

    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(InstallationCenter.this,"Unable to get location",Toast.LENGTH_LONG).show();
            if (ActivityCompat.checkSelfPermission(InstallationCenter.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(InstallationCenter.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Task<Location> t = fusedLocationProviderClient.getLastLocation();
            if (t.isSuccessful() && t.getResult() != null)
            {
                Start=new LatLng(t.getResult().getLatitude(),t.getResult().getLongitude());
                    Routing routing = new Routing.Builder()
                            .travelMode(AbstractRouting.TravelMode.DRIVING)
                            .withListener(this)
                            .alternativeRoutes(true)
                            .waypoints(Start, End)
                            .key(apiKey)  //also define your api key here.
                            .build();
                    routing.execute();
                }
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(apiKey)  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
       // Log.e("Basit",String.valueOf(e));
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(InstallationCenter.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
       // CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(12);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = map.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polylines.add(polyline);

                //Add Marker on route ending position
                MarkerOptions endMarker = new MarkerOptions();
                endMarker.position(polylineEndLatLng);
                endMarker.title("Destination");
                map.addMarker(endMarker);

            } else {

            }

        }
    }

        @Override
        public void onRoutingCancelled() {
            Findroutes(startLocation,endLocation);
        }

    public void distanceAndAddress(LatLng start, LatLng end) {
        try {
            Location location1 = new Location("locationA");
            location1.setLatitude(start.latitude);
            location1.setLongitude(start.longitude);
            Location location2 = new Location("locationB");
            location2.setLatitude(end.latitude);
            location2.setLongitude(end.longitude);
            double dist = location1.distanceTo(location2);
           double total= Math.round(dist) /1000.0;
           distance.setText("Total Distance : "+new DecimalFormat(".###").format((total)) +" KM");

           try {
                geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(end.latitude, end.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String comple_address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
               String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                address.setText("Full address : "+comple_address+"\n KnownName : "+knownName);
           }
           catch (Exception e)
           {
               if(locationObj != null)
               {
                   address.setText("Full address : "+locationObj.getString("address"));
               }
               else {
                   address.setText("Address : Not found");
               }
           }

        } catch (Exception e) {
            address.setText("Address: Not Found");
            distance.setText("Distance: Null");

        }
    }

    // when the reach center button is click this function is run
    public void Reach_Installation_Center(View view) {

        try {
            HashMap<String,String> mm=new HashMap<>();
            mm.put("userID",userData.getData()[0]);
            JSONObject ob=new JSONObject(storeCampaignData.getData());
            JSONObject item=ob.getJSONObject("data");
            mm.put("campID",item.getString("id"));
            mm.put("state","2");
            apiCall.Insert(mm, "changeCamp_Assign_Status.php", new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                try {
                    JSONObject res=new JSONObject(result);
                    if (res.getString("status").trim().equals("1")){
                        Intent intent=new Intent(getApplicationContext(),ViewCampaignDetails.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(InstallationCenter.this, res.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(InstallationCenter.this, "Error occurred try again!", Toast.LENGTH_SHORT).show();
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
    }
}