package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

public class CampaginInstallationTotalTime {

    Context context;
    private SharedPreferences mPreferences;
    private String sharedPrefFileName = "CampaginInstallationTotalTime";

    public CampaginInstallationTotalTime(Context context)
    {
        this.context=context;
        mPreferences=context.getSharedPreferences(sharedPrefFileName,Context.MODE_PRIVATE);
    }

    public void setData(int value)
    {
        SharedPreferences.Editor editor=mPreferences.edit();
        editor.putInt("value",value);
        editor.apply();
    }

    public int getData()
    {
        return mPreferences.getInt("value",0);
    }
}
