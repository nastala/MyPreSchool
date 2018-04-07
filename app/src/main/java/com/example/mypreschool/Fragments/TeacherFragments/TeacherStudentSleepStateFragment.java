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
import android.widget.LinearLayout;
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
public class TeacherStudentSleepStateFragment extends Fragment {
    private final String TAG = "TEACHERSTUDENTSLEEP";

    private FirebaseFirestore db;
    private ProgressBar pbStudent;
    private ListView lvStudents;
    private ArrayList<Student> students;
    private ArrayList<String> studentNames;
    private Teacher teacher;
    private TextView tvDate;
    private Dialog dialog;
    private boolean kontrol;
    private String dateNow, stat;
    private boolean documanKontrol;

    public TeacherStudentSleepStateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_student_sleep_state, container, false);

        db = FirebaseFirestore.getInstance();
        students = new ArrayList<>();
        studentNames = new ArrayList<>();

        pbStudent = view.findViewById(R.id.pbStudent);
        lvStudents = view.findViewById(R.id.lvStudents);
        tvDate = view.findViewById(R.id.tvDate);

        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialogGoster(students.get(i));
            }
        });

        fillTvDate();
        bringStudents();

        return view;
    }

    private void bringStudents() {
        studentNames.clear();
        students.clear();
        pbStudent.setVisibility(View.VISIBLE);

        db.collection("Students").whereEqualTo("classID", teacher.getTeacherClassID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()){
                        Toast.makeText(getActivity(), "There is no student in this class", Toast.LENGTH_SHORT).show();
                        pbStudent.setVisibility(View.GONE);
                        return;
                    }

                    Student student = new Student();
                    student.setStudentID(documentSnapshot.getId());
                    student.setName(documentSnapshot.getString("name"));

                    students.add(student);
                    studentNames.add(documentSnapshot.getString("name"));
                }

                showLvStudents();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Student getirme hata: " + e.getMessage());
                pbStudent.setVisibility(View.GONE);
            }
        });
    }

    private void showLvStudents() {
        if(getActivity() == null)
            return;

        if(students.size() < 1 || studentNames.size() < 1){
            Log.d(TAG, "Students size < 1");
            pbStudent.setVisibility(View.GONE);
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, studentNames);
        lvStudents.setAdapter(adapter);
        pbStudent.setVisibility(View.GONE);
        lvStudents.setVisibility(View.VISIBLE);
    }

    private void fillTvDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        dateNow = format.format(date);
        tvDate.setText(dateNow);
    }

    private void dialogGoster(final Student student){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_student_sleep_state);

        kontrol = false;
        stat = "";
        final CheckBox cbSlept = dialog.findViewById(R.id.cbSlept);
        final CheckBox cbNot = dialog.findViewById(R.id.cbNot);
        final ProgressBar pbDialog = dialog.findViewById(R.id.pbDialog);
        final ProgressBar pbCheck = dialog.findViewById(R.id.pbCheck);
        final LinearLayout llDialog = dialog.findViewById(R.id.llDialog);
        TextView tvName = dialog.findViewById(R.id.tvName);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        pbCheck.setVisibility(View.VISIBLE);

        db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                collection("SleepStates").document(dateNow).collection("Status").document(student.getStudentID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String stat2 = documentSnapshot.getString("status");

                    if(stat2 != null) {
                        switch (stat2) {
                            case "slept":
                                cbSlept.setChecked(true);
                                break;
                            case "not":
                                cbNot.setChecked(true);
                                break;
                        }
                    }
                }

                pbCheck.setVisibility(View.GONE);
                llDialog.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Uyku kontrol HATA: " + e.getMessage());
                pbCheck.setVisibility(View.GONE);
            }
        });

        cbSlept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbNot.setChecked(false);
                    kontrol = true;
                    stat = "slept";
                }
            }
        });

        cbNot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    cbSlept.setChecked(false);
                    kontrol = true;
                    stat = "not";
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

                final Map<String, Object> map = new HashMap<>();
                map.put("status", stat);

                documanKontrol = false;

                db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                        collection("SleepStates").document(dateNow).collection("Status").document(student.getStudentID()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                documanKontrol = documentSnapshot.exists();
                                Log.d(TAG, "Documan kontrol: " + documanKontrol);

                                if(documanKontrol) {
                                    db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                                            collection("SleepStates").document(dateNow).collection("Status").document(student.getStudentID()).update(map).
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "Sleep State guncellendi");
                                                    dialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "SleepState guncelleme hata: " + e.getMessage());
                                            dialog.setCancelable(true);
                                        }
                                    });
                                }
                                else {
                                    db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                                            collection("SleepStates").document(dateNow).collection("Status").document(student.getStudentID()).set(map).
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "SleepState guncellendi");
                                                    dialog.dismiss();
                                                    //studentlariGetir();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "SleepState guncelleme hata: " + e.getMessage());
                                            dialog.setCancelable(true);
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "DOKUMAN KONTROL HATA: " + e.getMessage());
                    }
                });
            }
        });

        tvName.setText(student.getName());

        dialog.show();
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
