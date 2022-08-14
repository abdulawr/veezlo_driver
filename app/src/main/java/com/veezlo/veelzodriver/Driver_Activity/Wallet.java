package com.veezlo.veelzodriver.Driver_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.veezlo.veelzodriver.Adapter.Transaction_History_Adapter;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.CheckInternetConnection;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.DataContainer.Transaction_Container;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Wallet extends AppCompatActivity {

    TextView balance;
    ApiCall apiCall;
    Store_User_Data_For_Local_User userData;
    Toolbar toolbar;
    FloatingActionButton makeTransaction;
    RecyclerView rec;
    ArrayList<Transaction_Container> list;
    Transaction_History_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

       init();
       if(CheckInternetConnection.Connection(Wallet.this)) {
           HashMap<String, String> map = new HashMap<>();
           map.put("id", userData.getData()[0]);
           apiCall.Insert(map, "getDriverCurrentBalance.php", new VolleyCallback() {
               @Override
               public void onSuccess(String result) {
                  try {
                      JSONObject ob=new JSONObject(result);

                      if(ob.getString("status").trim().equals("1"))
                      {
                          JSONArray array=ob.getJSONArray("tranArray");
                          if(array.length() > 0)
                          {
                              for (int i=0; i<array.length(); i++)
                              {
                                  JSONObject TranItem=array.getJSONObject(i);
                                  Transaction_Container container=new Transaction_Container();
                                  container.setAmount(TranItem.getString("amount"));
                                  container.setDate(TranItem.getString("date"));
                                  container.setMobile(TranItem.getString("mobile"));
                                  container.setTranID(TranItem.getString("T_ID"));
                                  list.add(container);
                              }
                              adapter=new Transaction_History_Adapter(Wallet.this,list);
                              rec.setLayoutManager(new LinearLayoutManager(Wallet.this));
                              rec.setAdapter(adapter);
                          }
                          balance.setText(ob.getString("balance"));
                      }
                      else {
                          Toast.makeText(Wallet.this, ob.getString("message"), Toast.LENGTH_SHORT).show();
                      }
                  }
                  catch (Exception e)
                  {
                      Toast.makeText(Wallet.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                  }
               }
           });
       }
       else {
           Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
       }


    }

    private void init()
    {
        toolbar=findViewById(R.id.wallet);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        balance=findViewById(R.id.balance);
        apiCall=new ApiCall(Wallet.this);
        userData=new Store_User_Data_For_Local_User(Wallet.this);
        rec=findViewById(R.id.rec);
        list=new ArrayList<>();
        rec.hasFixedSize();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

}