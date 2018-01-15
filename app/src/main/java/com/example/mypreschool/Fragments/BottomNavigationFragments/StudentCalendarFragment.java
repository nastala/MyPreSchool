package com.example.mypreschool.Fragments.BottomNavigationFragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentCalendarFragment extends Fragment {
    private final String TAG = "STUDENTCALENDAR";

    private ArrayList<String> menu, stats;
    private Student student;
    private FirebaseFirestore db;
    private Button btnPrev, btnNext;
    private TextView tvDate;
    private ProgressBar pbFood;
    private LinearLayout llDetay;
    private ListView lvMenu, lvStat;
    private String dateNow;
    private Date date, dateCurrent;

    public StudentCalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_calendar, container, false);

        db = FirebaseFirestore.getInstance();

        btnNext = view.findViewById(R.id.btnNext);
        btnPrev = view.findViewById(R.id.btnPrev);
        tvDate = view.findViewById(R.id.tvDate);
        pbFood = view.findViewById(R.id.pbFood);
        llDetay = view.findViewById(R.id.llDetay);
        lvMenu = view.findViewById(R.id.lvMenu);
        lvStat = view.findViewById(R.id.lvStat);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birGunOnceyiGetir();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                birGunSonrayiGetir();
            }
        });

        bugunuGetir();
        menuBilgileriniGetir();

        return view;
    }

    private void menuBilgileriniGetir(){
        menu = new ArrayList<>();
        pbFood.setVisibility(View.VISIBLE);
        buttonlariPasifEt();

        db.collection("Schools").document(student.getSchoolID()).collection("Classes").document(student.getClassID()).collection("FoodLists")
                .document(dateNow).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    Toast.makeText(getActivity(), "No information found!", Toast.LENGTH_SHORT).show();
                    pbFood.setVisibility(View.GONE);
                    buttonlariAktifEt();
                    return;
                }

                menu = (ArrayList)documentSnapshot.get("menu");
                statBilgileriniGetir();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "MENU GETİRME HATA: " + e.getMessage());
                pbFood.setVisibility(View.GONE);
                buttonlariAktifEt();
            }
        });
    }

    private void buttonlariPasifEt(){
        btnNext.setClickable(false);
        btnPrev.setClickable(false);
    }

    private void buttonlariAktifEt(){
        btnNext.setClickable(true);
        btnPrev.setClickable(true);
    }

    private void statBilgileriniGetir(){
        stats = new ArrayList<>();

        db.collection("Schools").document(student.getSchoolID()).collection("Classes").document(student.getClassID()).collection("FoodLists")
                .document(dateNow).collection("Status").document(student.getStudentID()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    Toast.makeText(getActivity(), "No information found!", Toast.LENGTH_SHORT).show();
                    pbFood.setVisibility(View.GONE);
                    buttonlariAktifEt();
                    return;
                }

                for(int i = 0; i < menu.size(); i++) {
                    Log.d(TAG, menu.get(i) + " " + documentSnapshot.getString(i + ""));

                    if(documentSnapshot.getString(i + "") == null)
                        stats.add("");
                    else
                        stats.add(documentSnapshot.getString(i + ""));
                }
                lvleriDoldur();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "STAT GETİRME HATA: " + e.getMessage());
                pbFood.setVisibility(View.GONE);
                buttonlariAktifEt();
            }
        });
    }

    private void lvleriDoldur(){
        if(getActivity() == null)
            return;

        ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menu);
        ArrayAdapter<String> statAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, stats);

        lvStat.setAdapter(statAdapter);
        lvMenu.setAdapter(menuAdapter);
        llDetay.setVisibility(View.VISIBLE);
        pbFood.setVisibility(View.GONE);
        buttonlariAktifEt();
    }

    private void birGunOnceyiGetir(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        date = calendar.getTime();
        dateNow = dateFormat.format(date);
        tvDate.setText(dateNow);

        llDetay.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        menuBilgileriniGetir();
    }

    private void birGunSonrayiGetir(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, +1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        date = calendar.getTime();
        dateNow = dateFormat.format(date);
        tvDate.setText(dateNow);

        if(!date.before(dateCurrent))
            btnNext.setVisibility(View.GONE);
        llDetay.setVisibility(View.GONE);
        menuBilgileriniGetir();
    }

    private void bugunuGetir(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        date = Calendar.getInstance().getTime();
        dateNow = dateFormat.format(date);
        dateCurrent = date;
        tvDate.setText(dateNow);
    }

    public void setStudent(Student student) { this.student = student; }
}
