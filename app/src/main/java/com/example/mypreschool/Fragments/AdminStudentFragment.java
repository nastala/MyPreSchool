package com.example.mypreschool.Fragments;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mypreschool.Adapters.StudentAdapter;
import com.example.mypreschool.Classes.Parent;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminStudentFragment extends Fragment {
    private static final String TAG = "ADMINSTUDENT";
    private static final int IMAGE_REQUEST = 10001;

    private StorageReference mStorage;
    private FirebaseFirestore db;
    private ListView lvStudents;
    private ProgressBar pbAdmin;
    private TextView tvAdminAddStudent;
    private Student student;
    private CircleImageView civStudent;
    private Uri uri;
    private Spinner spnAdminParent;
    private LinearLayout llAdminStudents;
    private Parent sParent;

    private ArrayList<Parent> parents;
    private ArrayList<Student> students;

    public AdminStudentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_student, container, false);

        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        students = new ArrayList<>();
        parents = new ArrayList<>();

        lvStudents = view.findViewById(R.id.lvStudents);
        pbAdmin = view.findViewById(R.id.pbAdmin);
        tvAdminAddStudent = view.findViewById(R.id.tvAdminAddStudent);
        spnAdminParent = view.findViewById(R.id.spnAdminParent);
        llAdminStudents = view.findViewById(R.id.llAdminStudents);

        spnAdminParent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!(parents.size() > 0))
                    return;

                sParent = parents.get(position);
                listStudents(sParent.getUid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bringParents();

        return view;
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

                if(parents.size() > 0)
                    listStudents(parents.get(0).getUid());
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, parents2);
                spnAdminParent.setAdapter(arrayAdapter);
                pbAdmin.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbAdmin.setVisibility(View.GONE);
            }
        });
    }

    private void listStudents(final String parentID){
        students.clear();
        lvStudents.setAdapter(null);
        pbAdmin.setVisibility(View.VISIBLE);
        db.collection("Parents").document(parentID).collection("Students").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(documentSnapshots == null) {
                    pbAdmin.setVisibility(View.GONE);
                    return;
                }

                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!(documentSnapshot.exists())){
                        pbAdmin.setVisibility(View.GONE);
                        return;
                    }

                    Student student = new Student();
                    student.setName(documentSnapshot.getString("name"));
                    student.setParentID(documentSnapshot.getString("parentID"));
                    student.setSgurl(documentSnapshot.getString("sgurl"));
                    student.setStudentID(documentSnapshot.getId());
                    Log.d(TAG, student.getName());
                    students.add(student);
                }
                StudentAdapter adapter = new StudentAdapter(getActivity(), students);
                lvStudents.setAdapter(adapter);
                pbAdmin.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "HATA " + e.getMessage());
            }
        });

        tvAdminAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddStudentDialog(parentID);
                pbAdmin.setVisibility(View.GONE);
            }
        });

        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                student = students.get(position);
                showEditStudentDialog(student);
            }
        });
    }

    private void showAddStudentDialog(final String parentID){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_student_dialog);
        final EditText etStudentName = dialog.findViewById(R.id.etStudentName);
        Button btnAddStudent = dialog.findViewById(R.id.btnAddStudent);
        CircleImageView civStudent = dialog.findViewById(R.id.civStudent);
        final ProgressBar pbAddStudent = dialog.findViewById(R.id.pbAddStudent);
        mStorage = FirebaseStorage.getInstance().getReference();

        civStudent.setVisibility(View.GONE);

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbAddStudent.setVisibility(View.VISIBLE);

                Map<String, String> studentDetail = new HashMap<>();
                String studentName = etStudentName.getText().toString();
                studentDetail.put("name", studentName);
                studentDetail.put("parentID", parentID);
                studentDetail.put("sgurl", "default");

                db.collection("Parents").document(parentID).collection("Students").document().set(studentDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Student Added");
                        dialog.dismiss();
                        pbAddStudent.setVisibility(View.GONE);
                        students.clear();
                        listStudents(parentID);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Student Not Added");
                        dialog.dismiss();
                        pbAddStudent.setVisibility(View.GONE);
                    }
                });
            }
        });

        dialog.show();
    }

    private void showEditStudentDialog(final Student student){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_student_dialog);
        final EditText etStudentName = dialog.findViewById(R.id.etStudentName);
        Button btnAddStudent = dialog.findViewById(R.id.btnAddStudent);
        civStudent = dialog.findViewById(R.id.civStudent);
        final ProgressBar pbAddStudent = dialog.findViewById(R.id.pbAddStudent);
        mStorage = FirebaseStorage.getInstance().getReference();

        if(!(student.getSgurl().equals("default"))){
            Glide.with(civStudent.getContext())
                    .load(student.getSgurl())
                    .into(civStudent);
        }

        etStudentName.setText(student.getName());
        btnAddStudent.setText("UPDATE STUDENT");

        civStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST);
            }
        });

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbAddStudent.setVisibility(View.VISIBLE);

                Map<String, Object> studentDetail = new HashMap<>();
                String studentName = etStudentName.getText().toString();
                studentDetail.put("name", studentName);
                studentDetail.put("sgurl", "default");

                db.collection("Parents").document(student.getParentID()).collection("Students").document(student.getStudentID()).update(studentDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Student Added");
                        dialog.dismiss();
                        pbAddStudent.setVisibility(View.GONE);
                        students.clear();
                        listStudents(student.getParentID());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Student Not Added");
                        dialog.dismiss();
                        pbAddStudent.setVisibility(View.GONE);
                    }
                });
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == IMAGE_REQUEST){
            if(data.getData() == null)
                return;

            Uri uri = data.getData();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
