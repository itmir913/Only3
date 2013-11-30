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
	         * 1.5������Ʈ
	         * ������ ������ ������ ������ �ǵ� �ڵ����� ������� �ʵ��� �ڵ� ����
	         */
	    	ComponentName adminComponent = new ComponentName(context, AdminReceiver.class);
	    	DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
	        
	        if (devicePolicyManager.isAdminActive(adminComponent)){
	        	try { Thread.sleep(10000); } catch (InterruptedException e) {}
	        	
//	        	Log.d("��ε�ĳ��Ʈ", "������ �Ϸ�Ǿ����ϴ� ���񽺸� �����մϴ�");
	        	context.startService(new Intent(context, AndroidService.class));
				context.startService(new Intent(context, MainService.class));
	        }
//			alarm(context);
			
        }else if(Intent.ACTION_DATE_CHANGED.equals(intent.getAction())){
        	DATE_CHANGE(context);
        	
        }else if("ACTION_DATE_CHANGE".equals(intent.getAction())){
//        	Log.d("��ε�ĳ��Ʈ", "������ 24�ð� �˶��� �۵��Ͽ����ϴ�");
        	DATE_CHANGE(context);
        	
        }else if("ACTION_FALSE_THE_STOP".equals(intent.getAction())){
//        	Log.d("��ε�ĳ��Ʈ", "�۵����� 10���� ����Ǿ����ϴ�");
        	setting = context.getSharedPreferences("setting", 0);
    		setting_Editor = setting.edit();
    		
    		setting_Editor.putBoolean("Ten_minutes", false).commit();
    		
        }else if("ACTION_FIVE_MINUTE".equals(intent.getAction())){
        	setting = context.getSharedPreferences("setting", 0);
        	setting_Editor = setting.edit();
        	
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
//    			//���۽ð� 0�̹Ƿ� ��� ����, 500: ������ �︱�ð�, 200: �� �ð�, 400:������ �︱ �ð�, 100: ���ð�
//    			//�� ��(500����), ��(200��), ��(400����), ��(100��)
//    			//http://mainia.tistory.com/623
    			vide.vibrate(pattern, -1);
        	}
        	
        	Five_count(context, FIVE_COUNT);
        	
//        	Log.d("��ε�ĳ��Ʈ", "������ ������� "+FIVE_COUNT+"���� �������ϴ�");
        	
        }else{
//        	Log.d("��ε�ĳ��Ʈ", "���񽺸� �ٽ� �����մϴ�");
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
//		Log.d("��ε�ĳ��Ʈ", "�Ϸ簡 �������ϴ�, ī���͸� �ʱ�ȭ �մϴ�");
    	
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
		
		//�˸� �Ҹ��� �ѹ��� ������
//		noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
	    
	    //Ȯ���ϸ� �ڵ����� �˸��� ���� �ǵ���
	    noti.flags = Notification.FLAG_AUTO_CANCEL;
	    
	    //����ڰ� �˶��� Ȯ���ϰ� Ŭ�������� ���ο� ��Ƽ��Ƽ�� ������ ����Ʈ ��ü
	    Intent i = new Intent(context, MainActivity.class);
	    
	    //���ο� �½�ũ(Task) �󿡼� ����ǵ���(������ �½�ũ1�� �������� �½�ũ2�� ���� ���� �ٸ� �������� �����Ѵ�)
	    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    
	    //����Ʈ ��ü�� �����ؼ� ������ ����Ʈ ������ ��ü
	    PendingIntent pendingI = PendingIntent.getActivity(context, 0, i, 0);
	    
	    //��ܹٸ� �巡�� ������ ������ ���� �����ϱ�
	    noti.setLatestEventInfo(context, context.getString(R.string.count_clean_title),
	    		context.getString(R.string.count_clean_message), pendingI);
	    
	    //�˸�â ����(�˸��� �������ϼ��� ������ �˸��� ������ �����, ��������� ������� �޸� ��� �Ѵ�.)
	    nm.notify(1, noti);
	}
	
//	public void alarm(Context context){
//	    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//	    Intent intent = new Intent(context, BroadCast.class);
//		intent.setAction("ACTION_DATE_CHANGE");
//		
//        Calendar calendar = Calendar.getInstance();
//        
//        int year = calendar.get(Calendar.YEAR);//����
//        int month = calendar.get(Calendar.MONTH);//�̹���(10���̸� 9�� ���Ϲ޴´�. calendar�� 0������ 11�������� 12���ǿ��� ���)
//        int day = calendar.get(Calendar.DAY_OF_MONTH);//���ó�¥
////        int hour = calendar.get(Calendar.HOUR_OF_DAY);//����ð�
////        int minute = calendar.get(Calendar.MINUTE);//�����
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
		
		//�˸� �Ҹ��� �ѹ��� ������
//		noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
	    
	    //Ȯ���ϸ� �ڵ����� �˸��� ���� �ǵ���
	    noti.flags = Notification.FLAG_AUTO_CANCEL;
	    
	    //����ڰ� �˶��� Ȯ���ϰ� Ŭ�������� ���ο� ��Ƽ��Ƽ�� ������ ����Ʈ ��ü
	    Intent i = new Intent(context, MainActivity.class);
	    
	    //���ο� �½�ũ(Task) �󿡼� ����ǵ���(������ �½�ũ1�� �������� �½�ũ2�� ���� ���� �ٸ� �������� �����Ѵ�)
	    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    
	    //����Ʈ ��ü�� �����ؼ� ������ ����Ʈ ������ ��ü
	    PendingIntent pendingI = PendingIntent.getActivity(context, 0, i, 0);
	    
	    //��ܹٸ� �巡�� ������ ������ ���� �����ϱ�
	    noti.setLatestEventInfo(context, String.format(context.getString(R.string.five_minute), count),
	    		String.format(context.getString(R.string.five_minute), count), pendingI);
	    
	    //�˸�â ����(�˸��� �������ϼ��� ������ �˸��� ������ �����, ��������� ������� �޸� ��� �Ѵ�.)
	    nm.notify(5, noti);
	}
}
