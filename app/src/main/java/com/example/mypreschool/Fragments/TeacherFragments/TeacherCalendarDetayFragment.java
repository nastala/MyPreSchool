package com.example.mypreschool.Fragments.TeacherFragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherCalendarDetayFragment extends Fragment {
    private final String TAG = "TEACHERCALENDARDETAY";

    private TextView tvDate;
    private ListView lvFoodList;
    private EditText etFood;
    private Button btnAdd, btnSave;
    private Date selectedDate;
    private FirebaseFirestore db;
    private ProgressBar pbFood;
    private LinearLayout llFood;
    private Teacher teacher;
    private boolean foodKontrol;
    private String selectedDateString;
    private ArrayList<String> foods;

    public TeacherCalendarDetayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_calendar_detay, container, false);

        db = FirebaseFirestore.getInstance();

        tvDate = view.findViewById(R.id.tvDate);
        lvFoodList = view.findViewById(R.id.lvFoodList);
        etFood = view.findViewById(R.id.etFood);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnSave = view.findViewById(R.id.btnSave);
        pbFood = view.findViewById(R.id.pbFood);
        llFood = view.findViewById(R.id.llFood);

        foods = new ArrayList<>();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String food = etFood.getText().toString();
                foods.add(food);
                lvFoodListDoldur();
                etFood.setText("");
            }
        });

        lvFoodList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                foodSilAlertDialogCikart(position);

                return false;
            }
        });

        etFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etFood.requestFocus();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!foodKontrol)
                    dbyeKaydet();
                else
                    dbyiGuncelle();
            }
        });

        yemekKontrolEt();
        return view;
    }

    private void dbyeKaydet(){
        if(foods == null || foods.size() < 1)
            return;

        pbFood.setVisibility(View.VISIBLE);
        Map<String, Object>  map = new HashMap<>();
        map.put("menu", foods);

        db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").
                document(teacher.getTeacherClassID()).collection("FoodLists").document(selectedDateString).set(map).
                addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Menu added", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();
                Log.d(TAG, "FOODLIST ADDED");
                pbFood.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FOODLIST ADD HATA: " + e.getMessage());
                pbFood.setVisibility(View.GONE);
            }
        });
    }

    private void dbyiGuncelle(){
        if(foods == null || foods.size() < 1)
            return;

        pbFood.setVisibility(View.VISIBLE);
        Map<String, Object>  map = new HashMap<>();
        map.put("menu", foods);

        db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").
                document(teacher.getTeacherClassID()).collection("FoodLists").document(selectedDateString).update(map).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Menu added", Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack();
                        Log.d(TAG, "FOODLIST ADDED");
                        pbFood.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FOODLIST ADD HATA: " + e.getMessage());
                pbFood.setVisibility(View.GONE);
            }
        });
    }

    private void lvFoodListDoldur(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, foods);
        lvFoodList.setAdapter(adapter);
    }

    private void foodSilAlertDialogCikart(final int position){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("ATTENTION!");
        alertDialog.setMessage("Are you sure to remove the food?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                foods.remove(position);
                lvFoodListDoldur();
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void yemekKontrolEt(){
        tvDate.setText(selectedDateString);

        pbFood.setVisibility(View.VISIBLE);
        foodKontrol = true;
        CollectionReference colRef = db.collection("Schools").document(teacher.getTeacherSchoolID()).collection("Classes").document(teacher.getTeacherClassID()).collection("FoodLists");
        colRef.document(selectedDateString).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(!documentSnapshot.exists()){
                    Log.d(TAG, "YEMEK BULUMADI");
                    foodKontrol = false;
                }else {
                    foodKontrol = true;
                    Log.d(TAG, "YEMEK BULUNDU");
                    foods = (ArrayList<String>)documentSnapshot.get("menu");
                }

                addFootLayoutGoster();
                pbFood.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FOODLIST GETIRME HATA: " + e.getMessage());
                pbFood.setVisibility(View.GONE);
            }
        });
    }

    private void addFootLayoutGoster(){
        if(!foodKontrol)
            llFood.setVisibility(View.VISIBLE);
        else {
            lvFoodListDoldur();
            btnSave.setText("UPDATE");
        }
    }

    public void setSelectedDate(Date selectedDate){
        this.selectedDate = selectedDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        selectedDateString = dateFormat.format(selectedDate);
    }

    public void setTeacher(Teacher teacher){ this.teacher = teacher; }
}
