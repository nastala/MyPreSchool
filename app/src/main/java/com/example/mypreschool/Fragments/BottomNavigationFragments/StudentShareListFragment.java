package com.example.mypreschool.Fragments.BottomNavigationFragments;


import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mypreschool.Adapters.ShareActivityAdapter;
import com.example.mypreschool.Classes.ShareActivity;
import com.example.mypreschool.Classes.Student;
import com.example.mypreschool.R;
import com.example.mypreschool.StudentMainActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudentShareListFragment extends Fragment {
    private final String TAG = "STUDENTSHARELIST";

    private ListView lvActivities;
    private ProgressBar pbShareActivity;
    private FirebaseFirestore db;
    private Student student;
    private ArrayList<ShareActivity> shareActivities;
    private StudentMainActivity activity;
    private Dialog dialog;
    private ShareActivityAdapter adapter;
    private boolean lvKontrol;

    public StudentShareListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_share_list, container, false);

        db = FirebaseFirestore.getInstance();

        lvActivities = view.findViewById(R.id.lvActivities);
        pbShareActivity = view.findViewById(R.id.pbShareActivity);

        lvKontrol = false;

        getShareActivities();
        return view;
    }

    private void getShareActivities(){
        Log.d(TAG, "Student Name: " + student.getName() + " Class ID: " + student.getClassID());

        pbShareActivity.setVisibility(View.VISIBLE);
        db.collection("ShareActivities").whereEqualTo("classID", student.getClassID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "STUDENT GETIRME HATA: " + e.getMessage());
                    pbShareActivity.setVisibility(View.GONE);
                    return;
                }
                shareActivities = new ArrayList<>();

                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()) {
                        pbShareActivity.setVisibility(View.GONE);
                        return;
                    }

                    ShareActivity shareActivity = new ShareActivity();

                    shareActivity.setSgurl(documentSnapshot.getString("sgurl"));
                    shareActivity.setActivityTitle(documentSnapshot.getString("title"));
                    shareActivity.setActivityDetails(documentSnapshot.getString("details"));
                    shareActivity.setLikeNumber(documentSnapshot.getLong("likes").intValue());
                    shareActivity.setTsgurl(documentSnapshot.getString("tsgurl"));
                    shareActivity.setId(documentSnapshot.getId());
                    shareActivity.setLikedParents((ArrayList<String>)documentSnapshot.get("likedParents"));
                    shareActivity = likeKontrolEt(shareActivity);
                    shareActivities.add(shareActivity);
                }
                if(!lvKontrol) {
                    lvKontrol = true;
                    lvActivitiesDoldur();
                }
                else
                    adapter.updateShareActivities(shareActivities);
            }
        });
    }

    private void lvActivitiesDoldur(){
        if (shareActivities == null)
            return;

        if(shareActivities.size() < 1)
            return;

        adapter = new ShareActivityAdapter(getActivity(), shareActivities, new ShareActivityAdapter.OnItemClickListener() {
            @Override
            public void onLikeButtonClick(ShareActivity shareActivity) {
                shareActivityLikeArttir(shareActivity);
            }

            @Override
            public void onImageVıewClick(Drawable drawable, String name) {
                ivActivityDialogAc(drawable, name);
            }
        });

        if(getActivity() == null)
            return;

        lvActivities.setAdapter(adapter);
        pbShareActivity.setVisibility(View.GONE);
    }

    private ShareActivity likeKontrolEt(final ShareActivity shareActivity){
        if(shareActivity.getLikedParents() == null) {
            shareActivity.setCurrentParentLiked(false);
            return shareActivity;
        }

        for(String id : shareActivity.getLikedParents()){
            if(id.equals(student.getParentID()))
                shareActivity.setCurrentParentLiked(true);
            else
                shareActivity.setCurrentParentLiked(false);
        }

        return shareActivity;
    }

    private void ivActivityDialogAc(final Drawable drawable, final String name){
        dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog_image_view);

        ImageView ivActivity = dialog.findViewById(R.id.ivActivity);
        Button btnDownload = dialog.findViewById(R.id.btnDownload);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setCancelable(false);
                drawableKaydet(drawable, name);
            }
        });

        ivActivity.setImageDrawable(drawable);
        dialog.show();
    }

    private void drawableKaydet(Drawable drawable, String name){
        Bitmap bm = ((BitmapDrawable)drawable).getBitmap();
        File extStorageDirectory = Environment.getExternalStorageDirectory();
        File folder = new File(extStorageDirectory.getAbsoluteFile(), "Download");
        folder.mkdir();
        if(!name.contains(".jpg"))
            name = name + ".jpg";
        File file = new File(folder.getAbsoluteFile(), name);
        /*if(file.exists())
            return;*/

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            saveImageNotificationGoster(file.getAbsolutePath());
            Toast.makeText(getActivity(), "Image saved location: " + file.getPath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                outStream.flush();
                outStream.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        dialog.setCancelable(true);
    }

    private void saveImageNotificationGoster(String path) {
        Log.d(TAG, "Notification geldi path: " + path);
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");

        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "default")
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentTitle("Image Saved")
                .setContentText("deneme")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setBadgeIconType(android.R.drawable.stat_sys_download)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(001, builder.build());
    }

    private void shareActivityLikeArttir(ShareActivity shareActivity){
        WriteBatch batch = db.batch();
        DocumentReference likeRef = db.collection("ShareActivities").document(shareActivity.getId()).collection("Likes").document(student.getParentID());

    }

    public void setStudent(Student student){
        Log.d(TAG, "Student Name: " + student.getName());
        this.student = student;
    }
}