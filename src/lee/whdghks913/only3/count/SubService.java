package lee.whdghks913.only3.count;

import lee.whdghks913.only3.BroadCast;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;

public class SubService extends Service {
	Boolean isService=true;
	SharedPreferences setting;
	PowerManager pm;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		setting = getSharedPreferences("setting", 0);
		
		Runnable task = new Runnable(){
			@Override
			public void run(){
				while(isService){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(pm.isScreenOn())
						if(!isServiceRunningCheck()){
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if(setting.getBoolean("Service", false)){
//								Log.d("서브서비스", "서비스 실행함");
								startService(new Intent(SubService.this, AndroidService.class));
							}
						}
				}
			}
		};
		
		Thread thread = new Thread(task);
		thread.start();
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(setting.getBoolean("Service", false)){
			Intent intent = new Intent(this, BroadCast.class);
			sendBroadcast(intent);
		}else{
			isService=false;
		}
	}
	
	public boolean isServiceRunningCheck(){
    	for (RunningServiceInfo service : ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE))
    	    if ("lee.whdghks913.only3.count.AndroidService".equals(service.service.getClassName()))
    	        return true;
    	return false;
    }
	
}
