package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class StoreNotificationID {

    Context context;
    private SharedPreferences mPreferences;
    private String sharedPrefFileName = "userData";

    public StoreNotificationID(Context context) {
        this.context = context;
        mPreferences=context.getSharedPreferences(sharedPrefFileName,MODE_PRIVATE);
    }

    public void setData(String id)
    {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("id",id);
        preferencesEditor.apply();
    }

    public String getData()
    {
        return  mPreferences.getString("id","null");
    }

}
