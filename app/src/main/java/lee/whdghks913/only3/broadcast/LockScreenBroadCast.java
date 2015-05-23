package lee.whdghks913.only3.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import lee.whdghks913.only3.tools.AlarmTools;
import lee.whdghks913.only3.tools.Only3;
import lee.whdghks913.only3.tools.Preference;
import lee.whdghks913.only3.tools.RunningActivity;
import lee.whdghks913.only3.tools.ServiceTools;

public class LockScreenBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context mContext, Intent mIntent) {
        String mAction = mIntent.getAction();

        if (Intent.ACTION_SCREEN_ON.equalsIgnoreCase(mAction)) {
            // 화면이 켜졌을때

        } else if (Intent.ACTION_USER_PRESENT.equalsIgnoreCase(mAction)) {
            // 잠금이 해제되었을때

            ServiceTools.startMainService(mContext);

        } else if (Intent.ACTION_SCREEN_OFF.equalsIgnoreCase(mAction)) {
            // 화면이 꺼졌을때

            ServiceTools.stopMainService(mContext);

            /**
             * MainService의 onDestory()가 정상 호출되지 않았을 경우를 대비해서 초기화 코드를 넣어둠
             */
            if (RunningActivity.mLastPackageName != null)
                RunningActivity.mLastPackageName = null;

            if (Only3.isNotifyAppAlarm) {
                AlarmTools.cancelStartNotification(mContext);
                new Preference(mContext).remove("ACTION_NOTIFY_MINUTE_REPEAT");
                Only3.isNotifyAppAlarm = false;
            }
        }
    }
}
