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
	 public void onDisabled(Context context, Intent intent) {
		 Toast.makeText(context, R.string.device_admin_disabled, Toast.LENGTH_SHORT).show();
	 }
	
	 /**
	  * 1.5 ������Ʈ
	  * ����-����-�������ڿ��� �������� ����Ұ��
	  * ���� ����Ͻðڽ��ϱ�?��� �˸��� ���ϴ�
	  */
	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		return context.getString(R.string.device_admin_disabled_warring);
	}
}