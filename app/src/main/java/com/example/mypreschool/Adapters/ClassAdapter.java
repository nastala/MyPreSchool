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
import com.example.mypreschool.Classes.SchoolClass;
import com.example.mypreschool.R;

import java.util.ArrayList;

/**
 * Created by Nastala on 12/19/2017.
 */

public class ClassAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<SchoolClass> classes;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onClassDelete(SchoolClass schoolClass);
        void onClassEdit(SchoolClass schoolClass);
    }

    public ClassAdapter(Activity activity, ArrayList<SchoolClass> classes, ClassAdapter.OnItemClickListener onItemClickListener){
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.classes = classes;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return classes.size();
    }

    @Override
    public Object getItem(int position) {
        return classes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = mInflater.inflate(R.layout.layout_list_school_adapter, parent, false);

        TextView tvSchoolName = convertView.findViewById(R.id.tvSchoolName);
        ImageView ivSchoolDelete = convertView.findViewById(R.id.ivSchoolDelete);
        ImageView ivSchoolEdit = convertView.findViewById(R.id.ivSchoolEdit);

        final SchoolClass schoolClass = classes.get(position);

        tvSchoolName.setText(schoolClass.getClassName());
        ivSchoolDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onClassDelete(schoolClass);
                }
            }
        });

        ivSchoolEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onClassEdit(schoolClass);
                }
            }
        });

        return convertView;
    }
}
