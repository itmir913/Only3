package lee.whdghks913.only3.tools;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;
import java.util.Random;

import lee.whdghks913.only3.service.Only3Service;

public class Tools {
    /**
     * 서비스가 실행중일경우 true를, 실행중이지 않을경우 false를 반환합니다
     *
     * @param mContext
     * @param className
     * @return
     */
    public static boolean isServiceRunningCheck(Context mContext, String className) {
        for (RunningServiceInfo service : ((ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningServices(Integer.MAX_VALUE))
            if (className.equals(service.service.getClassName()))
                return true;
        return false;
    }

    public static boolean getServiceRunning(Context mContext) {
        return Tools.isServiceRunningCheck(mContext, Only3Service.Only3ServiceName);
    }

    public static String[] getLauncherApp(Context mContext) {
        PackageManager mPackageManager = mContext.getPackageManager();
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN); // Action 값이 ACTION_MAIN 이고
        mHomeIntent.addCategory(Intent.CATEGORY_HOME); // Category 값이 CATEGORY_HOME 앱을 찾는다.

        // 위 조건을 만족시키는 ResolveInfo 리스트를 구한다.
        List<ResolveInfo> homeApps = mPackageManager.queryIntentActivities(mHomeIntent, PackageManager.GET_ACTIVITIES);
        String[] mHomeAppList = new String[homeApps.size()];
        for (int i = 0; i < homeApps.size(); i++) {
            mHomeAppList[i] = homeApps.get(i).activityInfo.packageName; // PackageName을 가져온다.
        }
        return mHomeAppList;
    }

    public static int randNumber(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static float randNumber(float min, float max) {
        return (new Random().nextFloat() * (max - min)) + min;
    }

    public static int StringToInt(String integer) {
        if(nullCheck(integer))
            return 0;
        return Integer.parseInt(integer);
    }

    public static boolean nullCheck(String isNull){
        return ("".equalsIgnoreCase(isNull) || isNull == null);
    }
}
