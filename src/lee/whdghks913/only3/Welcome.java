package lee.whdghks913.only3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Welcome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
	}
	
	public void next(View v){
		startActivity(new Intent(this, MainActivity.class));
		System.gc();
		finish();
	}

}
