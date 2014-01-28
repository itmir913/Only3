package lee.whdghks913.only3.fulllock;

import lee.whdghks913.only3.count.AndroidService;
import lee.whdghks913.only3.count.SubService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BroadCastFullLock extends BroadcastReceiver {
	SharedPreferences setting, full_lock;
	SharedPreferences.Editor setting_Editor;
	
	@Override
	public void onReceive(Context mContext, Intent intent) {
		String action = intent.getAction();
		
		full_lock = mContext.getSharedPreferences("full_lock", 0);
		setting = mContext.getSharedPreferences("setting", 0);
		setting_Editor = setting.edit();
		
		if("FullLockServiceRestart".equals(action)){
			mContext.startService(new Intent(mContext, FullLockService.class));
		}else if("ACTION_START_FULL_LOCK".equals(action)){
			Log.d("¿¹¾à¾Ë¶÷", "¿È");
			if(full_lock.getBoolean("Enable", false)){
				setting_Editor.putBoolean("Service", false).commit();
				mContext.stopService(new Intent(mContext, SubService.class));
				mContext.stopService(new Intent(mContext, AndroidService.class));
				
				mContext.startService(new Intent(mContext, FullLockService.class));
				
				Intent i = new Intent(mContext, FullLockActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
						Intent.FLAG_ACTIVITY_CLEAR_TOP |
						Intent.FLAG_ACTIVITY_SINGLE_TOP );
				mContext.startActivity(intent);
				return;
			}
		}
		
		System.gc();
	}
}
