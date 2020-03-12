package com.wattathlon.wattathlon2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper {
    private final static String TAG = "Database";

    public DataBase(Context context) {
        super(context,"Login.db",  null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase base) {
        Log.d(TAG, "creating table...");
        base.execSQL("Create table users (email text primary key, password text, name text, height int," +
                "weight int, rowFtp int, bikeFtp int, skiFtp int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase base, int i, int i1) {
        base.execSQL("drop table if exists users");
    }

    public boolean insert(String email, String password, String name, int height, int weight, int rowFtp,
                            int bikeFtp, int skiFtp) {

        SQLiteDatabase base = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("email", email);
        values.put("password", password);
        values.put("name", name);
        values.put("height", height);
        values.put("weight", weight);
        values.put("rowFtp", rowFtp);
        values.put("bikeFtp", bikeFtp);
        values.put("skiFtp", skiFtp);

        long insertion = base.insert("users", null, values);

        if(insertion == -1)
            return false; //failed
        else
            return true;
    }

    public boolean update (ContentValues values, String userEmail) {
       long update =  getWritableDatabase().update("users", values, "email=?", new String[] {userEmail});

        if(update == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    //if email exists
    public boolean checkEmail(String email) {
        SQLiteDatabase base = this.getReadableDatabase();
        Cursor cur = base.rawQuery("Select * from users where email=?", new String[]{email});

        if(cur.getCount() > 0)
            return false;
        else
            return true;
    }

    public boolean emailAndPass(String email, String password) {
        SQLiteDatabase base = this.getReadableDatabase();
        Cursor cur = base.rawQuery("Select * from users where email=? and password=?", new String[]{email,password});

        if(cur.getCount() > 0)
            return true;
        else
            return false;
    }

    public String getName(String email, String password) {
        SQLiteDatabase base = this.getReadableDatabase();
        Cursor cur = base.rawQuery("Select * from users where email=? and password=?", new String[] {email, password});

        cur.moveToFirst(); //index = 0

        return cur.getString(cur.getColumnIndex("name"));
    }

    public int getHeight(String email, String password) {
        SQLiteDatabase base = this.getReadableDatabase();
        Cursor cur = base.rawQuery("Select * from users where email=? and password=?", new String[] {email, password});

        cur.moveToFirst(); //index = 0

        return cur.getInt(cur.getColumnIndex("height"));
    }

    public int getWeight(String email, String password) {
        SQLiteDatabase base = this.getReadableDatabase();
        Cursor cur = base.rawQuery("Select * from users where email=? and password=?", new String[] {email, password});

        cur.moveToFirst(); //index = 0

        return cur.getInt(cur.getColumnIndex("weight"));
    }

    public int getRowFtp(String email, String password) {
        SQLiteDatabase base = this.getReadableDatabase();
        Cursor cur = base.rawQuery("Select * from users where email=? and password=?", new String[] {email, password});

        cur.moveToFirst(); //index = 0

        return cur.getInt(cur.getColumnIndex("rowFtp"));
    }

    public int getBikeFtp(String email, String password) {
        SQLiteDatabase base = this.getReadableDatabase();
        Cursor cur = base.rawQuery("Select * from users where email=? and password=?", new String[] {email, password});

        cur.moveToFirst(); //index = 0

        return cur.getInt(cur.getColumnIndex("bikeFtp"));
    }

    public int getSkiFtp(String email, String password) {
        SQLiteDatabase base = this.getReadableDatabase();
        Cursor cur = base.rawQuery("Select * from users where email=? and password=?", new String[] {email, password});

        cur.moveToFirst(); //index = 0

        return cur.getInt(cur.getColumnIndex("skiFtp"));
    }
}
