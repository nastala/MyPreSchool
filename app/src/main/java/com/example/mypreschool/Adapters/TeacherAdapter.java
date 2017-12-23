package com.example.mypreschool.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypreschool.Classes.School;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;

import java.util.ArrayList;

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
        View view = mInflater.inflate(R.layout.layout_list_teacher_adapter, null);

        TextView tvTeacherName = view.findViewById(R.id.tvTeacherName);
        ImageView ivTeacherDelete = view.findViewById(R.id.ivTeacherDelete);
        ImageView ivTeacherEdit = view.findViewById(R.id.ivTeacherEdit);

        final Teacher teacher = teachers.get(position);

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

        return view;
    }
}
