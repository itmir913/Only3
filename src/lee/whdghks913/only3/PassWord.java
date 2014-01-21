package lee.whdghks913.only3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;

public class PassWord extends Activity {
	SharedPreferences setting;
	SharedPreferences.Editor setting_Edit;
	
	EditText PassWord;
	String answer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pass_word);
		
		setting = getSharedPreferences("setting", 0);
		setting_Edit = setting.edit();
		
		answer = setting.getString("password", "");
		if(answer.equals("")){
			setting_Edit.remove("password_enable");
			setting_Edit.remove("password");
			setting_Edit.commit();
			finish();
		}
		
		PassWord = (EditText) findViewById(R.id.PassWord);
		PassWord.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().equals(answer)){
					/**
					 * 2.0 ������Ʈ
					 * ��й�ȣ ���� ����
					 */
					Intent i = new Intent(PassWord.this, MainActivity.class);
					i.putExtra("PassWord_Enable", true);
					startActivity(i);
					finish();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	/**
        	 * �ڷΰ��� Ű�� ������ Ȩȭ������ �̵��մϴ�
        	 */
//        	Intent i = new Intent();
//			i.setAction(Intent.ACTION_MAIN);
//			i.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS |
//					Intent.FLAG_ACTIVITY_FORWARD_RESULT |
//					Intent.FLAG_ACTIVITY_NEW_TASK |
//					Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP |
//					Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
//			i.addCategory(Intent.CATEGORY_HOME);
//			startActivity(i);
			
//        	/**
//        	 * ������ Kill�ع����� ���񽺱��� ����Ǵ�����
//        	 * �ڷΰ��� Ű�� ������ ���Ƚ��ϴ�
//        	 */
        	/**
        	 * �ڷΰ��� Ű�� ���� MainActivity�� �Ѿ�� ����� �����մϴ�
        	 */
        	moveTaskToBack(true);
        	finish();
        	android.os.Process.killProcess(android.os.Process.myPid());
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
