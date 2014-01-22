package lee.whdghks913.only3;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver {
	 /**
	  * 기기관리자가 해제되었을떄 할 작업을 명시해 줍니다
	  */
	 @Override
	 public void onDisabled(Context mContext, Intent intent) {
		 Toast.makeText(mContext, R.string.device_admin_disabled, Toast.LENGTH_SHORT).show();
	 }
	
	 /**
	  * 1.5 업데이트
	  * 설정-보안-기기관리자에서 세번만을 취소할경우
	  * 정말 취소하시겠습니까?라는 알림을 띄웁니다
	  */
	@Override
	public CharSequence onDisableRequested(Context mContext, Intent intent) {
		return mContext.getString(R.string.device_admin_disabled_warring);
	}
}