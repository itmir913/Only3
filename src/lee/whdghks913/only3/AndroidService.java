package lee.whdghks913.only3;

import java.util.Calendar;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

@SuppressLint("NewApi")
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
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		
		/**
		 * 1.5������Ʈ
		 * �Ŀ� �Ŵ����� �̿��Ͽ� ȭ���� ���������� �۵��ϵ��� ����
		 */
		final PowerManager mPm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		
		actvityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		package_count = getSharedPreferences("package_count", 0);
		package_count_Editor = package_count.edit();
		
		package_list = getSharedPreferences("package_list", 0);
		package_All_count = getSharedPreferences("package_All_count", 0);

		setting = getSharedPreferences("setting", 0);
		setting_Editor = setting.edit();
		
		// ���񽺸� ���׶��� ���·� ����� �ȵ���̵忡 ���� �������ʴ� ���·� ����ϴ�
		PendingIntent pendingIndent = PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), 0);
		Notification noti;
		if(setting.getBoolean("notification_clear", false))
			noti = new Notification(R.drawable.clear_icon, getString(R.string.app_name), System.currentTimeMillis());
		else
			noti = new Notification(R.drawable.ic_launcher, getString(R.string.app_name), System.currentTimeMillis());
		noti.setLatestEventInfo(this, getString(R.string.notification_title), getString(R.string.notification_message), pendingIndent);
		noti.flags = noti.flags|Notification.FLAG_ONGOING_EVENT;
		startForeground(1000, noti);
		
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
					
					showNotify(true);
				}else{
					showNotify(false);
				}
			}
		};
		
		Runnable task = new Runnable(){
			@Override
			public void run(){
				while(isService){
					/**
					 * 1.9 ������Ʈ
					 * �ڵ� ��ġ ���� - ȭ���� ������ �����̸� ������ ����
					 */
					if(setting.getInt("delay", 2)*1000==0)
						try { Thread.sleep(1000); } catch (InterruptedException e) {e.printStackTrace();}
					else
						try { Thread.sleep(setting.getInt("delay", 2)*1000); } catch (InterruptedException e) {e.printStackTrace();}
					
					/**
					 * 1.5������Ʈ
					 * �Ŀ� �Ŵ����� �̿��Ͽ� ȭ���� ���������� �۵��ϵ��� ����
					 */
					if(mPm.isScreenOn()){
						Top_Activity();
					}else{
						if(setting.getInt("Notification", 5)!=0)
							if( ! isRunningApp){
								am.cancel(sender);
								isRunningApp = ! isRunningApp;
								/**
								 * 1.9 ������Ʈ
								 * ȭ���� ������ last package �ʱ�ȭ
								 */
								last_packageName = "";
								setting_Editor.remove("FIVE_MINUTE").commit();
								Log.d(setting.getInt("Notification", 5)+"�� üũ", "����");
							}
					}
				}
			}
		};
		
		Thread thread = new Thread(task);
		thread.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	public void Top_Activity(){
		for(Iterator<RunningTaskInfo> iterator = actvityManager.getRunningTasks(1).iterator(); iterator.hasNext(); ){
			String pkgName = iterator.next().topActivity.getPackageName();
			
			if(pkgName.equals("lee.whdghks913.only3")){
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
				 * 1.1 ������Ʈ : 5�и��� �˸��� ���� �ڵ� �߰�
				 */
				if(setting.getInt("Notification", 5)!=0)
					if( ! isRunningApp){
						am.cancel(sender);
						isRunningApp = ! isRunningApp;
						setting_Editor.remove("FIVE_MINUTE").commit();
						Log.d(setting.getInt("Notification", 5)+"�� üũ", "����");
					}
			}else if(pkgName.equals(package_list.getString(pkgName, ""))){
				if(setting.getInt("Notification", 5)!=0)
					if(isRunningApp){
						alarm();
						Log.d(setting.getInt("Notification", 5)+"�� üũ", "����");
					}
			}
		}
		System.gc();
	}
	
	@SuppressWarnings("deprecation")
	private void showNotify(boolean isToomany) {
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
//	      nm.cancel(0);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		restartService();
		isService=false;
		System.gc();
		stopForeground(true);
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
	
	public void restartService(){
		if(setting.getBoolean("Service", false)){
			Intent intent = new Intent(this, BroadCast.class);
			sendBroadcast(intent);
		}
	}
	
	public void alarm(){
	    /**
		 * �˶� �Ŵ����� ���� �ڵ�
		 */
		Intent intent = new Intent(this, BroadCast.class);
		intent.setAction("ACTION_FIVE_MINUTE");
		
		/**
		 * �˶� ���� (���� ��� x�и��� �˸�)
		 */
        Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR);//����
        int month = calendar.get(Calendar.MONTH);//�̹���(10���̸� 9�� ���Ϲ޴´�. calendar�� 0������ 11�������� 12���ǿ��� ���)
        int day = calendar.get(Calendar.DAY_OF_MONTH);//���ó�¥
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//����ð�
        int minute = calendar.get(Calendar.MINUTE);//�����
        
        sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        // ���� 0�� 0��(24�ð���)�� �ʱ�ȭ �ǵ��� �����մϴ�
        calendar.set(year, month ,day, hour, minute+setting.getInt("Notification", 5));
        // 24 * 60 * 60 * 1000 : �Ϸ� (�и��� ������, 1000�� 1���̴�)
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), setting.getInt("Notification", 5) * 60 * 1000, sender);
        
        isRunningApp = ! isRunningApp;
	}

}
