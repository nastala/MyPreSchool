package com.example.mypreschool.Classes;

import android.util.Log;

import com.example.mypreschool.SharedPref;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Nastala on 1/8/2018.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    private final String TAG = "FIRABASEINSTANCEID";
    private SharedPref sharedPref;

    @Override
    public void onTokenRefresh() {
        sharedPref = new SharedPref(getApplicationContext());
        sharedPref.setTokenRefresh(true);
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "token: " + token);
    }
}
