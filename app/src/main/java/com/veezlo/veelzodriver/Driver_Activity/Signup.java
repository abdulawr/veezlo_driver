package com.veezlo.veelzodriver.Driver_Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Signup extends AppCompatActivity {

    TextInputEditText name,email,mobile,address,payment_no,cnic,car_no,password;
    Spinner model,model_year,carType,city,payment_type;
    Button register;
    ApiCall apiCall;

    List<String> cityName,cityIds,modelName,modelIds,cartypeName,cartypeIds,p_menthodName,p_menthodIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        address = findViewById(R.id.address);
        payment_no = findViewById(R.id.payment_no);
        cnic = findViewById(R.id.cnic);
        car_no = findViewById(R.id.car_no);
        password = findViewById(R.id.password);

        model = findViewById(R.id.model);
        model_year = findViewById(R.id.model_year);
        carType = findViewById(R.id.carType);
        city = findViewById(R.id.city);
        payment_type = findViewById(R.id.payment_type);
        register = findViewById(R.id.register);

        cityName = new ArrayList<>();
        cityIds = new ArrayList<>();
        modelName = new ArrayList<>();
        modelIds = new ArrayList<>();
        cartypeName = new ArrayList<>();
        cartypeIds = new ArrayList<>();
        p_menthodName = new ArrayList<>();
        p_menthodIds = new ArrayList<>();

        apiCall = new ApiCall(this);
        apiCall.get("getDriverReg_SpinnerInfor.php", new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                 try {
                     JSONObject object = new JSONObject(result);

                     JSONArray citys = object.getJSONArray("city");
                     for (int i=0; i<citys.length(); i++){
                         JSONObject single = citys.getJSONObject(i);
                         cityName.add(single.getString("name"));
                         cityIds.add(single.getString("id"));
                     }
                     ArrayAdapter<String> adp_1 = new ArrayAdapter<String>(Signup.this, android.R.layout.simple_list_item_1,cityName);
                     city.setAdapter(adp_1);

                     JSONArray cartype_arr = object.getJSONArray("cartype");
                     for (int i=0; i<cartype_arr.length(); i++){
                         JSONObject single = cartype_arr.getJSONObject(i);
                         cartypeName.add(single.getString("name"));
                         cartypeIds.add(single.getString("id"));
                     }
                     ArrayAdapter<String> adp_2 = new ArrayAdapter<String>(Signup.this, android.R.layout.simple_list_item_1,cartypeName);
                     carType.setAdapter(adp_2);

                     JSONArray models = object.getJSONArray("model");
                     for (int i=0; i<models.length(); i++){
                         JSONObject single = models.getJSONObject(i);
                         modelName.add(single.getString("name"));
                         modelIds.add(single.getString("id"));
                     }
                     ArrayAdapter<String> adp_3 = new ArrayAdapter<String>(Signup.this, android.R.layout.simple_list_item_1,modelName);
                     model.setAdapter(adp_3);


                     JSONArray p_methods = object.getJSONArray("p_methods");
                     for (int i=0; i<p_methods.length(); i++){
                         JSONObject single = p_methods.getJSONObject(i);
                         p_menthodName.add(single.getString("name"));
                         p_menthodIds.add(single.getString("id"));
                     }
                     ArrayAdapter<String> adp_4 = new ArrayAdapter<String>(Signup.this, android.R.layout.simple_list_item_1,p_menthodName);
                     payment_type.setAdapter(adp_4);

                 }
                 catch (Exception e){
                     Log.e("Basit",e.getMessage());
                     Toast.makeText(Signup.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                     //finish();
                 }
            }
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        List<Integer> dates = new ArrayList<>();
        for (int i = year; i >= 1995; i--){
           dates.add(i);
        }

        ArrayAdapter<Integer> date_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,dates);
        model_year.setAdapter(date_adapter);



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String vname = name.getText().toString();
                String vemail = email.getText().toString();
                String vmobile = mobile.getText().toString();
                String vaddress = address.getText().toString();
                String vpayment_no = payment_no.getText().toString();
                String vcnic = cnic.getText().toString();
                String vcar_no = car_no.getText().toString();
                String vpassword = password.getText().toString();

                String v_payment_type = p_menthodIds.get(payment_type.getSelectedItemPosition());
                String v_model = modelIds.get(model.getSelectedItemPosition());
                String v_model_year = String.valueOf(dates.get(model_year.getSelectedItemPosition()));
                String v_carType =cartypeIds.get(carType.getSelectedItemPosition());
                String v_city = cityIds.get(city.getSelectedItemPosition());



                if(HelperFunctions.verify(vname) && HelperFunctions.verify(vmobile) && HelperFunctions.verify(vaddress)
                 && HelperFunctions.verify(vpayment_no) && HelperFunctions.verify(v_payment_type) && HelperFunctions.verify(vcnic)
                && HelperFunctions.verify(vcar_no) && HelperFunctions.verify(vpassword)){

                    HashMap<String,String> map = new HashMap<>();
                    map.put("name",vname);
                    map.put("email",vemail);
                    map.put("mobile",vmobile);
                    map.put("address",vaddress);
                    map.put("payment_no",vpayment_no);
                    map.put("payment_type",v_payment_type);
                    map.put("cnic",vcnic);
                    map.put("car_no",vcar_no);
                    map.put("password",vpassword);
                    map.put("model",v_model);
                    map.put("model_year",v_model_year);
                    map.put("carType",v_carType);
                    map.put("city",v_city);

                    apiCall.Insert(map, "RegisterNew_driver_Mobile.php", new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                          try {
                               JSONObject object = new JSONObject(result);
                               if(object.getString("status").trim().equals("1")){
                                   startActivity(new Intent(getApplicationContext(),LoginDriver.class));
                                   finish();
                               }
                              Toast.makeText(Signup.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                          }
                          catch (Exception e){
                              Toast.makeText(Signup.this, "Error occurred while processing json data!", Toast.LENGTH_SHORT).show();
                          }
                        }
                    });
                    
                }
                else{
                    Toast.makeText(Signup.this, "Fill the form correctly!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}