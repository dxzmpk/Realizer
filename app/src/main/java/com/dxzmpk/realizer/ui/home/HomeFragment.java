package com.dxzmpk.realizer.ui.home;

import android.app.AlarmManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dxzmpk.realizer.databinding.FragmentHomeBinding;
import com.dxzmpk.realizer.ui.receiver.ScreenListener;

import java.util.Random;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";

    private static boolean closed;

    private static HomeViewModel homeViewModel;
    private static NotificationSender notificationSender;
    protected static FragmentHomeBinding binding;
    static int NOTIFY_TIMES = 30;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.settingButton.setOnClickListener(this);
        homeViewModel.getNotifyOpen().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                binding.setNotifyOpen(aBoolean);
            }
        });
        homeViewModel.getSessionTime().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.setSessionTime(integer);
            }
        });
        registerUnlockReceiver();
        notificationSender = new NotificationSender(getContext());
        return binding.getRoot();
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
                closed = true;
            }

            @Override
            public void onUserPresent() {
                Log.e(TAG, "onUserPresent");
                Toast.makeText( getContext() , "解锁了" , Toast.LENGTH_SHORT ).show();
                closed = false;
                // 若通知功能开启
                if (homeViewModel.getNotifyOpen().getValue()) {
                    sendMessageForTimes(NOTIFY_TIMES, homeViewModel.getSessionTime().getValue());
                }
            }
        });
    }

    static class NotificationHandler extends Handler {

        public NotificationHandler(@NonNull Looper looper) {
            super(looper);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Log.e(TAG, "handleMessage SHOW:   id = " + msg.arg1);
            } else {
                Log.e(TAG, "handleMessage CANCEL: id = " + msg.arg1);
            }
            Log.e(TAG, "handlingMessage, versionNum = " + (Integer)msg.obj + ", curVersion = " + homeViewModel.getCurVision());
            if (!closed && homeViewModel.getNotifyOpen().getValue() && ((Integer)msg.obj == homeViewModel.getCurVision())) {
                if (msg.what == 1) {
                    notificationSender.showNotification(msg.arg2, homeViewModel.getCurVision() + "已连续在线超过"+ homeViewModel.getSessionTime().getValue()+ "分钟，第" + (msg.arg1 + 1) + "次提醒");
                } else {
                    notificationSender.notificationManager.cancel(msg.arg2);
                }
            }
        }
    }


    private void sendMessageForTimes(int n, int minutes) {
        Handler handler = new NotificationHandler(getContext().getMainLooper());
        int minutesDelay = minutes * 60 * 1000;
        // 永远只记录最后的Version
        homeViewModel.setCurVision(homeViewModel.getCurVision() + 1);
        for (int i = 0; i < n; i ++) {
            int randomId = new Random().nextInt();
            Message message = new Message();
            message.what = 1;
            message.arg1 = i;
            message.arg2 = randomId;
            message.obj = homeViewModel.getCurVision();
            handler.sendMessageDelayed(message, i * 5000 + minutesDelay);
            Message cancelMsg = new Message();
            cancelMsg.what = 2;
            cancelMsg.arg1 = i;
            cancelMsg.arg2 = randomId;
            cancelMsg.obj = homeViewModel.getCurVision();
            // 2s后取消消息
            handler.sendMessageDelayed(cancelMsg, i * 5000 + 3000 + minutesDelay);
            Log.e(TAG, "sendMessageForTimes = " + i);
        }
    }

    @Override
    public void onClick(View v) {
        int minutes;
        try {
            minutes = Integer.parseInt(binding.sessionTimeText.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        boolean checked = binding.notifyOpenSwitch.isChecked();
        homeViewModel.getSessionTime().postValue(minutes);
        homeViewModel.getNotifyOpen().postValue(checked);
        Toast.makeText(getContext(), "设置保存成功", Toast.LENGTH_SHORT).show();
        if (checked) {
            sendMessageForTimes(NOTIFY_TIMES, minutes);
        }
    }
}