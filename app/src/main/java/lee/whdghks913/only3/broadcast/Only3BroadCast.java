package lee.whdghks913.only3.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.service.Only3Service;
import lee.whdghks913.only3.tools.AlarmTools;
import lee.whdghks913.only3.tools.CountTools;
import lee.whdghks913.only3.tools.NotificationTools;
import lee.whdghks913.only3.tools.Preference;
import lee.whdghks913.only3.tools.ToastTools;
import lee.whdghks913.only3.tools.Tools;

public class Only3BroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context mContext, Intent mIntent) {
        String mAction = mIntent.getAction();
        Preference mPref = new Preference(mContext);

        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(mAction)) {
            AlarmTools.setDateChangeAlarm(mContext);
            boolean autoStart = mPref.getBoolean("autoStart", false);
            if (autoStart) {
                mContext.startService(new Intent(mContext, Only3Service.class));
            }

        } else if (Intent.ACTION_DATE_CHANGED.equalsIgnoreCase(mAction)) {
            CountTools.resetCurrentCount(mContext);

            NotificationTools mNotify = new NotificationTools(mContext);
            mNotify.setTicker(mContext.getString(R.string.zero_count_title))
                    .setContentTitle(mContext.getString(R.string.zero_count_title))
                    .setContentText(mContext.getString(R.string.zero_count_msg))
                    .setOnGoing(false)
                    .setDefaults(0)
                    .notify(7777);

        } else if ("ACTION_NOTIFY_MINUTE".equalsIgnoreCase(mAction)) {
            int NotificationType = Tools.StringToInt(mPref.getString("notificationType", "1"));

            int appStartNotification = Tools.StringToInt(mPref.getString("appStartNotification", "-1"));
            int repeatCount = mPref.getInt("ACTION_NOTIFY_MINUTE_REPEAR", 1);

            if (NotificationType == 1 || NotificationType == 3) {
                NotificationTools mNotify = new NotificationTools(mContext);
                mNotify.setTicker(mContext.getString(R.string.app_started_minute_title))
                        .setContentTitle(mContext.getString(R.string.app_started_minute_title))
                        .setContentText(String.format(mContext.getString(R.string.app_started_minute_msg), appStartNotification * repeatCount))
                        .setOnGoing(false)
                        .notify(555);
            }
            if (NotificationType == 2 || NotificationType == 3) {
                ToastTools.createToast(mContext, String.format(mContext.getString(R.string.app_started_minute_msg), appStartNotification * repeatCount), false);
            }
        }
    }
}
