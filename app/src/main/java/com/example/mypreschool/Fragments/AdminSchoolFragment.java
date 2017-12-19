package com.example.mypreschool.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypreschool.Adapters.SchoolAdapter;
import com.example.mypreschool.Classes.School;
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
public class AdminSchoolFragment extends Fragment {
    private final String TAG = "ADMINSCHOOL";

    private ArrayList<School> schools;
    private School currentSchool;
    private EditText etSchoolName;
    private Button btnAddScholl;
    private ProgressBar pbAddSchool;
    private ListView lvSchools, lvClasses;
    private TextView tvAdminAddSchool;
    private FirebaseFirestore db;
    private ProgressBar pbAdmin;
    private RelativeLayout rlAdminSchool;

    public AdminSchoolFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_school, container, false);

        db = FirebaseFirestore.getInstance();

        rlAdminSchool = view.findViewById(R.id.rlAdminSchool);
        pbAdmin = view.findViewById(R.id.pbAdmin);

        getSchoolLayout();

        return view;
    }

    private void getClassLayout(){
        rlAdminSchool.removeAllViews();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_admin_school_class, rlAdminSchool, true);

        lvClasses = view.findViewById(R.id.lvClasses);
        TextView tvAddClass = view.findViewById(R.id.tvAddClass);

        Log.d(TAG, "School id: " + currentSchool.getSchoolID());

        tvAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showAddClassDialog();
            }
        });
    }

    private void getSchoolLayout(){
        rlAdminSchool.removeAllViews();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_admin_school_school, rlAdminSchool, true);

        lvSchools = view.findViewById(R.id.lvSchools);
        tvAdminAddSchool = view.findViewById(R.id.tvAdminAddSchool);

        tvAdminAddSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSchoolDialog();
            }
        });

        lvSchools.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSchool = schools.get(position);
                getClassLayout();
            }
        });

        getSchools();
    }

    private void getSchools(){
        schools = new ArrayList<>();

        pbAdmin.setVisibility(View.VISIBLE);

        db.collection("Schools").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!(documentSnapshot.exists())){
                        Log.d(TAG, "DOCUMENT NOT EXIST");
                        pbAdmin.setVisibility(View.GONE);
                        break;
                    }

                    School school = new School();
                    school.setSchoolName(documentSnapshot.getString("name"));
                    school.setSchoolID(documentSnapshot.getId());
                    schools.add(school);
                }

                pbAdmin.setVisibility(View.GONE);
                SchoolAdapter adapter = new SchoolAdapter(getActivity(), schools, new SchoolAdapter.OnItemClickListener() {
                    @Override
                    public void onSchoolDelete(School school) {
                        showDeleteSchoolDialog(school);
                    }

                    @Override
                    public void onSchoolEdit(School school) {
                        showEditSchoolDialog(school);
                    }
                });
                lvSchools.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbAdmin.setVisibility(View.GONE);
                Log.d(TAG, "DOCUMENT GETIRME HATA: " + e.getMessage());
            }
        });
    }

    private void showEditSchoolDialog(final School school){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_school_dialog);

        etSchoolName = dialog.findViewById(R.id.etSchoolName);
        btnAddScholl = dialog.findViewById(R.id.btnAddSchool);
        pbAddSchool = dialog.findViewById(R.id.pbAddSchool);

        etSchoolName.setText(school.getSchoolName());
        btnAddScholl.setText("EDIT SCHOOL");

        btnAddScholl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbAddSchool.setVisibility(View.VISIBLE);

                Map<String, Object> map = new HashMap<>();
                map.put("name", etSchoolName.getText().toString());

                db.collection("Schools").document(school.getSchoolID()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "School updated", Toast.LENGTH_SHORT).show();
                        pbAdmin.setVisibility(View.GONE);
                        dialog.dismiss();
                        getSchools();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "School edit error: " + e.getMessage());
                    }
                });
            }
        });

        dialog.show();
    }

    private void showDeleteSchoolDialog(final School school){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("UYARI");
        alertDialog.setMessage("Okulu silmek istediğinizden emin misiniz?");
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "EVET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, school.getSchoolID());
                db.collection("Schools").document(school.getSchoolID()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "School removed", Toast.LENGTH_SHORT).show();
                        getSchools();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "School delete error: " + e.getMessage());
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

    private void showAddSchoolDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_school_dialog);

        etSchoolName = dialog.findViewById(R.id.etSchoolName);
        btnAddScholl = dialog.findViewById(R.id.btnAddSchool);
        pbAddSchool = dialog.findViewById(R.id.pbAddSchool);

        btnAddScholl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbAddSchool.setVisibility(View.VISIBLE);

                Map<String, Object> schoolDetail = new HashMap<>();
                schoolDetail.put("name", etSchoolName.getText().toString());

                db.collection("Schools").document().set(schoolDetail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "School Added", Toast.LENGTH_SHORT).show();
                        pbAddSchool.setVisibility(View.GONE);
                        getSchools();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "School Add Failed: " + e.getMessage());
                        pbAddSchool.setVisibility(View.GONE);
                    }
                });
            }
        });

        dialog.show();
    }

}
