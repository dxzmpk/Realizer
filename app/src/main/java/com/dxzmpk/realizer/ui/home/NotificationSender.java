package com.dxzmpk.realizer.ui.home;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.VibrationEffect;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.dxzmpk.realizer.R;

import java.time.LocalDateTime;
import java.util.Random;

public class NotificationSender {

    private static final String TAG = "NotificationSender";

    String channelId = "com.dxzmpk.notify";
    public NotificationManager notificationManager;
    private Context context;

    public NotificationSender(Context context) {
        this.context = context;
        createManagerChannel();
    }

    private void createManagerChannel() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "使用时长通知", NotificationManager.IMPORTANCE_HIGH);
            channel.setVibrationPattern(new long[] { 0, 1000, 1000, 1000, 1000 });
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showNotification(int randomId, String text) {
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("Realizer")
                .setContentText(text)
                .setVibrate(new long[] { 0, 1000, 1000, 1000, 1000 })
                .setSmallIcon(R.drawable.ic_menu_send)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_camera))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        Log.e(TAG, "curTime = " + LocalDateTime.now().toLocalTime().toString());
        notificationManager.notify(randomId, notification);
    }
}
