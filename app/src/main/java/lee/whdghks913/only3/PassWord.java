package lee.whdghks913.only3;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
                     * 2.0 업데이트
                     * 비밀번호 오류 수정
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
}
