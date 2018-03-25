package imt.logofinder.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import imt.logofinder.activity.MainActivity;

/**
 * Created by Nico on 25/03/2018.
 */

public class UpdateDataService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent contentIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Uri uriSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notif = new Notification.Builder(getApplicationContext())
                .setContentTitle("LogoFinder")
                .setContentText("Une nouvelle version est disponible sur le serveur")
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setVibrate(new long[]{0, 100, 20, 100})
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setSound(uriSound)
                .build();
        NotificationManager notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notifManager != null) {
            notifManager.notify(234, notif);
        }
    }
}
