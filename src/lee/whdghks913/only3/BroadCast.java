package lee.whdghks913.only3;

import java.util.Calendar;

import lee.whdghks913.only3.count.Alarm;
import lee.whdghks913.only3.count.AndroidService;
import lee.whdghks913.only3.count.CountCheckService;
import lee.whdghks913.only3.count.SubService;
import lee.whdghks913.only3.fulllock.FullLockActivity;
import lee.whdghks913.only3.fulllock.FullLockService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class BroadCast extends BroadcastReceiver {
	SharedPreferences package_count, setting, full_lock;
	SharedPreferences.Editor package_count_Editor, setting_Editor;
	
	@Override
	public void onReceive(Context mContext, Intent intent) {
		String action = intent.getAction();
		
//		Log.i("getAction", action);
		
		/**
		 * 1.7 업데이트
		 * setting을 지정하지 않아서 브로드캐스트 리시버에서 강제종료 되는 오류 수정
		 */
		setting = mContext.getSharedPreferences("setting", 0);
		setting_Editor = setting.edit();
		
		full_lock = mContext.getSharedPreferences("full_lock", 0);
		
		if (Intent.ACTION_BOOT_COMPLETED.equals(action)){
			if(full_lock.getBoolean("Enable", true)){
				mContext.startService(new Intent(mContext, FullLockService.class));
				
				Intent i = new Intent(mContext, FullLockActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TOP |
						Intent.FLAG_ACTIVITY_SINGLE_TOP );
				mContext.startActivity(intent);
				return;
			}
			
	        /**
	         * 1.5업데이트
	         * 관리자 권한이 없으면 부팅이 되도 자동으로 실행되지 않도록 코드 설정
	         */
	    	ComponentName adminComponent = new ComponentName(mContext, AdminReceiver.class);
	    	DevicePolicyManager devicePolicyManager = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
	        
	        if (devicePolicyManager.isAdminActive(adminComponent)){
	        	try { Thread.sleep(10000); } catch (InterruptedException e) {}
	        	
	        	mContext.startService(new Intent(mContext, AndroidService.class));
	        	mContext.startService(new Intent(mContext, SubService.class));
	        	
				Alarm alarm = new Alarm(mContext);
				alarm.setAlarm10M(mContext);
				alarm.setAlarmDateChange(mContext);
	        }
	        
	        /**
	         * 정각 12시 0분에 폰이 꺼져있고, 부팅시 카운트를 초기화 함
	         */
	        if(setting.getInt("Clear_day", 999) < Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
	        	if(setting.getInt("NotifiType", 0)==0 || setting.getInt("NotifiType", 0)==2)
	        		Notifi_DATE_CHANGE(mContext);
				if(setting.getInt("NotifiType", 0)==1 || setting.getInt("NotifiType", 0)==2)
					Toast_DATE_CHANGE(mContext);
	        }
        }else if(Intent.ACTION_DATE_CHANGED.equals(action) || "android.intent.action.DATE_CHANGED".equals(action)){
//        	Log.d("날짜 변경", "user");
        	setting_Editor.putBoolean("DateChangeByUser", true).commit();
        	
        	Alarm alarm = new Alarm(mContext);
        	alarm.setRemoveByUser(mContext);
        	
        }else if("ACTION_DATE_CHANGE_BY_MIR".equals(action)){
//        	Log.d("날짜 변경", "mir");
        	mContext.startService(new Intent(mContext, CountCheckService.class));
			
        }else if("ACTION_DATE_CHANGE_NO".equals(action)){
//        	Log.d("날짜 변경", "No");
        	if(setting.getInt("NotifiType", 0)==0 || setting.getInt("NotifiType", 0)==2)
        		DateChangeByUser_Noti(mContext);
			if(setting.getInt("NotifiType", 0)==1 || setting.getInt("NotifiType", 0)==2)
				DateChangeByUser_Toast(mContext);
        	
        }else if("ACTION_DATE_CHANGE_OK".equals(action)){
//        	Log.d("날짜 변경", "ok");
        	if(setting.getInt("NotifiType", 0)==0 || setting.getInt("NotifiType", 0)==2)
        		Notifi_DATE_CHANGE(mContext);
			if(setting.getInt("NotifiType", 0)==1 || setting.getInt("NotifiType", 0)==2)
				Toast_DATE_CHANGE(mContext);
        	
        }else if("ACTION_REMOVE_BY_USER".equals(action)){
//        	Log.d("날짜 변경", "취소함");
        	setting_Editor.remove("DateChangeByUser").commit();
        	
        }else if("ACTION_FALSE_THE_STOP".equals(action)){
    		setting_Editor.putBoolean("Ten_minutes", false).commit();
    		
        }else if("ACTION_FIVE_MINUTE".equals(action)){
        	int FIVE_COUNT = setting.getInt("FIVE_MINUTE", 0) + setting.getInt("Notification", 5);
        	setting_Editor.putInt("FIVE_MINUTE", FIVE_COUNT).commit();
        	
        	/**
			 * 1.5업데이트
			 * 진동 설정 추가
			 */
        	if(setting.getBoolean("vibrate", false)){
        		Vibrator vide = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
    			vide.vibrate(100);
    			
    			long[] pattern = { 0, 200, 0 };
    			vide.vibrate(pattern, -1);
        	}
        	
        	if(setting.getInt("NotifiType", 0)==0 || setting.getInt("NotifiType", 0)==2)
        		Notifi_FiveCount(mContext, FIVE_COUNT);
			if(setting.getInt("NotifiType", 0)==1 || setting.getInt("NotifiType", 0)==2)
				Notifi_Toast(mContext, FIVE_COUNT);
        	
        }else{
        	mContext.startService(new Intent(mContext, AndroidService.class));
        	mContext.startService(new Intent(mContext, SubService.class));
        }
		System.gc();
	}
	
	protected void cleanCount(Context mContext) {
		package_count = mContext.getSharedPreferences("package_count", 0);
		package_count_Editor = package_count.edit();
		
		package_count_Editor.clear();
		package_count_Editor.commit();
		
		setting_Editor.putInt("Clear_day",  Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).commit();;
	}
	
	
	protected void Notifi_DATE_CHANGE(Context mContext){
		cleanCount(mContext);
		
		NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification noti;
		
		if(setting.getBoolean("notification_clear", false))
			noti = new Notification(R.drawable.clear_icon,
					mContext.getString(R.string.count_clean_title), System.currentTimeMillis());
		else
			noti = new Notification(R.drawable.ic_launcher,
					mContext.getString(R.string.count_clean_title), System.currentTimeMillis());
		
	    noti.flags = Notification.FLAG_AUTO_CANCEL;
	    Intent i = new Intent(mContext, MainActivity.class);
	    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    PendingIntent pendingI = PendingIntent.getActivity(mContext, 0, i, 0);
	    noti.setLatestEventInfo(mContext, mContext.getString(R.string.count_clean_title),
	    		mContext.getString(R.string.count_clean_message), pendingI);
	    nm.notify(1, noti);
	}
	
	protected void Toast_DATE_CHANGE(Context mContext){
		cleanCount(mContext);
		Toast.makeText(mContext, mContext.getString(R.string.count_clean_title), Toast.LENGTH_LONG).show();
	}
	
	
	protected void Notifi_FiveCount(Context mContext, int count){
		NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification noti;
		if(setting.getBoolean("notification_clear", false))
			noti = new Notification(R.drawable.clear_icon,
					String.format(mContext.getString(R.string.five_minute), count), System.currentTimeMillis());
		else
			noti = new Notification(R.drawable.ic_launcher,
					String.format(mContext.getString(R.string.five_minute), count), System.currentTimeMillis());
		
		noti.flags = Notification.FLAG_AUTO_CANCEL;
	    Intent i = new Intent(mContext, MainActivity.class);
	    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    PendingIntent pendingI = PendingIntent.getActivity(mContext, 0, i, 0);
	    noti.setLatestEventInfo(mContext, String.format(mContext.getString(R.string.five_minute), count),
	    		String.format(mContext.getString(R.string.five_minute), count), pendingI);
	    nm.notify(5, noti);
	}
	
	protected void Notifi_Toast(Context mContext, int count) {
		Toast.makeText(mContext, String.format(mContext.getString(R.string.five_minute), count), Toast.LENGTH_LONG).show();
	}
	
	protected void DateChangeByUser_Noti(Context mContext) {
		NotificationManager nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification noti;
		if(setting.getBoolean("notification_clear", false))
			noti = new Notification(R.drawable.clear_icon,
					mContext.getString(R.string.date_changed_by_user), System.currentTimeMillis());
		else
			noti = new Notification(R.drawable.ic_launcher,
					mContext.getString(R.string.date_changed_by_user), System.currentTimeMillis());
		
		noti.flags = Notification.FLAG_AUTO_CANCEL;
	    Intent i = new Intent(mContext, MainActivity.class);
	    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    PendingIntent pendingI = PendingIntent.getActivity(mContext, 0, i, 0);
	    noti.setLatestEventInfo(mContext, mContext.getString(R.string.date_changed_by_user),
	    		mContext.getString(R.string.date_changed_by_user), pendingI);
	    nm.notify(5, noti);
	}
	
	protected void DateChangeByUser_Toast(Context mContext) {
		Toast.makeText(mContext, mContext.getString(R.string.date_changed_by_user), Toast.LENGTH_LONG).show();
	}
}
