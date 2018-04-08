package com.example.mypreschool.Fragments.TeacherFragments;


import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
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
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherListSharedActivitiesFragment extends Fragment {
    private final String TAG = "TEACHERLISTSHARED";
    private int EXTERNAL_STORAGE_REQUEST = 1001;

    private FirebaseFirestore db;
    private ProgressBar pbTeacher;
    private ListView lvSharedActivities;
    private ArrayList<ShareActivity> sharedActivities;
    private Teacher teacher;
    private ShareActivityAdapter adapter;
    private Drawable imageDrawable;
    private String imageName;
    private Dialog dialog;

    public TeacherListSharedActivitiesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_list_shared_activities, container, false);

        db = FirebaseFirestore.getInstance();
        sharedActivities = new ArrayList<>();

        pbTeacher = view.findViewById(R.id.pbTeacher);
        lvSharedActivities = view.findViewById(R.id.lvSharedActivies);

        bringSharedActivities();

        return view;
    }

    private void bringSharedActivities() {
        pbTeacher.setVisibility(View.VISIBLE);
        sharedActivities.clear();

        db.collection("ShareActivities").whereEqualTo("classID", teacher.getTeacherClassID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "STUDENT GETIRME HATA: " + e.getMessage());
                    pbTeacher.setVisibility(View.GONE);
                    return;
                }
                sharedActivities = new ArrayList<>();

                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()) {
                        pbTeacher.setVisibility(View.GONE);
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
                    shareActivity.setTeacher(true);
                    shareActivity.setDate(documentSnapshot.getDate("date"));
                    sharedActivities.add(shareActivity);
                }

                if(adapter == null)
                    showLvSharedActivities();
                else
                    adapter.updateShareActivities(sharedActivities);
            }
        });
    }

    private void showLvSharedActivities() {
        if(getActivity() == null)
            return;

        if (sharedActivities == null)
            return;

        if(sharedActivities.size() < 1) {
            pbTeacher.setVisibility(View.GONE);
            return;
        }

        sharedActivitiesSirala();

        adapter = new ShareActivityAdapter(getActivity(), sharedActivities, new ShareActivityAdapter.OnItemClickListener() {
            @Override
            public void onLikeButtonClick(ShareActivity shareActivity) {

            }

            @Override
            public void onImageVıewClick(Drawable drawable, String name) {
                imageDrawable = drawable;
                imageName = name;
                ivActivityDialogAc();
            }

            @Override
            public void onCivTeacherClick() {

            }
        });

        lvSharedActivities.setAdapter(adapter);
        lvSharedActivities.setVisibility(View.VISIBLE);
        pbTeacher.setVisibility(View.GONE);
    }

    private void sharedActivitiesSirala() {
        if(sharedActivities.size() < 2)
            return;

        Collections.sort(sharedActivities, new Comparator<ShareActivity>() {
            @Override
            public int compare(ShareActivity o1, ShareActivity o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
    }

    private void ivActivityDialogAc(){
        dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.layout_dialog_image_view);

        ImageView ivActivity = dialog.findViewById(R.id.ivActivity);
        Button btnDownload = dialog.findViewById(R.id.btnDownload);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_REQUEST);
                    }
                }
                else
                    drawableKaydet();
            }
        });

        ivActivity.setImageDrawable(imageDrawable);
        dialog.show();
    }

    private void drawableKaydet(){
        dialog.setCancelable(false);
        Bitmap bm = ((BitmapDrawable)imageDrawable).getBitmap();
        File extStorageDirectory = Environment.getExternalStorageDirectory();
        File folder = new File(extStorageDirectory.getAbsoluteFile(), "Downloads");
        folder.mkdir();
        if(!imageName.contains(".jpg"))
            imageName = imageName + ".jpg";
        File file = new File(folder.getAbsoluteFile(), imageName);
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

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == EXTERNAL_STORAGE_REQUEST){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                drawableKaydet();
            }
            else {
                Toast.makeText(this.getActivity(), "In order to download the image, you must grant the permission!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
