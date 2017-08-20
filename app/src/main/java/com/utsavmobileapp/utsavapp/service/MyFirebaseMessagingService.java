package com.utsavmobileapp.utsavapp.service;

/**
 * Created by Sumit on 10/7/2016.
 */


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.utsavmobileapp.utsavapp.ChatActivity;
import com.utsavmobileapp.utsavapp.R;

/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "important";
    String title,chatMessageUserId,chatMessageUserName,chatMessageSubTitle = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional

       Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            chatMessageUserId = remoteMessage.getData().get("chatMessageUserId");
            chatMessageUserName = remoteMessage.getData().get("chatMessageUserName");
            chatMessageSubTitle = remoteMessage.getData().get("chatMessageSubTitle");
          Log.e(TAG, "chatMessageUserId"+chatMessageUserId+"chatMessageUserName0"+chatMessageUserName+"chatMessageSubTitle"+chatMessageSubTitle);
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {

        if (title == null) {
            title = "Utsavapp";
        }
        Intent intent = new Intent(this, ChatActivity.class);

        intent.putExtra("chatMessageUserId", chatMessageUserId);
        intent.putExtra("chatMessageUserName", chatMessageUserName);
        intent.putExtra("chatMessageSubTitle", chatMessageSubTitle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        ;

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)

                .setLights(Color.BLUE,1,1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }
}