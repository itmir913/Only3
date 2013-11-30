package lee.whdghks913.only3;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends Activity {
	SharedPreferences setting;
	SharedPreferences.Editor setting_Edit;
	
	SeekBar delay, Five_Seekbar;
	TextView delay_text, Five_Text;
	CheckBox password, vibrate;
	
	TextView madeby;
	
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
		
		delay.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				delay_text.setText(String.format(getString(R.string.delay), ++progress));
				setting_Edit.putInt("delay", progress).commit();
			}
		});
		
		password.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
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
							// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				if(isChecked)
					setting_Edit.putBoolean("vibrate", true).commit();
				else
					setting_Edit.remove("vibrate").commit();
			}
		});
		
		Five_Seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
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
			// TODO Auto-generated catch block
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
	
}
