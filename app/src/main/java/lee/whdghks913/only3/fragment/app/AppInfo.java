package lee.whdghks913.only3.fragment.app;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by whdghks913 on 2015-05-17.
 */
public class AppInfo {
    public static interface AppFilter {
        public void init();

        public boolean filterApp(ApplicationInfo info);
    }

    // 아이콘
    public Drawable mIcon = null;
    // 어플리케이션 이름
    public String mAppName = null;
    // 패키지 명
    public String mAppPackage = null;
    // Count 추가 여부
    public boolean isAdded = false;

    /**
     * 서드파티 필터
     */
    /*
    public static final AppFilter THIRD_PARTY_FILTER = new AppFilter() {
        @Override
        public void init() {
        }

        @Override
        public boolean filterApp(ApplicationInfo info) {
            if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                return true;
            } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                return true;
            }
            return false;
        }
    };
    */

    /**
     * 알파벳순으로 정렬
     */
    public static final Comparator<AppInfo> ALPHA_COMPARATOR = new Comparator<AppInfo>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AppInfo object1, AppInfo object2) {
            return sCollator.compare(object1.mAppName, object2.mAppName);
        }
    };
}
