package lee.whdghks913.only3.welcome;

import lee.whdghks913.only3.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WelcomeApplist extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide_applist);
	}

	public void next(View v) {
		System.gc();
		finish();
		startActivity(new Intent(this, WelcomeFulllock.class));
	}
}
