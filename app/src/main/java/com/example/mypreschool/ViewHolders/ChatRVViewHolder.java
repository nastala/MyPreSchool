package com.example.mypreschool.ViewHolders;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mypreschool.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sezgi on 3/29/2018.
 */

public class ChatRVViewHolder extends RecyclerView.ViewHolder {
    private TextView tvMessage, tvDate, tvUsername;
    private RelativeLayout rlChat;
    private Context context;

    public ChatRVViewHolder(View itemView) {
        super(itemView);

        context = itemView.getContext();

        tvMessage = itemView.findViewById(R.id.tvMessage);
        tvDate = itemView.findViewById(R.id.tvDate);
        tvUsername = itemView.findViewById(R.id.tvUsername);
        rlChat = itemView.findViewById(R.id.rlChat);
    }

    public void setTvMessage(String message){
        tvMessage.setText(message);
    }

    public void setTvDate(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        tvDate.setText(dateFormat.format(date));
    }

    public void setTvUsername(String username){
        tvUsername.setText(username);
    }

    public void setRlChatGravityEnd(){
        float density = context.getResources().getDisplayMetrics().density;
        int left = ((int)(30 * density));
        int right = ((int)(5 * density));
        int top = ((int)(5 * density));
        int bottom = ((int)(5 * density));
        rlChat.setPadding(left, top, right, bottom);
    }
}
