package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

public class StoreTotalKM {

    Context context;
    private SharedPreferences mPreferences;
    private String sharedPrefFileName = "StoreTotalKM";

    public StoreTotalKM(Context context)
    {
        this.context=context;
        mPreferences=context.getSharedPreferences(sharedPrefFileName,Context.MODE_PRIVATE);
    }

    public void setData(String value)
    {
        SharedPreferences.Editor editor=mPreferences.edit();
        editor.putString("TotalKM",value);
        editor.apply();
    }

    public String getData()
    {
        return mPreferences.getString("TotalKM","0");
    }
}
