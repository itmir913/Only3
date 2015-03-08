package lee.whdghks913.only3.fulllock;

import java.lang.ref.WeakReference;

import lee.whdghks913.only3.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

@SuppressLint("InlinedApi")
public class FullLockActivity extends Activity {
    static TextView remaingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT < 14)
            setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        else
            setTheme(android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        setContentView(R.layout.full_lock_show);

        remaingText = (TextView) findViewById(R.id.remainingTime);

        final Handler handler = new MyHandler(this);

        Runnable remainingTime = new Runnable() {
            @Override
            public void run() {
                while(FullLockService.remainingSec >= 0){
                    handler.sendEmptyMessage(0);

                    try { Thread.sleep(1000); } catch (InterruptedException e) {e.printStackTrace();}

                    if(FullLockService.remainingSec <= 0){
                        finish();
                        break;
                    }
                }
            }
        };

        Thread remaining = new Thread(remainingTime);
        remaining.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyHandler extends Handler {
        private final WeakReference<FullLockActivity> mActivity;

        public MyHandler(FullLockActivity activity) {
            mActivity = new WeakReference<FullLockActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            FullLockActivity activity = mActivity.get();
            if (activity != null) {
                int tmp = FullLockService.remainingSec;

                Object[] arrayOfObject = new Object[3];

                arrayOfObject[0] = Integer.valueOf(tmp / 3600);
                arrayOfObject[1] = Integer.valueOf(tmp / 60 % 60);
                arrayOfObject[2] = Integer.valueOf(tmp % 60);

                remaingText.setText(String.format("%02d:%02d:%02d", arrayOfObject));
            }
        }
    }
}
