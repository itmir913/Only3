package lee.whdghks913.only3.tools;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by whdghks913 on 2015-05-05.
 */
@Deprecated
public class PowerTools {
    private static PowerManager mPower;

    @Deprecated
    public PowerTools(Context mContext) {
        mPower = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
    }

    @Deprecated
    public boolean isScreenOn() {
        return mPower.isScreenOn();
    }
}
