package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class KML {

    Context context;
    private SharedPreferences mPreferences;
    private String sharedPrefFileName = "KML";

    public KML(Context context) {
        this.context = context;
        mPreferences=context.getSharedPreferences(sharedPrefFileName,MODE_PRIVATE);
    }

    public void  setData(String kml)
    {
       SharedPreferences.Editor editor=mPreferences.edit();
       editor.putString("kml",kml);
       editor.apply();
    }

    public String getData()
    {
      return mPreferences.getString("kml","null");
    }

    public Boolean checkData()
    {
        if(mPreferences.contains("kml"))
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
