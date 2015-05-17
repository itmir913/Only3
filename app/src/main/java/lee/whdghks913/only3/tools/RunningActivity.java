package lee.whdghks913.only3.tools;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

/**
 * Created by whdghks913 on 2015-05-05.
 */
public class RunningActivity {
    private static ActivityManager mActivityManager;
    public static String mLastPackageName;

    private static ComponentName getData(Context mContext) {
        if (mActivityManager == null)
            mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        ComponentName topActivity = taskInfo.get(0).topActivity;

        return topActivity;
    }

    public static ComponentName getComponentName(Context mContext) {
        return getData(mContext);
    }

    public static String getPackageName(Context mContext) {
        return getData(mContext).getPackageName();
    }

    public static String getClassName(Context mContext) {
        return getData(mContext).getClassName();
    }
}
