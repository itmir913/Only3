package lee.whdghks913.only3.count;

import lee.whdghks913.only3.R;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

public class Abort extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/**
		 * 1.5������Ʈ
		 * 
		 * ���� ī��Ʈ �ʰ� ȭ���� Ǯ ��ũ������ ����
		 */
		if(Build.VERSION.SDK_INT < 14)
			setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
		else
			setTheme(android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
		
		setContentView(R.layout.activity_abort);
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

}
