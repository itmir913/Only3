package lee.whdghks913.only3.count;

import java.util.Calendar;

import lee.whdghks913.only3.BroadCast;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Alarm {
	AlarmManager am;
	
    PendingIntent sender_DATE;
    PendingIntent sender_10minute;
    PendingIntent sender_byUser;
    PendingIntent sender_fulllock;
	
	public Alarm(Context mContext){
		am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void setAlarm10M(Context mContext){
		Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR);//����
        int month = calendar.get(Calendar.MONTH);//�̹���(10���̸� 9�� ���Ϲ޴´�. calendar�� 0������ 11�������� 12���ǿ��� ���)
        int day = calendar.get(Calendar.DAY_OF_MONTH);//���ó�¥
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//����ð�
        int minute = calendar.get(Calendar.MINUTE);//�����
		
        Intent intent_10minute = new Intent(mContext, BroadCast.class);
		intent_10minute.setAction("ACTION_FALSE_THE_STOP");
		
		sender_10minute = PendingIntent.getBroadcast(mContext, 0, intent_10minute, 0);
        calendar.set(year, month ,day, hour, minute+10);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender_10minute);
	}
	
	public void setAlarmDateChange(Context mContext){
		Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR);//����
        int month = calendar.get(Calendar.MONTH);//�̹���(10���̸� 9�� ���Ϲ޴´�. calendar�� 0������ 11�������� 12���ǿ��� ���)
        int day = calendar.get(Calendar.DAY_OF_MONTH);//���ó�¥
        
        Intent intent_DATE = new Intent(mContext, BroadCast.class);
		intent_DATE.setAction("ACTION_DATE_CHANGE_BY_MIR");
        
        sender_DATE = PendingIntent.getBroadcast(mContext, 0, intent_DATE, 0);
        // ���� 0�� 0��(24�ð���)�� �ʱ�ȭ �ǵ��� �����մϴ�
        calendar.set(year, month ,day+1, 0, 0);
        // 24 * 60 * 60 * 1000 : �Ϸ� (�и��� ������, 1000�� 1���̴�)
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender_DATE);
	}
	
	
	public void cencleAlarm10M(){
		am.cancel(sender_10minute);
	}
	
	public void cencleAlarmDateChange(){
		am.cancel(sender_DATE);
	}
	
	public void setRemoveByUser(Context mContext){
		Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR);//����
        int month = calendar.get(Calendar.MONTH);//�̹���(10���̸� 9�� ���Ϲ޴´�. calendar�� 0������ 11�������� 12���ǿ��� ���)
        int day = calendar.get(Calendar.DAY_OF_MONTH);//���ó�¥
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//����ð�
        int minute = calendar.get(Calendar.MINUTE);//�����
        int second = calendar.get(Calendar.SECOND);//������
		
        Intent intent_byUser = new Intent(mContext, BroadCast.class);
        intent_byUser.setAction("ACTION_REMOVE_BY_USER");
		
        sender_byUser = PendingIntent.getBroadcast(mContext, 0, intent_byUser, 0);
        calendar.set(year, month ,day, hour, minute, second+30);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender_byUser);
	}
	
	public void cencleRemoveByUser(){
		am.cancel(sender_byUser);
	}
	
	public void setFullLockAlarm(Context mContext, long TimeInMillis){
		Intent intent_fulllock = new Intent(mContext, BroadCast.class);
    	intent_fulllock.setAction("ACTION_START_FULL_LOCK");
    	
    	sender_fulllock = PendingIntent.getBroadcast(mContext, 0, intent_fulllock, 0);
    	
    	AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    	am.set(AlarmManager.RTC_WAKEUP, TimeInMillis, sender_fulllock);
	}
	
	public void cencleFullLockAlarm(){
		am.cancel(sender_fulllock);
	}

}
