package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class DriverTrailKmPerCity {

    Context context;
    private SharedPreferences mPreferences;
    private String sharedPrefFileName = "DriverTrailKmPerCity";

    public DriverTrailKmPerCity(Context context) {
        this.context = context;
        mPreferences=context.getSharedPreferences(sharedPrefFileName,MODE_PRIVATE);
    }

    public void setData(int id)
    {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt("km",id);
        preferencesEditor.apply();
    }

    public int getData()
    {
        return  mPreferences.getInt("km",220);
    }

    public Boolean checkData()
    {
        if(mPreferences.contains("km"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void Clear()
    {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.clear().commit();
    }
}
