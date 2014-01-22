package lee.whdghks913.only3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("CommitPrefEdits")
public class Setting extends Activity {
	SharedPreferences setting;
	SharedPreferences.Editor setting_Edit;
	
	SeekBar delay, Five_Seekbar;
	TextView delay_text, Five_Text;
	/**
	 * 1.6 업데이트
	 * 상단바 알림 아이콘 투명 기능 지원
	 */
	CheckBox password, vibrate, clear_icon;
	
	/**
	 * 1.8 업데이트
	 * 백업, 복원 지원
	 */
	CheckBox preference_backup;
	
	TextView madeby;
	
	RadioButton All, toast, Notifi;
	RadioGroup alertType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		setting = getSharedPreferences("setting", 0);
		setting_Edit = setting.edit();
		
		delay = (SeekBar) findViewById(R.id.delay_seekbar);
		delay_text = (TextView) findViewById(R.id.delay);
		
		password = (CheckBox) findViewById(R.id.password);
		vibrate = (CheckBox) findViewById(R.id.vibrate);
		clear_icon = (CheckBox) findViewById(R.id.clear_icon_noti);
		
		preference_backup = (CheckBox) findViewById(R.id.preference_backup);
		
		alertType = (RadioGroup) findViewById(R.id.alertType);
		All = (RadioButton) findViewById(R.id.All);
		toast = (RadioButton) findViewById(R.id.Toast);
		Notifi = (RadioButton) findViewById(R.id.Notifi);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		if(setting.getInt("NotifiType", 0)==0)
			Notifi.setChecked(true);
		else if(setting.getInt("NotifiType", 0)==1)
			toast.setChecked(true);
		else if(setting.getInt("NotifiType", 0)==2)
			All.setChecked(true);
		
		alertType.setOnCheckedChangeListener(new RadioGroupListener());
		
		BackupCheck();
		
		Five_Seekbar = (SeekBar) findViewById(R.id.Five_Seekbar);
		Five_Text = (TextView) findViewById(R.id.Five_Text);
		
		int delay_int = setting.getInt("delay", 10);
		if(delay_int != 10){
			delay_text.setText(String.format(getString(R.string.delay), delay_int--));
			delay.setProgress(delay_int);
		}else{
			delay.setProgress(1);
			delay_text.setText(String.format(getString(R.string.delay), 2));
		}
		
		int five_int = setting.getInt("Notification", 5);
		if(five_int==0)
			Five_Text.setText(R.string.five_minute_setting_TextView_not);
		else
			five_seekbar(five_int);
		
		if(setting.getBoolean("password_enable", false))
			password.setChecked(true);
		if(setting.getBoolean("notification_clear", false))
			clear_icon.setChecked(true);
		if(setting.getBoolean("vibrate", false))
			vibrate.setChecked(true);
		if(isServiceRunningCheck())
			clear_icon.setEnabled(false);
		
		delay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				delay_text.setText(String.format(getString(R.string.delay), ++progress));
				setting_Edit.putInt("delay", progress).commit();
			}
		});
		
		password.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
//					startActivity(new Intent(Setting.this, PassWord_Make.class));
					LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					final View view = inflater.inflate(R.layout.activity_pass_word_make, null);
					
					AlertDialog.Builder alert = new AlertDialog.Builder(Setting.this);
					alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					    @Override
					    public void onClick(DialogInterface dialog, int which) {
					    	String Inputpassword = ((EditText) view.findViewById(R.id.password_edittext)).getText().toString();
					    	if(Inputpassword.equals("")){
					    		Toast.makeText(Setting.this, R.string.blank, Toast.LENGTH_SHORT).show();
					    		password.setChecked(false);
					    	}else{
					    		setting_Edit.putString("password", Inputpassword);
					    		setting_Edit.putBoolean("password_enable", true).commit();
					    		Toast.makeText(Setting.this, R.string.enter_password_ok, Toast.LENGTH_SHORT).show();
					    	}
					        dialog.dismiss();
					    }
					});
					alert.setNegativeButton(R.string.exit, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							password.setChecked(false);
							dialog.dismiss();
						}
					});
					alert.setView(view);
					alert.show();
				}else{
					setting_Edit.remove("password_enable");
					setting_Edit.remove("password").commit();
				}
			}
		});
		
		vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
//					Log.i("진동", "설정됨");
					setting_Edit.putBoolean("vibrate", true).commit();
				}else{
					setting_Edit.remove("vibrate").commit();
				}
			}
		});
		
		clear_icon.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					setting_Edit.putBoolean("notification_clear", true).commit();
				}else{
					setting_Edit.remove("notification_clear").commit();
				}
			}
		});
		
		/**
		 * 1.8 업데이트
		 * 백업, 복원 지원
		 */
		preference_backup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				preference_backup.setChecked(!isChecked);
				
				AlertDialog.Builder alert = new AlertDialog.Builder(Setting.this);
				alert.setTitle(R.string.preference_backup_restore);
				alert.setPositiveButton(R.string.preference_backup, new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	String sdcard = Environment.getExternalStorageDirectory().toString() + "/only3/";
				    	
				    	File folder =new File(sdcard);
				    	if(!folder.exists())
				    		folder.mkdirs();
				    	
				    	if(saveSharedPreferencesToFile(new File(sdcard + "package_All_count.backup"), "package_All_count")
				    			&& saveSharedPreferencesToFile(new File(sdcard + "package_count.backup"), "package_count")
				    			&& saveSharedPreferencesToFile(new File(sdcard + "package_list.backup"), "package_list")
				    			&& saveSharedPreferencesToFile(new File(sdcard + "setting.backup"), "setting")){
				    		Toast.makeText(Setting.this, R.string.preference_backup_complete, Toast.LENGTH_LONG).show();
				    	}else
				    		Toast.makeText(Setting.this, R.string.preference_fail, Toast.LENGTH_LONG).show();
				    	dialog.dismiss();
				    }
				});
				alert.setNegativeButton(R.string.preference_restore, new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	String sdcard = Environment.getExternalStorageDirectory().toString() + "/only3/";
				    	
				    	if(!new File(sdcard).exists()){
				    		Toast.makeText(Setting.this, R.string.preference_not_file, Toast.LENGTH_LONG).show();
				    		return;
				    	}
				    	
				    	if(loadSharedPreferencesFromFile(new File(sdcard + "package_All_count.backup"), "package_All_count")
				    			&& loadSharedPreferencesFromFile(new File(sdcard + "package_count.backup"), "package_count")
				    			&& loadSharedPreferencesFromFile(new File(sdcard + "package_list.backup"), "package_list")
				    			&& loadSharedPreferencesFromFile(new File(sdcard + "setting.backup"), "setting")){
				    		Toast.makeText(Setting.this, R.string.preference_restore_complete, Toast.LENGTH_LONG).show();
				    	}else
				    		Toast.makeText(Setting.this, R.string.preference_fail, Toast.LENGTH_LONG).show();
				    	dialog.dismiss();
				    }
				});
				alert.show();
