package lee.whdghks913.only3;

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

public class BroadCast extends BroadcastReceiver {
	SharedPreferences package_count, setting;
	SharedPreferences.Editor package_count_Editor, setting_Editor;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
	        /**
	         * 1.5업데이트
	         * 관리자 권한이 없으면 부팅이 되도 자동으로 실행되지 않도록 코드 설정
	         */
	    	ComponentName adminComponent = new ComponentName(context, AdminReceiver.class);
	    	DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
	        
	        if (devicePolicyManager.isAdminActive(adminComponent)){
	        	try { Thread.sleep(10000); } catch (InterruptedException e) {}
	        	
//	        	Log.d("브로드캐스트", "부팅이 완료되었습니다 서비스를 시작합니다");
	        	context.startService(new Intent(context, AndroidService.class));
				context.startService(new Intent(context, MainService.class));
	        }
//			alarm(context);
			
        }else if(Intent.ACTION_DATE_CHANGED.equals(intent.getAction())){
        	DATE_CHANGE(context);
        	
        }else if("ACTION_DATE_CHANGE".equals(intent.getAction())){
//        	Log.d("브로드캐스트", "설정한 24시간 알람이 작동하였습니다");
        	DATE_CHANGE(context);
        	
        }else if("ACTION_FALSE_THE_STOP".equals(intent.getAction())){
//        	Log.d("브로드캐스트", "작동한지 10분이 경과되었습니다");
        	setting = context.getSharedPreferences("setting", 0);
    		setting_Editor = setting.edit();
    		
    		setting_Editor.putBoolean("Ten_minutes", false).commit();
    		
        }else if("ACTION_FIVE_MINUTE".equals(intent.getAction())){
        	setting = context.getSharedPreferences("setting", 0);
        	setting_Editor = setting.edit();
        	
        	int FIVE_COUNT = setting.getInt("FIVE_MINUTE", 0) + setting.getInt("Notification", 5);
        	setting_Editor.putInt("FIVE_MINUTE", FIVE_COUNT).commit();
        	
        	/**
			 * 1.5업데이트
			 * 진동 설정 추가
			 */
        	if(setting.getBoolean("vibrate", false)){
        		Vibrator vide = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    			vide.vibrate(100);
    			
    			long[] pattern = { 0, 200, 0 };
//    			//시작시간 0이므로 즉시 시작, 500: 진동이 울릴시간, 200: 쉴 시간, 400:진동이 울릴 시간, 100: 쉴시간
//    			//즉 윙(500진동), 쉼(200쉼), 윙(400진동), 쉼(100쉼)
//    			//http://mainia.tistory.com/623
    			vide.vibrate(pattern, -1);
        	}
        	
        	Five_count(context, FIVE_COUNT);
        	
//        	Log.d("브로드캐스트", "어플을 사용한지 "+FIVE_COUNT+"분이 지났습니다");
        	
        }else{
//        	Log.d("브로드캐스트", "서비스를 다시 시작합니다");
        	context.startService(new Intent(context, AndroidService.class));
        	/**
			 * 1.5업데이트
			 * 서비스를 하나 추가함에 따라 서비스 종료시 MainService도 시작되도록 코드 추가
			 */
			context.startService(new Intent(context, MainService.class));
        }
		System.gc();
	}
	
	@SuppressWarnings("deprecation")
	public void DATE_CHANGE(Context context){
//		Log.d("브로드캐스트", "하루가 지났습니다, 카운터를 초기화 합니다");
    	
    	package_count = context.getSharedPreferences("package_count", 0);
		package_count_Editor = package_count.edit();
		
		package_count_Editor.clear();
		package_count_Editor.commit();
		
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification noti;
		
		if(setting.getBoolean("notification_clear", false))
			noti = new Notification(R.drawable.clear_icon,
					context.getString(R.string.count_clean_title), System.currentTimeMillis());
		else
			noti = new Notification(R.drawable.ic_launcher,
					context.getString(R.string.count_clean_title), System.currentTimeMillis());
		
		//알림 소리를 한번만 내도록
//		noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
	    
	    //확인하면 자동으로 알림이 제거 되도록
	    noti.flags = Notification.FLAG_AUTO_CANCEL;
	    
	    //사용자가 알람을 확인하고 클릭했을때 새로운 액티비티를 시작할 인텐트 객체
	    Intent i = new Intent(context, MainActivity.class);
	    
	    //새로운 태스크(Task) 상에서 실행되도록(보통은 태스크1에 쌓이지만 태스크2를 만들어서 전혀 다른 실행으로 관리한다)
	    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    
	    //인텐트 객체를 포장해서 전달할 인텐트 전달자 객체
	    PendingIntent pendingI = PendingIntent.getActivity(context, 0, i, 0);
	    
	    //상단바를 드래그 했을때 보여질 내용 정의하기
	    noti.setLatestEventInfo(context, context.getString(R.string.count_clean_title),
	    		context.getString(R.string.count_clean_message), pendingI);
	    
	    //알림창 띄우기(알림이 여러개일수도 있으니 알림을 구별할 상수값, 여러개라면 상수값을 달리 줘야 한다.)
	    nm.notify(1, noti);
	}
	
//	public void alarm(Context context){
//	    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//	    Intent intent = new Intent(context, BroadCast.class);
//		intent.setAction("ACTION_DATE_CHANGE");
//		
//        Calendar calendar = Calendar.getInstance();
//        
//        int year = calendar.get(Calendar.YEAR);//올해
//        int month = calendar.get(Calendar.MONTH);//이번달(10월이면 9를 리턴받는다. calendar는 0월부터 11월까지로 12개의월을 사용)
//        int day = calendar.get(Calendar.DAY_OF_MONTH);//오늘날짜
////        int hour = calendar.get(Calendar.HOUR_OF_DAY);//현재시간
////        int minute = calendar.get(Calendar.MINUTE);//현재분
//        
//        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
//        calendar.set(year, month ,day+1);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender);
//	}
	
	@SuppressWarnings("deprecation")
	public void Five_count(Context context, int count){
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification noti;
		if(setting.getBoolean("notification_clear", false))
			noti = new Notification(R.drawable.clear_icon,
					String.format(context.getString(R.string.five_minute), count), System.currentTimeMillis());
		else
			noti = new Notification(R.drawable.ic_launcher,
					String.format(context.getString(R.string.five_minute), count), System.currentTimeMillis());
		
		//알림 소리를 한번만 내도록
//		noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
	    
	    //확인하면 자동으로 알림이 제거 되도록
	    noti.flags = Notification.FLAG_AUTO_CANCEL;
	    
	    //사용자가 알람을 확인하고 클릭했을때 새로운 액티비티를 시작할 인텐트 객체
	    Intent i = new Intent(context, MainActivity.class);
	    
	    //새로운 태스크(Task) 상에서 실행되도록(보통은 태스크1에 쌓이지만 태스크2를 만들어서 전혀 다른 실행으로 관리한다)
	    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    
	    //인텐트 객체를 포장해서 전달할 인텐트 전달자 객체
	    PendingIntent pendingI = PendingIntent.getActivity(context, 0, i, 0);
	    
	    //상단바를 드래그 했을때 보여질 내용 정의하기
	    noti.setLatestEventInfo(context, String.format(context.getString(R.string.five_minute), count),
	    		String.format(context.getString(R.string.five_minute), count), pendingI);
	    
	    //알림창 띄우기(알림이 여러개일수도 있으니 알림을 구별할 상수값, 여러개라면 상수값을 달리 줘야 한다.)
	    nm.notify(5, noti);
	}
}
