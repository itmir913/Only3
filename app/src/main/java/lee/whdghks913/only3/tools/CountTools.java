package lee.whdghks913.only3.tools;

import android.content.Context;

/**
 * Created by 종환 on 2015-05-17.
 */
public class CountTools {
    public static final String PREF_PACKAGE_NAME = "PackageName";
    public static final String PREF_PACKAGE_COUNT = "PackageCount";

    public static final int MinCount = 5;

    public static Preference mName, mCount;

    /**
     * 전체 카운트를 얻습니다.
     * 저장되어 있지 않을경우 -1을 반환합니다.
     *
     * @param mContext
     * @param packageName
     * @return
     */
    public static int getAllCount(Context mContext, String packageName) {
        if (mName == null)
            mName = new Preference(mContext, PREF_PACKAGE_NAME);

        int AllCount = mName.getInt(packageName, -1);

        return AllCount;
    }

    /**
     * 지금 카운트를 얻습니다.
     * 저장되어 있지 않을경우 0을 반환합니다.
     *
     * @param mContext
     * @param packageName
     * @return
     */
    public static int getCurrentCount(Context mContext, String packageName) {
        if (mCount == null)
            mCount = new Preference(mContext, PREF_PACKAGE_COUNT);

        return mCount.getInt(packageName, 0);
    }

    /**
     * 저장되어 있다면 true,
     * 저장되어있지 않다면 false을 반환합니다.
     *
     * @param mContext
     * @param packageName
     * @return
     */
    public static boolean isAddedCheck(Context mContext, String packageName) {
        return (getAllCount(mContext, packageName) >= MinCount);
    }

    /**
     * PackageName을 추가합니다.
     *
     * @param mContext
     * @param packageName
     * @param Count
     */
    public static void addPackageAllCount(Context mContext, String packageName, int Count) {
        if (mName == null)
            mName = new Preference(mContext, PREF_PACKAGE_NAME);

        mName.putInt(packageName, Count);
    }

    /**
     * PackageName을 삭제합니다.
     */
    public static void removePackage(Context mContext, String packageName) {
        if (mName == null)
            mName = new Preference(mContext, PREF_PACKAGE_NAME);
        if (mCount == null)
            mCount = new Preference(mContext, PREF_PACKAGE_COUNT);

        mName.remove(packageName);
        mCount.remove(packageName);
    }

    /**
     * 현재 카운트가 전체 카운트를 초과했으면 true, 이하라면 false를 반환합니다.
     *
     * @param mContext
     * @param packageName
     * @return
     */
    public static boolean isExceedCount(Context mContext, String packageName) {
        int AllCount = getAllCount(mContext, packageName);
        int Count = getCurrentCount(mContext, packageName);

        return (Count > AllCount);
    }

    /**
     * 현재 카운트를 하나 추가한후 저장합니다.
     *
     * @param mContext
     * @param packageName
     */
    public static void setCountUp(Context mContext, String packageName) {
        if (mCount == null)
            mCount = new Preference(mContext, PREF_PACKAGE_COUNT);

        int Count = getCurrentCount(mContext, packageName);
        mCount.putInt(packageName, ++Count);
    }

    /**
     * 현재 카운트를 초기화 합니다.
     */
    public static void resetCurrentCount(Context mContext) {
        if (mCount == null)
            mCount = new Preference(mContext, PREF_PACKAGE_COUNT);

        mCount.clear();
    }
}
