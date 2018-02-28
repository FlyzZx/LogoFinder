package imt.logofinder.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Tom on 28/02/2018.
 */

public class SocketService extends Service {

    private final static String TAG = "SocketService";

    private Socket socket = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        try {
            socket = IO.socket("http://51.254.205.180:8080");
            socket.on("logofinder", onNewData);
            socket.connect();
            if (socket.connected()) {
                Log.d(TAG, "Socket is connected");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }


    private Emitter.Listener onNewData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Notification notif = new Notification.Builder(getApplicationContext())
                    .setContentTitle("LogoFinder")
                    .setContentText("Une nouvelle version est disponible sur le serveur")
                    .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                    .setVibrate(new long[]{0, 100, 20, 100})
                    .build();
            NotificationManager notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notifManager != null) {
                notifManager.notify(234, notif);
            }
        }
    };

}
