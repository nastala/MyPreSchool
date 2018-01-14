package com.example.mypreschool.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mypreschool.Classes.Announcement;
import com.example.mypreschool.R;

import java.util.ArrayList;

/**
 * Created by Nastala on 1/14/2018.
 */

public class AnnouncementAdapter extends BaseAdapter {
    private ArrayList<Announcement> announcements;
    private LayoutInflater mInflater;

    public AnnouncementAdapter(Activity activity, ArrayList<Announcement> announcements){
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.announcements = announcements;
    }

    @Override
    public int getCount() {
        return announcements.size();
    }

    @Override
    public Object getItem(int position) {
        return announcements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.layout_announcement_adapter, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvSchoolName = view.findViewById(R.id.tvSchoolName);
        TextView tvDetails = view.findViewById(R.id.tvDetails);

        Announcement announcement = announcements.get(position);

        tvTitle.setText(announcement.getTitle());
        tvSchoolName.setText(announcement.getSchoolName());
        tvDetails.setText(announcement.getDetails());

        return view;
    }
}
