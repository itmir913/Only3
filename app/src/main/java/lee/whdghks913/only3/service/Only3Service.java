package lee.whdghks913.only3.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.lang.ref.WeakReference;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.tools.AlarmTools;
import lee.whdghks913.only3.tools.CountTools;
import lee.whdghks913.only3.tools.NotificationTools;
import lee.whdghks913.only3.tools.PowerTools;
import lee.whdghks913.only3.tools.Preference;
import lee.whdghks913.only3.tools.RunningActivity;
import lee.whdghks913.only3.tools.ToastTools;
import lee.whdghks913.only3.tools.Tools;

public class Only3Service extends Service {
    public static final String Only3ServiceName = "lee.whdghks913.only3.service.Only3Service";

    private NotificationTools mNotify;
    private PowerTools mPower;
    private Preference mPref;

    private int mDelay;
    private int NotificationType;
    private boolean isServiceRun, isNotifyAppAlarm;

    private Handler mHandler;

    private void init() {
        mNotify = new NotificationTools(getApplicationContext());
        mPref = new Preference(getApplicationContext());
        mPower = new PowerTools(getApplicationContext());
        isServiceRun = true;
        isNotifyAppAlarm = false;
        NotificationType = Tools.StringToInt(mPref.getString("notificationType", "1"));

        mHandler = new mHandler(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        init();

        mNotify.setTicker(getString(R.string.Only3ServiceTicker))
                .setContentTitle(getString(R.string.Only3ServiceTitle))
                .setContentText(getString(R.string.Only3ServiceMsg))
                .setOnGoing(true);

        startForeground(913, mNotify.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTask();

        return super.onStartCommand(intent, flags, startId);
    }

    private void onTask() {
        if (Build.VERSION.SDK_INT >= 11)
            new Only3Task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new Only3Task().execute();
    }

    private class Only3Task extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            mDelay = Tools.StringToInt(mPref.getString("appCheckDelay", "2"));
        }

        @Override
        protected Void doInBackground(Void... params) {
            do {
                try {
                    Thread.sleep(mDelay * 1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (mPower.isScreenOn()) {
                    doingAppCheck();

                } else {
                    RunningActivity.mLastPackageName = "";
                    if (isNotifyAppAlarm) {
                        AlarmTools.cancelStartNotification(getApplicationContext());
                        isNotifyAppAlarm = false;
                        mPref.remove("ACTION_NOTIFY_MINUTE_REPEAT");
                    }
                }

            } while (isServiceRun);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }

    public void doingAppCheck() {
        String pkgName = RunningActivity.getPackageName(getApplicationContext());

//        Log.e(" ", " ");
//        Log.e("pkgName", pkgName);
//        Log.e("mLastPackageName", " " + RunningActivity.mLastPackageName);

        /**
         * 세번만 어플과 SystemUI 어플은 패스
         */
        boolean isOnly3App = "lee.whdghks913.only3".equals(pkgName);
        boolean isSystemUI = "com.android.systemui".equals(pkgName);
        if (isOnly3App || isSystemUI) {
            System.gc();
            return;
        }

        /**
         * mLastPackageName가 null 이면 ""으로
         */
        if (Tools.nullCheck(RunningActivity.mLastPackageName)) {
            RunningActivity.mLastPackageName = "";
        }

        /**
         * 저번 체크시 실행한 앱을 그대로 실행하고 있을때 패스
         */
        boolean isLastApp = RunningActivity.mLastPackageName.equals(pkgName);
        if (isLastApp) {
            System.gc();
            return;
        }

        /**
         * mLastPackageName와 pkgName가 다르면 저장
         */
        if (!RunningActivity.mLastPackageName.equals(pkgName)) {
            RunningActivity.mLastPackageName = pkgName;
        }

        /**
         * 추가되지 않은 어플이 실행중이면 패스
         */
        boolean isAddedApp = CountTools.isAddedCheck(getApplicationContext(), pkgName);
        if (!isAddedApp) {
            System.gc();
            return;
        }

        /**
         * 카운트가 초과되지 않았으면
         */
        boolean isExceedCount = CountTools.isExceedCount(getApplicationContext(), pkgName);
        if (!isExceedCount) {
            CountTools.setCountUp(getApplicationContext(), pkgName);

            int mAllCount = CountTools.getAllCount(getApplicationContext(), pkgName);
            int mCurrentCount = CountTools.getCurrentCount(getApplicationContext(), pkgName);

            notifyCount(getString(R.string.notification_title), getString(R.string.notification_title), String.format(getString(R.string.notification_msg), mAllCount, mCurrentCount));

            int AppNotification = Tools.StringToInt(mPref.getString("appStartNotification", "-1"));
            if (AppNotification != -1) {
                AlarmTools.setStartNotification(getApplicationContext(), Tools.StringToInt(mPref.getString("appStartNotification", "-1")));
                isNotifyAppAlarm = true;
            }

        } else {
            notifyCount(getString(R.string.exceed_count_title), getString(R.string.exceed_count_title), getString(R.string.exceed_count_mgs));

            startExceedActivity();
        }
    }

    private void startExceedActivity() {
        Intent mHome = new Intent();
        mHome.setAction(Intent.ACTION_MAIN);
        mHome.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
                Intent.FLAG_ACTIVITY_FORWARD_RESULT |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        mHome.addCategory(Intent.CATEGORY_HOME);
        startActivity(mHome);

//        Intent mIntent = new Intent(getApplicationContext(), Abort.class);
//        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
//                Intent.FLAG_ACTIVITY_SINGLE_TOP );
//        startActivity(mIntent);
    }

    private void notifyCount(String ticker, String title, String msg) {
        if (NotificationType == 1 || NotificationType == 3) {
            mNotify = new NotificationTools(getApplicationContext());
            mNotify.cancel(1998);
            mNotify.setTicker(ticker)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setOnGoing(false)
                    .notify(1998);
            mNotify = null;
        }
        if (NotificationType == 2 || NotificationType == 3) {
            Message message = new Message();
            message.obj = msg;
            mHandler.sendMessage(message);
        }
    }

    protected class mHandler extends Handler {
        private final WeakReference<Only3Service> mActivity;

        protected mHandler(Only3Service activity) {
            mActivity = new WeakReference<Only3Service>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ToastTools.createToast(getApplicationContext(), msg.obj.toString(), false);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isServiceRun = false;

        super.onDestroy();

        stopForeground(true);
        AlarmTools.cancelStartNotification(getApplicationContext());
        isNotifyAppAlarm = false;
        RunningActivity.mLastPackageName = null;
        mPref.remove("ACTION_NOTIFY_MINUTE_REPEAT");

        if (mNotify == null)
            mNotify = new NotificationTools(getApplicationContext());
        mNotify.cancel(1998);
        mNotify.cancel(913);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
