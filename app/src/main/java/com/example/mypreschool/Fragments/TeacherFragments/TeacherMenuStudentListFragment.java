package com.example.mypreschool.Fragments.TeacherFragments;


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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherMenuStudentListFragment extends Fragment {
    private final String TAG = "TEACHERMENUSTUDENTLIST";
    private Teacher teacher;
    private String food, dateNow, stat;
    private int foodPosition;
    private ArrayList<String> studentNames;
    private ArrayList<Student> students;
    private FirebaseFirestore db;
    private Dialog dialog;
    private boolean kontrol, documanKontrol;

    private TextView tvDate, tvFood;
    private ProgressBar pbStudent;
    private ListView lvStudents;

    public TeacherMenuStudentListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_menu_student_list, container, false);

        db = FirebaseFirestore.getInstance();

        tvDate = view.findViewById(R.id.tvDate);
        tvFood = view.findViewById(R.id.tvFood);
        pbStudent = view.findViewById(R.id.pbStudent);
        lvStudents = view.findViewById(R.id.lvStudents);

        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogGoster(students.get(position));
            }
        });

        textViewlariDoldur();
        ogrencileriGetir();

        return view;
    }

    private void ogrencileriGetir(){
        students = new ArrayList<>();
        studentNames = new ArrayList<>();
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

                lvDoldur();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Student getirme hata: " + e.getMessage());
                pbStudent.setVisibility(View.GONE);
            }
        });
    }

    private void lvDoldur(){
        if(getActivity() == null)
            return;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, studentNames);
        lvStudents.setAdapter(adapter);
        pbStudent.setVisibility(View.GONE);
    }

    private void dialogGoster(final Student student){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_update_food_stat_dialog);

        kontrol = false;
        stat = "";
        TextView tvName = dialog.findViewById(R.id.tvName);
        final CheckBox cbNone = dialog.findViewById(R.id.cbNone);
        final CheckBox cbHalf = dialog.findViewById(R.id.cbHalf);
        final CheckBox cbFull = dialog.findViewById(R.id.cbFull);
        final ProgressBar pbDialog = dialog.findViewById(R.id.pbDialog);
        final ProgressBar pbCheck = dialog.findViewById(R.id.pbCheck);
        final LinearLayout llDialog = dialog.findViewById(R.id.llDialog);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        pbCheck.setVisibility(View.VISIBLE);

        db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                collection("FoodLists").document(dateNow).collection("Status").document(student.getStudentID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String stat2 = documentSnapshot.getString(String.valueOf(foodPosition));

                    if(stat2 != null) {
                        switch (stat2) {
                            case "none":
                                cbNone.setChecked(true);
                                break;
                            case "half":
                                cbHalf.setChecked(true);
                                break;
                            case "full":
                                cbFull.setChecked(true);
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
                Log.d(TAG, "YEMEK KONTROL HATA: " + e.getMessage());
                pbCheck.setVisibility(View.GONE);
            }
        });

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

                final Map<String, Object> map = new HashMap<>();
                map.put(foodPosition + "", stat);

                documanKontrol = false;

                db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                        collection("FoodLists").document(dateNow).collection("Status").document(student.getStudentID()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        documanKontrol = documentSnapshot.exists();
                        Log.d(TAG, "Documan kontrol: " + documanKontrol);

                        if(documanKontrol) {
                            db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                                    collection("FoodLists").document(dateNow).collection("Status").document(student.getStudentID()).update(map).
                                    addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "FoodList guncellendi");
                                            dialog.dismiss();
                                            //studentlariGetir();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "FoodList guncelleme hata: " + e.getMessage());
                                    dialog.setCancelable(true);
                                }
                            });
                        }
                        else {
                            db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                                    collection("FoodLists").document(dateNow).collection("Status").document(student.getStudentID()).set(map).
                                    addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "FoodList guncellendi");
                                            dialog.dismiss();
                                            //studentlariGetir();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "FoodList guncelleme hata: " + e.getMessage());
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

    private void textViewlariDoldur(){
        tvFood.setText(food);
        tvDate.setText(dateNow);
    }


    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public void setFoodPosition(int foodPosition) {
        this.foodPosition = foodPosition;
    }

    public void setDateNow(String dateNow) {
        this.dateNow = dateNow;
    }
}
