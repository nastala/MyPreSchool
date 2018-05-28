package com.example.mypreschool.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mypreschool.Classes.School;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nastala on 12/23/2017.
 */

public class TeacherAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Teacher> teachers;
    private TeacherAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onTeacherDelete(Teacher teacher);
        void onTeacherEdit(Teacher teacher);
    }

    public TeacherAdapter(Activity activity, ArrayList<Teacher> teachers, TeacherAdapter.OnItemClickListener onItemClickListener){
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.teachers = teachers;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return teachers.size();
    }

    @Override
    public Object getItem(int position) {
        return teachers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = mInflater.inflate(R.layout.layout_list_teacher_adapter, parent, false);

        TextView tvTeacherName = convertView.findViewById(R.id.tvTeacherName);
        ImageView ivTeacherDelete = convertView.findViewById(R.id.ivTeacherDelete);
        ImageView ivTeacherEdit = convertView.findViewById(R.id.ivTeacherEdit);
        CircleImageView civTeacher = convertView.findViewById(R.id.civTeacher);

        final Teacher teacher = teachers.get(position);

        if(!teacher.getTeacherPhoto().equals("default")){
            Log.d("TEACHERADAPTER", "teacher sgurl: " + teacher.getTeacherPhoto());
            Glide.with(civTeacher.getContext())
                    .load(teacher.getTeacherPhoto())
                    .into(civTeacher);
        }

        tvTeacherName.setText(teacher.getTeacherName());
        ivTeacherDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onTeacherDelete(teacher);
                }
            }
        });

        ivTeacherEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onTeacherEdit(teacher);
                }
            }
        });

        return convertView;
    }
}
