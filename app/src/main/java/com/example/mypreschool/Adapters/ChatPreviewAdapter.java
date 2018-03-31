package com.example.mypreschool.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mypreschool.Classes.ChatPreview;
import com.example.mypreschool.Classes.Chats;
import com.example.mypreschool.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatPreviewAdapter extends BaseAdapter {
    private ArrayList<ChatPreview> chats;
    private LayoutInflater layoutInflater;

    public ChatPreviewAdapter(Activity activity, ArrayList<ChatPreview> chats){
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.chats = chats;
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int i) {
        return chats.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View mView = layoutInflater.inflate(R.layout.adapter_chat_preview, null);

        TextView tvTitle = mView.findViewById(R.id.tvTitle);
        TextView tvMessage = mView.findViewById(R.id.tvMessage);
        TextView tvDate = mView.findViewById(R.id.tvDate);

        Chats chat = chats.get(i).getChats();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        tvTitle.setText(chat.getTitle());
        tvMessage.setText(chat.getLastMessage());
        tvDate.setText(simpleDateFormat.format(new Date(chat.getTimestamp())));

        return mView;
    }

    public void update(ArrayList<ChatPreview> chats){
        this.chats = chats;
        this.notifyDataSetChanged();
    }
}
