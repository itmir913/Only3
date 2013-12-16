package lee.whdghks913.only3;

import android.annotation.SuppressLint;
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

@SuppressLint("DefaultLocale")
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
//	SharedPreferences package_All_count, package_list, package_count, setting;
//	SharedPreferences.Editor package_list_Edit, package_All_count_Edit, package_count_Editor, setting_Editor;
	SharedPreferences setting;
	SharedPreferences.Editor setting_Editor;
	
	/**
	 * ���̾ƿ� ���÷����ʹ� �޴�Ű�� ���� ��Ű���� ���� �߰��Ҷ� �ʿ��ϴ�
	 */
//	LayoutInflater inflater;
//	View view;
	
    /**
     * 1.1 ������Ʈ
	 * ������ ������ ������� �ڵ�
	 */
	DevicePolicyManager devicePolicyManager;
	ComponentName adminComponent;
	
	/**
	 * 2.0 ������Ʈ
	 * 10�� ��� ����� ��� ��Ȱ��ȭ
	 */
	/**
	 * 1.6 ������Ʈ
	 * 
	 * debug_10minute�� 10���� ��ٷ��� �ϴ� �������� �����ϱ� ����
	 * �����ڰ� �߰��� �ڵ��Դϴ�
	 * �� int���� 5�̻�Ǹ� 10���� ���������� �Ǵ��ϵ��� �ϴ� �ڵ尡 �����Ǿ� �ֽ��ϴ�
	 */
//	int kill=0, debug_10minute=0;
	int kill=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/**
		 * 1.5������Ʈ
		 * ��Ű�� �̸����� �߰� �޴��� �����Ͽ����ϴ�
		 */
		/**
		 * �� ������ ���� �ڵ�
		 */
//		package_list = getSharedPreferences("package_list", 0);
//		package_list_Edit = package_list.edit();
//		
//		package_All_count = getSharedPreferences("package_All_count", 0);
//		package_All_count_Edit = package_All_count.edit();
//		
//		package_count = getSharedPreferences("package_count", 0);
//		package_count_Editor = package_count.edit();
		
		setting = getSharedPreferences("setting", 0);
		setting_Editor = setting.edit();
		
		if(setting.getBoolean("welcome", true)){
			setting_Editor.putBoolean("welcome", false).commit();
//			setting_Editor.commit();
			
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		boolean unlocked_password = getIntent().getBooleanExtra("PassWord_Enable", false);
		if(!unlocked_password)
			if(setting.getBoolean("password_enable", false))
				startActivity(new Intent(this, PassWord.class));
		
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
				/**
				 * 2.1 ������Ʈ
				 * 10�� ��Ȱ��ȭ ����� ��� ��Ȱ��ȭ
				 */
				/**
				 * 1.6 ������Ʈ
				 * 
				 * 10���� ��ٷ��� �ϴ� ���������� �غ��ϱ� ���� ���� �������
				 * ���ѽð�(1��)�ȿ� 6���� ��ư��ġ�� �ϸ� Ǯ���� �Ǿ� �ֽ��ϴ�
				 */
//				++debug_10minute;
//				if(debug_10minute>=5){
//					Intent intent_10minute = new Intent(this, BroadCast.class);
//					intent_10minute.setAction("ACTION_FALSE_THE_STOP");
//					sendBroadcast(intent_10minute);
//				}
//				debug_10M();
//				Log.d("����� ī��Ʈ", "ī��Ʈ : "+debug_10minute);
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
				
//				alarm();
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
		startActivity(new Intent(this, Installed_AppList.class));
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
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		alert.setView(view);
		alert.show();
//		return false;
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
//				moveTaskToBack(true);
				finish();
//				android.os.Process.killProcess(android.os.Process.myPid());
			}
			Toast.makeText(getBaseContext(), R.string.back_to_kill, Toast.LENGTH_SHORT).show();
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 2.1 ������Ʈ
	 * 10�� ���� ���� ����� ��� ��Ȱ��ȭ
	 */
	/**
	 * 1.6 ������Ʈ (�ӽ�)
	 * 
	 * 1�ʸ� ��ٷ� �ִ� ������ �մϴ�
	 * 1�ʳ� 5�� �̻��� ��ġ�� ��������� 10���� ����� ������ �Ǵ��մϴ�
	 * 1�ʰ� �ʰ��Ǹ� ī���͸� �ʱ�ȭ �մϴ�
	 */
