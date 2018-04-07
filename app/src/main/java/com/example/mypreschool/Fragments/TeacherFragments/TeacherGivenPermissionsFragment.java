package com.example.mypreschool.Fragments.TeacherFragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.mypreschool.Classes.PermissionRequestClass;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherGivenPermissionsFragment extends Fragment {
    private final String TAG = "TEACHERGIVENPERM";

    private ArrayList<PermissionRequestClass> requests;
    private FirebaseFirestore db;
    private ListView lvGivenPermissions;
    private ProgressBar pbPermission;
    private Teacher teacher;

    public TeacherGivenPermissionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_given_permissions, container, false);

        db = FirebaseFirestore.getInstance();
        requests = new ArrayList<>();

        lvGivenPermissions = view.findViewById(R.id.lvPermissionRequests);
        pbPermission = view.findViewById(R.id.pbPermission);

        bringRequests();

        lvGivenPermissions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                TeacherGivenPermissions2Fragment hedef = new TeacherGivenPermissions2Fragment();
                hedef.setPermissionRequestClass(requests.get(i));
                ft.replace(R.id.flTeacherMain, hedef).addToBackStack(null);
                ft.commit();
            }
        });

        return view;
    }

    private void bringRequests() {
        pbPermission.setVisibility(View.VISIBLE);
        requests.clear();

        db.collection("PermissionRequests").whereEqualTo("classID", teacher.getTeacherClassID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if(documentSnapshots.isEmpty()){
                    Log.d(TAG, "No doc found");
                    pbPermission.setVisibility(View.GONE);
                    return;
                }

                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    String id = documentSnapshot.getId();
                    String title = documentSnapshot.getString("title");
                    String details = documentSnapshot.getString("details");
                    double price = documentSnapshot.getDouble("price");
                    Date date = documentSnapshot.getDate("date");
                    PermissionRequestClass request = new PermissionRequestClass(id, title, details, price, date);

                    requests.add(request);
                }

                showLvRequests();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "PermissionRequests getirme hata: " + e.getMessage());
                pbPermission.setVisibility(View.GONE);
            }
        });
    }

    private void showLvRequests() {
        if(getActivity() == null)
            return;

        if(requests.size() < 1){
            Log.d(TAG, "requests size < 1");
            pbPermission.setVisibility(View.GONE);
            return;
        }

        sortRequests();

        ArrayList<String> permissionNames = new ArrayList<>();
        for(PermissionRequestClass request : requests){
            permissionNames.add(request.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, permissionNames);
        lvGivenPermissions.setAdapter(adapter);
        pbPermission.setVisibility(View.GONE);
        lvGivenPermissions.setVisibility(View.VISIBLE);
    }

    private void sortRequests() {
        if(requests.size() < 2){
            return;
        }

        Collections.sort(requests, new Comparator<PermissionRequestClass>() {
            @Override
            public int compare(PermissionRequestClass permissionRequestClass, PermissionRequestClass t1) {
                return permissionRequestClass.getDate().compareTo(t1.getDate());
            }
        });
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
}
