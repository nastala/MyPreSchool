package com.example.mypreschool.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onYorumSilClick(Student student);
    }

    public StudentAdapter(Activity activity, ArrayList<Student> students, OnItemClickListener onItemClickListener) {
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.students = students;
        this.onItemClickListener = onItemClickListener;
    }

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
        final ImageView ivStudentDelete = view.findViewById(R.id.ivStudentDelete);

        final Student student = students.get(position);

        if(onItemClickListener == null)
            ivStudentDelete.setVisibility(View.GONE);

        ivStudentDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onYorumSilClick(student);
                }
            }
        });

        if(!(student.getSgurl().equals("default"))){
            Glide.with(civStudentImage.getContext())
                    .load(student.getSgurl())
                    .into(civStudentImage);
        }
        else
            civStudentImage.setImageResource(R.drawable.defaultprofil);

        tvStudentName.setText(student.getName());

        return view;
    }
}
