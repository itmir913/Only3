package lee.whdghks913.only3.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by whdghks913 on 2015-05-17.
 */
public class ToastTools {
    public static void createToast(Context mContext, String mText, boolean isLong) {
        if (isLong)
            Toast.makeText(mContext, mText, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
    }

    public static void createToast(Context mContext, int mText, boolean isLong) {
        if (isLong)
            Toast.makeText(mContext, mContext.getString(mText), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(mContext, mContext.getString(mText), Toast.LENGTH_SHORT).show();
    }
}
