package lee.whdghks913.only3.fulllock;

import java.util.Calendar;
import java.util.List;

import lee.whdghks913.only3.MainActivity;
import lee.whdghks913.only3.R;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;

@SuppressLint({ "NewApi", "CommitPrefEdits" })
public class FullLockService extends Service {
	protected long finishTime;
	public static int remainingSec;
	
	Calendar calendar;
	
	Boolean isService=true;
	
	SharedPreferences full_lock, setting, full_lock_appList;
    SharedPreferences.Editor full_lock_Editor;
	
	ActivityManager actvityManager;
	PowerManager pm;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		
		full_lock = getSharedPreferences("full_lock", 0);
        full_lock_Editor = full_lock.edit();
        full_lock_appList = getSharedPreferences("full_lock_package", 0);
        
        setting = getSharedPreferences("setting", 0);
		
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		actvityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		calendar = Calendar.getInstance();
		
        int year = full_lock.getInt("Year", 0);
        int month = full_lock.getInt("Month", 0);
        int day = full_lock.getInt("Day", 0);
        int hour = full_lock.getInt("Hour", 0);
        int minute = full_lock.getInt("Minute", 0);
        int second = calendar.get(Calendar.SECOND);
        
        // 매일 0시 0분(24시간제)에 초기화 되도록 설정합니다
        calendar.set(year, month, day, calendar.get(Calendar.HOUR_OF_DAY)+hour, calendar.get(Calendar.MINUTE)+minute, second);
        
        finishTime = calendar.getTimeInMillis();
        remainingSec = ((int)((finishTime - System.currentTimeMillis()) / 1000));
        
        if((hour==0 && minute==0) || (! full_lock.getBoolean("Enable", false)))
			stopSelf();
        
		PendingIntent pendingIndent = PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), 0);
		Notification noti;
		if(setting.getBoolean("notification_clear", false))
			noti = new Notification(R.drawable.clear_icon, getString(R.string.app_name), System.currentTimeMillis());
		else
			noti = new Notification(R.drawable.ic_launcher, getString(R.string.app_name), System.currentTimeMillis());
		noti.setLatestEventInfo(this, getString(R.string.notification_title), getString(R.string.all_lock_notifi_message), pendingIndent);
		noti.flags = noti.flags|Notification.FLAG_ONGOING_EVENT;
		startForeground(10, noti);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(! full_lock_appList.getString("com.android.phone", "").equals("")){
			full_lock_appList.edit().putString("com.android.phone", "com.android.phone").commit();
		}
		
		Runnable checkActivity = new Runnable(){
			@Override
			public void run(){
				final String[] home = getHomeLauncher();
				
				whileLabel:
				while(isService && remainingSec>=0){
					if(pm.isScreenOn()){
						
						try { Thread.sleep(500); } catch (InterruptedException e) {e.printStackTrace();}
						List<RunningTaskInfo>taskInfos = actvityManager.getRunningTasks(1); 
						ComponentName topActivity= taskInfos.get(0).topActivity;
						String pkgName = topActivity.getPackageName();
						
						if(full_lock_appList.getString(pkgName, "").equals(pkgName)){
							continue;
						}
						
						for(int i=0 ; i<home.length ; i++ ){
							if(home[i].equals(pkgName)){
								continue whileLabel;
							}
						}
						
						String className = topActivity.getClassName();
						if(!className.equals("lee.whdghks913.only3.fulllock.FullLockActivity")){
							Intent i = new Intent();
							i.setAction(Intent.ACTION_MAIN);
							i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
									Intent.FLAG_ACTIVITY_FORWARD_RESULT |
									Intent.FLAG_ACTIVITY_NEW_TASK |
									Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP |
									Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
							i.addCategory(Intent.CATEGORY_HOME);
							startActivity(i);
							
							Intent intent = new Intent(FullLockService.this, FullLockActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
									Intent.FLAG_ACTIVITY_CLEAR_TOP |
									Intent.FLAG_ACTIVITY_SINGLE_TOP );
							startActivity(intent);
						}
					}
				}
				stopSelf();
			}
		};
		
		Thread thread = new Thread(checkActivity);
		thread.start();
		
		
		Runnable remainingTime = new Runnable() {
			
			@Override
			public void run() {
				while(isService && remainingSec>=0){
					
					remainingSec = ((int)((finishTime - System.currentTimeMillis()) / 1000L));
					
					if(remainingSec==0)
						break;
					
					try { Thread.sleep(1000); } catch (InterruptedException e) {e.printStackTrace();}
				}
				full_lock_Editor.clear().commit();
				stopSelf();
			}
		};
		Thread remaining = new Thread(remainingTime);
		remaining.start();
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
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
		if(full_lock.getBoolean("Enable", false)){
			Intent intent = new Intent(this, BroadCastFullLock.class);
			intent.setAction("FullLockServiceRestart");
			sendBroadcast(intent);
		}else{
			stopForeground(true);
			isService=false;
			
			if(setting.getBoolean("vibrate", false)){
        		Vibrator vide = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    			vide.vibrate(100);
    			
    			long[] pattern = { 0, 200, 0 };
    			vide.vibrate(pattern, -1);
        	}
		}
	}
	
	private String[] getHomeLauncher(){
		String[] homes;
		PackageManager pm =  getPackageManager();
		Intent homeIntent = new Intent(Intent.ACTION_MAIN); // Action 값이 ACTION_MAIN
		homeIntent.addCategory(Intent.CATEGORY_HOME); // Category 값이 CATEGORY_HOME
		
		//위 Intent의 조건을 만족시켜 주는 ResolveInfo 리스트를 구한다.
		List<ResolveInfo> homeApps = pm.queryIntentActivities(homeIntent, PackageManager.GET_ACTIVITIES);
		homes = new String[homeApps.size()];
		for(int i=0; i<homeApps.size(); i++){
			ResolveInfo info = homeApps.get(i); //구해진 ResolveInfo 를 통해서 PackageName을 가져온다.
			homes[i] = info.activityInfo.packageName; 
		}
		return homes;
	}
}
