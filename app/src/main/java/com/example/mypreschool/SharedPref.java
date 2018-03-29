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
    private static final String TOKENREFRESH = "tokenrefresh";
    private static final String USERNAME = "username";

    public SharedPref(Context context){
        this.context = context;
        sharedPref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPref.edit();
    }

    public void setTokenRefresh(boolean bo) {
        editor.putBoolean(TOKENREFRESH, bo);
        editor.commit();
    }

    public boolean getTokenRefresh() { return  sharedPref.getBoolean(TOKENREFRESH, false); }

    public void setTip(String tip){
        editor.putString(TIP, tip);
        editor.commit();
    }

    public String getTip(){ return  sharedPref.getString(TIP, "yok"); }

    public void setUsername(String username){
        editor.putString(USERNAME, username);
        editor.commit();
    }

    public String getUsername(){ return sharedPref.getString(USERNAME, "username"); }
}
