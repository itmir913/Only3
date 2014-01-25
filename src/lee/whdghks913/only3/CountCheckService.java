package lee.whdghks913.only3;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class CountCheckService extends Service {
	SharedPreferences package_count, setting;
	SharedPreferences.Editor package_count_Editor, setting_Editor;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		setting = getSharedPreferences("setting", 0);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				Intent intent = new Intent(CountCheckService.this, BroadCast.class);
				
				if(setting.getBoolean("DateChangeByUser", false)){
					setting_Editor = setting.edit();
					setting_Editor.remove("DateChangeByUser").commit();
					
					intent.setAction("ACTION_DATE_CHANGE_NO");
					sendBroadcast(intent);
				}else{
					intent.setAction("ACTION_DATE_CHANGE_OK");
					sendBroadcast(intent);
				}
				
				stopSelf();
			}
		}).start();
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
}
