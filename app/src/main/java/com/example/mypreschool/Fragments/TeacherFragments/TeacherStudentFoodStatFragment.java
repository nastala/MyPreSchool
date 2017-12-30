package com.example.mypreschool.Fragments.TeacherFragments;


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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherStudentFoodStatFragment extends Fragment {
    private final String TAG = "TEACHERSTUDENTFOODSTAT";

    private FirebaseFirestore db;
    private Teacher teacher;
    private ArrayList<Student> students;
    private ArrayList<String> studentNames;
    private ListView lvStudents;
    private ProgressBar pbStat;
    private Dialog dialog;
    private String currentDate;
    private boolean kontrol;
    private String stat;

    public TeacherStudentFoodStatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_student_food_stat, container, false);

        db = FirebaseFirestore.getInstance();
        lvStudents = view.findViewById(R.id.lvStudents);
        pbStat = view.findViewById(R.id.pbStat);

        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(students.get(position).getStat() != null && students.get(position).isStated())
                    dialogGoster(students.get(position), students.get(position).getStat());
                else
                    dialogGoster(students.get(position), "");
            }
        });

        studentlariGetir();
        suAnkiZamaniGetir();

        return view;
    }

    private void suAnkiZamaniGetir(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date now = Calendar.getInstance().getTime();

        currentDate = dateFormat.format(now);
        Log.d(TAG, "SU ANKI ZAMAN: " + currentDate);
    }

    private void dialogGoster(final Student student, String stat2){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_update_food_stat_dialog);

        kontrol = false;
        stat = "";
        TextView tvName = dialog.findViewById(R.id.tvName);
        final CheckBox cbNone = dialog.findViewById(R.id.cbNone);
        final CheckBox cbHalf = dialog.findViewById(R.id.cbHalf);
        final CheckBox cbFull = dialog.findViewById(R.id.cbFull);
        final ProgressBar pbDialog = dialog.findViewById(R.id.pbDialog);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        switch (stat2){
            case "none":
                cbNone.setChecked(true);
                break;
            case "half":
                cbHalf.setChecked(true);
                break;
            case  "full":
                cbFull.setChecked(true);
                break;
        }

        cbNone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbHalf.setChecked(false);
                    cbFull.setChecked(false);
                    kontrol = true;
                    stat = "none";
                }
            }
        });

        cbHalf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbNone.setChecked(false);
                    cbFull.setChecked(false);
                    kontrol = true;
                    stat = "half";
                }
            }
        });

        cbFull.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbNone.setChecked(false);
                    cbHalf.setChecked(false);
                    kontrol = true;
                    stat = "full";
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!kontrol) {
                    Toast.makeText(getActivity(), "Select a checkbox", Toast.LENGTH_SHORT).show();
                    return;
                }

                pbDialog.setVisibility(View.VISIBLE);
                dialog.setCancelable(false);

                Map<String, String> map = new HashMap<>();
                map.put("stat", stat);
                db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                        collection("FoodLists").document(currentDate).collection("Status").document(student.getStudentID()).set(map).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "FoodList guncellendi");
                        dialog.dismiss();
                        student.setStated(true);
                        student.setStat(stat);
                        //studentlariGetir();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "FoodList guncelleme hata: " + e.getMessage());
                        dialog.setCancelable(true);
                        pbStat.setVisibility(View.GONE);
                    }
                });
            }
        });

        tvName.setText(student.getName());

        dialog.show();
    }

    private void studentlariGetir(){
        pbStat.setVisibility(View.VISIBLE);

        Log.d(TAG, "STUDENTLARI GETIRMEYE CALISIYORUM");
        Query query = db.collection("Students").whereEqualTo("classID", teacher.getTeacherClassID());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                students = new ArrayList<>();
                studentNames = new ArrayList<>();

                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!(documentSnapshot.exists())){
                        Log.d(TAG, "STUDENT BULUNAMADI");
                        break;
                    }

                    Student student = new Student();
                    student.setName(documentSnapshot.getString("name"));
                    student.setStudentID(documentSnapshot.getId());
                    Log.d(TAG, "STUDENT NAME: " + student.getName());
                    students.add(student);
                    studentNames.add(student.getName());
                }
                studentYemekDurumunuKontrolEt();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "STUDENT GETIRME HATA: " + e.getMessage());
                pbStat.setVisibility(View.GONE);
            }
        });
    }

    private void studentYemekDurumunuKontrolEt(){
        db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                collection("FoodLists").document(currentDate).collection("Status").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()){
                        Log.d(TAG, "STATUS OGRENCI BULUNAMADI");
                        return;
                    }

                    Log.d(TAG, "STUDENT BULUNDU");
                    studentiGuncelle(documentSnapshot.getId(), documentSnapshot.getString("stat"));
                }
                lvStudentsDoldur();
            }
        });
    }

    private void studentiGuncelle(String studentID, String stat){
        if(students == null || students.size() < 1)
            return;

        Student student = null;
        for(int i = 0; i < students.size(); i++){
            if(students.get(i).getStudentID().equals(studentID)){
                student = students.get(i);
                student.setStated(true);
                student.setStat(stat);
                break;
            }
        }
    }

    private void lvStudentsDoldur(){
        if(studentNames == null || studentNames.size() < 1)
            return;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, studentNames);
        lvStudents.setAdapter(adapter);
        lvStudents.setVisibility(View.VISIBLE);
        pbStat.setVisibility(View.GONE);
    }

    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}
