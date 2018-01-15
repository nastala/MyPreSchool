package com.example.mypreschool.Classes;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Nastala on 1/13/2018.
 */

public class ShareActivityRequest extends StringRequest {
    private static final String REQUEST_URL = "http://codegiveaway.xyz/nastala/PushNotification.php";
    private HashMap<String, String> params;

    public ShareActivityRequest(String token, String message, Response.Listener<String> listener){
        super(Method.POST, REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("token", token);
        params.put("message", message);
    }

    @Override
    public HashMap<String, String> getParams() {
        return params;
    }
}
