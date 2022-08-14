package com.veezlo.veelzodriver.Driver_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.veezlo.veelzodriver.R;

public class Push_Notification_Alert extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.push_notification_alert);

        TextView title=findViewById(R.id.title);
        TextView message=findViewById(R.id.message);
        title.setText(getIntent().getStringExtra("title"));
        message.setText(getIntent().getStringExtra("message"));
    }

    public void OK(View view) {
        startActivity(new Intent(getApplicationContext(),DriverHomeScreen.class));
        finish();
    }
}
