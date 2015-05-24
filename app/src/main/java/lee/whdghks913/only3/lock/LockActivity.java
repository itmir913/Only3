package lee.whdghks913.only3.lock;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.tools.LockTools;
import lee.whdghks913.only3.tools.ServiceTools;

public class LockActivity extends ActionBarActivity {
    Toolbar mToolbar;

    TextView mFormat;
    ButtonFlat mIf;
    SimpleDateFormat mDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.flat_melon_yellow));
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);

        mDateFormat = new SimpleDateFormat(LockTools.TimeFormat);

        mFormat = (TextView) findViewById(R.id.mFormat);

        long finishTime = LockTools.getFinishTime(getApplicationContext());
        if (finishTime == -1L) {
            ServiceTools.stopLockSubService(getApplicationContext());
            ServiceTools.stopLockService(getApplicationContext());
            finish();
            return;
        }

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(finishTime);

        mFormat.setText(String.format(getString(R.string.LockActivity_main_2), mDateFormat.format(mCalendar.getTime())));

        mIf = (ButtonFlat) findViewById(R.id.mIf);
        mIf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long finishTime = LockTools.getFinishTime(getApplicationContext());
                long currentTime = System.currentTimeMillis();

                if (currentTime >= finishTime) {
                    ServiceTools.stopLockSubService(getApplicationContext());
                    ServiceTools.stopLockService(getApplicationContext());

                    LockTools.removeFinishTime(getApplicationContext());

                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
