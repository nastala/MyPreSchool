package com.example.mypreschool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.mypreschool.Adapters.ChatRVAdapter;
import com.example.mypreschool.Classes.ChatMessage;
import com.example.mypreschool.ViewHolders.ChatRVViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ParentChatActivity extends AppCompatActivity {
    private final String TAG = "PARENTCHATACTIVITY";

    private RecyclerView rvChat;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String username;
    private ArrayList<ChatMessage> messages;
    private DatabaseReference databaseReference;
    private ChatRVAdapter adapter;
    private FirebaseRecyclerAdapter<ChatMessage, ChatRVViewHolder> firebaseRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Button btnSend;
    private EditText etMessage;
    private SharedPref sharedPref;
    private String user1, user2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_chat);

        Intent intent = getIntent();
        if(intent.getExtras() == null){
            this.finish();
            return;
        }

        user1 = intent.getExtras().getString("user1");
        user2 = intent.getExtras().getString("user2");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        linearLayoutManager = new LinearLayoutManager(this);
        sharedPref = new SharedPref(this);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        if(firebaseUser == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        username = firebaseUser.getDisplayName();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
            @Override
            public ChatMessage parseSnapshot(DataSnapshot snapshot) {
                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                if(chatMessage != null){
                    chatMessage.setId(snapshot.getKey());
                }

                return chatMessage;
            }
        };

        DatabaseReference messageRef = databaseReference.child(user1).child(user2);
        FirebaseRecyclerOptions<ChatMessage> options =  new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(messageRef, parser)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatRVViewHolder>(options) {
            @Override
            protected void onBindViewHolder(ChatRVViewHolder holder, int position, ChatMessage model) {
                if(model == null){
                    Log.d(TAG, "Message null");
                    return;
                }

                if(model.getUid().equals(firebaseUser.getUid()))
                    holder.setRlChatGravityEnd();

                holder.setTvDate(model.getDate());
                holder.setTvUsername(model.getUsername());
                holder.setTvMessage(model.getMessage());
            }

            @Override
            public ChatRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
                return new ChatRVViewHolder(layoutInflater.inflate(R.layout.adapter_chat_rv, parent, false));
            }
        };

        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int messageCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();

                if(lastVisiblePosition == -1 || (positionStart >= (messageCount - 1 ) &&
                        lastVisiblePosition == (positionStart - 1))){

                    rvChat.scrollToPosition(positionStart);
                }
            }
        });

        rvChat.setLayoutManager(linearLayoutManager);
        rvChat.setAdapter(firebaseRecyclerAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check()){
                    Date currentDate = Calendar.getInstance().getTime();
                    ChatMessage chatMessage = new ChatMessage(sharedPref.getUsername(), firebaseAuth.getUid(), etMessage.getText().toString(), currentDate);
                    databaseReference.child(user1).child(user2).push().setValue(chatMessage);
                    databaseReference.child(user2).child(user1).push().setValue(chatMessage);
                    etMessage.setText("");
                }
            }
        });
    }

    private boolean check() {
        boolean check = true;

        if(etMessage.getText().toString().isEmpty()){
            check = false;
        }

        return check;
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseRecyclerAdapter.stopListening();
    }
}
