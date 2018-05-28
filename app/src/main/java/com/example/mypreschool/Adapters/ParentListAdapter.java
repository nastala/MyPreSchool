package com.example.mypreschool.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.example.mypreschool.Classes.Parent;
import com.example.mypreschool.R;
import java.util.ArrayList;

public class ParentListAdapter extends BaseAdapter {
    private ArrayList<Parent> parents;
    private ArrayList<Parent> checkedParents;
    private LayoutInflater layoutInflater;

    public ParentListAdapter(Activity activity, ArrayList<Parent> parents){
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.parents = parents;
        checkedParents = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return parents.size();
    }

    @Override
    public Object getItem(int i) {
        return parents.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = layoutInflater.inflate(R.layout.adapter_parent_list, viewGroup, false);

        TextView tvParentName = view.findViewById(R.id.tvParentName);
        CheckBox cbCheck = view.findViewById(R.id.cbCheck);

        Parent parent = parents.get(i);

        tvParentName.setText(parent.getIsim());

        cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                parents.get(i).setCheck(b);
                Log.d("PARENTLISTADAPTER", parents.get(i).getIsim() + " check change to: " + b);
                if(b)
                    checkedParents.add(parents.get(i));
                else
                    checkedParents.remove(parents.get(i));
            }
        });

        return view;
    }

    public ArrayList<Parent> getParents() { return parents; }

    public ArrayList<Parent> getCheckedParents() {
        return checkedParents;
    }
}
