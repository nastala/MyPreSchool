package com.example.mypreschool.Classes;

import java.util.ArrayList;

public class ChatMessagePack {
    private ArrayList<ChatMessage> messages;
    private String senderName;

    public ChatMessagePack(ArrayList<ChatMessage> messages, String senderName) {
        this.messages = messages;
        this.senderName = senderName;
    }

    public ArrayList<ChatMessage> getMessages() {
        return messages;
    }

    public String getSenderName() {
        return senderName;
    }
}
