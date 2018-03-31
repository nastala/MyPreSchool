package com.example.mypreschool.Classes;

public class Chats {
    private String title, lastMessage;
    private long timestamp;

    public Chats(String title, String lastMessage, long timestamp) {
        this.title = title;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
