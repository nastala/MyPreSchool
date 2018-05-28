package com.example.mypreschool.Fragments.BottomNavigationFragments;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.example.mypreschool.Adapters.PermissionRequestAdapter;
import com.example.mypreschool.Classes.PermissionRequestClass;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudentPermissionRequestFragment extends Fragment {
    private final String TAG = "STUDENTPERMISSION";

    private FirebaseFirestore db;
    private ProgressBar pbStudent;
    private ListView lvPermissionRequests, lvGivenPermissions;
    private Student student;
    private ArrayList<PermissionRequestClass> requests, givenRequests;
    private PermissionRequestAdapter adapter, gpAdapter;
    private RelativeLayout rlGivenPermissions;
    private int counter;

    public StudentPermissionRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_permission_request, container, false);

        db = FirebaseFirestore.getInstance();
        requests = new ArrayList<>();
        givenRequests = new ArrayList<>();

        pbStudent = view.findViewById(R.id.pbStudent);
        lvPermissionRequests = view.findViewById(R.id.lvPermissionRequests);
        lvGivenPermissions = view.findViewById(R.id.lvGivenPermissions);
        rlGivenPermissions = view.findViewById(R.id.rlGivenPermissions);

        bringRequests();

        return view;
    }

    private void bringRequests() {
        pbStudent.setVisibility(View.VISIBLE);
        requests.clear();

        db.collection("PermissionRequests").whereEqualTo("classID", student.getClassID())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                counter = 0;
                Log.d(TAG, "Counter " + counter);

                if(documentSnapshots.isEmpty()){
                    Log.d(TAG, "Permission requets document yok");
                    pbStudent.setVisibility(View.GONE);
                    return;
                }

                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()){
                        Log.d(TAG, "Permission requets document yok");
                        pbStudent.setVisibility(View.GONE);
                        return;
                    }

                    if(documentSnapshot.getDate("date").before(Calendar.getInstance().getTime())) {
                        Log.d(TAG, "Date bugunden once geldi. Date: " + documentSnapshot.getDate("date"));
                        continue;
                    }

                    counter++;

                    String key = documentSnapshot.getId();
                    Date date = documentSnapshot.getDate("date");
                    double price = documentSnapshot.getDouble("price");
                    String title = documentSnapshot.getString("title");
                    String details = documentSnapshot.getString("details");
                    PermissionRequestClass request = new PermissionRequestClass(key, title, details, price, date);

                    check(request);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Permission Requests getirme hata: " + e.getMessage());
                pbStudent.setVisibility(View.GONE);
            }
        });
    }

    private void check(final PermissionRequestClass request) {
        db.collection("GivenRequests").whereEqualTo("permissionRequestID", request.getId()).whereEqualTo("studentID", student.getStudentID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "Given Requests check hata: " + e.getMessage());
                    pbStudent.setVisibility(View.GONE);
                    return;
                }

                if(documentSnapshots.isEmpty()){
                    Log.d(TAG, "GivenRequests'de böyle bir doc yok.");
                    requests.add(request);
                    counter--;
                }
                else {
                    for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                        if (documentSnapshot.exists()) {
                            Log.d(TAG, "GivenRequests'de böyle bir doc var.");
                            requests.remove(request);
                            request.setGivenRequestId(documentSnapshot.getId());
                            request.setGivenRequest(true);
                            if(checkRequests(request)) {
                                request.setAllowed(documentSnapshot.getBoolean("permission"));
                                givenRequests.add(request);
                            }
                            else{
                                for (int i = 0; i < givenRequests.size(); i++){
                                    if(request.getGivenRequestId().equals(givenRequests.get(i).getGivenRequestId())){
                                        givenRequests.get(i).setGivenRequest(true);
                                        givenRequests.get(i).setAllowed(documentSnapshot.getBoolean("permission"));
                                    }
                                }
                            }

                            showLvGivenRequests();

                            if (adapter != null)
                                adapter.update(requests);
                            if(gpAdapter != null)
                                gpAdapter.update(givenRequests);

                            counter--;
                        }
                        else {
                            Log.d(TAG, "GivenRequests'de böyle bir doc yok.");
                            requests.add(request);
                            counter--;
                        }
                    }
                }

                Log.d(TAG, "check Counter " + counter);

                if(counter <= 0)
                    showLvRequests();
            }
        });
    }

    private boolean checkRequests(PermissionRequestClass request) {
        boolean check = true;

        for(PermissionRequestClass requestClass : givenRequests){
            if(requestClass.getGivenRequestId().equals(request.getGivenRequestId())){
                check = false;
                break;
            }
        }

        return check;
    }

    private void showLvGivenRequests(){
        if(getActivity() == null) {
            return;
        }

        if(gpAdapter != null)
            return;

        if(givenRequests.size() < 1){
            Log.d(TAG, "GivenRequests size < 1");
            return;
        }

        gpAdapter = new PermissionRequestAdapter(getActivity(), givenRequests, new PermissionRequestAdapter.onItemClickListener() {
            @Override
            public void onIvYesClick(PermissionRequestClass permissionRequest, int index) {
                if(checkDate(permissionRequest.getDate())) {
                    Log.d(TAG, "Date < current time");
                    return;
                }

                Log.d(TAG, "gpAdapter onIvYesClick");
                Map<String, Object> map = new HashMap<>();
                map.put("permission", true);

                //givenRequests.remove(permissionRequest);
                permissionRequest.setGivenRequest(true);
                permissionRequest.setAllowed(true);
                givenRequests.set(index, permissionRequest);

                db.collection("GivenRequests").document(permissionRequest.getGivenRequestId()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Given Requests'e eklendi");
                        gpAdapter.update(givenRequests);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Given Requests set hata: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onIvNoClick(PermissionRequestClass permissionRequest, int index) {
                if(checkDate(permissionRequest.getDate()))
                    return;

                Log.d(TAG, "gpAdapter onIvNoClick");
                Map<String, Object> map = new HashMap<>();
                map.put("permission", false);

                //givenRequests.remove(permissionRequest);
                permissionRequest.setGivenRequest(true);
                permissionRequest.setAllowed(false);
                givenRequests.set(index, permissionRequest);

                db.collection("GivenRequests").document(permissionRequest.getGivenRequestId()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Given Requests'e eklendi");
                        gpAdapter.update(givenRequests);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Given Requests set hata: " + e.getMessage());
                    }
                });
            }
        });

        lvGivenPermissions.setAdapter(gpAdapter);
        rlGivenPermissions.setVisibility(View.VISIBLE);
        lvGivenPermissions.setVisibility(View.VISIBLE);
    }

    private boolean checkDate(Date date) {
        return date.before(Calendar.getInstance().getTime());
    }

    private void showLvRequests() {
        if(getActivity() == null) {
            pbStudent.setVisibility(View.GONE);
            return;
        }

        if(requests.size() < 1){
            Log.d(TAG, "Requests size < 1");
            pbStudent.setVisibility(View.GONE);
            return;
        }

        if(requests.size() >= 2){
            sortRequests();
        }

        adapter = new PermissionRequestAdapter(getActivity(), requests, new PermissionRequestAdapter.onItemClickListener() {
            @Override
            public void onIvYesClick(final PermissionRequestClass permissionRequest, final int index) {
                Map<String, Object> map = new HashMap<>();
                map.put("permissionRequestID", permissionRequest.getId());
                map.put("studentID", student.getStudentID());
                map.put("permission", true);

                permissionRequest.setAllowed(true);
                permissionRequest.setGivenRequest(true);
                givenRequests.add(permissionRequest);

                db.collection("GivenRequests").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Given Requests'e eklendi");
                        adapter.update(requests);
                        gpAdapter.update(givenRequests);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Given Requests set hata: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onIvNoClick(final PermissionRequestClass permissionRequest, final int index) {
                Map<String, Object> map = new HashMap<>();
                map.put("permissionRequestID", permissionRequest.getId());
                map.put("studentID", student.getStudentID());
                map.put("permission", false);

                permissionRequest.setAllowed(false);
                permissionRequest.setGivenRequest(true);
                givenRequests.add(permissionRequest);

                db.collection("GivenRequests").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Given Requests'e eklendi");
                        adapter.update(requests);
                        gpAdapter.update(givenRequests);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Given Requests set hata: " + e.getMessage());
                    }
                });
            }
        });

        lvPermissionRequests.setAdapter(adapter);
        lvPermissionRequests.setVisibility(View.VISIBLE);
        pbStudent.setVisibility(View.GONE);
    }

    private void sortRequests() {
        Collections.sort(requests, new Comparator<PermissionRequestClass>() {
            @Override
            public int compare(PermissionRequestClass permissionRequestClass, PermissionRequestClass t1) {
                return permissionRequestClass.getDate().compareTo(t1.getDate());
            }
        });

        if(givenRequests.size() > 1){
            Collections.sort(givenRequests, new Comparator<PermissionRequestClass>() {
                @Override
                public int compare(PermissionRequestClass permissionRequestClass, PermissionRequestClass t1) {
                    return permissionRequestClass.getDate().compareTo(t1.getDate());
                }
            });
        }
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
