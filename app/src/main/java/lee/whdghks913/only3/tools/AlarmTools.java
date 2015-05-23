package lee.whdghks913.only3.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import lee.whdghks913.only3.broadcast.Only3BroadCast;

/**
 * Created by whdghks913 on 2015-05-17.
 */
public class AlarmTools {
    public static AlarmManager mAlarmManager;

    public static PendingIntent mStartNotification;

    public static void setDateChangeAlarm(Context mContext) {
        if (mAlarmManager == null)
            mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Calendar mCalendar = Calendar.getInstance();

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mCalendar.set(year, month, day + 1, 0, 0);

        Intent mIntentDate = new Intent(mContext, Only3BroadCast.class);
        mIntentDate.setAction(Intent.ACTION_DATE_CHANGED);
        PendingIntent mDatePendingIntent = PendingIntent.getBroadcast(mContext, 0, mIntentDate, 0);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), 24 * 60 * 60 * 1000, mDatePendingIntent);
    }

    public static void setStartNotification(Context mContext, int notifyMinute) {
        if (mAlarmManager == null)
            mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Calendar mCalendar = Calendar.getInstance();

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        if (notifyMinute == -1)
            return;

        Intent mIntent = new Intent(mContext, Only3BroadCast.class);
        mIntent.setAction("ACTION_NOTIFY_MINUTE");
        mStartNotification = PendingIntent.getBroadcast(mContext, 0, mIntent, 0);
        mCalendar.set(year, month, day, hour, minute + notifyMinute);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), notifyMinute * 60 * 1000, mStartNotification);
    }

    public static void cancelStartNotification(Context mContext) {
        if (mAlarmManager == null)
            mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        if (mStartNotification == null) {
            Intent mIntent = new Intent(mContext, Only3BroadCast.class);
            mIntent.setAction("ACTION_NOTIFY_MINUTE");
            mStartNotification = PendingIntent.getBroadcast(mContext, 0, mIntent, 0);
        }

        mAlarmManager.cancel(mStartNotification);
    }
}
