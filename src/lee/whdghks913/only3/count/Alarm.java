package lee.whdghks913.only3.count;

import java.util.Calendar;

import lee.whdghks913.only3.BroadCast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Alarm {
	AlarmManager am;
	
	Intent intent_DATE;
	Intent intent_10minute;
	Intent intent_byUser;
	
    PendingIntent sender_DATE;
    PendingIntent sender_10minute;
    PendingIntent sender_byUser;
	
	public Alarm(Context mContext){
		am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void setAlarm10M(Context mContext){
		Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR);//올해
        int month = calendar.get(Calendar.MONTH);//이번달(10월이면 9를 리턴받는다. calendar는 0월부터 11월까지로 12개의월을 사용)
        int day = calendar.get(Calendar.DAY_OF_MONTH);//오늘날짜
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//현재시간
        int minute = calendar.get(Calendar.MINUTE);//현재분
		
		intent_10minute = new Intent(mContext, BroadCast.class);
		intent_10minute.setAction("ACTION_FALSE_THE_STOP");
		
		sender_10minute = PendingIntent.getBroadcast(mContext, 0, intent_10minute, 0);
        calendar.set(year, month ,day, hour, minute+10);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender_10minute);
	}
	
	public void setAlarmDateChange(Context mContext){
		Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR);//올해
        int month = calendar.get(Calendar.MONTH);//이번달(10월이면 9를 리턴받는다. calendar는 0월부터 11월까지로 12개의월을 사용)
        int day = calendar.get(Calendar.DAY_OF_MONTH);//오늘날짜
        
        intent_DATE = new Intent(mContext, BroadCast.class);
		intent_DATE.setAction("ACTION_DATE_CHANGE_BY_MIR");
        
        sender_DATE = PendingIntent.getBroadcast(mContext, 0, intent_DATE, 0);
        // 매일 0시 0분(24시간제)에 초기화 되도록 설정합니다
        calendar.set(year, month ,day+1, 0, 0);
        // 24 * 60 * 60 * 1000 : 하루 (밀리초 단위로, 1000이 1초이다)
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
        
        int year = calendar.get(Calendar.YEAR);//올해
        int month = calendar.get(Calendar.MONTH);//이번달(10월이면 9를 리턴받는다. calendar는 0월부터 11월까지로 12개의월을 사용)
        int day = calendar.get(Calendar.DAY_OF_MONTH);//오늘날짜
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//현재시간
        int minute = calendar.get(Calendar.MINUTE);//현재분
        int second = calendar.get(Calendar.SECOND);//현재초
		
        intent_byUser = new Intent(mContext, BroadCast.class);
        intent_byUser.setAction("ACTION_REMOVE_BY_USER");
		
        sender_byUser = PendingIntent.getBroadcast(mContext, 0, intent_byUser, 0);
        calendar.set(year, month ,day, hour, minute, second+30);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender_byUser);
	}
	
	public void cencleRemoveByUser(){
		am.cancel(sender_byUser);
	}

}
