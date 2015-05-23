package lee.whdghks913.only3.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.broadcast.LockScreenBroadCast;
import lee.whdghks913.only3.tools.NotificationTools;

public class Only3SubService extends Service {
    public static final String Only3SubServiceName = "lee.whdghks913.only3.service.Only3SubService";

    private NotificationTools mNotify;
    private BroadcastReceiver mReceiver;

    private void init() {
        mNotify = new NotificationTools(getApplicationContext());
        mReceiver = new LockScreenBroadCast();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();

        mNotify.setTicker(getString(R.string.Only3ServiceTicker))
                .setContentTitle(getString(R.string.Only3ServiceTitle))
                .setContentText(getString(R.string.Only3ServiceMsg))
                .setOnGoing(true);

        startForeground(913, mNotify.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mReceiver == null)
            mReceiver = new LockScreenBroadCast();

        IntentFilter mFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mFilter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(mReceiver, mFilter);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
        mNotify.cancel(913);

        try {
            unregisterReceiver(mReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
