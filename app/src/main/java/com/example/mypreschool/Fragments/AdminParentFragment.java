package com.example.mypreschool.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.mypreschool.Classes.Parent;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminParentFragment extends Fragment {
    private static final String TAG = "ADMINPARENT";

    private FirebaseFirestore db;
    private ListView lvParents;
    private ProgressBar pbAdmin, pbAddParent;
    private ArrayList<Parent> parents;
    private ArrayList<Student> students;
    private TextView tvAdminAddParent;

    public AdminParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_parent, container, false);

        db = FirebaseFirestore.getInstance();
        parents = new ArrayList<>();
        students = new ArrayList<>();

        tvAdminAddParent = view.findViewById(R.id.tvAdminAddParent);
        lvParents = view.findViewById(R.id.lvParents);
        pbAdmin = view.findViewById(R.id.pbAdmin);

        lvParents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        tvAdminAddParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddParentDialog();
            }
        });

        bringParents();
        return view;
    }

    private void showAddParentDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_parent_dialog);
        final EditText etParentName = dialog.findViewById(R.id.etParentName);
        Button btnAddStudent = dialog.findViewById(R.id.btnAddParent);
        final ProgressBar pbAddParent = dialog.findViewById(R.id.pbAddParent);

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbAddParent.setVisibility(View.VISIBLE);

                Map<String, String> parentDetail = new HashMap<>();
                String parentName = etParentName.getText().toString();
                parentDetail.put("name", parentName);

                db.collection("Parents").document().set(parentDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Parent Added");
                        dialog.dismiss();
                        pbAddParent.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Parent Not Added " + e.getMessage());
                        dialog.dismiss();
                        pbAddParent.setVisibility(View.GONE);
                    }
                });
            }
        });

        dialog.show();
    }

    private void bringParents(){
        final ArrayList<String> parents2 = new ArrayList<>();
        pbAdmin.setVisibility(View.VISIBLE);

        db.collection("Parents").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(documentSnapshots == null)
                    return;

                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    String name = documentSnapshot.getString("name");
                    String id = documentSnapshot.getId();
                    Parent parent = new Parent(name, id);
                    parents.add(parent);
                    parents2.add(name);
                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, parents2);
                lvParents.setAdapter(arrayAdapter);
                pbAdmin.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbAdmin.setVisibility(View.GONE);
            }
        });
    }
}
