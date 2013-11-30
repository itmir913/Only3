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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class MainActivity extends Activity {
	/**
	 * 어플 실행에 필요한 코드를 작성합니다
	 * 
	 * Button에서 start_Btn만 정의한 이유는 다른 버튼은 onClick으로 처리하지만
	 * strat_Btn의 background는 바뀌는 과정이 있기 때문이다
	 */
	Button start_Btn;
	
	/**
	 * 1.5업데이트
	 * 패키지 이름으로 추가 메뉴를 삭제하였습니다
	 */
	/**
	 * Preferences를 정의한 부분
	 */
//	SharedPreferences package_All_count, package_list, package_count, setting;
//	SharedPreferences.Editor package_list_Edit, package_All_count_Edit, package_count_Editor, setting_Editor;
	SharedPreferences setting;
	SharedPreferences.Editor setting_Editor;
	
	/**
	 * 레이아웃 인플레이터는 메뉴키를 눌러 패키지를 수동 추가할때 필요하다
	 */
//	LayoutInflater inflater;
//	View view;
	
    /**
     * 1.1 업데이트
	 * 관리자 권한을 얻기위한 코드
	 */
	DevicePolicyManager devicePolicyManager;
	ComponentName adminComponent;
	
	int kill=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/**
		 * 1.5업데이트
		 * 패키지 이름으로 추가 메뉴를 삭제하였습니다
		 */
		/**
		 * 값 저장을 위한 코드
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
			
			// 처음실행일경우 Welcome화면을 표시합니다
			startActivity(new Intent(this, Welcome.class));
			finish();
		}else{
			/**
			 * 1.1 업데이트
			 * 관리자 권한이 없으면 권한 승인창을 표시합니다
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
		
		if(setting.getBoolean("password_enable", false))
			startActivity(new Intent(this, PassWord.class));
		
		start_Btn = (Button) findViewById(R.id.start_Btn);

		if(isServiceRunningCheck("lee.whdghks913.only3.AndroidService")){
			/**
			 * 서비스가 이미 실행되어 있다면 버튼을 실행상태로 만듭니다
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
		if(!isServiceRunningCheck("lee.whdghks913.only3.AndroidService")){
			if (devicePolicyManager.isAdminActive(adminComponent)){
				devicePolicyManager.removeActiveAdmin(adminComponent);
			}
			/**
			 * 1.5업데이트
			 * 어플을 바로 삭제할수 있는 기능 추가
			 */
			Uri uri = Uri.fromParts("package", "lee.whdghks913.only3", null);    
			Intent it = new Intent(Intent.ACTION_DELETE, uri);    
			startActivity(it);
		}else{
			/**
			 * 1.5업데이트
			 * 서비스가 실행중일때 권한을 취소할수 없다는 토스트 알림 설정
			 */
			Toast.makeText(this, R.string.Not_Install, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			kill++;
			Toast.makeText(getBaseContext(), R.string.back_to_kill, Toast.LENGTH_SHORT).show();
			if(kill == 2){
				System.gc();
				moveTaskToBack(true);
				finish();
//				android.os.Process.killProcess(android.os.Process.myPid());
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 1.5업데이트
	 * 패키지 이름으로 추가 메뉴를 삭제하였습니다
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
//				 * 1.2 업데이트 : 세번만 어플은 카운트를 설정할수 없음
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
