package lee.whdghks913.only3.fragment.app;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.tools.CountTools;
import lee.whdghks913.only3.tools.LockTools;

/**
 * Created by whdghks913 on 2015-05-17.
 */
class AppInfoViewHolder {
    // App Icon
    public ImageView mIcon;
    // App Name
    public TextView mName;
    // App Package Name
    public TextView mPackage;
    // Background layout
    public LinearLayout mLayout;

    // Count TextView
    public TextView mCount;

    // White List Icon
    public ImageView mWhiteListIcon;
}

public class AppInfoAdapter extends BaseAdapter {
    private Context mContext = null;

    private List<ResolveInfo> mAppList = null;
    private ArrayList<AppInfo> mListData = new ArrayList<AppInfo>();

//    private PackageManager mPackageManager;

    public AppInfoAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void add(AppInfo addInfo) {
        mListData.add(addInfo);
    }

    public void add(int position, AppInfo addInfo) {
        mListData.add(position, addInfo);
    }

    public void sort() {
        Collections.sort(mListData, AppInfo.ALPHA_COMPARATOR);
    }

    public List<ResolveInfo> getAppList() {
        return mAppList;
    }

    public void clear() {
        mListData.clear();
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public AppInfo getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppInfoViewHolder holder;

        if (convertView == null) {
            holder = new AppInfoViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_appinfo_listview, null);

            holder.mLayout = (LinearLayout) convertView.findViewById(R.id.mLayout);
            holder.mIcon = (ImageView) convertView.findViewById(R.id.app_icon);
            holder.mName = (TextView) convertView.findViewById(R.id.app_name);
            holder.mPackage = (TextView) convertView.findViewById(R.id.app_package);
            holder.mCount = (TextView) convertView.findViewById(R.id.mCount);
            holder.mWhiteListIcon = (ImageView) convertView.findViewById(R.id.mWhiteListIcon);

            convertView.setTag(holder);
        } else {
            holder = (AppInfoViewHolder) convertView.getTag();
        }

        AppInfo data = mListData.get(position);

        if (data.mIcon != null) {
            holder.mIcon.setImageDrawable(data.mIcon);
        } else {
            holder.mIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_no_app_icon));
        }

        holder.mName.setText(data.mAppName);
        holder.mPackage.setText(data.mAppPackage);

        Resources mResources = mContext.getResources();

        if (data.isAdded) {
            boolean isExceed = CountTools.isExceedCount(mContext, data.mAppPackage);
            if (isExceed) {
                holder.mLayout.setBackgroundColor(mResources.getColor(R.color.flat_bright_red_orange));
            } else {
                holder.mLayout.setBackgroundColor(mResources.getColor(R.color.flat_melon_yellow));
            }
            int AllCount = CountTools.getAllCount(mContext, data.mAppPackage);
            int Count = CountTools.getCurrentCount(mContext, data.mAppPackage);

            holder.mCount.setVisibility(View.VISIBLE);
            holder.mCount.setText(String.format(mResources.getString(R.string.count_string_format), AllCount, Count));
        } else {
            holder.mCount.setVisibility(View.GONE);
            holder.mLayout.setBackgroundColor(mResources.getColor(android.R.color.transparent));
        }

        if (!LockTools.isPackageWhiteList(mContext, data.mAppPackage)) {
            // 추가되어 있지 않으면
            holder.mWhiteListIcon.setImageDrawable(null);
            holder.mWhiteListIcon.setVisibility(View.GONE);
        } else {
            // 추가되어 있으면
            holder.mWhiteListIcon.setImageResource(R.drawable.ic_lock_open);
            holder.mWhiteListIcon.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

}
