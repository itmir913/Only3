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
	 public void onDisabled(Context context, Intent intent) {
		 Toast.makeText(context, R.string.device_admin_disabled, Toast.LENGTH_SHORT).show();
	 }
	 
/*	@Override
	public void onEnabled(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onEnabled(context, intent);
	}*/
	
	 /**
	  * 1.5 업데이트
	  * 설정-보안-기기관리자에서 세번만을 취소할경우
	  * 정말 취소하시겠습니까?라는 알림을 띄웁니다
	  */
	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		// TODO Auto-generated method stub
//		return super.onDisableRequested(context, intent);
		return context.getString(R.string.device_admin_disabled_warring);
	}
	 
	 /**
	  * http://hns17.tistory.com/114 이사이트를 아주 많이 참조했습니다
	  */
}