package com.veezlo.veelzodriver.Driver_Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Dash;
import com.squareup.picasso.Picasso;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.BaseURL;
import com.veezlo.veelzodriver.Config.CheckInternetConnection;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.R;

import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {

    CircleImageView profileImage;
    TextView name,email,mobile,cnic,date,carType,balance,completeTextview,acceptTextview,rejectTextview;
    ProgressBar complete,accept,reject;
    ApiCall apiCall;
    Store_User_Data_For_Local_User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        init();

        if (CheckInternetConnection.Connection(Dashboard.this))
        {
            HashMap<String,String> map=new HashMap<>();
            map.put("id",userData.getData()[0]);
            apiCall.Insert(map, "getDashBoardDetails.php", new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                  try {

                      JSONObject jsonObject=new JSONObject(result);
                      if (jsonObject.getString("status").trim().equals("1"))
                      {
                          JSONObject data=jsonObject.getJSONObject("data");
                          Picasso.get().load(BaseURL.DriverPath() +data.getString("image")).into(profileImage);
                          name.setText(data.getString("name"));
                          email.setText(data.getString("email"));
                          mobile.setText(data.getString("phone"));
                          cnic.setText(data.getString("cnic"));
                          date.setText(data.getString("date"));
                          carType.setText(data.getString("type"));
                          balance.setText(data.getString("balance"));

                          String complete_value=data.getString("complete");
                          String accept_value=data.getString("accept");
                          String reject_value=data.getString("reject");

                          if(!complete_value.trim().equals("null") && !accept_value.trim().equals("null") &&
                                  !reject_value.trim().equals("null") )
                          {
                              completeTextview.setText(complete_value);
                              acceptTextview.setText(accept_value);
                              rejectTextview.setText(reject_value);

                              int total= Integer.parseInt(complete_value) + Integer.parseInt(accept_value) + Integer.parseInt(reject_value);

                              float com_per=(Float.parseFloat(complete_value)/total) * 100;
                              float acc_per=(Float.parseFloat(accept_value)/total) * 100;
                              float rej_per=(Float.parseFloat(reject_value)/total) * 100;

                              complete.setProgress((int) com_per);
                              accept.setProgress((int) acc_per);
                              reject.setProgress((int) rej_per);
                          }
                          else {
                              completeTextview.setText("0");
                              acceptTextview.setText("0");
                              rejectTextview.setText("0");
                          }

                      }
                      else {
                          Toast.makeText(Dashboard.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                      }
                  }
                  catch (Exception e)
                  {
                      Toast.makeText(Dashboard.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                      Log.e("Basit",e.getMessage());
                  }
                }
            });
        }
        else {
            Toast.makeText(this, "Check you internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void init()
    {
        apiCall=new ApiCall(Dashboard.this);
        userData=new Store_User_Data_For_Local_User(Dashboard.this);
        profileImage=findViewById(R.id.profileImage);
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.mobile);
        cnic=findViewById(R.id.cnic);
        date=findViewById(R.id.date);
        carType=findViewById(R.id.carType);
        balance=findViewById(R.id.balance);
        completeTextview=findViewById(R.id.completeTextview);
        acceptTextview=findViewById(R.id.acceptTextview);
        rejectTextview=findViewById(R.id.rejectTextview);
        complete=findViewById(R.id.complete);
        accept=findViewById(R.id.accept);
        reject=findViewById(R.id.reject);
    }

    // ############### to finish the activity and Run when the back press button is click #############
    public void Close(View view) {
        finish();
    }
}