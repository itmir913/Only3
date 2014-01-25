package lee.whdghks913.only3;

import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.widget.Toast;

@SuppressLint({ "NewApi", "CommitPrefEdits" })
public class AndroidService extends Service {
	SharedPreferences package_All_count, package_count, package_list, setting;
	SharedPreferences.Editor package_count_Editor, setting_Editor;
	
	Handler handler;
	String last_packageName="";
	ActivityManager actvityManager;
	
	int All_Count=0, Count=0;
	Boolean isService=true, isRunningApp=true;
	
	AlarmManager am;
	PendingIntent sender;
	
	PowerManager pm;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		
		/**
		 * 1.5업데이트
		 * 파워 매니저를 이용하여 화면이 켜졌을때만 작동하도록 설정
		 */
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		
		actvityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		package_count = getSharedPreferences("package_count", 0);
		package_count_Editor = package_count.edit();
		
		package_list = getSharedPreferences("package_list", 0);
		package_All_count = getSharedPreferences("package_All_count", 0);

		setting = getSharedPreferences("setting", 0);
		setting_Editor = setting.edit();
		
		// 서비스를 포그라운드 상태로 만들어 안드로이드에 의해 꺼지지않는 상태로 만듭니다
		PendingIntent pendingIndent = PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), 0);
		Notification noti;
		if(setting.getBoolean("notification_clear", false))
			noti = new Notification(R.drawable.clear_icon, getString(R.string.app_name), System.currentTimeMillis());
		else
			noti = new Notification(R.drawable.ic_launcher, getString(R.string.app_name), System.currentTimeMillis());
		noti.setLatestEventInfo(this, getString(R.string.notification_title), getString(R.string.notification_message), pendingIndent);
		noti.flags = noti.flags|Notification.FLAG_ONGOING_EVENT;
		startForeground(1000, noti);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(All_Count<Count){
					Intent i = new Intent();
					i.setAction(Intent.ACTION_MAIN);
					i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
							Intent.FLAG_ACTIVITY_FORWARD_RESULT |
							Intent.FLAG_ACTIVITY_NEW_TASK |
							Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP |
							Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
					i.addCategory(Intent.CATEGORY_HOME);
					startActivity(i);
					
					Intent intent = new Intent(AndroidService.this, Abort.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
							Intent.FLAG_ACTIVITY_CLEAR_TOP |
							Intent.FLAG_ACTIVITY_SINGLE_TOP );
					startActivity(intent);
					//getString(R.string.count_much_added), Count, All_Count
					if(setting.getInt("NotifiType", 0)==0 || setting.getInt("NotifiType", 0)==2)
						showNotify(true);
					if(setting.getInt("NotifiType", 0)==1 || setting.getInt("NotifiType", 0)==2)
						showToast(true);
				}else{
					if(setting.getInt("NotifiType", 0)==0 || setting.getInt("NotifiType", 0)==2)
						showNotify(false);
					if(setting.getInt("NotifiType", 0)==1 || setting.getInt("NotifiType", 0)==2)
						showToast(false);
				}
			}
		};
		
		Runnable task = new Runnable(){
			@Override
			public void run(){
				while(isService){
					/**
					 * 1.9 업데이트
					 * 코드 위치 변경 - 화면이 꺼져도 딜레이를 갖도록 설정
					 */
					if(setting.getInt("delay", 2)*1000==0)
						try { Thread.sleep(1000); } catch (InterruptedException e) {e.printStackTrace();}
					else
						try { Thread.sleep(setting.getInt("delay", 2)*1000); } catch (InterruptedException e) {e.printStackTrace();}
					
					/**
					 * 1.5업데이트
					 * 파워 매니저를 이용하여 화면이 켜졌을때만 작동하도록 설정
					 */
					if(pm.isScreenOn()){
						Top_Activity();
					}else{
						if(setting.getInt("Notification", 5)!=0)
							if( ! isRunningApp){
								am.cancel(sender);
								isRunningApp = ! isRunningApp;
								/**
								 * 1.9 업데이트
								 * 화면이 꺼지면 last package 초기화
								 */
								last_packageName = "";
								setting_Editor.remove("FIVE_MINUTE").commit();
//								Log.d(setting.getInt("Notification", 5)+"분 체크", "종료");
							}
					}
				}
			}
		};
		
		Thread thread = new Thread(task);
		thread.start();
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	protected void Top_Activity(){
//		for(Iterator<RunningTaskInfo> iterator = actvityManager.getRunningTasks(1).iterator(); iterator.hasNext(); ){
//		String pkgName = iterator.next().topActivity.getPackageName();
//	}
		
		List<RunningTaskInfo>taskInfos = actvityManager.getRunningTasks(1); 
		ComponentName topActivity= taskInfos.get(0).topActivity;
//		String className = topActivity.getClassName();
		String pkgName = topActivity.getPackageName();
		
		/**
		 * 2.7 Update : Do not check the SystemUI
		 */
		if(pkgName.equals("lee.whdghks913.only3") || pkgName.equals("com.android.systemui")){
			System.gc();
			return;
		}
		
		if(!pkgName.equals(package_list.getString(pkgName, "")) && last_packageName.equals(pkgName)){
			System.gc();
			return;
		}
		
		All_Count = package_All_count.getInt(pkgName, 0);
		Count = package_count.getInt(pkgName, 0);
		
		if (pkgName.equals(package_list.getString(pkgName, "")) && Count > All_Count){
			handler.sendEmptyMessage(0);
			return;
		}else if( pkgName.equals(package_list.getString(pkgName, "")) && !last_packageName.equals(pkgName) ) {
			++Count;
			package_count_Editor.putInt(pkgName, Count).commit();
			handler.sendEmptyMessage(0);
		}
		
		if( ! last_packageName.equals(pkgName) ){
			last_packageName = pkgName;
			/**
			 * 1.1 업데이트 : 5분마다 알림을 띄우는 코드 추가
			 */
			if(setting.getInt("Notification", 5)!=0)
				if( ! isRunningApp){
					am.cancel(sender);
					isRunningApp = ! isRunningApp;
					setting_Editor.remove("FIVE_MINUTE").commit();
//					Log.d(setting.getInt("Notification", 5)+"분 체크", "종료");
				}
		}else if(pkgName.equals(package_list.getString(pkgName, ""))){
			if(setting.getInt("Notification", 5)!=0)
				if(isRunningApp){
					alarm();
//					Log.d(setting.getInt("Notification", 5)+"분 체크", "시작");
				}
		}
		System.gc();
	}
	
	@SuppressWarnings("deprecation")
	protected void showNotify(boolean isToomany) {
		NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		Notification noti;
		
		if(isToomany)
			if(setting.getBoolean("notification_clear", false))
				noti = new Notification(R.drawable.clear_icon,
						String.format( getString(R.string.count_much_added), Count, All_Count ), System.currentTimeMillis());
			else
				noti = new Notification(R.drawable.ic_launcher,
						String.format( getString(R.string.count_much_added), Count, All_Count ), System.currentTimeMillis());
		else
			if(setting.getBoolean("notification_clear", false))
				noti = new Notification(R.drawable.clear_icon,
						String.format( getString(R.string.count_added), Count, All_Count ), System.currentTimeMillis());
			else
				noti = new Notification(R.drawable.ic_launcher,
					String.format( getString(R.string.count_added), Count, All_Count ), System.currentTimeMillis());
		
	    noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
	    noti.flags = Notification.FLAG_AUTO_CANCEL;
	    Intent intent = new Intent(AndroidService.this, MainActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    PendingIntent pendingI = PendingIntent.getActivity(AndroidService.this, 0, intent, 0);
	    noti.setLatestEventInfo(AndroidService.this, getString(R.string.count_added),
	    		getString(R.string.count_added), pendingI);
	    nm.notify(0, noti);
	    nm.cancel(0);
	}
	
	protected void showToast(boolean isToomany) {
		String toastText;
		if(isToomany)
			toastText = String.format( getString(R.string.count_much_added), Count, All_Count );
		else
			toastText = String.format( getString(R.string.count_added), Count, All_Count );
		
		Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		restartService();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		restartService();
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		restartService();
	}
	
	protected void restartService(){
		if(setting.getBoolean("Service", false)){
			Intent intent = new Intent(this, BroadCast.class);
			sendBroadcast(intent);
		}else{
			stopForeground(true);
			isService=false;
		}
	}
	
	protected void alarm(){
	    /**
		 * 알람 매니저를 위한 코드
		 */
		Intent intent = new Intent(this, BroadCast.class);
		intent.setAction("ACTION_FIVE_MINUTE");
		
		/**
		 * 알람 지정 (어플 사용 x분마다 알림)
		 */
        Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR);//올해
        int month = calendar.get(Calendar.MONTH);//이번달(10월이면 9를 리턴받는다. calendar는 0월부터 11월까지로 12개의월을 사용)
        int day = calendar.get(Calendar.DAY_OF_MONTH);//오늘날짜
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//현재시간
        int minute = calendar.get(Calendar.MINUTE);//현재분
        
        sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        // 매일 0시 0분(24시간제)에 초기화 되도록 설정합니다
        calendar.set(year, month ,day, hour, minute+setting.getInt("Notification", 5));
        // 24 * 60 * 60 * 1000 : 하루 (밀리초 단위로, 1000이 1초이다)
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), setting.getInt("Notification", 5) * 60 * 1000, sender);
        
        isRunningApp = ! isRunningApp;
	}
}
