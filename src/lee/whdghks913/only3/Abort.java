package lee.whdghks913.only3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

public class Abort extends Activity {

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/**
		 * 1.5업데이트
		 * 
		 * 일일 카운트 초과 화면을 풀 스크린으로 설정
		 */
		if(Build.VERSION.SDK_INT < 14)
			setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
		else
			setTheme(android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
		
		setContentView(R.layout.activity_abort);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	/**
        	 * 뒤로가기 키를 누르면 홈화면으로 이동합니다
        	 */
        	Intent i = new Intent();
			i.setAction(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
					Intent.FLAG_ACTIVITY_FORWARD_RESULT |
					Intent.FLAG_ACTIVITY_NEW_TASK |
					Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP |
					Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);
			
//        	moveTaskToBack(true);
        	finish();
//        	android.os.Process.killProcess(android.os.Process.myPid());
        	return true;
        }
        return super.onKeyDown(keyCode, event);
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
