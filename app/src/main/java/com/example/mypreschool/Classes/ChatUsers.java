package com.example.mypreschool.Classes;

public class ChatUsers {
    private String name;
    private long timestamp;

    public ChatUsers(String name, long timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
