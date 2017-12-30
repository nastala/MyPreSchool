package com.example.mypreschool.Fragments.TeacherFragments;


import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mypreschool.Classes.ShareActivity;
import com.example.mypreschool.Classes.Teacher;
import com.example.mypreschool.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherShareActivityFragment extends Fragment {
    private final int IMAGE_REQUEST = 10001;

    private Teacher teacher;
    private FirebaseFirestore db;
    private EditText etActivityTitle, etActivityDetails;
    private ImageView ivActivity;
    private Button btnShareActivity;
    private ProgressBar pbShareActivity;
    private Dialog dialog;
    private Uri uri;
    private boolean ivKontrol;
    private ShareActivity shareActivity;
    private StorageReference mStorage;


    public TeacherShareActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_share_activity, container, false);

        db = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();

        etActivityTitle = view.findViewById(R.id.etActivityTitle);
        etActivityDetails = view.findViewById(R.id.etActivityDetails);
        ivActivity = view.findViewById(R.id.ivActivity);
        btnShareActivity = view.findViewById(R.id.btnShareActivity);
        pbShareActivity = view.findViewById(R.id.pbShareActivity);

        shareActivity = new ShareActivity();

        ivKontrol = false;

        ivActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST);
            }
        });

        btnShareActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!kontrolEt())
                    return;

                shareActivityiEkle();
            }
        });

        return view;
    }

    private void shareActivityiEkle(){
        pbShareActivity.setVisibility(View.VISIBLE);

        Map<String, Object> map = new HashMap<>();
        map.put("title", shareActivity.getActivityTitle());
        map.put("details", shareActivity.getActivityDetails());
        map.put("sgurl", shareActivity.getSgurl());
        map.put("likes", 0);
        map.put("classID", teacher.getTeacherClassID());
        map.put("tsgurl", teacher.getTeacherPhoto());
        map.put("likedParents", new ArrayList<String>());

        db.collection("ShareActivities").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Activity shared successfully", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
                pbShareActivity.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "An error occured", Toast.LENGTH_SHORT).show();
                Log.d("TEACHERSHARE", "ACTIVITY SHARE ERROR: " + e.getMessage());
                pbShareActivity.setVisibility(View.GONE);
            }
        });
    }

    private boolean kontrolEt(){
        String activityTitle = etActivityTitle.getText().toString();
        String activityDetails = etActivityDetails.getText().toString();
        boolean control = true;

        if(etActivityTitle.getText() == null || activityTitle.isEmpty()){
            etActivityTitle.setError("Activity name can not be empty");
            control = false;
        }

        if(etActivityDetails.getText() == null || activityDetails.isEmpty()){
            etActivityDetails.setError("Activity title can not be empty");
            control = false;
        }

        if(!ivKontrol){
            Toast.makeText(getActivity(), "Image can not be empty", Toast.LENGTH_SHORT).show();
            control = false;
        }

        if(control) {
            shareActivity.setActivityDetails(activityDetails);
            shareActivity.setActivityTitle(activityTitle);
        }

        return control;
    }

    private void yukleniyorDialogGoster(){
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.layout_progress_bar);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void yukleniyorDialogKapat(){
        dialog.dismiss();
    }

    public void setTeacher(Teacher teacher){ this.teacher = teacher; }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == IMAGE_REQUEST) {
            if (data == null)
                return;

            yukleniyorDialogGoster();
            uri = data.getData();
            Glide.with(ivActivity.getContext())
                    .load(uri)
                    .into(ivActivity);

            StorageReference filePath = mStorage.child("ShareActivities/" + uri.getLastPathSegment());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    shareActivity.setSgurl(downloadUri.toString());
                    ivKontrol = true;
                    yukleniyorDialogKapat();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TEACHERSHARE", "TEACHERSHARE FIREBASE STORAGE HATA: " + e.getMessage());
                    yukleniyorDialogKapat();
                    ivKontrol = false;
                }
            });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
