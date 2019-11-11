package com.misael.appchat.server;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.misael.appchat.BuildConfig;
import com.misael.appchat.R;
import com.misael.appchat.model.Notification;
import com.misael.appchat.view.ChatActivity;
import com.misael.appchat.model.User;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        final Map<String, String> data = remoteMessage.getData();

        if (data.get("sender") == null) return;

        final Intent intent = new Intent(this, ChatActivity.class);

        FirebaseFirestore.getInstance().collection("/users")
                .document(data.get("sender"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User sender = documentSnapshot.toObject(User.class);

                        intent.putExtra("user", sender);

                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                                                                                intent, 0);
                        NotificationManager notificationManager =  (NotificationManager)
                                getSystemService(Context.NOTIFICATION_SERVICE);

                        String notificationChannelId = "chanel_01";

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel notificationChannel = new NotificationChannel(notificationChannelId, "My notification",
                                    NotificationManager.IMPORTANCE_DEFAULT);

                            notificationChannel.setDescription("Description");
                            notificationChannel.enableLights(true);
                            notificationChannel.setLightColor(Color.RED);

                            notificationManager.createNotificationChannel(notificationChannel);
                        }

                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(getApplicationContext(), notificationChannelId);

                        builder.setAutoCancel(true)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(data.get("title"))
                            .setContentText(data.get("body"))
                            .setContentIntent(pendingIntent);

                        notificationManager.notify(1, builder.build());
                    }
                });
    }
}
