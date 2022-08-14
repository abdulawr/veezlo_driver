package com.veezlo.veelzodriver.Driver_Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.HelperFunctions;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.R;

import org.json.JSONObject;

import java.util.HashMap;

public class Driver_Pre_Registration extends AppCompatActivity {

    EditText fname,lname,email,mobile;
    ApiCall apiCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_pre_registration);

        fname=findViewById(R.id.fname);
        lname=findViewById(R.id.lname);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.mobile);
        apiCall=new ApiCall(this);


    }

    public void Submit(View view) {
        String fn=fname.getText().toString(),
                ln=lname.getText().toString(),
                em=email.getText().toString(),
                mob=mobile.getText().toString();
        if(HelperFunctions.verify(fn) && HelperFunctions.verify(ln) && HelperFunctions.verify(em) && HelperFunctions.verify(mob)){
            HashMap<String,String> mm=new HashMap<>();
            mm.put("fname",fn);
            mm.put("lname",ln);
            mm.put("email",em);
            mm.put("mobile",mob);
            apiCall.Insert(mm, "driverPre_Regristration.php", new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                try {
                    JSONObject object=new JSONObject(result);
                    if(object.getString("status").trim().equals("1")){
                        mobile.getText().clear();
                        fname.getText().clear();
                        lname.getText().clear();
                        email.getText().clear();

                        Toast.makeText(Driver_Pre_Registration.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(Driver_Pre_Registration.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(Driver_Pre_Registration.this, "Error occured in json parsing", Toast.LENGTH_SHORT).show();
                }
                }
            });
        }
        else{
            Toast.makeText(this, "Enter values correctly!", Toast.LENGTH_SHORT).show();
        }
    }
}