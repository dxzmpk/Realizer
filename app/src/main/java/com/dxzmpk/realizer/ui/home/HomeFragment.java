package com.dxzmpk.realizer.ui.home;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dxzmpk.realizer.MainActivity;
import com.dxzmpk.realizer.R;
import com.dxzmpk.realizer.ui.receiver.AlarmReceiverService;
import com.dxzmpk.realizer.ui.receiver.ScreenListener;

import java.lang.ref.WeakReference;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private HomeViewModel homeViewModel;
    private AlarmManager alarmManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.session_time);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        registerUnlockReceiver();
        return root;
    }

    private void registerUnlockReceiver() {
        ScreenListener screenListener = new ScreenListener(getContext());
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Log.e(TAG, "onScreenOn");
            }

            @Override
            public void onScreenOff() {
                Log.e(TAG, "onScreenOff");
                // cancel the alarm
                if (alarmManager != null) {
                    Intent intent = new Intent(getContext(), AlarmReceiverService.class);
                    PendingIntent pendingIntent = PendingIntent.getService(getContext(), 0, intent, 0);
                    alarmManager.cancel(pendingIntent);
                }
            }

            @Override
            public void onUserPresent() {
                Toast.makeText( getContext() , "解锁了" , Toast.LENGTH_SHORT ).show();
                setAlarmTask();
                Log.e(TAG, "onUserPresent");
            }
        });
    }

    static class NotificationHandler extends Handler {
        private WeakReference<Context> context;

        public NotificationHandler(@NonNull Looper looper, WeakReference<Context> context) {
            super(looper);
            this.context = context;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Intent intent = new Intent(context.get(), AlarmReceiverService.class);
            context.get().startService(intent);
        }
    }

    private void sendMessageForTimes() {

    }

    private void setAlarmTask() {
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiverService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getContext(), 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC,
                System.currentTimeMillis(),
                60 * 1000, pendingIntent);
    }

}