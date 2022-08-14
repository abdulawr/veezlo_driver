package com.veezlo.veelzodriver.Driver_Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.veezlo.veelzodriver.Config.ApiCall;
import com.veezlo.veelzodriver.Config.VolleyCallback;
import com.veezlo.veelzodriver.DataStorage.DB_Helper;
import com.veezlo.veelzodriver.R;

import org.json.JSONObject;

import java.util.HashMap;

public class ContactUS extends AppCompatActivity {

    TextView address,phone,mobile,email;
    ApiCall apiCall;
    DB_Helper helper;
    String m_number,e_address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_u_s);

        helper=new DB_Helper(ContactUS.this);
        address=findViewById(R.id.address);
        phone=findViewById(R.id.phone);
        mobile=findViewById(R.id.mobile);
        email=findViewById(R.id.email);
        apiCall=new ApiCall(ContactUS.this);

        HashMap<String,String> mm=new HashMap<>();
        apiCall.Insert(mm,"getCompanyInfo.php", new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object=new JSONObject(result);
                    if(object.getString("status").trim().equals("1"))
                    {
                        JSONObject data=object.getJSONObject("data");
                        address.setText(data.getString("address"));
                        mobile.setText(data.getString("mobile"));
                        phone.setText(data.getString("phone"));
                        email.setText(data.getString("email"));
                        m_number=data.getString("mobile");
                        e_address=data.getString("email");
                    }
                    else {
                        Toast.makeText(ContactUS.this, object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(ContactUS.this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void Close(View view) {
        finish();
    }

    // to make call
    public void CallUSNow(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + m_number));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
        }
    }

    // to make email
    public void EmailUsNow(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, e_address);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Your subject here");
        intent.putExtra(Intent.EXTRA_TEXT, "Your message here");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            Toast.makeText(this, "Something went wrong try again", Toast.LENGTH_SHORT).show();
        }
    }
}