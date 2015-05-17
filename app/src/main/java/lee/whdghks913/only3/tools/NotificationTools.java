package lee.whdghks913.only3.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import lee.whdghks913.only3.MainActivity;
import lee.whdghks913.only3.R;

/**
 * Created by 종환 on 2015-05-11.
 */
public class NotificationTools {
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mCompatBuilder;

    public NotificationTools(Context mContext) {
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, Context.MODE_PRIVATE, new Intent(mContext, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mCompatBuilder = new NotificationCompat.Builder(mContext);
        mCompatBuilder.setWhen(System.currentTimeMillis());
        mCompatBuilder.setContentIntent(pendingIntent);
        mCompatBuilder.setAutoCancel(true);

        Preference mPref = new Preference(mContext);
        if (mPref.getBoolean("useVibrate", false))
            mCompatBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

        if (mPref.getBoolean("useTransparentIcon", false))
            mCompatBuilder.setSmallIcon(R.drawable.transparent_icon);
        else
            mCompatBuilder.setSmallIcon(R.mipmap.ic_launcher);
    }

    public NotificationTools setPendingIntent(PendingIntent pendingIntent) {
        mCompatBuilder.setContentIntent(pendingIntent);
        return this;
    }

    public NotificationTools setDefaults(int defaults) {
        mCompatBuilder.setDefaults(defaults);
        return this;
    }

    public NotificationTools setNumber(int num) {
        mCompatBuilder.setNumber(num);
        return this;
    }

    public NotificationTools setTicker(String msg) {
        mCompatBuilder.setTicker(msg);
        return this;
    }

    public NotificationTools setContentTitle(String msg) {
        mCompatBuilder.setContentTitle(msg);
        return this;
    }

    public NotificationTools setContentText(String msg) {
        mCompatBuilder.setContentText(msg);
        return this;
    }

    public NotificationTools setOnGoing(boolean onGoing) {
        if (onGoing == true)
            mCompatBuilder.setOngoing(onGoing);
        else{
            mCompatBuilder.setOngoing(false);
            mCompatBuilder.setAutoCancel(true);
        }

        return this;
    }

    public Notification build() {
        if (mCompatBuilder != null)
            return mCompatBuilder.build();
        return null;
    }

    public void notify(int integer) {
        if (mCompatBuilder != null)
            mNotificationManager.notify(integer, build());
    }

    public void cancel(int integer) {
        mNotificationManager.cancel(integer);
    }
}
