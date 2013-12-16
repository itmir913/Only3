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
	
	// 설정값 확인을 위해 SharedPreferences을 사용한다
	SharedPreferences package_All_count, package_count, package_list, setting;
	SharedPreferences.Editor package_count_Editor, setting_Editor;
	
	int All_Count=0, Count=0;
	
	/**
	 * 1.5업데이트
	 * 일부 객체를 미리 생성하게 하지 않도록 하여 메모리 절약
	 */
	// 일정 시간마다 작동을 위해 이 어플에서는 쓰래드를 이용한다 
//	Thread thread;
	Handler handler;
//	String pkgName, last_packageName="";
	String last_packageName="";
	ActivityManager actvityManager;
//	RunningTaskInfo runningTaskInfo;
	
	// 쓰래드의 무한반복문을 탈출하기 위한 Boolean값 입니다
	Boolean isService=true, isRunningApp=true;
	
	/**
	 * 1.1 업데이트
	 * 알람 매니저를 이용하여 사용한지 x분마다 알림
	 */
	/**
	 * 1.5업데이트
	 * 일부 객체를 미리 생성하게 하지 않도록 하여 메모리 절약
	 */
	AlarmManager am;
//	Intent intent;
	PendingIntent sender;
	
	@SuppressLint("CommitPrefEdits")
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		
		/**
		 * 1.5업데이트
		 * 파워 매니저를 이용하여 화면이 켜졌을때만 작동하도록 설정
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
//				int num=0;
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
					if(mPm.isScreenOn()){
//						int Sleep = setting.getInt("delay", 2)*1000;
//						if(setting.getInt("delay", 2)*1000==0)
//							try { Thread.sleep(1000); } catch (InterruptedException e) {e.printStackTrace();}
//						else
//							try { Thread.sleep(setting.getInt("delay", 2)*1000); } catch (InterruptedException e) {e.printStackTrace();}
						Top_Activity();
					}else{
//						if(num<2){
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
									Log.d(setting.getInt("Notification", 5)+"분 체크", "종료");
								}
//							num++;
//							System.gc();
//						}
					}
				}
			}
		};
		
		Thread thread = new Thread(task);
		thread.start();
		
		
//		// 쓰래드를 시작합니다
//		thread = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				while(isService){
//					try {
//						thread.sleep(2000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					Top_Activity();
//				}
//			}});
//		thread.start();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	public void Top_Activity(){
//		List<RunningTaskInfo> taskInfos = actvityManager.getRunningTasks(1);
//		for(Iterator<RunningTaskInfo> iterator = taskInfos.iterator(); iterator.hasNext(); ){
		for(Iterator<RunningTaskInfo> iterator = actvityManager.getRunningTasks(1).iterator(); iterator.hasNext(); ){
			/**
			 * 1.5업데이트
			 * 메모리 절약을 위해 주석처리와 코드 간소화
			 */
//			RunningTaskInfo runningTaskInfo = (RunningTaskInfo) iterator.next();
//			String pkgName = runningTaskInfo.topActivity.getPackageName();
//		    String className = runningTaskInfo.topActivity.getClassName();
			String pkgName = iterator.next().topActivity.getPackageName();
			
//			Log.d("최상단 액티비티", pkgName);
//			Log.d("last_packageName", last_packageName);
//			Log.d("카운트 속도", ""+setting.getInt("delay", 2)*1000);
			
			if(pkgName.equals("lee.whdghks913.only3")){
				/**
				 * 1.5업데이트
				 * 불필요한 객체를 빨리 지워버릴수 있도록 코드 설정
				 */
				System.gc();
				return;
			}
			
			/**
			 * 1.5업데이트
			 * 설정하지 않은 어플에서 불필요한 연산을 하지 않도록 설정하여 메모리 절약
			 */
			if(!pkgName.equals(package_list.getString(pkgName, "")) && last_packageName.equals(pkgName)){
//				Log.d("메모리 낭비 방지 기능 작동", "메모리 낭비를 막기위해 리턴합니다");
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
//				showNotify(pkgName);
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
						Log.d(setting.getInt("Notification", 5)+"분 체크", "종료");
					}
			}else if(pkgName.equals(package_list.getString(pkgName, ""))){
				if(setting.getInt("Notification", 5)!=0)
					if(isRunningApp){
						alarm();
						Log.d(setting.getInt("Notification", 5)+"분 체크", "시작");
					}
			}
		}
		System.gc();
	}
	
	@SuppressWarnings("deprecation")
	private void showNotify(boolean isToomany) {
//		package_count_Editor.putInt(pkgName, Count).commit();
		
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
		
	      //알림 소리를 한번만 내도록
	      noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
	      
	      //확인하면 자동으로 알림이 제거 되도록
	      noti.flags = Notification.FLAG_AUTO_CANCEL;
	      
	      //사용자가 알람을 확인하고 클릭했을때 새로운 액티비티를 시작할 인텐트 객체
	      Intent intent = new Intent(AndroidService.this, MainActivity.class);
	      
	      //새로운 태스크(Task) 상에서 실행되도록(보통은 태스크1에 쌓이지만 태스크2를 만들어서 전혀 다른 실행으로 관리한다)
	      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	      
	      //인텐트 객체를 포장해서 전달할 인텐트 전달자 객체
	      PendingIntent pendingI = PendingIntent.getActivity(AndroidService.this, 0, intent, 0);
	      
	      //상단바를 드래그 했을때 보여질 내용 정의하기
//	      noti.setLatestEventInfo(AndroidService.this, "제목", "내용", pendingI);
	      noti.setLatestEventInfo(AndroidService.this, getString(R.string.count_added),
	    		  getString(R.string.count_added), pendingI);
	      
	      //알림창 띄우기(알림이 여러개일수도 있으니 알림을 구별할 상수값, 여러개라면 상수값을 달리 줘야 한다.)
	      nm.notify(0, noti);
	      nm.cancel(0);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		restartService();
		
		// 서비스가 삭제되면 무한 반복을 해제합니다
		isService=false;
		
		System.gc();
		
		stopForeground(true);
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		restartService();
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		// TODO Auto-generated method stub
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
