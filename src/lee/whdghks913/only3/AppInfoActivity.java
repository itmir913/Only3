package lee.whdghks913.only3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lee.whdghks913.only3.AppInfo.AppFilter;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 2.2 업데이트
 */
public class AppInfoActivity extends Activity {
	// 메뉴 KEY
	private final int MENU_DOWNLOAD = 0;
	private final int MENU_ALL = 1;
	private int MENU_MODE = MENU_DOWNLOAD;

	private PackageManager pm;

	private View mLoadingContainer;
	private ListView mListView = null;
	private ListAdapter mAdapter = null;
	
	SharedPreferences package_All_count, package_list, package_count, setting;
	SharedPreferences.Editor package_list_Edit, package_All_count_Edit, package_count_Editor;
	
	View inflater_view;
	LayoutInflater inflater;
	
	String appName, packageName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_info);
		
		package_list = getSharedPreferences("package_list", 0);
		package_list_Edit = package_list.edit();
		package_All_count = getSharedPreferences("package_All_count", 0);
		package_All_count_Edit = package_All_count.edit();
		package_count = getSharedPreferences("package_count", 0);
		package_count_Editor = package_count.edit();
		
		setting = getSharedPreferences("setting", 0);
		
		inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mLoadingContainer = findViewById(R.id.loading_container);
		mListView = (ListView) findViewById(R.id.listView1);
		
		mAdapter = new ListAdapter(this);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View view, int position,
					long id) {
				String package_name = ((TextView) view.findViewById(R.id.app_package)).getText().toString();
				
				if(package_list.getString(package_name, "").equals(package_name)){
					remove_Alert(view);
				}else{
					Add_Alert(view);
				}
			}
		});
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View view,
					int position, long id) {
				return true;
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		startTask();
	}

	/**
	 * 작업 시작
	 */
	private void startTask() {
		new AppTask().execute();
	}

	/**
	 * 로딩뷰 표시 설정
	 */
	private void setLoadingView(boolean isView) {
		if (isView) {
			// 화면 로딩뷰 표시
			mLoadingContainer.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		} else {
			// 화면 어플 리스트 표시
			mListView.setVisibility(View.VISIBLE);
			mLoadingContainer.setVisibility(View.GONE);
		}
	}

	/**
	 * List Fast Holder
	 */
	private class ViewHolder {
		// App Icon
		public ImageView mIcon;
		// App Name
		public TextView mName;
		// App Package Name
		public TextView mPacakge;
		// Background layout
		public LinearLayout mLayout;
		
		// Count layout
		public LinearLayout mCountLayout;
		// Count TextView
		public TextView Count_TextView;
	}

	/**
	 * List Adapter
	 */
	private class ListAdapter extends BaseAdapter {
		private Context mContext = null;

		private List<ApplicationInfo> mAppList = null;
		private ArrayList<AppInfo> mListData = new ArrayList<AppInfo>();

		public ListAdapter(Context mContext) {
			super();
			this.mContext = mContext;
		}

		@Override
		public int getCount() {
			return mListData.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int id) {
			return id;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();

				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.list_item_layout, null);

				holder.mIcon = (ImageView) convertView
						.findViewById(R.id.app_icon);
				holder.mName = (TextView) convertView
						.findViewById(R.id.app_name);
				holder.mPacakge = (TextView) convertView
						.findViewById(R.id.app_package);
				holder.mLayout = (LinearLayout) convertView
						.findViewById(R.id.layout);
				holder.mCountLayout = (LinearLayout) convertView
						.findViewById(R.id.Count_Layout);
				holder.Count_TextView = (TextView) convertView
						.findViewById(R.id.Count_TextView);
				
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			AppInfo data = mListData.get(position);

			if (data.mIcon != null) {
				holder.mIcon.setImageDrawable(data.mIcon);
			}
			
			holder.mName.setText(data.mAppNaem);
			holder.mPacakge.setText(data.mAppPackge);
			
			if(package_list.getString(data.mAppPackge, "").equals(data.mAppPackge)){
				/**
				 * 2.3 Update
				 */
				int now = package_count.getInt(data.mAppPackge, 0);
				int all = package_All_count.getInt(data.mAppPackge, 0);
				
				if(all<=now)
					holder.mLayout.setBackgroundResource(R.color.TotalCount);
				else
					holder.mLayout.setBackgroundResource(R.color.Background);
				
				holder.mCountLayout.setVisibility(View.VISIBLE);
				holder.Count_TextView.setText( String.format(getString(R.string.now_count),
						now, all ));
			}else{
				holder.mLayout.setBackgroundResource(android.R.color.white);
				holder.mCountLayout.setVisibility(View.GONE);
			}
			
			return convertView;
		}

		/**
		 * 어플리케이션 리스트 작성
		 */
		public void rebuild() {
			if (mAppList == null) {
				
				// 패키지 매니저 취득
				pm = AppInfoActivity.this.getPackageManager();
				
				// 설치된 어플리케이션 취득
				mAppList = pm
						.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES
								| PackageManager.GET_DISABLED_COMPONENTS);
			}
			
			AppFilter filter;
			switch (MENU_MODE) {
			case MENU_DOWNLOAD:
				filter = AppInfo.THIRD_PARTY_FILTER;
				break;
			default:
				filter = null;
				break;
			}
			
			if (filter != null) {
				filter.init();
			}
			
			// 기존 데이터 초기화
			mListData.clear();
			
			AppInfo addInfo = null;
			ApplicationInfo info = null;
			for (ApplicationInfo app : mAppList) {
				info = app;
				
				if (filter == null || filter.filterApp(info)) {
					// 필터된 데이터
					
					addInfo = new AppInfo();
					// App Icon
					addInfo.mIcon = app.loadIcon(pm);
					// App Name
					addInfo.mAppNaem = app.loadLabel(pm).toString();
					// App Package Name
					addInfo.mAppPackge = app.packageName;
					
					mListData.add(addInfo);
				}
			}
			
			// 알파벳 이름으로 소트(한글, 영어)
			Collections.sort(mListData, AppInfo.ALPHA_COMPARATOR);
		}
	}
	
	/**
	 * 작업 태스크
	 */
	private class AppTask extends AsyncTask<Void, Void, Void> {
		
		@Override
		protected void onPreExecute() {
			// 로딩뷰 시작
			setLoadingView(true);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// 어플리스트 작업시작
			mAdapter.rebuild();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// 어댑터 갱신
			mAdapter.notifyDataSetChanged();
			
			// 로딩뷰 정지
			setLoadingView(false);
		}
		
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_DOWNLOAD, 1, R.string.menu_download);
		menu.add(0, MENU_ALL, 2, R.string.menu_all);
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (MENU_MODE == MENU_DOWNLOAD) {
			menu.findItem(MENU_DOWNLOAD).setVisible(false);
			menu.findItem(MENU_ALL).setVisible(true);
		}else if (MENU_MODE == MENU_ALL) {
			menu.findItem(MENU_DOWNLOAD).setVisible(true);
			menu.findItem(MENU_ALL).setVisible(false);
		}else{
			menu.findItem(MENU_DOWNLOAD).setVisible(true);
			menu.findItem(MENU_ALL).setVisible(true);
		}
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == MENU_DOWNLOAD) {
			MENU_MODE = MENU_DOWNLOAD;
		}else{
			MENU_MODE = MENU_ALL;
		}
		
		startTask();
		
		return true;
	}
	
	public void Add_Alert(View v){
		appName = ((TextView) v.findViewById(R.id.app_name)).getText().toString();
		packageName = ((TextView) v.findViewById(R.id.app_package)).getText().toString();
		
		inflater_view = inflater.inflate(R.layout.add_package, null);
		
		/**
		 * 2.7 Update : Do not check the SystemUI
		 */
		/**
		 * 1.2 업데이트 : 세번만 어플은 카운트를 설정할수 없음
		 */
		if(packageName.equals("lee.whdghks913.only3") || packageName.equals("com.android.systemui")){
			Toast.makeText(this, R.string.me_app, Toast.LENGTH_SHORT).show();
			return;
		}
		
		/**
		 * 2.5 Update
		 */
		String[] home = getHomeLauncher();
		for(int i=0 ; i<home.length ; i++ ){
			if(home[i].equals(packageName)){
				Toast.makeText(this, R.string.count_launcher, Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
		((TextView) inflater_view.findViewById(R.id.AppName)).setText( appName );
		((TextView) inflater_view.findViewById(R.id.PackageName)).setText( packageName );
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	String count_String = ((EditText) inflater_view.findViewById(R.id.Count)).getText().toString();
		    	if(count_String.equals("")){
		    		Toast.makeText(AppInfoActivity.this, R.string.blank, Toast.LENGTH_SHORT).show();
		    		return;
		    	}else{
		    		int count = Integer.valueOf(count_String);
		    		if(count==0){
		    			Toast.makeText(AppInfoActivity.this, R.string.count_is_zero, Toast.LENGTH_SHORT).show();
		    			return;
		    		}else if(count>250 || count<3){
		    			Toast.makeText(AppInfoActivity.this, R.string.count_250, Toast.LENGTH_SHORT).show();
		    			return;
		    		}else{
		    			// 선택한 값을 넣는다
		    			package_list_Edit.putString(packageName, packageName);
		    			package_All_count_Edit.putInt(packageName, count);
		    			
		    			package_list_Edit.commit();
		    			package_All_count_Edit.commit();

		    			Toast.makeText(AppInfoActivity.this, R.string.count_add, Toast.LENGTH_SHORT).show();
		    			
		    			startTask();
		    		}
		    	}
		    	removeView(inflater_view);
		        dialog.dismiss();
		    }
		});
		alert.setNegativeButton(R.string.exit, null);
		alert.setView(inflater_view);
		alert.show();
	}
	
	public void remove_Alert(View v){
		appName = ((TextView) v.findViewById(R.id.app_name)).getText().toString();
		packageName = ((TextView) v.findViewById(R.id.app_package)).getText().toString();
		
		/**
		 * 2.3 Update
		 */
		int now = package_count.getInt(packageName, 0);
		int all = package_All_count.getInt(packageName, 0);
		
		if(all<=now){
			Toast.makeText(AppInfoActivity.this, R.string.count_not_fix_total, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(isServiceRunningCheck("lee.whdghks913.only3.count.AndroidService"))
			if(setting.getBoolean("Ten_minutes", true)){
				Toast.makeText(AppInfoActivity.this, R.string.count_not_fix_ten, Toast.LENGTH_SHORT).show();
				return;
			}
		
		inflater_view = inflater.inflate(R.layout.remove_package, null);
		
		((TextView) inflater_view.findViewById(R.id.AppName)).setText( appName );
		((TextView) inflater_view.findViewById(R.id.PackageName)).setText( packageName );
		((TextView) inflater_view.findViewById(R.id.Count_Status)).setText( String.format(getString(R.string.now_count),
				now, all ));
		
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
				
		        dialog.dismiss();
		        
		        startTask();
		    }
		});
		alert.setNeutralButton(R.string.count_edit, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				removeView(inflater_view);
				Edit_Count();
				dialog.dismiss();
			}
		});
		alert.setNegativeButton(R.string.exit, null);
		alert.setView(inflater_view);
		alert.show();
	}
	
	public void Edit_Count(){
		((EditText) inflater_view.findViewById(R.id.Count)).setVisibility(View.VISIBLE);
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	
		    	String count_String = ((EditText) inflater_view.findViewById(R.id.Count)).getText().toString();
		    	if(count_String.equals(""))
		    		Toast.makeText(AppInfoActivity.this, R.string.blank, Toast.LENGTH_SHORT).show();
		    	else{
		    		int count = Integer.valueOf(count_String);
		    		if(count==0){
		    			Toast.makeText(AppInfoActivity.this, R.string.count_is_zero, Toast.LENGTH_SHORT).show();
		    		}else if(count>250 || count<3){
		    			Toast.makeText(AppInfoActivity.this, R.string.count_250, Toast.LENGTH_SHORT).show();
		    			return;
		    		}else{
		    			package_All_count_Edit.putInt(packageName, count);
		    			package_All_count_Edit.commit();
		    			
		    			Toast.makeText(AppInfoActivity.this, R.string.count_fixed, Toast.LENGTH_SHORT).show();
		    			
		    			startTask();
		    		}
		    	}
		        dialog.dismiss();
		    }
		});
		alert.setNegativeButton(R.string.exit, null);
		alert.setView(inflater_view);
		alert.show();
	}
	
	public void removeView(View v){
		View viewThatAlreadyHasAParent = v;
		ViewGroup parentViewGroup = (ViewGroup)viewThatAlreadyHasAParent.getParent();
		parentViewGroup.removeView(v);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		System.gc();
		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();
		System.gc();
		finish();
	}
	
	private String[] getHomeLauncher(){
		String[] homes;
		PackageManager pm =  getPackageManager();
		Intent homeIntent = new Intent(Intent.ACTION_MAIN); // Action 값이 ACTION_MAIN
		homeIntent.addCategory(Intent.CATEGORY_HOME); // Category 값이 CATEGORY_HOME
		
		//위 Intent의 조건을 만족시켜 주는 ResolveInfo 리스트를 구한다.
		List<ResolveInfo> homeApps = pm.queryIntentActivities(homeIntent, PackageManager.GET_ACTIVITIES);
		homes = new String[homeApps.size()];
		for(int i=0; i<homeApps.size(); i++){
			ResolveInfo info = homeApps.get(i); //구해진 ResolveInfo 를 통해서 PackageName을 가져온다.
			homes[i] = info.activityInfo.packageName; 
		}
		return homes;
	}
	
	public boolean isServiceRunningCheck(String service_Name){
    	for (RunningServiceInfo service : ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE))
    	    if (service_Name.equals(service.service.getClassName()))
    	        return true;
    	return false;
    }
}