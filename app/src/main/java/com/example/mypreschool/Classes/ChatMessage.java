package com.example.mypreschool.Classes;

import java.util.Date;

/**
 * Created by sezgi on 3/29/2018.
 */

public class ChatMessage {
    private String username, uid, message, id;
    private long timestamp;

    public ChatMessage(String username, String uid, String message, long timestamp) {
        this.username = username;
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ChatMessage() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
