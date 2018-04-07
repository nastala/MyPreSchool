package com.example.mypreschool.Fragments.BottomNavigationFragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentSleepStateFragment extends Fragment {
    private final String TAG = "STUDENTSLEEP";
    
    private FirebaseFirestore db;
    private Button btnPrev, btnNext;
    private TextView tvDate, tvName, tvStatus;
    private ProgressBar pbSleepState;
    private LinearLayout llSleepState;
    private Date date, dateCurrent;
    private String dateNow;
    private Student student;

    public StudentSleepStateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_sleep_state, container, false);
        
        db = FirebaseFirestore.getInstance();
        
        btnNext = view.findViewById(R.id.btnNext);
        btnPrev = view.findViewById(R.id.btnPrev);
        tvDate = view.findViewById(R.id.tvDate);
        tvName = view.findViewById(R.id.tvName);
        tvStatus = view.findViewById(R.id.tvStatus);
        pbSleepState = view.findViewById(R.id.pbSleepState);
        llSleepState = view.findViewById(R.id.llSleepState);

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

        bringStatus();
        
        return view;
    }

    private void bringStatus() {
        pbSleepState.setVisibility(View.VISIBLE);

        db.collection("Schools").document(student.getSchoolID()).collection("Classes").document(student.getClassID())
                .collection("SleepStates").document(dateNow).collection("Status").document(student.getStudentID()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(!documentSnapshot.exists()){
                            Toast.makeText(getActivity(), "No information found!", Toast.LENGTH_SHORT).show();
                            pbSleepState.setVisibility(View.GONE);
                            return;
                        }

                        String status = documentSnapshot.getString("status");
                        tvName.setText(student.getName());

                        if(status.equals("slept"))
                            tvStatus.setText("Slept");
                        else
                            tvStatus.setText("Not Slept");

                        llSleepState.setVisibility(View.VISIBLE);
                        pbSleepState.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "SleepStates getirme hata: " + e.getMessage());
                pbSleepState.setVisibility(View.GONE);
            }
        });
    }

    private void bugunuGetir(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        date = Calendar.getInstance().getTime();
        dateNow = dateFormat.format(date);
        dateCurrent = date;
        tvDate.setText(dateNow);
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
        llSleepState.setVisibility(View.GONE);
        bringStatus();
    }

    private void birGunOnceyiGetir(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        date = calendar.getTime();
        dateNow = dateFormat.format(date);
        tvDate.setText(dateNow);

        llSleepState.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        bringStatus();
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
