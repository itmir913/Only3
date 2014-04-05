package lee.whdghks913.only3.staticfunctions;

import lee.whdghks913.only3.MainActivity;
import lee.whdghks913.only3.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Tools {

	public static void Notification(Context mContext, int notifiIcon,
			String notifiTitle, String notifiMessage, boolean autoCancle,
			boolean clearIcon, boolean vibrate, int notifiInt, boolean isCalcle) {

		NotificationManager nm = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
				new Intent(mContext, MainActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(
				mContext);

		if (clearIcon)
			mCompatBuilder.setSmallIcon(R.drawable.clear_icon);
		else
			mCompatBuilder.setSmallIcon(notifiIcon);

		mCompatBuilder.setTicker(notifiTitle);
		mCompatBuilder.setWhen(System.currentTimeMillis());
		mCompatBuilder.setContentTitle(notifiTitle);
		mCompatBuilder.setContentText(notifiMessage);
		if (vibrate)
			mCompatBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
		mCompatBuilder.setContentIntent(pendingIntent);

		if (autoCancle)
			mCompatBuilder.setAutoCancel(true);
		else
			mCompatBuilder.setOngoing(true);

		nm.notify(notifiInt, mCompatBuilder.build());

		if (isCalcle)
			nm.cancel(notifiInt);
	}

}
