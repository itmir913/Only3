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
	 * AlarmManager�� ���� �ڵ�
	 */
	AlarmManager am;
    Intent intent_DATE, intent_10minute;
    PendingIntent sender_DATE, sender_10minute;
    
	@Override
	public void onCreate() {
		super.onCreate();
		
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		/**
		 * �˶� �Ŵ����� ���� �ڵ�
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
		 * ù��° �˶� (�� 12�ø��� ī��Ʈ �ʱ�ȭ�ϱ�)
		 */
        Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR);//����
        int month = calendar.get(Calendar.MONTH);//�̹���(10���̸� 9�� ���Ϲ޴´�. calendar�� 0������ 11�������� 12���ǿ��� ���)
        int day = calendar.get(Calendar.DAY_OF_MONTH);//���ó�¥
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//����ð�
        int minute = calendar.get(Calendar.MINUTE);//�����
        
        sender_DATE = PendingIntent.getBroadcast(this, 0, intent_DATE, 0);
        // ���� 0�� 0��(24�ð���)�� �ʱ�ȭ �ǵ��� �����մϴ�
        calendar.set(year, month ,day+1, 0, 0);
        // 24 * 60 * 60 * 1000 : �Ϸ� (�и��� ������, 1000�� 1���̴�)
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender_DATE);
        
        /**
         * �ι�° �˶� (�������� 10���� ������ ������ ���� �Ұ���)
         */
        sender_10minute = PendingIntent.getBroadcast(this, 0, intent_10minute, 0);
        calendar.set(year, month ,day, hour, minute+10);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender_10minute);
	}

}
