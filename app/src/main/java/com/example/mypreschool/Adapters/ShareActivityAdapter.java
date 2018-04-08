package com.example.mypreschool.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mypreschool.Classes.ShareActivity;
import com.example.mypreschool.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nastala on 12/29/2017.
 */

public class ShareActivityAdapter extends BaseAdapter {
    private ArrayList<ShareActivity> shareActivities;
    private LayoutInflater mInflater;
    private ShareActivityAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onLikeButtonClick(ShareActivity shareActivity);
        void onImageVıewClick(Drawable drawable, String name);
        void onCivTeacherClick();
    }

    public ShareActivityAdapter(Activity activity, ArrayList<ShareActivity> shareActivities, ShareActivityAdapter.OnItemClickListener onItemClickListener){
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.shareActivities = shareActivities;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return shareActivities.size();
    }

    @Override
    public Object getItem(int position) {
        return shareActivities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.layout_share_activity_adapter, null);

        CircleImageView civTeacher = view.findViewById(R.id.civTeacher);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvDetails = view.findViewById(R.id.tvDetails);
        final ImageView ivActivity = view.findViewById(R.id.ivActivity);
        TextView tvLikes = view.findViewById(R.id.tvLikes);
        Button btnLike = view.findViewById(R.id.btnLike);

        final ShareActivity shareActivity = shareActivities.get(position);

        if(!shareActivity.getTsgurl().equals("default")) {
            Glide.with(civTeacher.getContext())
                    .load(shareActivity.getTsgurl())
                    .into(civTeacher);
        }

        Glide.with(ivActivity.getContext())
                .load(shareActivity.getSgurl())
                .into(ivActivity);

        tvTitle.setText(shareActivity.getActivityTitle());
        tvDetails.setText(shareActivity.getActivityDetails());
        if(shareActivity.getLikeNumber() > 0)
            tvLikes.setText(shareActivity.getLikeNumber() + " parent liked.");
        else
            tvLikes.setVisibility(View.GONE);

        ivActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) {
                    String name = shareActivity.getSgurl().substring(shareActivity.getSgurl().length() - 10, shareActivity.getSgurl().length());
                    onItemClickListener.onImageVıewClick(ivActivity.getDrawable(), name);
                }
            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null && !shareActivity.isTeacher())
                    onItemClickListener.onLikeButtonClick(shareActivity);
            }
        });

        if(shareActivity.getCurrentParentLiked()){
            btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.liked2, 0, 0, 0);
            btnLike.setText("LIKED");
        }else {
            btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like4, 0, 0, 0);
            btnLike.setText("LIKE");
        }

        civTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null)
                    onItemClickListener.onCivTeacherClick();
            }
        });

        if(shareActivity.isTeacher())
            btnLike.setClickable(false);

        return view;
    }

    public void updateShareActivities(ArrayList<ShareActivity> shareActivities) {
        this.shareActivities.clear();
        this.shareActivities.addAll(shareActivities);
        this.notifyDataSetChanged();
    }
}
