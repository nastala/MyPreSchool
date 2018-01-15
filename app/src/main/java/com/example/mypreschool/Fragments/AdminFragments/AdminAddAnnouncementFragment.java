package com.example.mypreschool.Fragments.AdminFragments;


import android.app.Dialog;
import android.nfc.Tag;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mypreschool.Adapters.AnnouncementAdapter;
import com.example.mypreschool.Classes.AddAnnouncementRequest;
import com.example.mypreschool.Classes.Announcement;
import com.example.mypreschool.Classes.School;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminAddAnnouncementFragment extends Fragment {
    private final String TAG = "ADMINADDANNOUNCEMENT";
    private FirebaseFirestore db;
    private ArrayList<Announcement> announcements;
    private ArrayList<String> schoolNames;
    private ArrayList<School> schools;
    private ArrayList<String> parentSGCMs;
    private int selectionID = -1;
    private Dialog dialog;

    private Spinner spnSchools;
    private LinearLayout llAnnouncements;
    private ProgressBar pbAnnouncement;
    private ListView lvAnnouncements;
    private TextView tvAddAnnouncement;

    public AdminAddAnnouncementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_add_announcement, container, false);

        db = FirebaseFirestore.getInstance();

        spnSchools = view.findViewById(R.id.spnSchools);
        llAnnouncements = view.findViewById(R.id.llAnnouncements);
        pbAnnouncement = view.findViewById(R.id.pbAnnouncement);
        lvAnnouncements = view.findViewById(R.id.lvAnnouncements);
        tvAddAnnouncement = view.findViewById(R.id.tvAddAnnouncement);

        spnSchools.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectionID = position;
                announcementlariGetir();
                Log.d(TAG, "Seletion degisti: " + selectionID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvAddAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGoster();
            }
        });

        spnSchoolsDoldur();
        return view;
    }

    private void dialogGoster(){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_add_announcement_dialog);

        if(selectionID == -1){
            Log.d(TAG, "SelectionID = -1");
            dialog.dismiss();
        }

        final EditText etTitle = dialog.findViewById(R.id.etTitle);
        final EditText etDetails = dialog.findViewById(R.id.etDetails);
        Button btnAddAnnouncement = dialog.findViewById(R.id.btnAddAnnouncement);
        TextView tvSchoolName = dialog.findViewById(R.id.tvSchoolName);
        final ProgressBar pbAnnouncement = dialog.findViewById(R.id.pbAnnouncement);

        final School school = schools.get(selectionID);
        tvSchoolName.setText(school.getSchoolName());

        btnAddAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etTitle.getText().toString();
                String details = etDetails.getText().toString();

                if(title.isEmpty()){
                    etTitle.setError("Title can not be empty!");
                    etTitle.requestFocus();
                    return;
                }

                pbAnnouncement.setVisibility(View.VISIBLE);
                dialog.setCancelable(false);

                Date date = Calendar.getInstance().getTime();
                Map<String, Object> map = new HashMap<>();
                map.put("title", title);
                map.put("schoolID", school.getSchoolID());
                map.put("schoolName", school.getSchoolName());
                map.put("details", details);
                map.put("date", date);

                final Announcement announcement = new Announcement();
                announcement.setSchoolName(school.getSchoolName());
                announcement.setTitle(title);
                announcement.setDetails(details);

                db.collection("Announcements").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Announcement eklendi");
                        notificationHazirlik(school.getSchoolID(), announcement);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Announcement ekleme hata: " + e.getMessage());
                        pbAnnouncement.setVisibility(View.GONE);
                        dialog.setCancelable(true);
                    }
                });
            }
        });

        dialog.show();
    }

    private void notificationHazirlik(String schoolID, final Announcement announcement){
        parentSGCMs = new ArrayList<>();
        Log.d(TAG, "NotificationHazirlik cagrildi schoolID: " + schoolID);

        db.collection("Students").whereEqualTo("schoolID", schoolID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()){
                        Log.d(TAG, "Parent id getirilemedi. Dokuman yok.");
                        announcementlariGetir();
                        dialog.dismiss();
                        return;
                    }

                    String parentID = documentSnapshot.getString("parentID");
                    parentSGCMGetir(parentID, announcement);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Parent id getirme hata: " + e.getMessage());
                announcementlariGetir();
                dialog.dismiss();
            }
        });
    }

    private void parentSGCMGetir(String parentID, final Announcement announcement){
        Log.d(TAG, "parentSGCMGetir cagrildi parentID: " + parentID);

        db.collection("Parents").document(parentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    Log.d(TAG, "Parent sgcm getirilemedi. Dokuman yok.");
                    announcementlariGetir();
                    dialog.dismiss();
                    return;
                }

                String parentSGCM = documentSnapshot.getString("sgcm");
                for(String sgcm : parentSGCMs){
                    if(sgcm.equals(parentSGCM)){
                        Log.d(TAG, "Notification daha once yollanmis parentSGCM: " + parentSGCM);
                        dialog.dismiss();
                        return;
                    }
                }

                parentSGCMs.add(parentSGCM);
                notificationGonderParent(parentSGCM, announcement);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Parent SGCM getirme hata: " + e.getMessage());
                announcementlariGetir();
                dialog.dismiss();
            }
        });
    }

    private void notificationGonderParent(String parentSGCM, Announcement announcement){
        Log.d(TAG, "notificationGonderParent cagrildi parentSGCM: " + parentSGCM);
        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Parent Notification Response: " + response);
                announcementlariGetir();
                dialog.dismiss();
            }
        };

        AddAnnouncementRequest request = new AddAnnouncementRequest(parentSGCM, announcement, listener);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private void spnSchoolsDoldur(){
        schools = new ArrayList<>();
        schoolNames = new ArrayList<>();

        db.collection("Schools").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists())
                        break;

                    School school = new School();
                    school.setSchoolID(documentSnapshot.getId());
                    school.setSchoolName(documentSnapshot.getString("name"));
                    schoolNames.add(documentSnapshot.getString("name"));
                    schools.add(school);
                }

                spinnerDoldur();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "School getirme hata: " + e.getMessage());
            }
        });
    }

    private void spinnerDoldur(){
        if(getActivity() == null)
            return;

        if(schoolNames.size() < 1){
            Toast.makeText(getActivity(), "There are no schools", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, schoolNames);
        spnSchools.setAdapter(adapter);
    }

    private void announcementlariGetir(){
        announcements = new ArrayList<>();
        pbAnnouncement.setVisibility(View.VISIBLE);

        if(selectionID == -1){
            Log.d(TAG, "SelectionID = -1");
            return;
        }

        String schoolID = schools.get(selectionID).getSchoolID();
        db.collection("Announcements").whereEqualTo("schoolID", schoolID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()) {
                        lvAnnouncements.setVisibility(View.VISIBLE);
                        break;
                    }

                    Announcement announcement = new Announcement();
                    announcement.setTitle(documentSnapshot.getString("title"));
                    announcement.setSchoolName(documentSnapshot.getString("schoolName"));
                    announcement.setDetails(documentSnapshot.getString("details"));
                    announcement.setDate(documentSnapshot.getDate("date"));
                    Log.d(TAG, "Announcement geldi, Title: " + announcement.getTitle());
                    announcements.add(announcement);
                }

                lvAnnouncementsDoldur();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Announcement getirme hata: " + e.getMessage());
            }
        });
    }

    private void lvAnnouncementsDoldur(){
        if(getActivity() == null)
            return;

        announcementsSirala();

        if(announcements.size() < 1){
            Log.d(TAG, "Announcemnt size 0");
            pbAnnouncement.setVisibility(View.GONE);
            llAnnouncements.setVisibility(View.VISIBLE);
        }

        AnnouncementAdapter adapter = new AnnouncementAdapter(getActivity(), announcements);
        lvAnnouncements.setAdapter(adapter);
        llAnnouncements.setVisibility(View.VISIBLE);
        pbAnnouncement.setVisibility(View.GONE);
    }

    private void announcementsSirala(){
        Collections.sort(announcements, new Comparator<Announcement>() {
            @Override
            public int compare(Announcement o1, Announcement o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
    }

}
