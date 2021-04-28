package com.dxzmpk.realizer.ui.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.dxzmpk.realizer.R;

import java.time.LocalDateTime;
import java.util.Random;

public class AlarmReceiverService extends Service {

    private static final String TAG = "AlarmReceiverService";

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        createManagerChannel();
        showNotification();
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    String channelId = "com.dxzmpk.notify";
    private NotificationManager notificationManager;
    private void createManagerChannel() {
        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "使用时长通知", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification() {
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("title")
                .setContentText("text")
                .setSmallIcon(R.drawable.ic_menu_send)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_camera))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        int randomId = new Random().nextInt();
        Log.e(TAG, "curTime = " + LocalDateTime.now().toString() + "\nNotification id = "
        + randomId);
        notificationManager.notify(randomId, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}