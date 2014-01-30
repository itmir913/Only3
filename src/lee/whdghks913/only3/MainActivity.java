package lee.whdghks913.only3;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import lee.whdghks913.only3.count.AndroidService;
import lee.whdghks913.only3.count.SubService;
import lee.whdghks913.only3.fulllock.FullLockActivity;
import lee.whdghks913.only3.fulllock.FullLockService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

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
    SharedPreferences full_lock, setting;
    SharedPreferences.Editor full_lock_Editor, setting_Editor;
    
    /**
     * 1.1 업데이트
     * 관리자 권한을 얻기위한 코드
     */
    DevicePolicyManager devicePolicyManager;
    ComponentName adminComponent;
    
    int kill=0;
    
    int hour, minute;
    
    AlarmManager am;
    PendingIntent sender_fulllock;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setting = getSharedPreferences("setting", 0);
        setting_Editor = setting.edit();
        
        full_lock = getSharedPreferences("full_lock", 0);
        full_lock_Editor = full_lock.edit();
        
        if(setting.getBoolean("welcome", true)){
            setting_Editor.putBoolean("welcome", false).commit();
            
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

        if(isServiceRunningCheck("lee.whdghks913.only3.count.AndroidService")){
            /**
             * 서비스가 이미 실행되어 있다면 버튼을 실행상태로 만듭니다
             */
            start_Btn_start();
        }
        
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent_fulllock = new Intent(MainActivity.this, BroadCast.class);
        intent_fulllock.setAction("ACTION_START_FULL_LOCK");
        sender_fulllock = PendingIntent.getBroadcast(MainActivity.this, 0, intent_fulllock, 0);
    }
    
    public void start_btn(View v){
        if(isServiceRunningCheck("lee.whdghks913.only3.count.AndroidService")){
            if(setting.getBoolean("Ten_minutes", true)){
                Toast.makeText(this, R.string.ten, Toast.LENGTH_SHORT).show();
            }else{
                setting_Editor.putBoolean("Service", false).commit();
                
                stopService(new Intent(this, SubService.class));
                stopService(new Intent(this, AndroidService.class));
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
    
    public boolean isServiceRunningCheck(String serviceName){
        for (RunningServiceInfo service : ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE))
            if (serviceName.equals(service.service.getClassName()))
                return true;
        return false;
    }
    
    public void AppList_Btn(View v){
        startActivity(new Intent(this, AppInfoActivity.class));
    }
    
    public void settings(View v){
        startActivity(new Intent(this, Setting.class));
    }
    
    public void FullLock_Btn(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        
        if(full_lock.getBoolean("Enable", false)){
            alert.setPositiveButton(R.string.all_lock_alarm_remove, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    full_lock_Editor.clear().commit();
                    am.cancel(sender_fulllock);
                    Toast.makeText(MainActivity.this, R.string.all_lock_alarm_remove, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }else{
            alert.setPositiveButton(R.string.all_lock_alarm_btn_1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ReserveLock();
                    dialog.dismiss();
                }
            });
        }
        
        alert.setNeutralButton(R.string.all_lock_alarm_btn_2, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                justNowLock();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.exit, null);
        alert.setMessage(R.string.all_lock_alarm_btn_help);
        alert.show();
    }
    
    public void justNowLock(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.full_lock_timeset, null);
        
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(view);
        
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.fullLock_timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(0);
        timePicker.setCurrentMinute(0);
        
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                
                if(hour==0 && minute==0){
                    Toast.makeText(MainActivity.this, R.string.all_lock_select_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                againFullLockCheck(hour, minute);
                
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.exit, null);
        alert.show();
    }
    
    public void ReserveLock(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.full_lock_reserve_timeset, null);
        
        final TimePicker afterTime = (TimePicker) view.findViewById(R.id.afterTime);
        final TimePicker lockTime = (TimePicker) view.findViewById(R.id.lockTime);
        lockTime.setIs24HourView(true);
        lockTime.setCurrentHour(0);
        lockTime.setCurrentMinute(0);
        
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(view);
        
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
                int hour = lockTime.getCurrentHour();
                int minute = lockTime.getCurrentMinute();
                
                if(hour==0 && minute==0){
                    Toast.makeText(MainActivity.this, R.string.all_lock_select_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                againReserveLock(afterTime, lockTime);
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.exit, null);
        alert.show();
    }
    
    protected void againReserveLock(TimePicker afterTime, TimePicker lockTime){
        hour = lockTime.getCurrentHour();
        minute = lockTime.getCurrentMinute();
        
        int amPm;
        
        if(afterTime.getCurrentHour() >= 12){
            amPm = R.string.pm;
        }else{
            amPm = R.string.am;
        }
        
        final Calendar calendar = Calendar.getInstance();
        
        int year = calendar.get(Calendar.YEAR); //올해
        int month = calendar.get(Calendar.MONTH); //이번달(10월이면 9를 리턴받는다. calendar는 0월부터 11월까지로 12개의월을 사용)
        int day = calendar.get(Calendar.DAY_OF_MONTH); //오늘날짜
        
        calendar.set(year, month ,day, afterTime.getCurrentHour(), afterTime.getCurrentMinute());
        
        if(System.currentTimeMillis()>=calendar.getTimeInMillis()){
            Toast.makeText(this, R.string.all_lock_alarm_past, Toast.LENGTH_SHORT).show();
            return;
        }
        
        String YYYY = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(calendar.getTime());
        
        String HHMM = new SimpleDateFormat("hh:mm", Locale.KOREA).format(calendar.getTime());
        
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                full_lock_Editor.putInt("Year", calendar.get(Calendar.YEAR));
                full_lock_Editor.putInt("Month", calendar.get(Calendar.MONTH));
                full_lock_Editor.putInt("Day", calendar.get(Calendar.DAY_OF_MONTH));
                full_lock_Editor.putInt("Hour", hour);
                full_lock_Editor.putInt("Minute", minute);
                full_lock_Editor.putBoolean("Enable", true).commit();
                
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender_fulllock);
                
                Toast.makeText(MainActivity.this, R.string.all_lock_alarm_finish, Toast.LENGTH_LONG).show();
                
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.exit, null);
        alert.setMessage(String.format(getString(R.string.all_lock_select_check_reserve), lockTime.getCurrentHour(), lockTime.getCurrentMinute(),
                YYYY, getString(amPm), HHMM));
        alert.show();
    }
    
    protected void againFullLockCheck(int H, int M){
        hour = H;
        minute = M;
        
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(String.format(getString(R.string.all_lock_select_check), hour, minute));
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar calendar = Calendar.getInstance();
                
                 int year = calendar.get(Calendar.YEAR);//올해
                 int month = calendar.get(Calendar.MONTH);//이번달(10월이면 9를 리턴받는다. calendar는 0월부터 11월까지로 12개의월을 사용)
                 int day = calendar.get(Calendar.DAY_OF_MONTH);//오늘날짜
                 
                full_lock_Editor.putInt("Year", year);
                full_lock_Editor.putInt("Month", month);
                full_lock_Editor.putInt("Day", day);
                full_lock_Editor.putInt("Hour", hour);
                full_lock_Editor.putInt("Minute", minute);
                full_lock_Editor.putBoolean("Enable", true).commit();
                
//                setting_Editor.putBoolean("Service", false).commit();
                
//                stopService(new Intent(MainActivity.this, SubService.class));
//                stopService(new Intent(MainActivity.this, AndroidService.class));
//                start_Btn_stop();
                
                startService(new Intent(MainActivity.this, FullLockService.class));
                startActivity(new Intent(MainActivity.this, FullLockActivity.class));
                finish();
                
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.exit, null);
        alert.show();
    }
    
    public void delete(View v){
        /**
         * 2.0 업데이트
         * 비밀번호가 있으면 비번을 쳐야 삭제 가능
         */
        if(!isServiceRunningCheck("lee.whdghks913.only3.count.AndroidService")){
            if(setting.getBoolean("password_enable", false))
                Del_check();
            else
                intent_Del();
        }else{
            /**
             * 1.5업데이트
             * 서비스가 실행중일때 권한을 취소할수 없다는 토스트 알림 설정
             */
            Toast.makeText(this, R.string.Not_Install, Toast.LENGTH_SHORT).show();
        }
    }
    
    public void intent_Del(){
        if (devicePolicyManager.isAdminActive(adminComponent)){
            devicePolicyManager.removeActiveAdmin(adminComponent);
        }
        /**
         * 1.5업데이트
         * 어플을 바로 삭제할수 있는 기능 추가
         */
        Uri uri = Uri.fromParts("package", "lee.whdghks913.only3", null);    
        startActivity(new Intent(Intent.ACTION_DELETE, uri));
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
                    intent_Del();
                }
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.exit, null);
        alert.setView(view);
        alert.show();
    }
}
