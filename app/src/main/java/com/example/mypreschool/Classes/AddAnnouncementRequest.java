package com.example.mypreschool.Classes;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;

/**
 * Created by Nastala on 1/15/2018.
 */

public class AddAnnouncementRequest extends StringRequest {
    private static final String REQUEST_URL = "http://codegiveaway.xyz/nastala/ParentAnnouncementNotification.php";
    private HashMap<String, String> params;

    public AddAnnouncementRequest(String token, Announcement announcement, Response.Listener<String> listener){
        super(Method.POST, REQUEST_URL, listener, null);

        params = new HashMap<>();
        params.put("token", token);
        params.put("title", announcement.getTitle());
        params.put("schoolName", announcement.getSchoolName());
    }

    @Override
    public HashMap<String, String> getParams() {
        return params;
    }
}
