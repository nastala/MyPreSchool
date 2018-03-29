package com.example.mypreschool.Classes;

import java.util.Date;

/**
 * Created by sezgi on 3/29/2018.
 */

public class ChatMessage {
    private String username, uid, message, id;
    private Date date;

    public ChatMessage(String username, String uid, String message, Date date) {
        this.username = username;
        this.uid = uid;
        this.message = message;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
