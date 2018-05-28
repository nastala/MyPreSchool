package com.example.mypreschool.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mypreschool.Classes.GivenRequest;
import com.example.mypreschool.R;

import java.util.ArrayList;

public class GivenRequestsAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<GivenRequest> requests;

    public GivenRequestsAdapter(Activity activity, ArrayList<GivenRequest> requests){
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.requests = requests;
    }


    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int i) {
        return requests.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = layoutInflater.inflate(R.layout.adapter_given_requests, viewGroup, false);

        TextView tvStudentName = view.findViewById(R.id.tvStudentName);
        ImageView ivPermission = view.findViewById(R.id.ivPermission);

        GivenRequest givenRequest = requests.get(i);

        tvStudentName.setText(givenRequest.getStudentName());

        if(givenRequest.isPermission())
            ivPermission.setImageResource(R.drawable.yes);
        else
            ivPermission.setImageResource(R.drawable.no);

        return view;
    }
}
