package com.example.mypreschool.Classes;

public class ChatPreview {
    private Chats chats;
    private String key;

    public ChatPreview(Chats chats, String key) {
        this.chats = chats;
        this.key = key;
    }

    public Chats getChats() {
        return chats;
    }

    public String getKey() {
        return key;
    }

    public void setChats(Chats chats) {
        this.chats = chats;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
