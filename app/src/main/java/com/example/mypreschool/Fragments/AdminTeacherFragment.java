package com.example.mypreschool.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypreschool.Classes.School;
import com.example.mypreschool.Classes.SchoolClass;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminTeacherFragment extends Fragment {
    private ArrayList<Teacher> teachers;
    private ArrayList<String>teachersNames;
    private ArrayList<School> schools;
    private ArrayList<SchoolClass> classes;
    private ListView lvTeachers;
    private TextView tvAddTeacher;
    private FirebaseFirestore db;
    private EditText etTeacherName, etTeacherPhoneNumber;
    private Spinner spnSchools, spnClasses;
    private ProgressBar pbAddTeacher;
    private Dialog dialog;

    public AdminTeacherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_teacher, container, false);

        db = FirebaseFirestore.getInstance();

        lvTeachers = view.findViewById(R.id.lvTeachers);
        tvAddTeacher = view.findViewById(R.id.tvAddTeacher);

        tvAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTeacherDialog();
            }
        });

        bringTeachers();

        return view;
    }

    private void bringTeachers(){
        teachers = new ArrayList<>();
        teachersNames = new ArrayList<>();

        db.collection("Teachers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!(documentSnapshot.exists()))
                        continue;

                    Teacher teacher = new Teacher();
                    teacher.setTeacherClassID(documentSnapshot.getString("classID"));
                    teacher.setTeacherName(documentSnapshot.getString("name"));
                    teacher.setTeacherPhoneNumber(documentSnapshot.getLong("phoneNumber").intValue());
                    teacher.setTeacherID(documentSnapshot.getId());

                    teachersNames.add(teacher.getTeacherName());
                    teachers.add(teacher);
                    Log.d("ADMINTEACHER", "Teacher Name: " + teacher.getTeacherName());
                }

                fillLvTeachers();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ADMINTEACHER", "ERROR: " + e.getMessage());
            }
        });
    }

    private void fillLvTeachers(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, teachersNames);
        lvTeachers.setAdapter(adapter);
    }

    private void showAddTeacherDialog(){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_teacher_dialog);

        TextView tvAddTeacher = dialog.findViewById(R.id.tvAddTeacher);
        spnClasses = dialog.findViewById(R.id.spnClasses);
        spnSchools = dialog.findViewById(R.id.spnSchools);
        etTeacherName = dialog.findViewById(R.id.etTeacherName);
        etTeacherPhoneNumber = dialog.findViewById(R.id.etTeacherPhoneNumber);
        pbAddTeacher = dialog.findViewById(R.id.pbAddTeacher);

        tvAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTeacher();
            }
        });

        spnSchools.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillDialogClasses(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fillDialogSchools();
        dialog.show();
    }

    private void addTeacher(){
        if(classes.size() < 1)
            return;

        if(schools.size() < 1)
            return;

        pbAddTeacher.setVisibility(View.VISIBLE);
        long teacherPN = Integer.parseInt(etTeacherPhoneNumber.getText().toString());

        Map<String, Object> teacherDetails = new HashMap<>();
        teacherDetails.put("name", etTeacherName.getText().toString());
        teacherDetails.put("classID", classes.get((int)spnClasses.getSelectedItemId()).getClassID());
        teacherDetails.put("phoneNumber", teacherPN);

        Map<String, Object> map = new HashMap<>();
        map.put("teacher_assigned", true);

        Log.d("ADMINTEACHER", "spnSchool: " + spnSchools.getSelectedItemId());
        Log.d("ADMINTEACHER", "spnClasses: " + spnClasses.getSelectedItemId());
        Log.d("ADMINTEACHER", "School Name: " + schools.get((int) spnSchools.getSelectedItemId()).getSchoolName());
        Log.d("ADMINTEACHER", "Class Name: " + classes.get((int) spnClasses.getSelectedItemId()).getClassName());

        DocumentReference teacherRef = db.collection("Teachers").document();
        DocumentReference classRef = db.collection("Schools").document(schools.get((int) spnSchools.getSelectedItemId()).
                getSchoolID()).collection("Classes").document(classes.get((int) spnClasses.getSelectedItemId()).getClassID());

        WriteBatch batch = db.batch();

        batch.set(teacherRef, teacherDetails);
        batch.update(classRef, map);

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Teacher Added", Toast.LENGTH_SHORT).show();
                pbAddTeacher.setVisibility(View.GONE);
                dialog.dismiss();

                bringTeachers();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ADMINTEACHER", "TEACHER ADD FAILURE E: " + e.getMessage());
                pbAddTeacher.setVisibility(View.GONE);
            }
        });

    }

    private void fillDialogClasses(int position){
        pbAddTeacher.setVisibility(View.VISIBLE);
        classes = new ArrayList<>();
        final ArrayList<String> classesNames = new ArrayList<>();

        CollectionReference collectionReference = db.collection("Schools").document(schools.get(position).getSchoolID()).collection("Classes");
        Query query = collectionReference.whereEqualTo("teacher_assigned", false);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!(documentSnapshot.exists())) {
                        Toast.makeText(getActivity(), "Herhangi bir sınıf bulunamadı", Toast.LENGTH_SHORT).show();
                        
                        continue;
                    }

                    SchoolClass schoolClass = new SchoolClass();
                    schoolClass.setClassName(documentSnapshot.getString("name"));
                    schoolClass.setClassID(documentSnapshot.getId());
                    
                    classesNames.add(schoolClass.getClassName());
                    classes.add(schoolClass);
                }
                
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, classesNames);
                spnClasses.setAdapter(adapter);
                pbAddTeacher.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbAddTeacher.setVisibility(View.GONE);
                Log.d("ADMINTEACHER", "ERROR BRINGING CLASSES E: " + e.getMessage());
            }
        });
    }

    private void fillDialogSchools(){
        pbAddTeacher.setVisibility(View.VISIBLE);
        schools = new ArrayList<>();
        final ArrayList<String> schoolsNames = new ArrayList<>();

        db.collection("Schools").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!(documentSnapshot.exists()))
                        continue;

                    School school = new School();
                    school.setSchoolName(documentSnapshot.getString("name"));
                    school.setSchoolID(documentSnapshot.getId());

                    schools.add(school);
                    schoolsNames.add(school.getSchoolName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, schoolsNames);
                spnSchools.setAdapter(adapter);
                pbAddTeacher.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ADMINTEACHER", "ERROR BRINGING SCHOOLS E: " + e.getMessage());
                pbAddTeacher.setVisibility(View.GONE);
            }
        });
    }
}
