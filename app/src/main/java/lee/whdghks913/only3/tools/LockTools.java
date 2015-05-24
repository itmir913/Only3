package lee.whdghks913.only3.tools;

import android.content.Context;

/**
 * Created by 종환 on 2015-05-24.
 */
public class LockTools {
    public static final String PREF_LOCK_WHITE_LIST = "LockWhiteList";
    public static final String PREF_LOCK_FINISH_TIME = "FinishTime";
    public static final String PREF_LOCK_STARTED_TIME = "Started";

    public static final String TimeFormat = "yyyy년 MM월 dd일 hh시 mm분";

    private static Preference mWhiteList;

    private static void init(Context mContext) {
        if (mWhiteList == null)
            mWhiteList = new Preference(mContext, PREF_LOCK_WHITE_LIST);
    }

    /**
     * WhiteList에 packageName을 추가합니다.
     *
     * @param mContext
     * @param packageName
     */
    public static void addWhiteList(Context mContext, String packageName) {
        init(mContext);

        mWhiteList.putBoolean(packageName, true);
    }

    /**
     * 등록되어 있으면 true, 등록되어 있지 않으면 false를 반환합니다.
     *
     * @param mContext
     * @param packageName
     * @return
     */
    public static boolean isPackageWhiteList(Context mContext, String packageName) {
        init(mContext);

        return mWhiteList.getBoolean(packageName, false);
    }

    /**
     * package를 WhiteList에서 제거합니다.
     *
     * @param mContext
     * @param packageName
     */
    public static void removeWhiteList(Context mContext, String packageName) {
        init(mContext);

        mWhiteList.remove(packageName);
    }

    /**
     * FinishTime
     *
     * @param mContext
     * @param finishTime
     */
    public static void putFinishTime(Context mContext, long finishTime) {
        init(mContext);

        mWhiteList.putLong(LockTools.PREF_LOCK_FINISH_TIME, finishTime);
    }

    public static long getFinishTime(Context mContext) {
        init(mContext);

        return mWhiteList.getLong(LockTools.PREF_LOCK_FINISH_TIME, -1L);
    }

    public static void removeFinishTime(Context mContext) {
        init(mContext);

        mWhiteList.remove(LockTools.PREF_LOCK_FINISH_TIME);
    }


    public static void putLockStarted(Context mContext, boolean started) {
        init(mContext);

        mWhiteList.putBoolean(LockTools.PREF_LOCK_STARTED_TIME, started);
    }

    public static boolean getLockStarted(Context mContext) {
        init(mContext);

        return mWhiteList.getBoolean(LockTools.PREF_LOCK_STARTED_TIME, false);
    }

    public static void removeLockStarted(Context mContext) {
        init(mContext);

        mWhiteList.remove(LockTools.PREF_LOCK_STARTED_TIME);
    }
}
