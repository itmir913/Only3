package lee.whdghks913.only3.tools;

import android.content.Context;
import android.content.Intent;

import lee.whdghks913.only3.lock.LockService;
import lee.whdghks913.only3.lock.LockSubService;
import lee.whdghks913.only3.service.Only3Service;
import lee.whdghks913.only3.service.Only3SubService;

/**
 * Created by whdghks913 on 2015-05-23.
 */
public class ServiceTools {

    public static void startService(Context mContext) {
        // 실행중이 아니면
        startSubService(mContext);
        startMainService(mContext);
    }

    public static void stopService(Context mContext) {
        // 실행중이면
        stopSubService(mContext);
        stopMainService(mContext);
    }

    public static void startMainService(Context mContext) {
        if (!Tools.getOnly3ServiceRunning(mContext)) {
            mContext.startService(new Intent(mContext, Only3Service.class));
        }
    }

    public static void stopMainService(Context mContext) {
        if (Tools.getOnly3ServiceRunning(mContext)) {
            mContext.stopService(new Intent(mContext, Only3Service.class));
        }
    }

    public static void startSubService(Context mContext) {
        if (!Tools.getOnly3SubServiceRunning(mContext)) {
            mContext.startService(new Intent(mContext, Only3SubService.class));
        }
    }

    public static void stopSubService(Context mContext) {
        if (Tools.getOnly3SubServiceRunning(mContext)) {
            mContext.stopService(new Intent(mContext, Only3SubService.class));
        }
    }

    public static boolean isServiceRunning(Context mContext) {
        return (Tools.getOnly3ServiceRunning(mContext) && Tools.getOnly3SubServiceRunning(mContext));
    }

    public static void startLockService(Context mContext) {
        if (!Tools.isServiceRunningCheck(mContext, LockService.LockServiceName)) {
            mContext.startService(new Intent(mContext, LockService.class));
        }
    }

    public static void stopLockService(Context mContext) {
        if (Tools.isServiceRunningCheck(mContext, LockService.LockServiceName)) {
            mContext.stopService(new Intent(mContext, LockService.class));
        }
    }

    public static void startLockSubService(Context mContext) {
        if (!Tools.isServiceRunningCheck(mContext, LockSubService.LockSubServiceName)) {
            mContext.startService(new Intent(mContext, LockSubService.class));
            LockTools.putLockStarted(mContext, true);
        }
    }

    public static void stopLockSubService(Context mContext) {
        if (Tools.isServiceRunningCheck(mContext, LockSubService.LockSubServiceName)) {
            mContext.stopService(new Intent(mContext, LockSubService.class));
            LockTools.removeLockStarted(mContext);
        }
    }

}
