package com.example.mypreschool.Classes;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Helper {
    private final static String TAG = "HELPER";
    private static ArrayList<String> parentTokens;
    private static FirebaseFirestore db;

    public static ArrayList<String> getParentTokens(){
        parentTokens = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        db.collection("Users").whereEqualTo("type", "parent").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                    if(!documentSnapshot.exists()){
                        Log.d(TAG, "Users'da parent yok");
                        return;
                    }

                    String token = documentSnapshot.getString("sgcm");
                    if(checkToken(token, parentTokens))
                        parentTokens.add(token);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Users getirme hata: " + e.getMessage());
            }
        });

        return parentTokens;
    }

    private static boolean checkToken(String token, ArrayList<String> tokens) {
        boolean check = true;

        for(String sgcm : tokens){
            if(sgcm.equals(token)) {
                check = false;
                break;
            }
        }

        return check;
    }
}
