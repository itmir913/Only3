package lee.whdghks913.only3.fulllock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadCastFullLock extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context mContext, Intent intent) {
		String action = intent.getAction();
		
		if("FullLockServiceRestart".equals(action)){
			mContext.startService(new Intent(mContext, FullLockService.class));
		}
		
		System.gc();
	}
}
