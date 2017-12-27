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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mypreschool.Adapters.TeacherAdapter;
import com.example.mypreschool.Classes.School;
import com.example.mypreschool.Classes.SchoolClass;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
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
public class AdminTeacherFragment extends Fragment {
    private static final int IMAGE_REQUEST = 10001;
    private ArrayList<Teacher> teachers;
    private ArrayList<String>teachersNames;
    private ArrayList<School> schools;
    private ArrayList<SchoolClass> classes;
    private ListView lvTeachers;
    private TextView tvAddTeacher;
    private FirebaseFirestore db;
    private EditText etTeacherName, etTeacherPhoneNumber, etTeacherEmail;
    private Spinner spnSchools, spnClasses;
    private ProgressBar pbAddTeacher;
    private Dialog dialog;
    private FirebaseAuth mAuth;
    private CircleImageView civTeacher;
    private StorageReference mStorage;
    private int tcID, tsID;
    private Teacher teacher;

    public AdminTeacherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_teacher, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

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
                    teacher.setTeacherPhoneNumber(documentSnapshot.getString("phoneNumber"));
                    teacher.setTeacherID(documentSnapshot.getId());
                    teacher.setTeacherSchoolID(documentSnapshot.getString("schoolID"));
                    teacher.setTeacherEmail(documentSnapshot.getString("email"));
                    teacher.setTeacherPhoto("sgurl");

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
        TeacherAdapter adapter = new TeacherAdapter(getActivity(), teachers, new TeacherAdapter.OnItemClickListener() {
            @Override
            public void onTeacherDelete(Teacher teacher) {
                showDeleteTeacherDialog(teacher);
            }

            @Override
            public void onTeacherEdit(Teacher teacher) {
                showEditTeacherDialog(teacher);
            }
        });
        lvTeachers.setAdapter(adapter);
    }

    private void showDeleteTeacherDialog(final Teacher teacher){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("UYARI");
        alertDialog.setMessage("Bu öğretmeni silmek istediğinizden emin misiniz?");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WriteBatch batch = db.batch();

                DocumentReference teacherRef = db.collection("Teachers").document(teacher.getTeacherID());
                DocumentReference classRef = db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID());

                Map<String, Object> map = new HashMap<>();
                map.put("teacher_assigned", false);

                batch.delete(teacherRef);
                batch.update(classRef, map);

                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("ADMINTEACHER", "Teacher deleted");
                        Toast.makeText(getActivity(), "Teacher Deleted", Toast.LENGTH_SHORT).show();
                        bringTeachers();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ADMINTEACHER", "Teacher delete error: " + e.getMessage());
                    }
                });
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "HAYIR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void showEditTeacherDialog(final Teacher teacher){
        this.teacher = teacher;

        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_teacher_dialog);

        TextView tvAddTeacher = dialog.findViewById(R.id.tvAddTeacher);
        spnClasses = dialog.findViewById(R.id.spnClasses);
        spnSchools = dialog.findViewById(R.id.spnSchools);
        etTeacherName = dialog.findViewById(R.id.etTeacherName);
        etTeacherPhoneNumber = dialog.findViewById(R.id.etTeacherPhoneNumber);
        pbAddTeacher = dialog.findViewById(R.id.pbAddTeacher);
        civTeacher = dialog.findViewById(R.id.civTeacher);

        civTeacher.setVisibility(View.VISIBLE);

        if(!(teacher.getTeacherPhoto().equals("default"))){
            Glide.with(civTeacher.getContext())
                    .load(teacher.getTeacherPhoto())
                    .into(civTeacher);
        }
        else {
            civTeacher.setImageResource(R.drawable.defaultprofil);
        }

        civTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST);
            }
        });

        tvAddTeacher.setText("Edit Teacher");
        etTeacherName.setText(teacher.getTeacherName());
        etTeacherPhoneNumber.setText(teacher.getTeacherPhoneNumber() + "");
        spnClasses.setVisibility(View.GONE);
        spnSchools.setVisibility(View.GONE);

        tvAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTeacher(teacher);
            }
        });

        fillDialogSchools();
        dialog.show();
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
        etTeacherEmail = dialog.findViewById(R.id.etTeacherEmail);

        tvAddTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTeacher();
            }
        });

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

        fillDialogSchools();
        dialog.show();
    }

    private void addTeacher(){
        if(classes.size() < 1)
            return;

        if(schools.size() < 1)
            return;

        pbAddTeacher.setVisibility(View.VISIBLE);
        final String teacherPN = (etTeacherPhoneNumber.getText().toString());
        final String teacherName = etTeacherName.getText().toString();
        final String teacherEmail = etTeacherEmail.getText().toString();

        mAuth.createUserWithEmailAndPassword(teacherEmail, "deneme").addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                teacheriEkle(teacherName, teacherPN, teacherEmail, authResult.getUser().getUid());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ADMINTEACHER", "TEACHER AUTH HATA: " + e.getMessage());
                pbAddTeacher.setVisibility(View.GONE);
            }
        });
    }

    private void teacheriEkle(String teacherName, String teacherPN, String teacherEmail, String id){
        Map<String, Object> teacherDetails = new HashMap<>();
        teacherDetails.put("name", teacherName);
        teacherDetails.put("classID", classes.get((int)spnClasses.getSelectedItemId()).getClassID());
        teacherDetails.put("phoneNumber", teacherPN);
        teacherDetails.put("schoolID", schools.get((int)spnSchools.getSelectedItemId()).getSchoolID());
        teacherDetails.put("email", teacherEmail);
        teacherDetails.put("sgurl", "default");

        Map<String, Object> map = new HashMap<>();
        map.put("teacher_assigned", true);

        Log.d("ADMINTEACHER", "spnSchool: " + spnSchools.getSelectedItemId());
        Log.d("ADMINTEACHER", "spnClasses: " + spnClasses.getSelectedItemId());
        Log.d("ADMINTEACHER", "School Name: " + schools.get((int) spnSchools.getSelectedItemId()).getSchoolName());
        Log.d("ADMINTEACHER", "Class Name: " + classes.get((int) spnClasses.getSelectedItemId()).getClassName());

        Map<String, String> userDetail = new HashMap<>();
        userDetail.put("userName", teacherName);
        userDetail.put("type", "teacher");

        DocumentReference userRef = db.collection("Users").document(id);
        DocumentReference teacherRef = db.collection("Teachers").document(id);
        DocumentReference classRef = db.collection("Schools").document(schools.get((int) spnSchools.getSelectedItemId()).
                getSchoolID()).collection("Classes").document(classes.get((int) spnClasses.getSelectedItemId()).getClassID());

        WriteBatch batch = db.batch();

        batch.set(teacherRef, teacherDetails);
        batch.update(classRef, map);
        batch.set(userRef, userDetail);

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

    private void editTeacher(Teacher teacher){
        if(etTeacherName.getText().toString().isEmpty())
            return;

        if(etTeacherPhoneNumber.getText().toString().isEmpty())
            return;

        pbAddTeacher.setVisibility(View.VISIBLE);
        String teacherPN = (etTeacherPhoneNumber.getText().toString());

        Map<String, Object> teacherDetails = new HashMap<>();
        teacherDetails.put("name", etTeacherName.getText().toString());
        teacherDetails.put("phoneNumber", teacherPN);

        db.collection("Teachers").document(teacher.getTeacherID()).update(teacherDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Teacher edited", Toast.LENGTH_SHORT).show();
                pbAddTeacher.setVisibility(View.GONE);
                dialog.dismiss();

                bringTeachers();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("ADMINTEACHER", "TEACHER EDIT FAILURE E: " + e.getMessage());
                pbAddTeacher.setVisibility(View.GONE);
            }
        });;
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
            Glide.with(civTeacher.getContext())
                    .load(uri)
                    .into(civTeacher);

            StorageReference filePath = mStorage.child(teacher.getTeacherID());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Map<String, Object> map = new HashMap<>();
                    map.put("sgurl", downloadUri.toString());

                    db.collection("Teachers").document(teacher.getTeacherID()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ADMINTEACHER", "TEACHER SGURL GUNCELLENDI");
                            yukleniyorDialogKapat();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ADMINTEACHER", "TEACHER SGURL GUNCELLENEMEDI HATA: " + e.getMessage());
                            yukleniyorDialogKapat();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("ADMINTEACHER", "TEACHER FIREBASE STORAGE HATA: " + e.getMessage());
                    yukleniyorDialogKapat();
                }
            });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
