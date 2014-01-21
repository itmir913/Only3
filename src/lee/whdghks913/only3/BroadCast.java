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
		/**
		 * 1.7 ������Ʈ
		 * setting�� �������� �ʾƼ� ��ε�ĳ��Ʈ ���ù����� �������� �Ǵ� ���� ����
		 */
		setting = context.getSharedPreferences("setting", 0);
		setting_Editor = setting.edit();
		
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
	        /**
	         * 1.5������Ʈ
	         * ������ ������ ������ ������ �ǵ� �ڵ����� ������� �ʵ��� �ڵ� ����
	         */
	    	ComponentName adminComponent = new ComponentName(context, AdminReceiver.class);
	    	DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
	        
	        if (devicePolicyManager.isAdminActive(adminComponent)){
	        	try { Thread.sleep(10000); } catch (InterruptedException e) {}
	        	
	        	context.startService(new Intent(context, AndroidService.class));
				context.startService(new Intent(context, MainService.class));
	        }
        }else if("ACTION_DATE_CHANGE".equals(intent.getAction())){
        	DATE_CHANGE(context);
        }else if("ACTION_FALSE_THE_STOP".equals(intent.getAction())){
    		setting_Editor.putBoolean("Ten_minutes", false).commit();
        }else if("ACTION_FIVE_MINUTE".equals(intent.getAction())){
        	int FIVE_COUNT = setting.getInt("FIVE_MINUTE", 0) + setting.getInt("Notification", 5);
        	setting_Editor.putInt("FIVE_MINUTE", FIVE_COUNT).commit();
        	
        	/**
			 * 1.5������Ʈ
			 * ���� ���� �߰�
			 */
        	if(setting.getBoolean("vibrate", false)){
        		Vibrator vide = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    			vide.vibrate(100);
    			
    			long[] pattern = { 0, 200, 0 };
    			vide.vibrate(pattern, -1);
        	}
        	Five_count(context, FIVE_COUNT);
        }else{
        	context.startService(new Intent(context, AndroidService.class));
        	/**
			 * 1.5������Ʈ
			 * ���񽺸� �ϳ� �߰��Կ� ���� ���� ����� MainService�� ���۵ǵ��� �ڵ� �߰�
			 */
			context.startService(new Intent(context, MainService.class));
        }
		System.gc();
	}
	
	@SuppressWarnings("deprecation")
	public void DATE_CHANGE(Context context){
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
		
	    noti.flags = Notification.FLAG_AUTO_CANCEL;
	    Intent i = new Intent(context, MainActivity.class);
	    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    PendingIntent pendingI = PendingIntent.getActivity(context, 0, i, 0);
	    noti.setLatestEventInfo(context, context.getString(R.string.count_clean_title),
	    		context.getString(R.string.count_clean_message), pendingI);
	    nm.notify(1, noti);
	}
	
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
		
		noti.flags = Notification.FLAG_AUTO_CANCEL;
	    Intent i = new Intent(context, MainActivity.class);
	    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    PendingIntent pendingI = PendingIntent.getActivity(context, 0, i, 0);
	    noti.setLatestEventInfo(context, String.format(context.getString(R.string.five_minute), count),
	    		String.format(context.getString(R.string.five_minute), count), pendingI);
	    nm.notify(5, noti);
	}
}
