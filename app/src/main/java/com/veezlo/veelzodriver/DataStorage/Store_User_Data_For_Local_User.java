package com.veezlo.veelzodriver.DataStorage;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Store_User_Data_For_Local_User {

    Context context;
    private SharedPreferences mPreferences;
    private String sharedPrefFileName = "driverData";

    public Store_User_Data_For_Local_User(Context context) {
        this.context = context;
        mPreferences=context.getSharedPreferences(sharedPrefFileName,MODE_PRIVATE);
    }

    public void setData(String id,String img,String email,String name,String type,String cityID,String actImg_Status)
    {
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("id",id);
        preferencesEditor.putString("image",img);
        preferencesEditor.putString("email",email);
        preferencesEditor.putString("name",name);
        preferencesEditor.putString("type",type);
        preferencesEditor.putString("city",cityID);
        preferencesEditor.putString("actImg_Status",actImg_Status);
        preferencesEditor.apply();
    }

    public void setActImg(String str){
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putString("actImg_Status",str);
        preferencesEditor.apply();
    }

    public String[] getData()
    {
     return new String[]{
             mPreferences.getString("id","null"),
             mPreferences.getString("image","null"),
             mPreferences.getString("email","null"),
             mPreferences.getString("name","null"),
             mPreferences.getString("type","null"),
             mPreferences.getString("city","null"),
             mPreferences.getString("actImg_Status","null")
     };
    }

    public Boolean checkData()
    {
        if(mPreferences.contains("id") && mPreferences.contains("name"))
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
