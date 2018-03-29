package com.example.mypreschool.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.mypreschool.Classes.TeacherContact;
import com.example.mypreschool.R;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherContactAdapter extends BaseAdapter {
    private ArrayList<TeacherContact> contacts;
    private LayoutInflater layoutInflater;

    public TeacherContactAdapter(Activity activity, ArrayList<TeacherContact> contacts){
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.contacts = contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View mView = layoutInflater.inflate(R.layout.adapter_teacher_contact_adapter, null);

        CircleImageView civContact = mView.findViewById(R.id.civContact);
        TextView tvName = mView.findViewById(R.id.tvName);
        TextView tvValue = mView.findViewById(R.id.tvValue);

        TeacherContact teacherContact = contacts.get(i);

        tvName.setText(teacherContact.getName());
        tvValue.setText(teacherContact.getValue());
        civContact.setImageResource(teacherContact.getResource());

        return mView;
    }
}
