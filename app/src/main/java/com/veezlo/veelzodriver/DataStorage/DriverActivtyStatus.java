package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

public class DriverActivtyStatus {

    Context context;
    private SharedPreferences mPreferences;
    private String sharedPrefFileName = "DriverActivtyStatus";

    public DriverActivtyStatus(Context context)
    {
        this.context=context;
        mPreferences=context.getSharedPreferences(sharedPrefFileName,Context.MODE_PRIVATE);
    }

    public void setData(Boolean value)
    {
        SharedPreferences.Editor editor=mPreferences.edit();
        editor.putBoolean("statusValue",value);
        editor.apply();
    }

    public Boolean getData()
    {
        return mPreferences.getBoolean("statusValue",false);
    }
}
