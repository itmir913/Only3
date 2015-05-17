package lee.whdghks913.only3.tools;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by 종환 on 2015-05-05.
 */
public class PowerTools {
    private static PowerManager mPower;

    public PowerTools(Context mContext) {
        mPower = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
    }

    public boolean isScreenOn() {
        return mPower.isScreenOn();
    }
}
