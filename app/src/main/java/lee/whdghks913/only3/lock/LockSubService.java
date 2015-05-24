package lee.whdghks913.only3.lock;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.tools.NotificationTools;
import lee.whdghks913.only3.tools.ServiceTools;

public class LockSubService extends Service {
    public static final String LockSubServiceName = "lee.whdghks913.only3.lock.LockSubService";

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

        mNotify.setTicker(getString(R.string.LockServiceTicker))
                .setContentTitle(getString(R.string.LockServiceTitle))
                .setContentText(getString(R.string.LockServiceMsg))
                .setOnGoing(true)
                .setPendingIntent(PendingIntent.getActivity(getApplicationContext(), Context.MODE_PRIVATE, new Intent(getApplicationContext(), LockActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

        startForeground(1234, mNotify.build());

//        ServiceTools.stopSubService(getApplicationContext());
//        ServiceTools.stopService(getApplicationContext());

        ServiceTools.startLockService(getApplicationContext());

        Intent mIntent = new Intent(getApplicationContext(), LockActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(mIntent);
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
        ServiceTools.stopLockService(getApplicationContext());

        if (mNotify == null)
            mNotify = new NotificationTools(getApplicationContext());
        mNotify.cancel(1234);

        try {
            if (mReceiver == null)
                mReceiver = new LockScreenBroadCast();
            unregisterReceiver(mReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
