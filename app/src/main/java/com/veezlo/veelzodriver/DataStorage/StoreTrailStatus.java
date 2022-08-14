package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

public class StoreTrailStatus {

    Context context;
    private SharedPreferences mPreferences;
    private String sharedPrefFileName = "trailDetails";

    public StoreTrailStatus(Context context)
    {
        this.context=context;
        mPreferences=context.getSharedPreferences(sharedPrefFileName,Context.MODE_PRIVATE);
    }

    public void setData(Boolean value)
    {
       SharedPreferences.Editor editor=mPreferences.edit();
       editor.putBoolean("status",value);
       editor.apply();
    }

    public Boolean getData()
    {
        return mPreferences.getBoolean("status",false);
    }
}
