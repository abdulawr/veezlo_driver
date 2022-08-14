package com.veezlo.veelzodriver.Driver_Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.veezlo.veelzodriver.R;


public class Help extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    public void Close(View view) {
        finish();
    }

    // Run when faqs button click
    public void Onclick_FAQ(View view) {
        startActivity(new Intent(getApplicationContext(),FAQ.class));
    }

    // Run when support button is click
    public void Onclick_Suppport(View view) {
        startActivity(new Intent(getApplicationContext(),Support.class));
    }

 // Run when contact us is click
    public void Onclick_Contact_US(View view) {
        startActivity(new Intent(getApplicationContext(),ContactUS.class));
    }

    // Run when about us is click
    public void Onclick_About(View view) {
        startActivity(new Intent(getApplicationContext(),About.class));
    }

    //Run when help center is click
    public void HelpCenterClick(View view) {
        startActivity(new Intent(getApplicationContext(),Help_Center.class));
    }
}