//	public void debug_10M(){
//		Runnable task = new Runnable(){
//			@Override
//			public void run(){
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {}
//				debug_10minute=0;
//			}
//		};
//		Thread thread = new Thread(task);
//		thread.start();
//	}
	
	/**
	 * 1.5������Ʈ
	 * ��Ű�� �̸����� �߰� �޴��� �����Ͽ����ϴ�
	 */
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		
//		if(item.getItemId()==R.id.Enter_packageName){
//			AlertDialog.Builder alert = new AlertDialog.Builder(this);
//			alert.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
//			    @Override
//			    public void onClick(DialogInterface dialog, int which) {
//			    	add_package();
//			    	dialog.dismiss();
//			    }
//			});
//			alert.setNeutralButton(R.string.del, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stubadd_package();
//					del_package();
//			    	dialog.dismiss();
//				}
//			});
//			alert.setMessage(R.string.Alert_Message);
//			alert.setNegativeButton(R.string.exit, null);
//			alert.show();
//		}
//		
//		return super.onOptionsItemSelected(item);
//	}
	
//	public boolean isInstalledPackage(String packageName){
//		if(((EditText) view.findViewById(R.id.Package)).getText().toString().equals("") || ((EditText) view.findViewById(R.id.count)).getText().toString().equals(""))
//			return false;
//		
//		PackageManager pm = getPackageManager();
//        try {
//        	pm.getApplicationInfo(packageName.toLowerCase(), PackageManager.GET_META_DATA);
//        	return true;
//        }
//        catch (NameNotFoundException e)
//        {
//        	Toast.makeText(MainActivity.this, R.string.Not_installed_Package, Toast.LENGTH_SHORT).show();
//        	return false;
//        }
//	}
	
//	public void add_package(){
//		inflater = (LayoutInflater)getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
//		view = inflater.inflate(R.layout.enter_the_packagename, null);
//		
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//		alert.setView(view);
//		alert.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
//		    @Override
//		    public void onClick(DialogInterface dialog, int which) {
//		    	EditText package_EditText, count_EditText;
//		    	
//		    	package_EditText = (EditText) view.findViewById(R.id.Package);
//		    	count_EditText = (EditText) view.findViewById(R.id.count);
//		    	
//		    	String package_name = package_EditText.getText().toString();
//		    	
//		    	/**
//				 * 1.2 ������Ʈ : ������ ������ ī��Ʈ�� �����Ҽ� ����
//				 */
//		    	if(package_name.equals("lee.whdghks913.only3")){
//					Toast.makeText(MainActivity.this, R.string.me_app, Toast.LENGTH_SHORT).show();
//					return;
//				}
//		    	
//		    	if(isInstalledPackage(package_name)){
//		    		int count = Integer.parseInt(count_EditText.getText().toString());
//		    		
//		    		package_list_Edit.putString(package_name, package_name);
//		    		package_All_count_Edit.putInt(package_name, count);
//		    		
//		    		package_list_Edit.commit();
//		    		package_All_count_Edit.commit();
//		    		Toast.makeText(MainActivity.this, String.format( getString(R.string.OK_Enter), package_name ), Toast.LENGTH_SHORT).show();
//		    		
//		    		removeView(view);
//		    		dialog.dismiss();
//		    	}
//		    }
//		});
//		alert.show();
//	}
	
//	public void del_package(){
//		inflater = (LayoutInflater)getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
//		view = inflater.inflate(R.layout.del_packagename, null);
//		
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//		alert.setView(view);
//		alert.setPositiveButton(R.string.del, new DialogInterface.OnClickListener() {
//		    @Override
//		    public void onClick(DialogInterface dialog, int which) {
//		    	String package_name = ((EditText) view.findViewById(R.id.Package)).getText().toString();
//		    	if(!(package_list.getString(package_name, "").equals(""))){
//		    		package_list_Edit.remove(package_name);
//		    		package_All_count_Edit.remove(package_name);
//		    		package_count_Editor.remove(package_name);
//		    		
//		    		package_list_Edit.commit();
//		    		package_All_count_Edit.commit();
//		    		package_count_Editor.commit();
//		    		
//		    		Toast.makeText(MainActivity.this, String.format( getString(R.string.Del_Enter), package_name ), Toast.LENGTH_SHORT).show();
//		    	}
//		    	removeView(view);
//		    	dialog.dismiss();
////		    	}
//		    }
//		});
//		alert.show();
//	}
	
//	public void removeView(View v){
//		View viewThatAlreadyHasAParent = v;
//		ViewGroup parentViewGroup = (ViewGroup)viewThatAlreadyHasAParent.getParent();
//		parentViewGroup.removeView(v);
//	}
	
}
