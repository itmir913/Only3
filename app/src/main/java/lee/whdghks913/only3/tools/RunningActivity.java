package lee.whdghks913.only3.tools;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * Created by whdghks913 on 2015-05-05.
 */
public class RunningActivity {
    private static ActivityManager mActivityManager;
    public static String mLastPackageName;

    @Deprecated
    private static ComponentName getData(Context mContext) {
        if (mActivityManager == null)
            mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
        ComponentName topActivity = taskInfo.get(0).topActivity;

        return topActivity;
    }

    @Deprecated
    public static ComponentName getComponentName(Context mContext) {
        return getData(mContext);
    }

    public static String getPackageName(Context mContext) {
        // http://stackoverflow.com/questions/28066231/how-to-gettopactivity-name-or-get-current-running-application-package-name-in-lo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mActivityManager.getRunningAppProcesses().get(0).processName;
        }

        return getData(mContext).getPackageName();
    }

    @Deprecated
    public static String getClassName(Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mActivityManager.getRunningAppProcesses().get(0).processName;
        }

        return getData(mContext).getClassName();
    }
}
