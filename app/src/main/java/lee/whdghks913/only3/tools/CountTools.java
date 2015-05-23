package lee.whdghks913.only3.tools;

import android.content.Context;

import java.util.Calendar;

import lee.whdghks913.only3.R;

/**
 * Created by whdghks913 on 2015-05-17.
 */
public class CountTools {
    public static final String PREF_PACKAGE_NAME = "PackageName";
    public static final String PREF_PACKAGE_COUNT = "PackageCount";
    public static final String PREF_COUNT_DATE = "CountDate";
//    public static final String PREF_NEW_APP_COUNT = "NewAppCount";

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
     * @param inputCount
     */
    public static void addPackageAllCount(Context mContext, String packageName, int inputCount) {
        if (mName == null)
            mName = new Preference(mContext, PREF_PACKAGE_NAME);

        if (inputCount < MinCount)
            inputCount = MinCount;

        mName.putInt(packageName, inputCount);
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
     * 현재 카운트가 전체 카운트와 같거나 크면 true, 이하라면 false를 반환합니다.
     *
     * @param mContext
     * @param packageName
     * @return
     */
    public static boolean isExceedCount(Context mContext, String packageName) {
        int AllCount = getAllCount(mContext, packageName);
        int Count = getCurrentCount(mContext, packageName);

        return (Count >= AllCount);
    }

    /**
     * 전체 카운트를 수정할때 카운트를 초과할 경우 true, 현재 카운트가 수정할 카운트보다 아래면 false를 반환합니다.
     *
     * @param mContext
     * @param packageName
     * @param willEditCount
     * @return
     */
    public static boolean ifExceedCount(Context mContext, String packageName, int willEditCount) {
        int Count = getCurrentCount(mContext, packageName);

        return (Count > willEditCount);
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

    public static void putCurrentDate(Context mContext) {
        Preference mDate = new Preference(mContext, PREF_COUNT_DATE);

        Calendar mCalendar = Calendar.getInstance();

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        mDate.putInt("YEAR", year);
        mDate.putInt("MONTH", month);
        mDate.putInt("DAY", day);
    }

    public static Calendar getSavedDateByCalendar(Context mContext) {
        Preference mDate = new Preference(mContext, PREF_COUNT_DATE);

        Calendar mCalendar = Calendar.getInstance();

        int year = mDate.getInt("YEAR", mCalendar.get(Calendar.YEAR));
        int month = mDate.getInt("MONTH", mCalendar.get(Calendar.MONTH));
        int day = mDate.getInt("DAY", mCalendar.get(Calendar.DAY_OF_MONTH));

        mCalendar.set(year, month, day);

        return mCalendar;
    }

    public static void isCountClear(Context mContext, boolean isNotify) {
        Preference mDate = new Preference(mContext, PREF_COUNT_DATE);

        Calendar mCalendar = Calendar.getInstance();

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        int pref_year = mDate.getInt("YEAR", 0);
        int pref_month = mDate.getInt("MONTH", 0);
        int pref_day = mDate.getInt("DAY", 0);

        if ((year == pref_year) && (month == pref_month) && (day == pref_day)) {
            // 오늘 날짜와 카운트가 초기화된 날짜가 같음

            return;

        } else {
            // 오늘 날짜와 카운트가 초기화된 날짜가 다르므로 초기화가 필요함.

            resetCurrentCount(mContext); // 카운트 초기화
            putCurrentDate(mContext); // 카운트 초기화 날짜를 기록한다.

            if (isNotify) {
                NotificationTools mNotify = new NotificationTools(mContext);
                mNotify.setTicker(mContext.getString(R.string.zero_count_title))
                        .setContentTitle(mContext.getString(R.string.zero_count_title))
                        .setContentText(mContext.getString(R.string.zero_count_msg))
                        .setOnGoing(false)
                        .setDefaults(0)
                        .notify(7777);
            }
        }
    }

    /**
     * 새로 설치되는 앱의 카운트 관리
     */
//    public static void setNewAppCount(Context mContext, int inputCount) {
//        if (mCount == null)
//            mCount = new Preference(mContext, PREF_PACKAGE_COUNT);
//
//        if (inputCount < MinCount)
//            inputCount = MinCount;
//
//        mCount.putInt(PREF_NEW_APP_COUNT, inputCount);
//    }
//
//    public static void removeNewAppCount(Context mContext) {
//        if (mCount == null)
//            mCount = new Preference(mContext, PREF_PACKAGE_COUNT);
//
//        mCount.remove(PREF_NEW_APP_COUNT);
//    }
//
//    public static int getNewAppCount(Context mContext) {
//        if (mCount == null)
//            mCount = new Preference(mContext, PREF_PACKAGE_COUNT);
//
//        return mCount.getInt(PREF_NEW_APP_COUNT, -1);
//    }
}
