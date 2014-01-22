package lee.whdghks913.only3;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver {
	 /**
	  * �������ڰ� �����Ǿ����� �� �۾��� ����� �ݴϴ�
	  */
	 @Override
	 public void onDisabled(Context mContext, Intent intent) {
		 Toast.makeText(mContext, R.string.device_admin_disabled, Toast.LENGTH_SHORT).show();
	 }
	
	 /**
	  * 1.5 ������Ʈ
	  * ����-����-�������ڿ��� �������� ����Ұ��
	  * ���� ����Ͻðڽ��ϱ�?��� �˸��� ���ϴ�
	  */
	@Override
	public CharSequence onDisableRequested(Context mContext, Intent intent) {
		return mContext.getString(R.string.device_admin_disabled_warring);
	}
}