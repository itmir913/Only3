package lee.whdghks913.only3;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	/**
	 * ���� ���࿡ �ʿ��� �ڵ带 �ۼ��մϴ�
	 * 
	 * Button���� start_Btn�� ������ ������ �ٸ� ��ư�� onClick���� ó��������
	 * strat_Btn�� background�� �ٲ�� ������ �ֱ� �����̴�
	 */
	Button start_Btn;
	
	/**
	 * 1.5������Ʈ
	 * ��Ű�� �̸����� �߰� �޴��� �����Ͽ����ϴ�
	 */
	/**
	 * Preferences�� ������ �κ�
	 */
	SharedPreferences setting;
	SharedPreferences.Editor setting_Editor;
	
    /**
     * 1.1 ������Ʈ
	 * ������ ������ ������� �ڵ�
	 */
	DevicePolicyManager devicePolicyManager;
	ComponentName adminComponent;
	
	int kill=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setting = getSharedPreferences("setting", 0);
		setting_Editor = setting.edit();
		
		if(setting.getBoolean("welcome", true)){
			setting_Editor.putBoolean("welcome", false).commit();
			
			// ó�������ϰ�� Welcomeȭ���� ǥ���մϴ�
			startActivity(new Intent(this, Welcome.class));
			finish();
		}else{
			/**
			 * 1.1 ������Ʈ
			 * ������ ������ ������ ���� ����â�� ǥ���մϴ�
			 */
			
			adminComponent = new ComponentName(this, AdminReceiver.class);
			devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			
			if (!devicePolicyManager.isAdminActive(adminComponent)){
				Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
				intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
						getString(R.string.device_admin));
				startActivityForResult(intent, 1);
			}
			
			try {
				PackageManager packageManager = this.getPackageManager();
				PackageInfo infor =  packageManager.getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
			    final int code = infor.versionCode;
			    
			    if (setting.getInt("update_code", 0)!=code){
					AlertDialog.Builder alert = new AlertDialog.Builder(this);
					alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    	setting_Editor.putInt("update_code", code);
					    	setting_Editor.commit();
					    	dialog.dismiss();
					    }
					});
					alert.setMessage(R.string.change_log);
					alert.show();
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		boolean unlocked_password = getIntent().getBooleanExtra("PassWord_Enable", false);
		if(!unlocked_password)
			if(setting.getBoolean("password_enable", false)){
				startActivity(new Intent(this, PassWord.class));
				finish();
			}
		
		start_Btn = (Button) findViewById(R.id.start_Btn);

		if(isServiceRunningCheck("lee.whdghks913.only3.AndroidService")){
			/**
			 * ���񽺰� �̹� ����Ǿ� �ִٸ� ��ư�� ������·� ����ϴ�
			 */
			start_Btn_start();
		}
		
	}
	
	public void start_btn(View v){
		if(isServiceRunningCheck("lee.whdghks913.only3.AndroidService")){
			if(setting.getBoolean("Ten_minutes", true)){
				Toast.makeText(this, R.string.ten, Toast.LENGTH_SHORT).show();
			}else{
				setting_Editor.putBoolean("Service", false).commit();
				
				stopService(new Intent(this, AndroidService.class));
				stopService(new Intent(this, MainService.class));
				start_Btn_stop();
			}
		}else{
			if (!devicePolicyManager.isAdminActive(adminComponent)){
				Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
				intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
						getString(R.string.device_admin));
				startActivityForResult(intent, 1);
			}else{
				setting_Editor.putBoolean("Ten_minutes", true);
				setting_Editor.putBoolean("Service", true).commit();
				
				startService(new Intent(this, AndroidService.class));
				startService(new Intent(this, MainService.class));
				start_Btn_start();
			}
		}
	}
	
	public void start_Btn_start(){
		start_Btn.setBackgroundResource(R.drawable.stop);
		start_Btn.setText(R.string.stop);
	}
	
	public void start_Btn_stop(){
		start_Btn.setBackgroundResource(R.drawable.start);
		start_Btn.setText(R.string.start);
	}
	
	public boolean isServiceRunningCheck(String service_Name){
    	for (RunningServiceInfo service : ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE))
    	    if (service_Name.equals(service.service.getClassName()))
    	        return true;
    	return false;
    }
	
	public void AppList_Btn(View v){
		startActivity(new Intent(this, AppInfoActivity.class));
	}
	
	public void settings(View v){
		startActivity(new Intent(this, Setting.class));
	}
	
	public void delete(View v){
		/**
		 * 2.0 ������Ʈ
		 * ��й�ȣ�� ������ ����� �ľ� ���� ����
		 */
		if(!isServiceRunningCheck("lee.whdghks913.only3.AndroidService")){
			if(setting.getBoolean("password_enable", false))
				Del_check();
			else
				intent_Del();
		}else{
			/**
			 * 1.5������Ʈ
			 * ���񽺰� �������϶� ������ ����Ҽ� ���ٴ� �佺Ʈ �˸� ����
			 */
			Toast.makeText(this, R.string.Not_Install, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void intent_Del(){
		if (devicePolicyManager.isAdminActive(adminComponent)){
			devicePolicyManager.removeActiveAdmin(adminComponent);
		}
		/**
		 * 1.5������Ʈ
		 * ������ �ٷ� �����Ҽ� �ִ� ��� �߰�
		 */
		Uri uri = Uri.fromParts("package", "lee.whdghks913.only3", null);    
		Intent it = new Intent(Intent.ACTION_DELETE, uri);    
		startActivity(it);
	}
	
	public void Del_check(){
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.activity_pass_word_make, null);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	String Inputpassword = ((EditText) view.findViewById(R.id.password_edittext)).getText().toString();
		    	if(Inputpassword.equals(setting.getString("password", ""))){
//					return true;
		    		intent_Del();
				}
		        dialog.dismiss();
		    }
		});
		alert.setNegativeButton(R.string.exit, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setView(view);
		alert.show();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			kill++;
			if(kill == 2){
				System.gc();
				/**
				 * 2.1 ������Ʈ
				 * ���� ����� ���񽺰� �����ٰ� ������ ���� ���� (Thanks for Edge)
				 */
				finish();
			}
			Toast.makeText(getBaseContext(), R.string.back_to_kill, Toast.LENGTH_SHORT).show();
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
