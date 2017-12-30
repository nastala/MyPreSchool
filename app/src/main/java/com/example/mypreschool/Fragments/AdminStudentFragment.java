package com.example.mypreschool.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mypreschool.Adapters.StudentAdapter;
import com.example.mypreschool.Classes.Parent;
import com.example.mypreschool.Classes.School;
import com.example.mypreschool.Classes.SchoolClass;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private Spinner spnAdminParent;
    private LinearLayout llAdminStudents;
    private Parent mParent;
    private Dialog dialog;
    private ArrayList<School> schools;
    private ArrayList<SchoolClass> classes;
    private ProgressBar pbAddStudent;
    private ArrayList<Parent> parents;
    private ArrayList<Student> students;
    private Spinner spnSchools, spnClasses;

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

                mParent = parents.get(position);
                listStudents(mParent.getUid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bringParents();

        return view;
    }

    private void fillDialogClasses(int position){
        pbAdmin.setVisibility(View.VISIBLE);
        classes = new ArrayList<>();
        final ArrayList<String> classesNames = new ArrayList<>();

        CollectionReference collectionReference = db.collection("Schools").document(schools.get(position).getSchoolID()).collection("Classes");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                pbAdmin.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbAdmin.setVisibility(View.GONE);
                Log.d("ADMINTEACHER", "ERROR BRINGING CLASSES E: " + e.getMessage());
            }
        });
    }

    private void fillDialogSchools(){
        pbAdmin.setVisibility(View.VISIBLE);
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
                pbAdmin.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ADMINTEACHER", "ERROR BRINGING SCHOOLS E: " + e.getMessage());
                pbAdmin.setVisibility(View.GONE);
            }
        });
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

                if(parents.size() > 0) {
                    mParent = parents.get(0);
                    listStudents(mParent.getUid());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, parents2);
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
        db.collection("Students").whereEqualTo("parentID", parentID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                StudentAdapter adapter = new StudentAdapter(getActivity(), students, new StudentAdapter.OnItemClickListener() {
                    @Override
                    public void onYorumSilClick(Student student) {
                        showRemoveStudentAlertDialog(student);
                    }
                });
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

    private void showRemoveStudentAlertDialog(final Student student){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("UYARI");
        alertDialog.setMessage("Öğrenciyi silmek istediğinizden emin misiniz?");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pbAdmin.setVisibility(View.VISIBLE);
                db.collection("Students").document(student.getStudentID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Student has been removed", Toast.LENGTH_SHORT).show();
                        pbAdmin.setVisibility(View.GONE);
                        alertDialog.dismiss();
                        listStudents(mParent.getUid());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "STUDENT REMOVE HATA: " + e.getMessage());
                        pbAdmin.setVisibility(View.GONE);
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "HAYIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showAddStudentDialog(final String parentID){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_student_dialog);
        spnClasses = dialog.findViewById(R.id.spnClasses);
        spnSchools = dialog.findViewById(R.id.spnSchools);
        final EditText etStudentName = dialog.findViewById(R.id.etStudentName);
        Button btnAddStudent = dialog.findViewById(R.id.btnAddStudent);
        CircleImageView civStudent = dialog.findViewById(R.id.civStudent);
        pbAddStudent = dialog.findViewById(R.id.pbAddStudent);
        mStorage = FirebaseStorage.getInstance().getReference();

        civStudent.setVisibility(View.GONE);

        spnSchools.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classes = null;
                fillDialogClasses(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etStudentName.getText().toString().isEmpty())
                    return;

                if(parentID == null)
                    return;

                addStudent(etStudentName.getText().toString(), parentID);
            }
        });

        fillDialogSchools();
        dialog.show();
    }

    private void addStudent(String studentName, final String parentID){
        if(classes.size() < 1)
            return;

        if(schools.size() < 1)
            return;

        pbAddStudent.setVisibility(View.VISIBLE);

        Map<String, String> studentDetail = new HashMap<>();
        studentDetail.put("name", studentName);
        studentDetail.put("parentID", parentID);
        studentDetail.put("sgurl", "default");
        studentDetail.put("classID", classes.get((int)spnClasses.getSelectedItemId()).getClassID());
        studentDetail.put("schoolID", schools.get((int)spnSchools.getSelectedItemId()).getSchoolID());

        db.collection("Students").document().set(studentDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void showEditStudentDialog(final Student student){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_student_dialog);
        spnClasses = dialog.findViewById(R.id.spnClasses);
        spnSchools = dialog.findViewById(R.id.spnSchools);
        TextView tvSelectSchool = dialog.findViewById(R.id.tvSelectSchool);
        TextView tvSelectClass = dialog.findViewById(R.id.tvSelectClass);
        final EditText etStudentName = dialog.findViewById(R.id.etStudentName);
        Button btnAddStudent = dialog.findViewById(R.id.btnAddStudent);
        civStudent = dialog.findViewById(R.id.civStudent);
        final ProgressBar pbAddStudent = dialog.findViewById(R.id.pbAddStudent);
        mStorage = FirebaseStorage.getInstance().getReference();

        spnClasses.setVisibility(View.GONE);
        spnSchools.setVisibility(View.GONE);
        tvSelectClass.setVisibility(View.GONE);
        tvSelectSchool.setVisibility(View.GONE);

        if(!(student.getSgurl().equals("default"))){
            Glide.with(civStudent.getContext())
                    .load(student.getSgurl())
                    .into(civStudent);
        }
        else {
            civStudent.setImageResource(R.drawable.defaultprofil);
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

                db.collection("Students").document(student.getStudentID()).update(studentDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void yukleniyorDialogGoster(){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_progress_bar);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void yukleniyorDialogKapat(){
        dialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == IMAGE_REQUEST){
            if(data.getData() == null)
                return;

            yukleniyorDialogGoster();
            Uri uri = data.getData();
            Glide.with(civStudent.getContext())
                    .load(uri)
                    .into(civStudent);

            StorageReference filePath = mStorage.child(student.getStudentID());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Map<String, Object> map = new HashMap<>();
                    map.put("sgurl", downloadUri.toString());

                    db.collection("Students").document(student.getStudentID()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "STUDENT SGURL GUNCELLENDI");
                            yukleniyorDialogKapat();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "STUDENT SGURL GUNCELLENEMEDI HATA: " + e.getMessage());
                            yukleniyorDialogKapat();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "STUDENT FIREBASE STORAGE HATA: " + e.getMessage());
                    yukleniyorDialogKapat();
                }
            });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
