package lee.whdghks913.only3.lock.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.fragment.app.AppInfo;
import lee.whdghks913.only3.fragment.app.AppInfoAdapter;
import lee.whdghks913.only3.tools.LockTools;
import lee.whdghks913.only3.tools.Tools;

public class WhiteListAppFragment extends Fragment {
    ListView mListView;
    AppInfoAdapter mAdapter;
    View mLoadingContainer;

    public static WhiteListAppFragment newInstance() {
        return new WhiteListAppFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_app, container, false);

        mLoadingContainer = mView.findViewById(R.id.loading_container);
        mListView = (ListView) mView.findViewById(R.id.mListView);

        mAdapter = new AppInfoAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo mInfo = mAdapter.getItem(position);
                String mPackageName = mInfo.mAppPackage;

                try {
                    PackageManager mManager = getActivity().getPackageManager();
                    Intent mIntent = mManager.getLaunchIntentForPackage(mPackageName);
                    startActivity(mIntent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        startTask();

        return mView;
    }

    private void startTask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new WhiteListAppTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new WhiteListAppTask().execute();
    }

    /**
     * set loading view
     */
    private void setLoadingView(boolean isView) {
        if (isView) {
            // 화면 로딩뷰 표시
            mLoadingContainer.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            // 화면 어플 리스트 표시
            mListView.setVisibility(View.VISIBLE);
            mLoadingContainer.setVisibility(View.GONE);
        }
    }

    private class WhiteListAppTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            // 로딩뷰 표시하기
            setLoadingView(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                /**
                 * 어플리케이션 리스트 작성
                 */

                List<ResolveInfo> mAppList = mAdapter.getAppList();
                PackageManager mPackageManager = null;

                // 어플리스트 불러오기 작업 시작
                if (mAppList == null) {

                    // 패키지 매니저 취득
                    mPackageManager = getActivity().getPackageManager();

                    // 설치된 어플리케이션 취득
                    Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    mAppList = mPackageManager.queryIntentActivities(intent, 0);
                }

                // 기존에 있던 데이터 초기화
                mAdapter.clear();

                String[] mLauncherAppList = Tools.getLauncherApp(getActivity());

                AppInfo addInfo = null;

                for (int i = 0; i < mAppList.size(); i++) {

                    ResolveInfo app = mAppList.get(i);
                    addInfo = new AppInfo();

                    String mPackageName = app.activityInfo.packageName;

                    // App Icon
                    addInfo.mIcon = app.loadIcon(mPackageManager);
                    // App Name
                    addInfo.mAppName = app.loadLabel(mPackageManager).toString();
                    // App Package Name
                    addInfo.mAppPackage = mPackageName;

                    boolean isPhone = "com.android.phone".equalsIgnoreCase(mPackageName);
                    boolean isContacts = "com.android.contacts".equalsIgnoreCase(mPackageName);
                    boolean isSMS = "com.android.mms".equalsIgnoreCase(mPackageName);
                    boolean isWhiteListApp = LockTools.isPackageWhiteList(getActivity(), mPackageName);

                    if (isPhone || isContacts || isSMS || isWhiteListApp) {
                        mAdapter.add(addInfo);
                        continue;
                    }
                }

                // 알파벳 이름으로 소트(한글, 영어)
                mAdapter.sort();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // 어댑터 새로고침
            mAdapter.notifyDataSetChanged();

            // 로딩뷰 숨기기
            setLoadingView(false);
        }

    }

}
