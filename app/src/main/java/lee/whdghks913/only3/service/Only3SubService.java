package lee.whdghks913.only3.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.tools.NotificationTools;

public class Only3SubService extends Service {
    public static final String Only3SubServiceName = "lee.whdghks913.only3.service.Only3SubService";

    private NotificationTools mNotify;

    private void init() {
        mNotify = new NotificationTools(getApplicationContext());
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
