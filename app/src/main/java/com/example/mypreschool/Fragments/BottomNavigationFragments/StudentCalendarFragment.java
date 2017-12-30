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

    private CheckBox cbNone, cbHalf, cbFull;
    private CalendarView cvStat;
    private Dialog dialog;
    private String selectedDateString;
    private Student student;
    private ListView lvFoodList;
    private ProgressBar pbFood;
    private LinearLayout llFood;
    private FirebaseFirestore db;
    private boolean statKontrol, menuKontrol;
    private ArrayList<String> currentMenu;
    private Date dateNow2, seledtedDate2;

    public StudentCalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_calendar, container, false);

        db = FirebaseFirestore.getInstance();
        cvStat = view.findViewById(R.id.cvStat);

        cvStat.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDateString = (month + 1) + "-" + dayOfMonth + "-" + year;
                if(kontrolEt())
                    dialogGoster();
                else
                    Toast.makeText(getActivity(), "You selected a date after the current date, please reselect", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    
    private boolean kontrolEt(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date now = Calendar.getInstance().getTime();
        String dateNow = dateFormat.format(now);
        try {
            dateNow2 = dateFormat.parse(dateNow);
            seledtedDate2 = dateFormat.parse(selectedDateString);
            Log.d(TAG, "Date Format: " + dateNow2 + " " + seledtedDate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        if(seledtedDate2.after(dateNow2))
            return false;
        else 
            return true;
    }

    private void dialogGoster(){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_student_status_dialog);

        cbNone = dialog.findViewById(R.id.cbNone);
        cbHalf = dialog.findViewById(R.id.cbHalf);
        cbFull = dialog.findViewById(R.id.cbFull);
        TextView tvName = dialog.findViewById(R.id.tvName);
        TextView tvDate = dialog.findViewById(R.id.tvDate);
        lvFoodList = dialog.findViewById(R.id.lvFoodList);
        pbFood = dialog.findViewById(R.id.pbFood);
        llFood = dialog.findViewById(R.id.llFood);

        tvDate.setText(selectedDateString);
        tvName.setText(student.getName());

        statiGetir();
        menuyuGetir();

        dialog.show();
    }

    private void menuyuGetir(){
        menuKontrol = false;
        pbFood.setVisibility(View.VISIBLE);
        currentMenu = new ArrayList<>();

        db.collection("Schools").document(student.getSchoolID()).collection("Classes").document(student.getClassID()).collection("FoodLists").
                document(selectedDateString).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    Toast.makeText(getActivity(), "No information found", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                currentMenu = (ArrayList<String>)documentSnapshot.get("menu");
                if(currentMenu == null || currentMenu.size() < 1){
                    Toast.makeText(getActivity(), "No information found", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }

                if(getActivity() == null)
                    return;

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, currentMenu);
                lvFoodList.setAdapter(adapter);
                menuKontrol = true;
                pbFood.setVisibility(View.GONE);
                if(statKontrol && menuKontrol)
                    llFood.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "MENU GETIRME HATA: " + e.getMessage());
                pbFood.setVisibility(View.GONE);
            }
        });
    }

    private void statiGetir(){
        statKontrol = false;
        pbFood.setVisibility(View.VISIBLE);

        Log.d(TAG, student.getName());
        Log.d(TAG, selectedDateString);
        Log.d(TAG, student.getStudentID());

        db.collection("Schools").document(student.getSchoolID()).collection("Classes").document(student.getClassID()).collection("FoodLists").
                document(selectedDateString).collection("Status").document(student.getStudentID()).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.exists()){
                            Toast.makeText(getActivity(), "No information found", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }

                        String stat = documentSnapshot.getString("stat");
                        switch (stat){
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
                        statKontrol = true;

                        pbFood.setVisibility(View.GONE);
                        if(statKontrol && menuKontrol)
                            llFood.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Log.d(TAG, "STUDENT STATUS GETIRME HATA: " + e.getMessage());
                dialog.setCancelable(true);
                pbFood.setVisibility(View.GONE);
            }
        });
    }

    public void setStudent(Student student) { this.student = student; }
}
