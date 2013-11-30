package lee.whdghks913.only3;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Installed_AppList extends Activity {
	ArrayList<String> items, settinged;
	ArrayList<String> items_package, settinged_package;
	
	ArrayAdapter<String> listAP, ApplistAP;
	
	PackageManager pm;
	ListView list, AppList_Settinged;
	
	SharedPreferences package_All_count, package_list, package_count;
	SharedPreferences.Editor package_list_Edit, package_All_count_Edit, package_count_Editor;
	
	String packageName, appName;
	int tmp_position;
	
	View view;
	LayoutInflater inflater;
	
	/**
	 * 1:���� ����
	 * 2:�ý��� ����
	 * 3:��� ����
	 */
	int Show_Mod=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_installed_app_list);

		package_list = getSharedPreferences("package_list", 0);
		package_list_Edit = package_list.edit();
		package_All_count = getSharedPreferences("package_All_count", 0);
		package_All_count_Edit = package_All_count.edit();
		package_count = getSharedPreferences("package_count", 0);
		package_count_Editor = package_count.edit();
		
		items = new ArrayList<String>();
		settinged = new ArrayList<String>();
		
		items_package = new ArrayList<String>();
		settinged_package = new ArrayList<String>();
		
		pm = this.getPackageManager();
		inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		list = (ListView) findViewById(R.id.AppList);
		AppList_Settinged = (ListView) findViewById(R.id.AppList_Settinged);
		
		getList();
		
		listAP = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
		list.setAdapter(listAP);
		
		ApplistAP = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settinged);
		AppList_Settinged.setAdapter(ApplistAP);
		
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> ap, View v, int position,
					long id) {
				Add_Alert(position);
			}
		});
		
		AppList_Settinged.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> ap, View v, int position,
					long id) {
				remove_Alert(position);
			}
		});
		
		/**
		 * LongClickListener�� Ȱ��ȭ �� �ʿ伺�� ����
		 * (���� ���� ����� ������ ���� �ʴ´�)
		 */
/*		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> ap, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				return true;
			}
		});*/
		
/*		AppList_Settinged.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> ap, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				return true;
			}
		});*/
	}
	
	/**
	 * ��ġ�� ���� ����Ʈ�� ��� �޼ҵ��̴�
	 */
	public void getList(){
//		List<PackageInfo> appinfo = getPackageManager().getInstalledPackages(
//				PackageManager.GET_ACTIVITIES);
//		PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_DISABLED_COMPONENTS);
		
		PackageManager packageManager = getPackageManager();
		List<PackageInfo> appinfo = packageManager
                .getInstalledPackages(PackageManager.GET_ACTIVITIES);
		
		for(int i = 0 ; i <appinfo.size() ; ++i ){
			PackageInfo pi = appinfo.get(i);
			boolean b=false;
			/**
			 * 1:���� ����
			 * 2:�ý��� ����
			 * 3:��� ����
			 */
//			int Show_Mod=1;
			if(Show_Mod==1){
				b = isSystemPackage(pi);
			}else if(Show_Mod==2){
				b = ! isSystemPackage(pi);
			}
			if(!b){
				String packageName = pi.packageName;
				String AppName = pi.applicationInfo.loadLabel(pm).toString();
				
				/**
				 * 1.2 ������Ʈ : ������ ������ ��Ͽ� ǥ�õ��� �ʰ� ����
				 */
				if(packageName.equals("lee.whdghks913.only3"))
					return;
				
				String Load_appName = package_list.getString(packageName, "");
//				Log.d("Hi", Load_appName);
				
				/**
				 * items.contains(AppName)�� true�϶��� �̹� �߰��Ȱ��� �ִ°��̹Ƿ�
				 * !�����ڸ� ����ؼ� items.contains(AppName)�� ���� false�϶� true�� ��ȯ�ǵ��� �������
				 */
				if( ! items.contains(AppName)){
					if(Load_appName.equals(packageName)){
						settinged.add(AppName);
						settinged_package.add(packageName);
						Log.d("������ ��Ű��", AppName+" ("+packageName+")");
					}else{
						items.add(AppName);
						items_package.add(packageName);
					}
				}
			}
		}
	}
	
	private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }
	
	@Override
	protected void onRestart() {
		super.onRestart();
		// ������� ��� ����Ʈ�� �����, �ٽ� ����ϴ�
		items.clear();
		settinged.clear();
		items_package.clear();
		settinged_package.clear();
		
		getList();
	}
	
	/**
	 * ����, �ߴܸ޼ҵ�(onPause, onStop)�� ����ɰ��
	 * �� ��Ƽ��Ƽ�� ������� ������ �����Ƿ� finish()�� ȣ���Ѵ�
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.gc();
		finish();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.gc();
		finish();
	}
	
	/**
	 * ������ �߰��Ҷ� ȣ��Ǵ� �޼ҵ�� ������ ���� position���� �Ѿ�´�
	 * list�� �����ϸ�, �����ϸ� �����ȵ� ����Ʈ���� �����Ѱ� ����, ������ ����Ʈ�� �����Ѱ� �ִ´� 
	 */
