package com.example.mypreschool.Fragments.TeacherFragments;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherFoodListFragment extends Fragment {
    private final String TAG = "TEACHERFOODLIST";

    private Date dateNow2, seledtedDate2;
    private Teacher teacher;
    private CalendarView cvCalendar;

    public TeacherFoodListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_food_list, container, false);

        cvCalendar = view.findViewById(R.id.cvCalendar);
        cvCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = (month + 1) + "-" + dayOfMonth + "-" + year;
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                Date now = Calendar.getInstance().getTime();
                String dateNow = dateFormat.format(now);
                try {
                    dateNow2 = dateFormat.parse(dateNow);
                    seledtedDate2 = dateFormat.parse(selectedDate);
                    Log.d(TAG, "Date Format: " + dateNow2 + " " + seledtedDate2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, dateNow.toString());
                Log.d(TAG, selectedDate);

                if(!(seledtedDate2.before(dateNow2)))
                    calendarDetayFragmentGetir();
                else
                    Toast.makeText(getActivity(), "You selected a date before current date, please reselect", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void calendarDetayFragmentGetir(){
        TeacherCalendarDetayFragment hedef = new TeacherCalendarDetayFragment();
        hedef.setTeacher(teacher);
        hedef.setSelectedDate(seledtedDate2);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.flMainActivity, hedef);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setTeacher(Teacher teacher){ this.teacher = teacher; }
}
