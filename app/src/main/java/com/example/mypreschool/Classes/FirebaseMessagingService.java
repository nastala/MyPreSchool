package com.example.mypreschool.Classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.mypreschool.MainActivity;
import com.example.mypreschool.ParentChatActivity;
import com.example.mypreschool.R;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private final String TAG = "FIREBASEMESSAGING";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification(remoteMessage);
    }

    private void showNotification(RemoteMessage remoteMessage) {
        Log.d(TAG, "REMOTEMESSAGE GELDI " + remoteMessage.getData().toString());

        String type = null;
        String key = null;
        if(remoteMessage.getData() != null) {
            key = remoteMessage.getData().get("key");
            type = remoteMessage.getData().get("type");
        }

        Intent i;

        if(key != null && type != null){
            if(type.equals("message")){
                i = new Intent(this, ParentChatActivity.class);
                i.putExtra("key", key);
            }
            else
                i = new Intent(this, MainActivity.class);
        }
        else
            i = new Intent(this, MainActivity.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                .setAutoCancel(true)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());
    }
}