//	public void Add_Alert(String AppName, String packageName){
	public void Add_Alert(int position){
		tmp_position = position;
		
		view = inflater.inflate(R.layout.add_package, null);
		
		/**
		 * ���� �̸��� ��Ű�� �̸��� position������ ��´�
		 */
		appName = items.get(position);
		packageName = items_package.get(position);
		
		/**
		 * 1.2 ������Ʈ : ������ ������ ī��Ʈ�� �����Ҽ� ����
		 */
		if(packageName.equals("lee.whdghks913.only3")){
			Toast.makeText(this, R.string.me_app, Toast.LENGTH_SHORT).show();
			return;
		}
		
//		if(!(packageName.indexOf("com.android")==-1) || !(packageName.indexOf("android")==-1))
//			Toast.makeText(this, R.string.count_add_Alert, Toast.LENGTH_SHORT).show();

		((TextView) view.findViewById(R.id.AppName)).setText( appName );
		((TextView) view.findViewById(R.id.PackageName)).setText( packageName );
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	String count_String = ((EditText) view.findViewById(R.id.Count)).getText().toString();
		    	if(count_String.equals("")){
		    		Toast.makeText(Installed_AppList.this, R.string.blank, Toast.LENGTH_SHORT).show();
		    		return;
		    	}else{
		    		int count = Integer.valueOf(count_String);
		    		if(count==0){
		    			Toast.makeText(Installed_AppList.this, R.string.count_is_zero, Toast.LENGTH_SHORT).show();
		    			return;
		    		}else if(count>250){
		    			Toast.makeText(Installed_AppList.this, R.string.count_250, Toast.LENGTH_SHORT).show();
		    			return;
		    		}else{
		    			// ������ ���� �ִ´�
		    			package_list_Edit.putString(packageName, packageName);
		    			package_All_count_Edit.putInt(packageName, count);
		    			
		    			package_list_Edit.commit();
		    			package_All_count_Edit.commit();

		    			
		    			items.remove(tmp_position);
		    			settinged.add(appName);
		    			
		    			items_package.remove(tmp_position);
		    			settinged_package.add(packageName);
		    			
		    			/**
		    			 * ����Ʈ�� ���ΰ�ħ�Ѵ�
		    			 */
						listAP.notifyDataSetChanged();
						ApplistAP.notifyDataSetChanged();
		    			
		    			Toast.makeText(Installed_AppList.this, R.string.count_add, Toast.LENGTH_SHORT).show();
		    		}
		    	}
		    	removeView(view);
		        dialog.dismiss();
		    }
		});
		alert.setNegativeButton(R.string.exit, null);
		alert.setView(view);
		alert.show();
	}
	
	public void remove_Alert(int position){
		tmp_position = position;
		
		view = inflater.inflate(R.layout.remove_package, null);
		
		appName = settinged.get(position);
		packageName = settinged_package.get(position);
		
		((TextView) view.findViewById(R.id.AppName)).setText( appName );
		((TextView) view.findViewById(R.id.PackageName)).setText( packageName );
		((TextView) view.findViewById(R.id.Count_Status)).setText( String.format(getString(R.string.now_count),
				package_count.getInt(packageName, 0), package_All_count.getInt(packageName, 0)) );
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton(R.string.del, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	package_list_Edit.remove(packageName);
		    	package_All_count_Edit.remove(packageName);
		    	package_count_Editor.remove(packageName);
		    	
		    	package_list_Edit.commit();
		    	package_All_count_Edit.commit();
		    	package_count_Editor.commit();
		    	
		    	
		    	settinged.remove(tmp_position);
		    	items.add(appName);
		    	
		    	settinged_package.remove(tmp_position);
		    	items_package.add(packageName);
    			
		    	/**
    			 * ����Ʈ�� ���ΰ�ħ�Ѵ�
    			 */
				listAP.notifyDataSetChanged();
				ApplistAP.notifyDataSetChanged();
				
		        dialog.dismiss();
		    }
		});
		alert.setNeutralButton(R.string.count_edit, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				removeView(view);
				Edit_Count();
				dialog.dismiss();
			}
		});
		alert.setNegativeButton(R.string.exit, null);
		alert.setView(view);
		alert.show();
	}
	
	public void Edit_Count(){
		((EditText) view.findViewById(R.id.Count)).setVisibility(View.VISIBLE);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	
		    	String count_String = ((EditText) view.findViewById(R.id.Count)).getText().toString();
		    	if(count_String.equals(""))
		    		Toast.makeText(Installed_AppList.this, R.string.blank, Toast.LENGTH_SHORT).show();
		    	else{
		    		int count = Integer.valueOf(count_String);
		    		if(count==0){
		    			Toast.makeText(Installed_AppList.this, R.string.count_is_zero, Toast.LENGTH_SHORT).show();
		    		}else{
		    			package_All_count_Edit.putInt(packageName, count);
		    			package_All_count_Edit.commit();
		    			
		    			Toast.makeText(Installed_AppList.this, R.string.count_fixed, Toast.LENGTH_SHORT).show();
		    		}
		    	}
		    	removeView(view);
		        dialog.dismiss();
		    }
		});
		alert.setNegativeButton(R.string.exit, null);
		alert.setView(view);
		alert.show();
	}
	
	public void removeView(View v){
		View viewThatAlreadyHasAParent = v;
		ViewGroup parentViewGroup = (ViewGroup)viewThatAlreadyHasAParent.getParent();
		parentViewGroup.removeView(v);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.install_app_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.all_app){
			Show_Mod=3;
		}else if(item.getItemId()==R.id.system_app){
			Show_Mod=2;
		}else if(item.getItemId()==R.id.user_app){
			Show_Mod=1;
		}
		items.clear();
		settinged.clear();
		items_package.clear();
		settinged_package.clear();
		
		getList();
		
		listAP.notifyDataSetChanged();
		ApplistAP.notifyDataSetChanged();
		
		return true;
	}
	
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		if(Show_Mod==1){
//			menu.findItem(R.id.all_app).setVisible(true);
//			menu.findItem(R.id.system_app).setVisible(true);
//			menu.findItem(R.id.user_app).setVisible(false);
//		}else if(Show_Mod==2){
//			menu.findItem(R.id.all_app).setVisible(true);
//			menu.findItem(R.id.system_app).setVisible(false);
//			menu.findItem(R.id.user_app).setVisible(true);
//		}else if(Show_Mod==3){
//			menu.findItem(R.id.all_app).setVisible(false);
//			menu.findItem(R.id.system_app).setVisible(true);
//			menu.findItem(R.id.user_app).setVisible(true);
//		}
//		return true;
//	}
	
}
