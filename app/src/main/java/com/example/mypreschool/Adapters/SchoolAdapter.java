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
import com.example.mypreschool.R;

import java.util.ArrayList;

/**
 * Created by Nastala on 12/19/2017.
 */

public class SchoolAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<School> schools;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onSchoolDelete(School school);
        void onSchoolEdit(School school);
    }

    public SchoolAdapter(Activity activity, ArrayList<School> schools, SchoolAdapter.OnItemClickListener onItemClickListener) {
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.schools = schools;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return schools.size();
    }

    @Override
    public Object getItem(int position) {
        return schools.get(position);
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

        final School school = schools.get(position);

        tvSchoolName.setText(school.getSchoolName());
        ivSchoolDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onSchoolDelete(school);
                }
            }
        });

        ivSchoolEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onSchoolEdit(school);
                }
            }
        });

        return convertView;
    }
}
