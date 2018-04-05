package com.example.mypreschool.Requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;

public class PermissionRequest extends StringRequest {
    private static final String REQUEST_URL = "http://codegiveaway.xyz/nastala/PermissionRequest.php";
    private HashMap<String, String> params;

    public PermissionRequest(String token, String title, Response.Listener<String> listener){
        super(Method.POST, REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("token", token);
        params.put("title", title);
    }

    @Override
    public HashMap<String, String> getParams() {
        return params;
    }
}
