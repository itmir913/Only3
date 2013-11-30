package lee.whdghks913.only3;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class MainService extends Service {
	/**
	 * AlarmManager을 위한 코드
	 */
	AlarmManager am;
    Intent intent_DATE, intent_10minute;
    PendingIntent sender_DATE, sender_10minute;
    
	@Override
	public void onCreate() {
		super.onCreate();
		
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		/**
		 * 알람 매니저를 위한 코드
		 */
		
		intent_DATE = new Intent(this, BroadCast.class);
		intent_DATE.setAction("ACTION_DATE_CHANGE");
		
		intent_10minute = new Intent(this, BroadCast.class);
		intent_10minute.setAction("ACTION_FALSE_THE_STOP");
		
		Count_Alarm();
		
		System.gc();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		SharedPreferences setting = getSharedPreferences("setting", 0);
		if(setting.getBoolean("Service", false)){
			Intent intent = new Intent(this, BroadCast.class);
			sendBroadcast(intent);
		}else{
			am.cancel(sender_DATE);
			am.cancel(sender_10minute);
		}
		
	}
	
	public void Count_Alarm(){
		/**
		 * 첫번째 알람 (매 12시마다 카운트 초기화하기)
		 */
        Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR);//올해
        int month = calendar.get(Calendar.MONTH);//이번달(10월이면 9를 리턴받는다. calendar는 0월부터 11월까지로 12개의월을 사용)
        int day = calendar.get(Calendar.DAY_OF_MONTH);//오늘날짜
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//현재시간
        int minute = calendar.get(Calendar.MINUTE);//현재분
        
        sender_DATE = PendingIntent.getBroadcast(this, 0, intent_DATE, 0);
        // 매일 0시 0분(24시간제)에 초기화 되도록 설정합니다
        calendar.set(year, month ,day+1, 0, 0);
        // 24 * 60 * 60 * 1000 : 하루 (밀리초 단위로, 1000이 1초이다)
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender_DATE);
        
        /**
         * 두번째 알람 (실행한지 10분이 지나지 않으면 종료 불가능)
         */
        sender_10minute = PendingIntent.getBroadcast(this, 0, intent_10minute, 0);
        calendar.set(year, month ,day, hour, minute+10);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender_10minute);
	}

}
