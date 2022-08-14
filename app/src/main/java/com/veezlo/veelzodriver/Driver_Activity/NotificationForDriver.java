package com.veezlo.veelzodriver.Driver_Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.veezlo.veelzodriver.Adapter.NotificationAdapter;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.DataContainer.NotificationContainer;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationForDriver extends AppCompatActivity {

    ApiCall apiCall;
    Store_User_Data_For_Local_User userData;
    RecyclerView recyclerView;
    TextView error;
    NotificationAdapter adapter;
    ArrayList<NotificationContainer> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_for_driver);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiCall=new ApiCall(this);
        userData=new Store_User_Data_For_Local_User(this);
        recyclerView=findViewById(R.id.rec);
        error=findViewById(R.id.error);
        list=new ArrayList<>();

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("driverID",userData.getData()[0]);
        apiCall.Insert(hashMap, "getDriverNotificationData.php", new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
               try {
                   JSONObject object=new JSONObject(result);
                   if(object.getString("status").trim().equals("1"))
                   {
                       recyclerView.setVisibility(View.VISIBLE);
                       error.setVisibility(View.GONE);
                       JSONArray array=object.getJSONArray("data");
                       for (int i=0; i<array.length(); i++)
                       {
                           JSONObject singleObject=array.getJSONObject(i);
                           NotificationContainer container=new NotificationContainer();
                           container.setDate(singleObject.getString("date"));
                           container.setDriverID(singleObject.getString("driverID"));
                           container.setUserID(singleObject.getString("userID"));
                           container.setNotID(singleObject.getString("id"));

                           list.add(container);
                       }
                       if(!list.isEmpty())
                       {
                           recyclerView.hasFixedSize();
                           adapter=new NotificationAdapter(NotificationForDriver.this,list);
                           recyclerView.setLayoutManager(new LinearLayoutManager(NotificationForDriver.this));
                           recyclerView.setAdapter(adapter);
                       }
                       else {
                           recyclerView.setVisibility(View.GONE);
                           error.setVisibility(View.VISIBLE);
                       }
                   }
                   else {
                       recyclerView.setVisibility(View.GONE);
                       error.setVisibility(View.VISIBLE);
                   }
               }
               catch (Exception e)
               {
                   Snackbar.make(findViewById(android.R.id.content),"Something went wrong try again", Snackbar.LENGTH_LONG)
                           .setActionTextColor(getResources().getColor(R.color.colorPrimary)).show();
               }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return true;
    }
}