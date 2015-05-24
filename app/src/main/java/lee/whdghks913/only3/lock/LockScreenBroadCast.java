package lee.whdghks913.only3.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import lee.whdghks913.only3.tools.ServiceTools;

public class LockScreenBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context mContext, Intent mIntent) {
        String mAction = mIntent.getAction();

        if (Intent.ACTION_SCREEN_ON.equalsIgnoreCase(mAction)) {
            // 화면이 켜졌을때

        } else if (Intent.ACTION_USER_PRESENT.equalsIgnoreCase(mAction)) {
            // 잠금이 해제되었을때

            ServiceTools.startLockService(mContext);

        } else if (Intent.ACTION_SCREEN_OFF.equalsIgnoreCase(mAction)) {
            // 화면이 꺼졌을때

            ServiceTools.stopLockService(mContext);
        }
    }
}
