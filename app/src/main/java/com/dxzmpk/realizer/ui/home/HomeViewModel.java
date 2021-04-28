package com.dxzmpk.realizer.ui.home;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {


    private MutableLiveData<Boolean> notifyOpen;

    private MutableLiveData<Integer> sessionTime;

    private int curVision = 0;

    public int getCurVision() {
        return curVision;
    }

    public void setCurVision(int curVision) {
        this.curVision = curVision;
    }

    public HomeViewModel() {
        notifyOpen = new MutableLiveData<>();
        notifyOpen.postValue(true);
        sessionTime = new MutableLiveData<>();
        sessionTime.postValue(1);
    }

    public MutableLiveData<Boolean> getNotifyOpen() {
        return notifyOpen;
    }

    public void setNotifyOpen(MutableLiveData<Boolean> notifyOpen) {
        this.notifyOpen = notifyOpen;
    }

    public MutableLiveData<Integer> getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(MutableLiveData<Integer> sessionTime) {
        this.sessionTime = sessionTime;
    }
}