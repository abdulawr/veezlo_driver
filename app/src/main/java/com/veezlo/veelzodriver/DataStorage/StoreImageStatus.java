package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

public class StoreImageStatus{
    SharedPreferences sharedPreferences;
    Context context;

    public StoreImageStatus(Context context) {
        this.context=context;
        sharedPreferences=context.getSharedPreferences("storeimagesstatusdetails",Context.MODE_PRIVATE);
    }

    public void set(boolean check){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("check",check);
        editor.apply();
    }

    public boolean get()
    {
        return sharedPreferences.getBoolean("check",true);
    }
}
