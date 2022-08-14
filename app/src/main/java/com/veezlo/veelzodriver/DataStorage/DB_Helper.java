package com.veezlo.veelzodriver.DataStorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB_Helper extends SQLiteOpenHelper {

    private static final String dbName="veezlo.db";
    private static final int dbVersion=1;

    public DB_Helper(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      //  db.execSQL("create table IF NOT EXISTS contact(id INTEGER PRIMARY KEY AUTOINCREMENT,adds text not null,email text not null,phone text not null,mobile text not null)");
        db.execSQL("create table if not exists hotmap(id INTEGER PRIMARY KEY AUTOINCREMENT, lat text not null,longs text not null)");
        db.execSQL("create table if not exists KMperDay(date TEXT PRIMARY KEY,km REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+dbName);
        onCreate(db);
    }

    public Boolean setValues(String lat,String longs)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("lat", lat);
        contentValues.put("longs", longs);
        Long result= db.insert("hotmap", null, contentValues);
        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public void Close()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        db.close();
    }

    public Cursor getHotmapData()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM hotmap",null);
        return cursor;
    }

    public void ClearHeapmap()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from hotmap");
        db.execSQL("delete from KMperDay");
    }

    public boolean insertDate(String date,double km)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("date",date);
        values.put("km",km);
        Long result= db.insert("KMperDay", null, values);
        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getDateandPerDayKM(String currentDate)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM KMperDay WHERE date='"+currentDate+"'",null);
        return cursor;
    }

    public void updateKMValue(double km,String date)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("km",km);
        db.update("KMperDay",values,"date='"+date+"'",null);
    }

}

