package com.example.mypreschool.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mypreschool.Classes.ChatMessage;
import com.example.mypreschool.R;
import com.example.mypreschool.ViewHolders.ChatRVViewHolder;

import java.util.ArrayList;

/**
 * Created by sezgi on 3/29/2018.
 */

public class ChatRVAdapter extends RecyclerView.Adapter<ChatRVViewHolder> {
    private ArrayList<ChatMessage> messages;

    public ChatRVAdapter(ArrayList<ChatMessage> messages){
        this.messages = messages;
    }

    @Override
    public ChatRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_chat_rv, parent, false);

        ChatRVViewHolder viewHolder = new ChatRVViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatRVViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        holder.setTvDate(message.getZamanfarki());
        holder.setTvMessage(message.getMessage());
        holder.setTvUsername(message.getUsername());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
