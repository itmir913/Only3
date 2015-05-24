package lee.whdghks913.only3.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.service.Only3Service;
import lee.whdghks913.only3.tools.AlarmTools;
import lee.whdghks913.only3.tools.CountTools;
import lee.whdghks913.only3.tools.LockTools;
import lee.whdghks913.only3.tools.NotificationTools;
import lee.whdghks913.only3.tools.Only3;
import lee.whdghks913.only3.tools.Preference;
import lee.whdghks913.only3.tools.ServiceTools;
import lee.whdghks913.only3.tools.ToastTools;
import lee.whdghks913.only3.tools.Tools;

public class Only3BroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context mContext, Intent mIntent) {
        String mAction = mIntent.getAction();
        Preference mPref = new Preference(mContext);

        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(mAction)) {
            AlarmTools.setDateChangeAlarm(mContext);
            CountTools.isCountClear(mContext, true);

            boolean lockService = LockTools.getLockStarted(mContext);
            if (lockService) {
                long finishTime = LockTools.getFinishTime(mContext);
                long currentTime = System.currentTimeMillis();

                if (currentTime < finishTime) {
                    ServiceTools.startLockSubService(mContext);
                } else {
                    LockTools.removeLockStarted(mContext);
                }
            }

            boolean autoStart = mPref.getBoolean("autoStart", true);
            if (autoStart) {
                mContext.startService(new Intent(mContext, Only3Service.class));
            }

        } else if (Intent.ACTION_DATE_CHANGED.equalsIgnoreCase(mAction)) {
            CountTools.resetCurrentCount(mContext);
            CountTools.putCurrentDate(mContext);

            NotificationTools mNotify = new NotificationTools(mContext);
            mNotify.setTicker(mContext.getString(R.string.zero_count_title))
                    .setContentTitle(mContext.getString(R.string.zero_count_title))
                    .setContentText(mContext.getString(R.string.zero_count_msg))
                    .setOnGoing(false)
                    .setDefaults(0)
                    .notify(7777);

        } else if (Only3.ACTION_NOTIFY_MINUTE.equalsIgnoreCase(mAction)) {
            int NotificationType = Tools.StringToInt(mPref.getString("notificationType", "1"));

            int appStartNotification = Tools.StringToInt(mPref.getString("appStartNotification", "-1"));
            int repeatCount = mPref.getInt(Only3.ACTION_NOTIFY_MINUTE_REPEAT, 1);

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

            mPref.putInt(Only3.ACTION_NOTIFY_MINUTE_REPEAT, ++repeatCount);

        } else if (Only3.ACTION_START_LOCK_SERVICE.equalsIgnoreCase(mAction)) {
            long finishTime = LockTools.getFinishTime(mContext);
            if (finishTime != -1L) {
                ServiceTools.startLockSubService(mContext);
            }
        } else if (Only3.ACTION_STOP_LOCK_SERVICE.equalsIgnoreCase(mAction)) {
            LockTools.removeFinishTime(mContext);
            ServiceTools.stopLockSubService(mContext);
        }
//        else if (Intent.ACTION_PACKAGE_ADDED.equalsIgnoreCase(mAction)) {
//            int newAppCount = CountTools.getNewAppCount(mContext);
//
//            if (newAppCount != -1) {
//                String pkgName = mIntent.getData().getEncodedSchemeSpecificPart();
//                CountTools.addPackageAllCount(mContext, pkgName, newAppCount);
//            }
//
//        } else if (Intent.ACTION_PACKAGE_REMOVED.equalsIgnoreCase(mAction)) {
//            String pkgName = mIntent.getData().getEncodedSchemeSpecificPart();
//            CountTools.removePackage(mContext, pkgName);
//        }
    }
}
