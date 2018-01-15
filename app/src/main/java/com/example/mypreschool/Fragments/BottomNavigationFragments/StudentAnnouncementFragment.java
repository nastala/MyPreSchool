package com.example.mypreschool.Fragments.BottomNavigationFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.mypreschool.Adapters.AnnouncementAdapter;
import com.example.mypreschool.Classes.Announcement;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentAnnouncementFragment extends Fragment {
    private final String TAG = "STUDENTANNOUNCEMENT";
    private Student student;
    private FirebaseFirestore db;
    private ArrayList<Announcement> announcements;

    private ProgressBar pbAnnouncement;
    private ListView lvAnnouncements;

    public StudentAnnouncementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_announcement, container, false);

        db = FirebaseFirestore.getInstance();

        pbAnnouncement = view.findViewById(R.id.pbAnnouncement);
        lvAnnouncements = view.findViewById(R.id.lvAnnouncements);

        announcementlariGetir();
        return view;
    }

    private void announcementlariGetir(){
        announcements = new ArrayList<>();

        pbAnnouncement.setVisibility(View.VISIBLE);
        db.collection("Announcements").whereEqualTo("schoolID", student.getSchoolID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()){
                        Log.d(TAG, "Announcement dokumani bulunamadi. School ID: " + student.getStudentID());
                        pbAnnouncement.setVisibility(View.GONE);
                        return;
                    }

                    Announcement announcement = new Announcement();
                    announcement.setTitle(documentSnapshot.getString("title"));
                    announcement.setDetails(documentSnapshot.getString("details"));
                    announcement.setSchoolName(documentSnapshot.getString("schoolName"));
                    announcement.setId(documentSnapshot.getId());
                    announcement.setDate(documentSnapshot.getDate("date"));

                    if(!announcementKontrolEt(announcement)){
                        announcements.add(announcement);
                    }
                }

                lvAnnouncementsDoldur();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Announcement getirme hata: " + e.getMessage());
                pbAnnouncement.setVisibility(View.GONE);
            }
        });
    }

    private void lvAnnouncementsDoldur(){
        if(getActivity() == null) {
            pbAnnouncement.setVisibility(View.GONE);
            return;
        }

        if(announcements == null || announcements.size() < 1) {
            pbAnnouncement.setVisibility(View.GONE);
            return;
        }

        announcementsSirala();

        AnnouncementAdapter adapter = new AnnouncementAdapter(getActivity(), announcements);
        lvAnnouncements.setAdapter(adapter);
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

    private boolean announcementKontrolEt(Announcement announcement){
        if(announcements == null || announcements.size() < 1)
            return false;

        for(int i = 0; i < announcements.size(); i++){
            if(announcements.get(i).getId().equals(announcement.getId())){
                return true;
            }
        }

        return false;
    }

    public void setStudent(Student student){ this.student = student; }
}
