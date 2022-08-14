package com.veezlo.veelzodriver.Driver_Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.DataStorage.Store_User_Data_For_Local_User;
import com.veezlo.veelzodriver.R;

import java.util.HashMap;

public class Support extends AppCompatActivity {

    EditText mobile,email,subject,message;
    Store_User_Data_For_Local_User userData;
    ApiCall apiCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        userData=new Store_User_Data_For_Local_User(this);
        apiCall=new ApiCall(this);
        mobile=findViewById(R.id.mobile);
        email=findViewById(R.id.email);
        subject=findViewById(R.id.subject);
        message=findViewById(R.id.message);

        email.setText(userData.getData()[2]);

    }

    public void SendData(View view) {

        String em=email.getText().toString();
        final String mob=mobile.getText().toString();
        final String sub=subject.getText().toString();
        String mess=message.getText().toString();

        if (check(mob) && check(sub) && check(mess)) {
            HashMap<String,String> hashMap=new HashMap<>();
            hashMap.put("email",em);
            hashMap.put("mobile",mob);
            hashMap.put("subject",sub);
            hashMap.put("message",mess);
            apiCall.Insert(hashMap, "insertContactUSData.php", new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    if (result.trim().equals("1")) {
                        message.getText().clear();
                        subject.getText().clear();
                        mobile.getText().clear();
                        Snackbar.make(findViewById(android.R.id.content), "Successfully Submitted", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "Successfully Submitted", Snackbar.LENGTH_LONG).show();
                    }

                }
            });
        } else {
            Toast.makeText(this, "Fill the form correclty", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean check(String value)
    {
        if(value.trim().length() > 0)
        {
          return true;
        }
        else {
            return false;
        }
    }
}