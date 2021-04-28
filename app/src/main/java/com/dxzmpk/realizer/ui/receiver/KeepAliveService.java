package com.dxzmpk.realizer.ui.receiver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class KeepAliveService extends Service {
    public KeepAliveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}