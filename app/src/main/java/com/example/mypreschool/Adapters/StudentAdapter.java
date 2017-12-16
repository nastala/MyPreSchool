package com.example.mypreschool.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nastala on 12/11/2017.
 */

public class StudentAdapter extends BaseAdapter {
    private ArrayList<Student> students;
    private LayoutInflater mInflater;

    public StudentAdapter(Activity activity, ArrayList<Student> students) {
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.students = students;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.layout_list_student_adapter, null);

        CircleImageView civStudentImage = view.findViewById(R.id.civStudentImage);
        TextView tvStudentName = view.findViewById(R.id.tvStudentName);

        Student student = students.get(position);
        if(!(student.getSgurl().isEmpty())){
            Glide.with(civStudentImage.getContext())
                    .load(student.getSgurl())
                    .into(civStudentImage);
        }

        tvStudentName.setText(student.getName());

        return view;
    }
}