//				}
			}
		});
		
		Five_Seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int tmp = 0;
				if(progress==0)
					tmp=0;
				else if(progress==1)
					tmp=5;
				else if(progress==2)
					tmp=10;
				else if(progress==3)
					tmp=15;
				else if(progress==4)
					tmp=20;
				else if(progress==5)
					tmp=25;
				else if(progress==6)
					tmp=30;
				if(progress==0)
					Five_Text.setText(R.string.five_minute_setting_TextView_not);
				else
					Five_Text.setText(String.format(getString(R.string.five_minute_setting_TextView), tmp));
				setting_Edit.putInt("Notification", tmp).commit();
			}
		});
		
		madeby = (TextView) findViewById(R.id.madeby);
		try{
			PackageManager packageManager = this.getPackageManager();
			PackageInfo infor =  packageManager.getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
			
			madeby.setText(String.format(getString(R.string.madeby), infor.versionName));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void five_seekbar(int progress){
		int tmp = 0;
		if(progress==5)
			tmp=1;
		else if(progress==10)
			tmp=2;
		else if(progress==15)
			tmp=3;
		else if(progress==20)
			tmp=4;
		else if(progress==25)
			tmp=5;
		else if(progress==30)
			tmp=6;
		Five_Seekbar.setProgress(tmp);
		Five_Text.setText(String.format(getString(R.string.five_minute_setting_TextView), progress));
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
	
	boolean isServiceRunningCheck() {
    	ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
    	for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
    	    if ("lee.whdghks913.only3.AndroidService".equals(service.service.getClassName()))
    	        return true;
    	return false;
    }
	
	
	/**
	 * 1.8업데이트
	 * 백업/복원 지원
	 * http://stackoverflow.com/questions/10864462/how-can-i-backup-sharedpreferences-to-sd-card
	 */
	private boolean saveSharedPreferencesToFile(File dst, String prefName) {
	    boolean res = false;
	    ObjectOutputStream output = null;
	    try {
	        output = new ObjectOutputStream(new FileOutputStream(dst));
	        SharedPreferences pref = 
	                            getSharedPreferences(prefName, MODE_PRIVATE);
	        output.writeObject(pref.getAll());

	        res = true;
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }finally {
	        try {
	            if (output != null) {
	                output.flush();
	                output.close();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    return res;
	}

	@SuppressWarnings({ "unchecked" })
	private boolean loadSharedPreferencesFromFile(File src, String prefName) {
	    boolean res = false;
	    ObjectInputStream input = null;
	    try {
	        input = new ObjectInputStream(new FileInputStream(src));
	            Editor prefEdit = getSharedPreferences(prefName, MODE_PRIVATE).edit();
	            prefEdit.clear();
	            Map<String, ?> entries = (Map<String, ?>) input.readObject();
	            for (Entry<String, ?> entry : entries.entrySet()) {
	                Object v = entry.getValue();
	                String key = entry.getKey();

	                if (v instanceof Boolean)
	                    prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
	                else if (v instanceof Float)
	                    prefEdit.putFloat(key, ((Float) v).floatValue());
	                else if (v instanceof Integer)
	                    prefEdit.putInt(key, ((Integer) v).intValue());
	                else if (v instanceof Long)
	                    prefEdit.putLong(key, ((Long) v).longValue());
	                else if (v instanceof String)
	                    prefEdit.putString(key, ((String) v));
	            }
	            prefEdit.commit();
	        res = true;         
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }finally {
	        try {
	            if (input != null) {
	                input.close();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    return res;
	}
	
	public boolean BackupCheck(){
		String sdcard = Environment.getExternalStorageDirectory().toString() + "/only3/";
		File all_count =new File(sdcard + "package_All_count.backup");
		File count =new File(sdcard + "package_count.backup");
		File package_list =new File(sdcard + "package_list.backup");
		File setting =new File(sdcard + "setting.backup");
		
		if(all_count.exists() && count.exists() && package_list.exists() && setting.exists()){
			preference_backup.setChecked(true);
			return true;
		}
		return false;
	}
	
	public class RadioGroupListener implements android.widget.RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if(checkedId==R.id.All)
				setting_Edit.putInt("NotifiType", 2).commit();
			if(checkedId==R.id.Toast)
				setting_Edit.putInt("NotifiType", 1).commit();
			if(checkedId==R.id.Notifi)
				setting_Edit.putInt("NotifiType", 0).commit();
		}
	}
	
}
