package lee.whdghks913.only3.fulllock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BroadCastFullLock extends BroadcastReceiver {
	SharedPreferences setting, full_lock;
	
	@Override
	public void onReceive(Context mContext, Intent intent) {
		String action = intent.getAction();
		
		full_lock = mContext.getSharedPreferences("full_lock", 0);
		setting = mContext.getSharedPreferences("setting", 0);
		
		if("FullLockServiceRestart".equals(action)){
			mContext.startService(new Intent(mContext, FullLockService.class));
		}
		
		System.gc();
	}
}
