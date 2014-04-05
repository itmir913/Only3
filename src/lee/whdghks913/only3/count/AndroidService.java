package lee.whdghks913.only3.count;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.List;

import lee.whdghks913.only3.BroadCast;
import lee.whdghks913.only3.MainActivity;
import lee.whdghks913.only3.R;
import lee.whdghks913.only3.staticfunctions.Tools;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

@SuppressLint({ "NewApi", "CommitPrefEdits" })
public class AndroidService extends Service {
	SharedPreferences package_All_count, package_count, package_list, setting;
	SharedPreferences.Editor package_count_Editor, setting_Editor;

	Handler handler;
	String last_packageName = "";
	ActivityManager actvityManager;

	int All_Count = 0, Count = 0;
	Boolean isService = true, isRunningApp = true;

	AlarmManager am;
	PendingIntent sender;

	PowerManager pm;

	Intent intent_10minute;
	PendingIntent sender_10minute;

	@Override
	public void onCreate() {
		super.onCreate();
		/**
		 * 1.5업데이트 파워 매니저를 이용하여 화면이 켜졌을때만 작동하도록 설정
		 */
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		actvityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		Alarm();

		package_count = getSharedPreferences("package_count", 0);
		package_count_Editor = package_count.edit();

		package_list = getSharedPreferences("package_list", 0);
		package_All_count = getSharedPreferences("package_All_count", 0);

		setting = getSharedPreferences("setting", 0);
		setting_Editor = setting.edit();

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 1,
				new Intent(this, MainActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(
				this);

		if (setting.getBoolean("notification_clear", false))
			mCompatBuilder.setSmallIcon(R.drawable.clear_icon);
		else
			mCompatBuilder.setSmallIcon(R.drawable.ic_launcher);

		mCompatBuilder.setTicker(getString(R.string.app_name));
		mCompatBuilder.setWhen(System.currentTimeMillis());
		mCompatBuilder.setContentTitle(getString(R.string.notification_title));
		mCompatBuilder.setContentText(getString(R.string.notification_message));
		mCompatBuilder.setContentIntent(pendingIntent);
		mCompatBuilder.setOngoing(true);

		startForeground(1000, mCompatBuilder.build());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		handler = new handler(this);

		Runnable task = new Runnable() {
			@Override
			public void run() {
				while (isService) {
					/**
					 * 1.9 업데이트 코드 위치 변경 - 화면이 꺼져도 딜레이를 갖도록 설정
					 */
					if (setting.getInt("delay", 2) * 1000 == 0)
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					else
						try {
							Thread.sleep(setting.getInt("delay", 2) * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					/**
					 * 1.5업데이트 파워 매니저를 이용하여 화면이 켜졌을때만 작동하도록 설정
					 */
					if (pm.isScreenOn()) {
						Top_Activity();
					} else {
						if (setting.getInt("Notification", 5) != 0)
							if (!isRunningApp) {
								am.cancel(sender);
								isRunningApp = !isRunningApp;
								/**
								 * 1.9 업데이트 화면이 꺼지면 last package 초기화
								 */
								last_packageName = "";
								setting_Editor.remove("FIVE_MINUTE").commit();
							}
					}
				}
			}
		};

		Thread thread = new Thread(task);
		thread.start();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	protected void Top_Activity() {
		// for(Iterator<RunningTaskInfo> iterator =
		// actvityManager.getRunningTasks(1).iterator(); iterator.hasNext(); ){
		// String pkgName = iterator.next().topActivity.getPackageName();
		// }

		List<RunningTaskInfo> taskInfos = actvityManager.getRunningTasks(1);
		ComponentName topActivity = taskInfos.get(0).topActivity;
		// String className = topActivity.getClassName();
		String pkgName = topActivity.getPackageName();

		/**
		 * 2.7 Update : Do not check the SystemUI
		 */
		if (pkgName.equals("lee.whdghks913.only3")
				|| pkgName.equals("com.android.systemui")) {
			System.gc();
			return;
		}

		if (!pkgName.equals(package_list.getString(pkgName, ""))
				&& last_packageName.equals(pkgName)) {
			System.gc();
			return;
		}

		All_Count = package_All_count.getInt(pkgName, 0);
		Count = package_count.getInt(pkgName, 0);

		if (pkgName.equals(package_list.getString(pkgName, ""))
				&& Count > All_Count) {
			handler.sendEmptyMessage(0);
			return;
		} else if (pkgName.equals(package_list.getString(pkgName, ""))
				&& !last_packageName.equals(pkgName)) {
			++Count;
			package_count_Editor.putInt(pkgName, Count).commit();
			handler.sendEmptyMessage(0);
		}

		if (!last_packageName.equals(pkgName)) {
			last_packageName = pkgName;
			/**
			 * 1.1 업데이트 : 5분마다 알림을 띄우는 코드 추가
			 */
			if (setting.getInt("Notification", 5) != 0)
				if (!isRunningApp) {
					am.cancel(sender);
					isRunningApp = !isRunningApp;
					setting_Editor.remove("FIVE_MINUTE").commit();
				}
		} else if (pkgName.equals(package_list.getString(pkgName, ""))) {
			if (setting.getInt("Notification", 5) != 0)
				if (isRunningApp)
					alarm_five();
		}
		System.gc();
	}

	protected void showNotify(boolean isToomany) {

		if (isToomany)
			Tools.Notification(this,
				R.drawable.ic_launcher,
				String.format(getString(R.string.count_much_added), Count, All_Count),
				String.format(getString(R.string.count_much_added), Count, All_Count),
				true,
				setting.getBoolean("notification_clear", false),
				setting.getBoolean("vibrate", false),
				0,
				true);
		else
			Tools.Notification(this,
				R.drawable.ic_launcher,
				String.format(getString(R.string.count_added), Count, All_Count),
				getString(R.string.count_added),
				true,
				setting.getBoolean("notification_clear", false),
				setting.getBoolean("vibrate", false),
				0,
				true);

		// NotificationManager nm = (NotificationManager)
		// getSystemService(Context.NOTIFICATION_SERVICE);
		// Notification noti;
		//
		// if (isToomany)
		// if (setting.getBoolean("notification_clear", false))
		// noti = new Notification(R.drawable.clear_icon,
		// String.format(getString(R.string.count_much_added),
		// Count, All_Count), System.currentTimeMillis());
		// else
		// noti = new Notification(R.drawable.ic_launcher,
		// String.format(getString(R.string.count_much_added),
		// Count, All_Count), System.currentTimeMillis());
		// else if (setting.getBoolean("notification_clear", false))
		// noti = new Notification(R.drawable.clear_icon, String.format(
		// getString(R.string.count_added), Count, All_Count),
		// System.currentTimeMillis());
		// else
		// noti = new Notification(R.drawable.ic_launcher, String.format(
		// getString(R.string.count_added), Count, All_Count),
		// System.currentTimeMillis());
		//
		// noti.flags = Notification.FLAG_ONLY_ALERT_ONCE
		// | Notification.FLAG_AUTO_CANCEL;
		// Intent intent = new Intent(AndroidService.this, MainActivity.class);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// PendingIntent pendingI =
		// PendingIntent.getActivity(AndroidService.this,
		// 0, intent, 0);
		// noti.setLatestEventInfo(AndroidService.this,
		// getString(R.string.count_added),
		// getString(R.string.count_added), pendingI);
		// nm.notify(0, noti);
		// nm.cancel(0);
	}

	protected void showToast(boolean isToomany) {
		String toastText;
		if (isToomany)
			toastText = String.format(getString(R.string.count_much_added),
					Count, All_Count);
		else
			toastText = String.format(getString(R.string.count_added), Count,
					All_Count);

		Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		stopForeground(true);
		isService = false;
		am.cancel(sender_10minute);

		restartService();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		restartService();
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		restartService();
	}

	protected void restartService() {
		if (setting.getBoolean("Service", false)) {
			Intent intent = new Intent(this, BroadCast.class);
			sendBroadcast(intent);
		}
	}

	protected void alarm_five() {
		Calendar calendar = Calendar.getInstance();

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		Intent intent = new Intent(this, BroadCast.class);
		intent.setAction("ACTION_FIVE_MINUTE");
		sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		calendar.set(year, month, day, hour,
				minute + setting.getInt("Notification", 5));
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				setting.getInt("Notification", 5) * 60 * 1000, sender);

		isRunningApp = !isRunningApp;
	}

	protected void Alarm() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		intent_10minute = new Intent(this, BroadCast.class);
		intent_10minute.setAction("ACTION_FALSE_THE_STOP");
		sender_10minute = PendingIntent.getBroadcast(this, 0, intent_10minute,
				0);
		calendar.set(year, month, day, hour, minute + 10);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				sender_10minute);

		Intent intent_DATE = new Intent(this, BroadCast.class);
		intent_DATE.setAction("ACTION_DATE_CHANGE_BY_MIR");
		PendingIntent sender_DATE = PendingIntent.getBroadcast(this, 0,
				intent_DATE, 0);
		calendar.set(year, month, day + 1, 0, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				24 * 60 * 60 * 1000, sender_DATE);
	}

	protected class handler extends Handler {
		private final WeakReference<AndroidService> mActivity;

		protected handler(AndroidService activity) {
			mActivity = new WeakReference<AndroidService>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mActivity.get() != null) {
				if (All_Count < Count) {
					Intent i = new Intent();
					i.setAction(Intent.ACTION_MAIN);
					i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
							| Intent.FLAG_ACTIVITY_FORWARD_RESULT
							| Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					i.addCategory(Intent.CATEGORY_HOME);
					startActivity(i);

					Intent intent = new Intent(AndroidService.this, Abort.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
							| Intent.FLAG_ACTIVITY_SINGLE_TOP);
					startActivity(intent);

					if (setting.getInt("NotifiType", 0) == 0
							|| setting.getInt("NotifiType", 0) == 2)
						showNotify(true);
					if (setting.getInt("NotifiType", 0) == 1
							|| setting.getInt("NotifiType", 0) == 2)
						showToast(true);
				} else {
					if (setting.getInt("NotifiType", 0) == 0
							|| setting.getInt("NotifiType", 0) == 2)
						showNotify(false);
					if (setting.getInt("NotifiType", 0) == 1
							|| setting.getInt("NotifiType", 0) == 2)
						showToast(false);
				}
			}
		}
	}
}
