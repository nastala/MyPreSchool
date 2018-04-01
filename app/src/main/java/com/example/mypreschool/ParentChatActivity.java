package com.example.mypreschool;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mypreschool.Classes.ChatMessage;
import com.example.mypreschool.Classes.Chats;
import com.example.mypreschool.Requests.MessageRequest;
import com.example.mypreschool.ViewHolders.ChatRVViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ParentChatActivity extends AppCompatActivity {
    private final String TAG = "PARENTCHATACTIVITY";

    private ArrayList<String> ids;
    private ArrayList<String> tokens;

    private RecyclerView rvChat;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<ChatMessage, ChatRVViewHolder> firebaseRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private EditText etMessage;
    private SharedPref sharedPref;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_chat);

        Intent intent = getIntent();
        if(intent.getExtras() == null){
            this.finish();
            return;
        }

        key = intent.getExtras().getString("key");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        linearLayoutManager = new LinearLayoutManager(this);
        sharedPref = new SharedPref(this);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        Button btnSend = findViewById(R.id.btnSend);

        if(firebaseUser == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

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

        DatabaseReference messageRef = databaseReference.child("messages").child(key);
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

                if(model.getUid().equals(firebaseUser.getUid())) {
                    holder.setRlChatGravityEnd();
                    holder.setTvUsername("Sen");
                }
                else
                    holder.setTvUsername(model.getUsername());

                holder.setTvDate(model.getTimestamp());
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
                    String message = etMessage.getText().toString();
                    long timestamp = Calendar.getInstance().getTimeInMillis();
                    ChatMessage chatMessage = new ChatMessage(sharedPref.getUsername(), firebaseAuth.getUid(), message, timestamp);
                    databaseReference.child("messages").child(key).push().setValue(chatMessage);
                    if(sharedPref.getTip().equals("parent")){
                        databaseReference.child("chats").child(key).setValue(new Chats(sharedPref.getUsername(), message, timestamp));
                    }
                    else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("lastMessage", message);
                        map.put("timestamp", timestamp);
                        databaseReference.child("chats").child(key).updateChildren(map);
                    }

                    prepareNotification(message, sharedPref.getUsername(), key);
                    etMessage.setText("");
                }
            }
        });
    }

    private void prepareNotification(final String message, final String username, final String key) {
        ids = new ArrayList<>();

        databaseReference.child("members").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    Log.d(TAG, "GG");
                    return;
                }

                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(!ds.getKey().equals(firebaseAuth.getUid())){
                        Log.d(TAG, "Id " + ds.getKey() + " Ids'e eklendi");
                        ids.add(ds.getKey());
                    }
                }

                bringTokens(message, username, key);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void bringTokens(final String message, final String username, final String key) {
        if(ids.size() < 1){
            Log.d(TAG, "Ids size < 1");
            return;
        }

        tokens = new ArrayList<>();

        for(final String id : ids){
            db.collection("Users").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(!documentSnapshot.exists()){
                        Log.d(TAG, "document firestore da yok. Id: " + id);
                        return;
                    }

                    tokens.add(documentSnapshot.getString("sgcm"));

                    if(tokens.size() >= ids.size()){
                        sendNotification(message, username, key);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "firestore token getirme hata: " + e.getMessage());
                }
            });
        }
    }

    private void sendNotification(String message, String username, String key){
        if(tokens.size() < 1){
            Log.d(TAG, "tokens size < 1");
            return;
        }

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response geldi " + response);
            }
        };

        for(String token : tokens){
            Log.d(TAG, "Request oluşturuluyor. Token: " + token + " Username " + username + " Key " + key);
            MessageRequest request = new MessageRequest(token, message, username, key, listener);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
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
