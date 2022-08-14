package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class storecampaignID {

    Context context;
    private SharedPreferences mPreferences;
    private String sharedPrefFileName = "storecampaignID";

    public storecampaignID(Context context) {
        this.context = context;
        mPreferences=context.getSharedPreferences(sharedPrefFileName,MODE_PRIVATE);
    }

    public void setData(String id)
    {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("value",id);
        preferencesEditor.apply();
    }

    public String getData()
    {
        return  mPreferences.getString("value","null");
    }
}
