package com.veezlo.veelzodriver.Driver_Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.Loading_AlertDialog;
import com.veezlo.veelzodriver.Config.VolleyCallback;

import com.veezlo.veelzodriver.DataStorage.StoreCampaignData;
import com.veezlo.veelzodriver.DataStorage.StoreImageStatus;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.DataStorage.storecampaignID;
import com.veezlo.veelzodriver.R;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UploadCampaignImages extends AppCompatActivity {

    Boolean checkPermission = false;
    private static final int REQUEST_CODE = 1122;

    private static final int REQUST_CODE_IMAGE = 121;

    LinearLayout uploadedimagesLinearyLayout;
    ImageView firstImage;
    Bitmap bitmap=null;
    Uri imageUri;
    ArrayList<Bitmap> imagesList;
    static Loading_AlertDialog loading_alertDialog;
    ApiCall apiCall;
    Store_User_Data_For_Local_User userData;
    String campaignID="null";
    StoreCampaignData storeCampaignData;
    storecampaignID camID;
    TextView message;
    ScrollView scrollview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_campaign_images);

        message=findViewById(R.id.message);
        scrollview=findViewById(R.id.scrollview);
        init();
        AskPermission();

        if (getIntent().getBooleanExtra("state",false)){
            message.setVisibility(View.VISIBLE);
            scrollview.setVisibility(View.GONE);
        }
        else {
            message.setVisibility(View.GONE);
            scrollview.setVisibility(View.VISIBLE);
        }

        campaignID = getIntent().getStringExtra("id");

        firstImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUST_CODE_IMAGE);
                    }
                } else {
                    AskPermission();
                }
            }
        });


    }

    private void init() {
        firstImage = findViewById(R.id.firstImage);
        uploadedimagesLinearyLayout = findViewById(R.id.uploadedimagesLinearyLayout);
        imagesList=new ArrayList<>();
        apiCall=new ApiCall(UploadCampaignImages.this);
        loading_alertDialog=new Loading_AlertDialog(UploadCampaignImages.this);
        userData=new Store_User_Data_For_Local_User(UploadCampaignImages.this);
        storeCampaignData=new StoreCampaignData(UploadCampaignImages.this);
        camID=new storecampaignID(UploadCampaignImages.this);
    }

    private void AskPermission() {
        if (ContextCompat.checkSelfPermission(UploadCampaignImages.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UploadCampaignImages.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            checkPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermission = true;
        } else {
            AskPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUST_CODE_IMAGE && resultCode == Activity.RESULT_OK && data != null) {


            if(!imagesList.isEmpty())
            {
                imagesList.clear();
            }

            if (data.getClipData() == null) {

                try {
                    uploadedimagesLinearyLayout.removeAllViews();
                    Uri u = data.getData();
                    InputStream stream = getApplicationContext().getContentResolver().openInputStream(u);
                    bitmap = BitmapFactory.decodeStream(stream);
                    firstImage.setImageBitmap(bitmap);
                    imagesList.add(bitmap);
                } catch (Exception e) {
                    Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                }

            } else {

                Resources r = getResources();
                float px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        200,
                        r.getDisplayMetrics()
                );
                try {
                    uploadedimagesLinearyLayout.removeAllViews();
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (int i = 0; i < count; i++) {
                        imageUri = data.getClipData().getItemAt(i).getUri();
                        InputStream stream = getApplicationContext().getContentResolver().openInputStream(imageUri);
                        bitmap = BitmapFactory.decodeStream(stream);
                        imagesList.add(bitmap);
                        if (i == 0) {
                            firstImage.setImageBitmap(bitmap);
                        } else {
                            ImageView imageView = new ImageView(UploadCampaignImages.this);
                            LinearLayout.LayoutParams imageheigh = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) px);
                            imageheigh.setMargins(25, 35, 25, 0);
                            imageView.setPadding(10,10,10,10);
                            imageView.setLayoutParams(imageheigh);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            imageView.requestLayout();
                            imageView.setBackgroundResource(R.drawable.choosebackground);
                            imageView.setImageBitmap(bitmap);
                            uploadedimagesLinearyLayout.addView(imageView);

                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error occurred select images again", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    // to finally upload images
    public void UploadImagesNow(View view) {
        loading_alertDialog.Show();
        if(imagesList.size() > 0)
        {
            try {
                final HashMap<String,String> hashMap=new HashMap<>();
                final HashMap<String,String> newhashMap=new HashMap<>();
                JSONObject object=new JSONObject(storeCampaignData.getData());
                JSONObject data=object.getJSONObject("data");

                for (int i=0; i<imagesList.size(); i++)
                {
                    hashMap.put("img"+(i+1),HelperFunctions.stringToImage(imagesList.get(i)));
                }
                hashMap.put("size",String.valueOf(imagesList.size()));

                hashMap.put("driverID",userData.getData()[0]);
                newhashMap.put("driverID",userData.getData()[0]);

                hashMap.put("camp_id",data.getString("id"));
                newhashMap.put("camp_id",data.getString("id"));

                /*if(!campaignID.trim().equals("null"))
                {
                    hashMap.put("camp_id",campaignID);
                    newhashMap.put("camp_id",campaignID);
                }
                else {
                    hashMap.put("camp_id",camID.getData());
                    newhashMap.put("camp_id",camID.getData());
                }*/
                hashMap.put("userID",data.getString("userID"));
                newhashMap.put("userID",data.getString("userID"));

                apiCall.Insert2(hashMap, "uploadCampaignImagesForUser.php", new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {

                        if(result.trim().equals("success"))
                        {
                            apiCall.Insert2(newhashMap, "sendimagesuploadNotification.php", new VolleyCallback() {
                                @Override
                                public void onSuccess(String result) {
                                 message.setVisibility(View.VISIBLE);
                                 scrollview.setVisibility(View.GONE);
                                }
                            });
                        }
                        else {
                            Toast.makeText(UploadCampaignImages.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                        }
                        loading_alertDialog.Hide();
                    }
                });
            }
            catch (Exception e)
            {

                loading_alertDialog.Hide();
                Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            loading_alertDialog.Hide();
            Toast.makeText(this, "Please select images", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        //Nothing To Do
    }

}

