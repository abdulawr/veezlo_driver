package com.veezlo.veelzodriver.Driver_Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.Loading_AlertDialog;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.DataContainer.NotificationContainer;
import com.veezlo.veelzodriver.DataStorage.NotificationStatus;
import com.veezlo.veelzodriver.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UploadImagesForUserRequestThroughNotification extends AppCompatActivity {

    NotificationContainer data;
    ImageView firstImage;
    Button upload;
    boolean checkPermision=false;
    private static final int REQUEST_CODE = 120;
    private static final int REQUST_CODE_IMAGE = 121;

    Bitmap bitmap=null;
    LinearLayout uploadedimagesLinearyLayout;
    Uri imageUri;
    Loading_AlertDialog loading_alertDialog;
    ApiCall apiCall;
    NotificationStatus notificationStatus;
    String address="Unknown";
    Loading_AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_images_for_user_request_through_notification);

           alertDialog=new Loading_AlertDialog(UploadImagesForUserRequestThroughNotification.this);
        alertDialog.Show();
        init();
        try {
             data = (NotificationContainer) getIntent().getSerializableExtra("object");

        }
        catch (Exception e)
        {
            Snackbar.make(findViewById(android.R.id.content),"Something went wrong try again", Snackbar.LENGTH_LONG)
                    .setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();
            finish();
        }


               FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(UploadImagesForUserRequestThroughNotification.this);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {

                       Location location = (Location) task.getResult();
                        Geocoder geocoder = new Geocoder(UploadImagesForUserRequestThroughNotification.this, Locale.getDefault());
                        try {
                            List<Address> addresses;
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            assignAddress(address);
                        } catch (Exception e) {
                            address="Unknown";
                        }

                    } else {
                        address="UnKnown";
                    }
                }
            });
            alertDialog.Hide();
        } catch (Exception er) {
           address="UnKnown";
            alertDialog.Hide();
        }


        // to fitch images from glallery
        firstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermision) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        startActivityForResult(takePictureIntent, REQUST_CODE_IMAGE);
                    } catch (Exception e) {
                        Toast.makeText(UploadImagesForUserRequestThroughNotification.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    AskPermision();
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_alertDialog.Show();
                if(bitmap != null)
                {
                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("requestID",data.getNotID());
                    hashMap.put("driverID",data.getDriverID());
                    hashMap.put("userID",data.getUserID());

                    hashMap.put("img", HelperFunctions.stringToImage(bitmap));
                    hashMap.put("date",HelperFunctions.currentDate());
                    hashMap.put("time",HelperFunctions.getCurrentTime());
                    hashMap.put("add",address);

                    apiCall.Insert2(hashMap, "UploadImagesForUserRequest.php", new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Log.e("Basit",result);
                          if(result.trim().equals("success"))
                          {
                              HashMap<String,String> updateHashmap=new HashMap<>();
                              updateHashmap.put("requestID",data.getNotID());
                              updateHashmap.put("driverID",data.getDriverID());
                              updateHashmap.put("userID",data.getUserID());

                            apiCall.Insert2(updateHashmap, "sendNotandChangeStatusOfUserRequest.php", new VolleyCallback() {
                                @Override
                                public void onSuccess(String result) {

                                    loading_alertDialog.Hide();
                                    notificationStatus.setData(true);
                                    startActivity(new Intent(getApplicationContext(),MainCampaignPage.class));
                                    finish();
                                    Toast.makeText(UploadImagesForUserRequestThroughNotification.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                                }
                            });
                          }
                          else {
                              loading_alertDialog.Hide();
                              Snackbar.make(findViewById(android.R.id.content),"Something went wrong try again", Snackbar.LENGTH_LONG)
                                      .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                                      .setTextColor(getResources().getColor(R.color.colorPrimary)).show();
                          }
                        }
                    });

                }
                else {
                    loading_alertDialog.Hide();
                    Snackbar.make(findViewById(android.R.id.content),"Please select images first", Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                            .setTextColor(getResources().getColor(R.color.colorPrimary)).show();
                }
            }
        });

    }


    private void assignAddress(String value)
    {
      address=value;
    }

    public void init()
    {
     upload=findViewById(R.id.upload);
     firstImage=findViewById(R.id.firstImage);
     uploadedimagesLinearyLayout=findViewById(R.id.uploadedimagesLinearyLayout);
     loading_alertDialog=new Loading_AlertDialog(UploadImagesForUserRequestThroughNotification.this);
     apiCall=new ApiCall(UploadImagesForUserRequestThroughNotification.this);
     notificationStatus=new NotificationStatus(UploadImagesForUserRequestThroughNotification.this);

     AskPermision();
    }

    private void AskPermision()
    {
        if(ContextCompat.checkSelfPermission(UploadImagesForUserRequestThroughNotification.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            checkPermision=true;
        }
        else {
            ActivityCompat.requestPermissions(UploadImagesForUserRequestThroughNotification.this,new String[]{Manifest.permission.CAMERA},REQUEST_CODE);
            checkPermision=false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermision = true;
        } else {
            AskPermision();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUST_CODE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
              bitmap = (Bitmap) data.getExtras().get("data");
              firstImage.setImageBitmap(bitmap);
        }

    }
}