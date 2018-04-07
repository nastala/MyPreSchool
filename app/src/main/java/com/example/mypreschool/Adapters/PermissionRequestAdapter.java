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

import com.example.mypreschool.Classes.PermissionRequestClass;
import com.example.mypreschool.R;

import java.util.ArrayList;

public class PermissionRequestAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<PermissionRequestClass> permissionRequests;
    private onItemClickListener onItemClickListener;

    public interface onItemClickListener{
        void onIvYesClick(PermissionRequestClass permissionRequest, int index);
        void onIvNoClick(PermissionRequestClass permissionRequest, int index);
    }

    public PermissionRequestAdapter(Activity activity, ArrayList<PermissionRequestClass> permissionRequests, onItemClickListener onItemClickListener){
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.permissionRequests = permissionRequests;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return permissionRequests.size();
    }

    @Override
    public Object getItem(int i) {
        return permissionRequests.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View mView = layoutInflater.inflate(R.layout.adapter_student_permission_requests, null);

        TextView tvTitle = mView.findViewById(R.id.tvTitle);
        TextView tvDetails = mView.findViewById(R.id.tvDetails);
        TextView tvPrice = mView.findViewById(R.id.tvPrice);
        ImageView ivYes = mView.findViewById(R.id.ivYes);
        ImageView ivNo = mView.findViewById(R.id.ivNo);

        final PermissionRequestClass request = permissionRequests.get(i);

        tvTitle.setText(request.getTitle());
        tvDetails.setText(request.getDetails());
        tvPrice.setText(String.format("%.2f TL", request.getPrice()));

        ivYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null) {
                    if(request.isGivenRequest()){
                        Log.d("PERMISSIONADAPTER", "request given request");
                        onItemClickListener.onIvYesClick(request, i);
                    }
                    else if(!request.isCheck()){
                        request.setCheck(true);
                        onItemClickListener.onIvYesClick(request, i);
                    }
                }
            }
        });

        ivNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null && !request.isCheck()) {
                    if(request.isGivenRequest()){
                        onItemClickListener.onIvNoClick(request, i);
                    }
                    else if(!request.isCheck()) {
                        request.setCheck(true);
                        onItemClickListener.onIvNoClick(request, i);
                    }
                }
            }
        });

        return mView;
    }

    public void update(ArrayList<PermissionRequestClass> requests){
        this.permissionRequests = requests;
        this.notifyDataSetChanged();
    }
}
