package com.example.mypreschool.Requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;

public class MessageRequest extends StringRequest {
    private static final String REQUEST_URL = "http://codegiveaway.xyz/nastala/MessageNotification.php";
    private HashMap<String, String> params;

    public MessageRequest(String token, String message, String sender, String key, Response.Listener<String> listener){
        super(Method.POST, REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("token", token);
        params.put("message", message);
        params.put("sender", sender);
        params.put("key", key);
    }

    @Override
    public HashMap<String, String> getParams() {
        return params;
    }
}
