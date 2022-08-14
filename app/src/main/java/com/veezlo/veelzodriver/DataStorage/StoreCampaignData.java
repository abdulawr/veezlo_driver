package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.core.content.ContextCompat;

public class StoreCampaignData {
    SharedPreferences sharedPreferences;
    Context context;

    public StoreCampaignData(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences("StoreCampaignData",Context.MODE_PRIVATE);
    }

    public void setData(String data){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("value",data);
        editor.apply();
    }

    public String getData()
    {
        return sharedPreferences.getString("value","null");
    }
}
