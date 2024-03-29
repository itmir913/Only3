package lee.whdghks913.only3.lock;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;

import lee.whdghks913.only3.tools.LockTools;
import lee.whdghks913.only3.tools.Preference;
import lee.whdghks913.only3.tools.RunningActivity;
import lee.whdghks913.only3.tools.Tools;

public class LockService extends Service {
    public static final String LockServiceName = "lee.whdghks913.only3.lock.LockService";
    public static String[] mLauncherAppList;

    private static Preference mWhiteList;

    private boolean isLockService;

    private void init() {
        if (mWhiteList == null) mWhiteList = new Preference(getApplicationContext(), LockTools.PREF_LOCK_WHITE_LIST);
        if (mLauncherAppList == null) mLauncherAppList = Tools.getLauncherApp(getApplicationContext());
        isLockService = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        onTask();

        return super.onStartCommand(intent, flags, startId);
    }

    private void onTask() {
        if (Build.VERSION.SDK_INT >= 11)
            new Only3LockTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new Only3LockTask().execute();
    }

    private class Only3LockTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            do {
                try {
                    Thread.sleep(700);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                doingAppCheck();

            } while (isLockService);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }


    public void doingAppCheck() {
        String pkgName = RunningActivity.getPackageName(getApplicationContext());

        /**
         * 일부 필수 어플은 패스
         */
//        boolean isSystemUI = "com.android.systemui".equalsIgnoreCase(pkgName);
        boolean isPhone = "com.android.phone".equalsIgnoreCase(pkgName);
        boolean isContacts = "com.android.contacts".equalsIgnoreCase(pkgName);
        boolean isSMS = "com.android.mms".equalsIgnoreCase(pkgName);
        boolean isOnly3 = "lee.whdghks913.only3".equalsIgnoreCase(pkgName);
        if (isPhone || isContacts || isSMS || isOnly3) {
            return;
        }

        /**
         * 화이트 리스트에 등록된 앱이면 패스
         */
        boolean isWhiteApp = mWhiteList.getBoolean(pkgName, false);
        if (isWhiteApp) {
            return;
        }

        /**
         * 런처 앱이면 패스
         */
        for (String launcherPackageName : mLauncherAppList) {
            if (launcherPackageName.equalsIgnoreCase(pkgName))
                return;
        }

        startLockActivity();
    }

    private void startLockActivity() {
        Intent mHome = new Intent();
        mHome.setAction(Intent.ACTION_MAIN);
        mHome.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                Intent.FLAG_ACTIVITY_FORWARD_RESULT |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        mHome.addCategory(Intent.CATEGORY_HOME);
        startActivity(mHome);

        Intent mIntent = new Intent(getApplicationContext(), LockActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(mIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isLockService = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
