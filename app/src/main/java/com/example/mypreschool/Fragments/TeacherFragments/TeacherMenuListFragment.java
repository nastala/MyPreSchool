package com.example.mypreschool.Fragments.TeacherFragments;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherMenuListFragment extends Fragment {
    private final String TAG = "TEACHERMENULIST";
    private Teacher teacher;
    private String dateNow;
    private ArrayList<String> menu;

    private ProgressBar pbMenuList;
    private ListView lvMenu;
    private TextView tvDate;
    private FirebaseFirestore db;

    public TeacherMenuListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_menu_list, container, false);

        db = FirebaseFirestore.getInstance();

        pbMenuList = view.findViewById(R.id.pbMenuList);
        lvMenu = view.findViewById(R.id.lvMenu);
        tvDate = view.findViewById(R.id.tvDate);

        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TeacherMenuStudentListFragment hedef = new TeacherMenuStudentListFragment();
                hedef.setFood(menu.get(position));
                hedef.setFoodPosition(position);
                hedef.setTeacher(teacher);
                hedef.setDateNow(dateNow);
                ekranaGit(hedef);
            }
        });

        tarihiGoster();
        menuyuGetir();

        return view;
    }

    private void ekranaGit(Fragment hedef){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flMainActivity, hedef);
        fragmentTransaction.addToBackStack("null");
        fragmentTransaction.commit();
    }

    private void tarihiGoster(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date now = Calendar.getInstance().getTime();
        dateNow = dateFormat.format(now);
        tvDate.setText(dateNow);
    }

    private void menuyuGetir(){
        Log.d(TAG, "Su anki zaman " + dateNow);
        pbMenuList.setVisibility(View.VISIBLE);

        db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).
                collection("FoodLists").document(dateNow).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    Toast.makeText(getActivity(), "No information found", Toast.LENGTH_SHORT).show();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.popBackStack();
                    return;
                }

                menu = (ArrayList)documentSnapshot.get("menu");
                lvMenuGoster();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbMenuList.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "An error occured", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Menu getirme hata: " + e.getMessage());
            }
        });
    }

    private void lvMenuGoster(){
        if(getActivity() == null)
            return;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menu);
        lvMenu.setAdapter(adapter);
        lvMenu.setVisibility(View.VISIBLE);
        pbMenuList.setVisibility(View.GONE);
    }

    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}
