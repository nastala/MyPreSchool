package com.example.mypreschool.Fragments.BottomNavigationFragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.mypreschool.Adapters.ChatPreviewAdapter;
import com.example.mypreschool.Classes.ChatPreview;
import com.example.mypreschool.Classes.Chats;
import com.example.mypreschool.ParentChatActivity;
import com.example.mypreschool.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ParentChatMainFragment extends Fragment {
    private final String TAG = "PARENTCHATMAIN";

    private ChatPreviewAdapter adapter;
    private DatabaseReference database;
    private ProgressBar pbChat;
    private ListView lvChats;
    private ArrayList<ChatPreview> chats;
    private ArrayList<String> chatIds;
    private FirebaseAuth mAuth;

    public ParentChatMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parent_chat_main, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        chats = new ArrayList<>();
        chatIds = new ArrayList<>();

        lvChats = view.findViewById(R.id.lvChats);
        pbChat = view.findViewById(R.id.pbChat);

        lvChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goChatActivity(chats.get(i).getKey());
            }
        });

        bringChatIds();

        return view;
    }

    private void goChatActivity(String key){
        Intent intent = new Intent(getActivity(), ParentChatActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    private void bringChatIds() {
        Log.d(TAG, "bringChatIds()");

        pbChat.setVisibility(View.VISIBLE);
        chatIds = new ArrayList<>();

        database.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.d(TAG, "No chatids found");
                    pbChat.setVisibility(View.GONE);
                    return;
                }

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if(dataSnapshot1.getValue() == null) {
                        Log.d(TAG, "DataSnapshot1 null");
                        return;
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(dataSnapshot1.getValue().toString());
                        if(jsonObject.getBoolean(mAuth.getUid())){
                            Log.d(TAG, mAuth.getUid() + " " + dataSnapshot1.getKey() + "'a kayıtlı.");
                            chatIds.add(dataSnapshot1.getKey());
                        }

                    } catch (JSONException e) {
                        pbChat.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }

                bringChats();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pbChat.setVisibility(View.GONE);
            }
        });
    }

    private void bringChats() {
        Log.d(TAG, "bringChats()");

        if(chatIds.size() < 1){
            Log.d(TAG, "ChatIds size < 1");
            pbChat.setVisibility(View.GONE);
            return;
        }

        chats = new ArrayList<>();

        for(String id : chatIds){
            database.child("chats").child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.exists()){
                        Log.d(TAG, "no chats found");
                        pbChat.setVisibility(View.GONE);
                        return;
                    }

                    checkAndAdd(new ChatPreview(dataSnapshot.getValue(Chats.class), dataSnapshot.getKey()));

                    if(chats.size() >= 1)
                        showLvChats();
                    else
                        Log.d(TAG, "Chats size: " + chats.size());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    pbChat.setVisibility(View.GONE);
                }
            });
        }
    }

    private void checkAndAdd(ChatPreview chatPreview) {
        Log.d(TAG, "checkAndAdd() " + chatPreview.getChats().getLastMessage());

        boolean check = false;
        for(int i = 0; i < chats.size(); i++){
            if(chats.get(i).getKey().equals(chatPreview.getKey())) {
                chats.get(i).setChats(chatPreview.getChats());
                check = true;
                break;
            }
        }

        if(!check)
            chats.add(chatPreview);
    }

    private void showLvChats(){
        Log.d(TAG, "showLvChats()");

        if(chats == null || chats.size() < 1){
            Log.d(TAG, "Chats size < 1 or null");
            pbChat.setVisibility(View.GONE);
            return;
        }

        if(chats.size() > 1)
            orderChats();

        if(getActivity() == null) {
            pbChat.setVisibility(View.GONE);
            return;
        }

        if(adapter == null)
            adapter = new ChatPreviewAdapter(getActivity(), chats);
        else
            adapter.update(chats);

        lvChats.setAdapter(adapter);
        pbChat.setVisibility(View.GONE);
        lvChats.setVisibility(View.VISIBLE);
    }

    private void orderChats() {
        Log.d(TAG, "orderChats()");

        Collections.sort(chats, new Comparator<ChatPreview>() {
            @Override
            public int compare(ChatPreview chatPreview, ChatPreview t1) {
                Date date1 = new Date(chatPreview.getChats().getTimestamp());
                Date date2 = new Date(t1.getChats().getTimestamp());
                return date2.compareTo(date1);
            }
        });
    }
}
