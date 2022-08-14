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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.api.Api;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.Loading_AlertDialog;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

public class Complete_Profile extends AppCompatActivity {

    Boolean checkPermission = false;
    Button uploadImg;
    private static final int REQUEST_CODE = 1122;

    private static final int IMG_CNIC_FRONT = 121;
    private static final int IMG_CNIC_BACK = 122;
    private static final int IMG_LICENCE_FRONT = 123;
    private static final int IMG_LICENCE_BACK = 124;
    private static final int IMG_CAR_REGISTRATION = 125;
    private static final int IMG_PROFILE = 126;

    Loading_AlertDialog alert ;
    ApiCall apiCall;

    Bitmap bitmap_profile = null, bitmap_cnic_front = null, bitmap_cnic_back=null,bitmap_licenc_front,
    bitmap_licence_back=null,bitmap_car_registration=null;

    ImageView profile_image,cnic_front_image,cnic_back_image,licence_front_image,licence_back_image,car_reg_image;

    Store_User_Data_For_Local_User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        uploadImg = findViewById(R.id.uploadImg);
        userData = new Store_User_Data_For_Local_User(this);
        AskPermission();

        profile_image = findViewById(R.id.profile_image);
        cnic_front_image = findViewById(R.id.cnic_front_image);
        cnic_back_image = findViewById(R.id.cnic_back_image);
        licence_front_image = findViewById(R.id.licence_front_image);
        licence_back_image = findViewById(R.id.licence_back_image);
        car_reg_image = findViewById(R.id.car_reg_image);
        alert = new Loading_AlertDialog(this);
        apiCall = new ApiCall(this);


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(IMG_PROFILE);
            }
        });

        cnic_front_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(IMG_CNIC_FRONT);
            }
        });

        cnic_back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(IMG_CNIC_BACK);
            }
        });


        licence_front_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(IMG_LICENCE_FRONT);
            }
        });


        licence_back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(IMG_LICENCE_BACK);
            }
        });


        car_reg_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(IMG_CAR_REGISTRATION);
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bitmap_car_registration != null && bitmap_profile != null && bitmap_cnic_front != null
                  && bitmap_cnic_back != null && bitmap_licence_back != null && bitmap_licenc_front != null){

                    alert.Show();
                    uploadImg.setClickable(false);
                    JSONObject img = new JSONObject();

                    try {
                        img.put("profile", HelperFunctions.stringToImage(bitmap_profile));
                        img.put("cnic_front",HelperFunctions.stringToImage(bitmap_cnic_front));
                        img.put("cnic_back",HelperFunctions.stringToImage(bitmap_cnic_back));
                        img.put("licence_front",HelperFunctions.stringToImage(bitmap_licenc_front));
                        img.put("licence_back",HelperFunctions.stringToImage(bitmap_licence_back));
                        img.put("car_registration",HelperFunctions.stringToImage(bitmap_car_registration));

                        HashMap<String,String> map = new HashMap<>();
                        map.put("imgs",img.toString());
                        map.put("userID",userData.getData()[0]);

                        apiCall.Insert2(map, "uploadDriver_Images.php", new VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                Log.e("Basit",result);
                             try {
                                 JSONObject object = new JSONObject(result);
                                 if(object.getString("status").trim().equals("1")){
                                     userData.setActImg("0");
                                     startActivity(new Intent(getApplicationContext(),DriverHomeScreen.class));
                                     finish();
                                 }
                                 else{
                                     alert.Hide();
                                     uploadImg.setClickable(true);
                                 }
                                 Toast.makeText(Complete_Profile.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                             }
                             catch (Exception e){
                                 alert.Hide();
                                 uploadImg.setClickable(true);
                             }
                            }
                        });


                    }
                    catch (Exception e){
                        alert.Hide();
                        uploadImg.setClickable(true);
                        Toast.makeText(Complete_Profile.this, "Error occurred in json parsing try again", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(Complete_Profile.this, "Please select all images", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }


    private void AskPermission() {
        if (ContextCompat.checkSelfPermission(Complete_Profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Complete_Profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            checkPermission = true;
        }
    }


    void selectImage(int Code){
        if (checkPermission) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Code);
        } else {
            AskPermission();
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

        try {
            Uri u = data.getData();
            InputStream stream = getApplicationContext().getContentResolver().openInputStream(u);

            if (requestCode == IMG_PROFILE && resultCode == Activity.RESULT_OK && data != null) {
              bitmap_profile = BitmapFactory.decodeStream(stream);
              profile_image.setImageBitmap(bitmap_profile);
            }
            else if (requestCode == IMG_CNIC_FRONT && resultCode == Activity.RESULT_OK && data != null) {
              bitmap_cnic_front = BitmapFactory.decodeStream(stream);
              cnic_front_image.setImageBitmap(bitmap_cnic_front);
            }
            else if (requestCode == IMG_CNIC_BACK && resultCode == Activity.RESULT_OK && data != null) {
              bitmap_cnic_back = BitmapFactory.decodeStream(stream);
              cnic_back_image.setImageBitmap(bitmap_cnic_back);
            }
            else if (requestCode == IMG_LICENCE_FRONT && resultCode == Activity.RESULT_OK && data != null) {
               bitmap_licenc_front = BitmapFactory.decodeStream(stream);
               licence_front_image.setImageBitmap(bitmap_licenc_front);
            }
            else if (requestCode == IMG_LICENCE_BACK && resultCode == Activity.RESULT_OK && data != null) {
               bitmap_licence_back = BitmapFactory.decodeStream(stream);
               licence_back_image.setImageBitmap(bitmap_licence_back);
            }
            else if (requestCode == IMG_CAR_REGISTRATION && resultCode == Activity.RESULT_OK && data != null) {
              bitmap_car_registration = BitmapFactory.decodeStream(stream);
              car_reg_image.setImageBitmap(bitmap_car_registration);
            }

        }
        catch (Exception e){
            Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
        }

    }

}