package com.example.mypreschool.Fragments.TeacherFragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.example.mypreschool.Adapters.ChatPreviewAdapter;
import com.example.mypreschool.Adapters.ParentListAdapter;
import com.example.mypreschool.Classes.ChatPreview;
import com.example.mypreschool.Classes.Chats;
import com.example.mypreschool.Classes.Parent;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.ParentChatActivity;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TeacherChatMainFragment extends Fragment {
    private final String TAG = "TEACHERCHATMAINFRAGMENT";

    private ChatPreviewAdapter adapter;
    private ParentListAdapter parentListAdapter;
    private ArrayList<ChatPreview> chats;
    private ArrayList<String> chatIds;
    private ListView lvChats;
    private ProgressBar pbChat, pbParentList;
    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FloatingActionButton fabMessage;
    private Dialog parentListDialog;
    private Teacher teacher;
    private ArrayList<Parent> parents;
    private ListView lvParentList;
    private LinearLayout llParentList;
    private EditText etChatTitle;
    private int count;


    public TeacherChatMainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_chat_main, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fabMessage = view.findViewById(R.id.fabMessage);
        lvChats = view.findViewById(R.id.lvChats);
        pbChat = view.findViewById(R.id.pbChat);

        lvChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goChatActivity(chats.get(i).getKey());
            }
        });

        fabMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showParentListDialog();
            }
        });

        return view;
    }

    private void goChatActivity(String key){
        Intent intent = new Intent(getActivity(), ParentChatActivity.class);
        intent.putExtra("key", key);
        startActivity(intent);
    }

    private void showParentListDialog() {
        parents = new ArrayList<>();
        parentListDialog = new Dialog(getActivity());
        parentListDialog.setContentView(R.layout.dialog_parent_list);

        pbParentList = parentListDialog.findViewById(R.id.pbParentList);
        lvParentList = parentListDialog.findViewById(R.id.lvParentList);
        llParentList = parentListDialog.findViewById(R.id.llParentList);
        Button btnCreate = parentListDialog.findViewById(R.id.btnCreate);
        etChatTitle = parentListDialog.findViewById(R.id.etChatTitle);

        pbParentList.setVisibility(View.VISIBLE);

        db.collection("Students").whereEqualTo("classID", teacher.getTeacherClassID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()){
                        Log.d(TAG, "Parent bulunamadı");
                        return;
                    }

                    Parent parent = new Parent();
                    parent.setUid(documentSnapshot.getString("parentID"));
                    checkParents(parent);
                }

                bringParentNames();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMembersOfNewChat();
            }
        });

        parentListDialog.show();
    }

    private void createMembersOfNewChat() {
        String title = etChatTitle.getText().toString();

        if(title.isEmpty()){
            etChatTitle.setError("Title can not be empty!");
            return;
        }

        ArrayList<Parent> checkedParents = parentListAdapter.getCheckedParents();

        if(checkedParents == null || checkedParents.size() < 1){
            Log.d(TAG, "checked parents null or size < 1");
            parentListDialog.dismiss();
            return;
        }

        Map<String, Object> members = new HashMap<>();
        Map<String, Object> chats = new HashMap<>();

        for(Parent parent : checkedParents){
            members.put(parent.getUid(), true);
        }
        members.put(mAuth.getUid(), true);

        String key = database.child("members").push().getKey();
        database.child("members").child(key).setValue(members);

        chats.put("lastMessage", "");
        chats.put("timestamp", Calendar.getInstance().getTimeInMillis());
        chats.put("title", title);

        database.child("chats").child(key).setValue(chats);

        parentListDialog.dismiss();
        goChatActivity(key);
    }

    private void bringParentNames(){
        count = 0;
        Log.d(TAG, "bringParentNames()");

        if(getActivity() == null)
            return;

        if(parents.size() < 1) {
            Log.d(TAG, "Parent size < 1");
            return;
        }

        for(int i = 0; i < parents.size(); i++){
            db.collection("Parents").document(parents.get(i).getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(!documentSnapshot.exists()){
                        Log.d(TAG, "Böyle bir parent yok kardeşim");
                        return;
                    }

                    checkAndAddParentName(documentSnapshot.getId(), documentSnapshot.getString("name"));

                    Log.d(TAG, "bringParentNames() documentSnapshots bitti.");
                    count++;

                    if(count >= parents.size())
                        showLvParentList();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Parent getirme hata: " + e.getMessage());
                }
            });
        }
    }

    private void showLvParentList() {
        Log.d(TAG, "Parents size: " + parents.size());

        ArrayList<String> parentNames = new ArrayList<>();

        for(int i = 0; i < parents.size(); i++){
            parentNames.add(parents.get(i).getIsim());
            Log.d(TAG, parents.get(i).getIsim());
        }

        Log.d(TAG, "ParentNames size: " + parentNames.size());

        if(parentNames.size() >= parents.size()){
            parentListAdapter = new ParentListAdapter(getActivity(), parents);
            lvParentList.setAdapter(parentListAdapter);
            pbParentList.setVisibility(View.GONE);
            llParentList.setVisibility(View.VISIBLE);
        }
    }

    private void checkAndAddParentName(String id, String name) {
        Log.d(TAG, "Id: " + id + " Name: " + name);

        for(int i = 0; i < parents.size(); i++){
            if(parents.get(i).getUid().equals(id)) {
                parents.get(i).setIsim(name);
                Log.d(TAG, "Id: " + id + " Name: " + name);
                break;
            }
        }
    }

    private void checkParents(Parent parent){
        if(parents.size() < 1){
            parents.add(parent);
            return;
        }

        for(int i = 0; i < parents.size(); i++){
            if(parents.get(i).getUid().equals(parent.getUid())){
                Log.d(TAG, "Parent parents a ekli");
                return;
            }
        }

        Log.d(TAG, "Parent uid: " + parent.getUid());
        parents.add(parent);
        Log.d(TAG, "Parent eklendi");
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
    }

    private void orderChats() {
        Log.d(TAG, "orderChats()");

        Collections.sort(chats, new Comparator<ChatPreview>() {
            @Override
            public int compare(ChatPreview chatPreview, ChatPreview t1) {
                Date date1 = new Date(chatPreview.getChats().getTimestamp());
                Date date2 = new Date(t1.getChats().getTimestamp());
                return date1.compareTo(date2);
            }
        });
    }

    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    @Override
    public void onResume() {
        super.onResume();
        bringChatIds();
    }
}
