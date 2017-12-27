package com.example.mypreschool;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nastala on 12/27/2017.
 */

public class SharedPref {
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Context context;

    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "mypreschool";
    private static final String TIP = "tip";

    public SharedPref(Context context){
        this.context = context;
        sharedPref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPref.edit();
    }

    public void setTip(String tip){
        editor.putString(TIP, tip);
        editor.commit();
    }

    public String getTip(){ return  sharedPref.getString(TIP, "yok"); }
}
