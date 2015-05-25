package lee.whdghks913.only3.fragment.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;

import java.util.List;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.tools.CountTools;
import lee.whdghks913.only3.tools.LockTools;
import lee.whdghks913.only3.tools.ToastTools;
import lee.whdghks913.only3.tools.Tools;

public class AppFragment extends Fragment {
    ListView mListView;
    AppInfoAdapter mAdapter;
    View mLoadingContainer;

    LayoutInflater mInflater;

    public static AppFragment newInstance() {
        return new AppFragment();
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

        mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mAdapter = new AppInfoAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo mInfo = mAdapter.getItem(position);
                boolean isAdded = mInfo.isAdded;

                if (position == 0) {
                    showEditCountDialog(mInfo.mIcon, mInfo.mAppName, mInfo.mAppPackage, true);
                    return;
                }
//                else if (position == 1) {
//                    showEditCountDialog(mInfo.mIcon, mInfo.mAppName, mInfo.mAppPackage, false, true);
//                    return;
//                }

                if (isAdded) {
                    boolean isExceed = CountTools.isExceedCount(getActivity(), mInfo.mAppPackage);
                    if (isExceed) {
                        showExceedCountDialog(mInfo.mIcon, mInfo.mAppName, mInfo.mAppPackage);
                    } else {
                        showEditCountDialog(mInfo.mIcon, mInfo.mAppName, mInfo.mAppPackage, false);
                    }
                } else {
                    showAddCountDialog(mInfo.mIcon, mInfo.mAppName, mInfo.mAppPackage);
                }
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return false;
                }

                AppInfo mInfo = mAdapter.getItem(position);
                String packageName = mInfo.mAppPackage;

                showWhiteList(mInfo.mIcon, mInfo.mAppName, mInfo.mAppPackage, LockTools.isPackageWhiteList(getActivity(), packageName));

                return true;
            }
        });

        /**
         * 서비스 실행과 어플 목록 로딩 실패가 연관이 없으므로 관련 코드 삭제
         */
//        if (ServiceTools.isServiceRunning(getActivity())) {
//            ((LinearLayout) mView.findViewById(R.id.mServiceInfoLayout)).setVisibility(View.VISIBLE);
//
//            ButtonFlat mLoadingRetry = (ButtonFlat) mView.findViewById(R.id.mLoadingRetry);
//            mLoadingRetry.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (ServiceTools.isServiceRunning(getActivity())) {
//                        // 서비스 실행중이므로 중지한다.
//                        getActivity().stopService(new Intent(getActivity(), Only3Service.class));
//                        ToastTools.createToast(getActivity(), getString(R.string.info_loading), false);
//                    }
//                }
//            });
//        }

        return mView;
    }

    /**
     * AppList Loading Task
     */
    @Override
    public void onResume() {
        super.onResume();

        startTask();
    }

    private void showAddCountDialog(final Drawable mIcon, final String mAppName, final String mAppPackage) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());

        View mView = mInflater.inflate(R.layout.dialog_add_package, null);

        ((ImageView) mView.findViewById(R.id.AppImage)).setImageDrawable(mIcon);
        ((TextView) mView.findViewById(R.id.AppName)).setText(mAppName);
        ((TextView) mView.findViewById(R.id.PackageName)).setText(mAppPackage);

        mAlertDialog.setView(mView);

        final Dialog mDialog = mAlertDialog.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((ButtonFlat) mView.findViewById(R.id.mCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        ((ButtonFlat) mView.findViewById(R.id.mOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputCount = Tools.StringToInt(((EditText) mDialog.findViewById(R.id.inputCount)).getText().toString());
                if (inputCount < CountTools.MinCount) {
                    ToastTools.createToast(getActivity(), R.string.fix_min_count, false);
                    return;
                }

                CountTools.addPackageAllCount(getActivity(), mAppPackage, inputCount);

                startTask();

                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void showEditCountDialog(final Drawable mIcon, final String mAppName, final String mAppPackage, final boolean isAllPackage) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());

        View mView = mInflater.inflate(R.layout.dialog_edit_package, null);

        final EditText inputCountEditText = (EditText) mView.findViewById(R.id.inputCount);
        TextView showCurrentCount = (TextView) mView.findViewById(R.id.showCurrentCount);

        ((ImageView) mView.findViewById(R.id.AppImage)).setImageDrawable(mIcon);
        ((TextView) mView.findViewById(R.id.AppName)).setText(mAppName);
        ((TextView) mView.findViewById(R.id.PackageName)).setText(mAppPackage);

//        if (isNewApp)
//            showCurrentCount.setText(R.string.new_app_count_msg);
        if (!isAllPackage)
            showCurrentCount.setText(String.format(getString(R.string.count_string_format), CountTools.getAllCount(getActivity(), mAppPackage), CountTools.getCurrentCount(getActivity(), mAppPackage)));
        else if (isAllPackage)
            showCurrentCount.setText(R.string.help_all_package_count);

        mAlertDialog.setView(mView);

        final Dialog mDialog = mAlertDialog.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //adding button click event
        ((ButtonFlat) mView.findViewById(R.id.mCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        ((ButtonFlat) mView.findViewById(R.id.mEditCount)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputCount = Tools.StringToInt(inputCountEditText.getText().toString());
                if (inputCount < CountTools.MinCount) {
                    ToastTools.createToast(getActivity(), R.string.fix_min_count, false);
                    return;
                }

//                if (isNewApp)
//                    CountTools.setNewAppCount(getActivity(), inputCount);
                if (!isAllPackage)
                    CountTools.addPackageAllCount(getActivity(), mAppPackage, inputCount);
                else
                    editAllPackage(inputCount);

                startTask();

                mDialog.dismiss();
            }
        });
        ((ButtonFlat) mView.findViewById(R.id.mRemove)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isNewApp)
//                    CountTools.removeNewAppCount(getActivity());
                if (!isAllPackage)
                    CountTools.removePackage(getActivity(), mAppPackage);
                else
                    removeAllPackage();

                startTask();

                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void showExceedCountDialog(final Drawable mIcon, final String mAppName, final String mAppPackage) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());

        View mView = mInflater.inflate(R.layout.dialog_exceed_package, null);


        ((ImageView) mView.findViewById(R.id.AppImage)).setImageDrawable(mIcon);
        ((TextView) mView.findViewById(R.id.AppName)).setText(mAppName);
        ((TextView) mView.findViewById(R.id.PackageName)).setText(mAppPackage);
        ((TextView) mView.findViewById(R.id.showCurrentCount)).setText(String.format(getString(R.string.count_string_format), CountTools.getAllCount(getActivity(), mAppPackage), CountTools.getCurrentCount(getActivity(), mAppPackage)));

        mAlertDialog.setView(mView);

        final Dialog mDialog = mAlertDialog.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //adding button click event
        ((ButtonFlat) mView.findViewById(R.id.mCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void editAllPackage(int inputCount) {
        for (int position = 1; position < mAdapter.getCount(); position++) {
            AppInfo mInfo = mAdapter.getItem(position);

            if (CountTools.isAddedCheck(getActivity(), mInfo.mAppPackage)) {
                if (CountTools.isExceedCount(getActivity(), mInfo.mAppPackage))
                    continue;
                if (CountTools.ifExceedCount(getActivity(), mInfo.mAppPackage, inputCount))
                    continue;
            }

            CountTools.addPackageAllCount(getActivity(), mInfo.mAppPackage, inputCount);
        }
    }

    private void removeAllPackage() {
        for (int position = 1; position < mAdapter.getCount(); position++) {
            AppInfo mInfo = mAdapter.getItem(position);

            if (CountTools.isExceedCount(getActivity(), mInfo.mAppPackage))
                continue;

            CountTools.removePackage(getActivity(), mInfo.mAppPackage);
        }
    }

    private void showWhiteList(final Drawable mIcon, final String mAppName, final String mAppPackage, final boolean isAdded) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());

        View mView = mInflater.inflate(R.layout.dialog_white_list, null);

        ((ImageView) mView.findViewById(R.id.AppImage)).setImageDrawable(mIcon);
        ((TextView) mView.findViewById(R.id.AppName)).setText(mAppName);
        ((TextView) mView.findViewById(R.id.PackageName)).setText(mAppPackage);
        TextView help_white_list = (TextView) mView.findViewById(R.id.help_white_list);

        if (!isAdded) {
            // 추가되어 있지 않으면 추가 안내 문구 표시
            help_white_list.setText(R.string.whiteList_dialog_add);
        } else {
            // 추가되어 있으면 삭제 안내 문구 표시
            help_white_list.setText(R.string.whiteList_dialog_remove);
        }

        mAlertDialog.setView(mView);

        final Dialog mDialog = mAlertDialog.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((ButtonFlat) mView.findViewById(R.id.mCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        ((ButtonFlat) mView.findViewById(R.id.mOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAdded) {
                    // 추가되어 있지 않으면 추가
                    LockTools.addWhiteList(getActivity(), mAppPackage);
                } else {
                    // 추가되어 있으면 삭제
                    LockTools.removeWhiteList(getActivity(), mAppPackage);
                }

                startTask();

                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    /**
     * Start Task that is loading app list
     * http://vo2max.egloos.com/1284495
     */
    private void startTask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            new AppListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new AppListTask().execute();
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

    private class AppListTask extends AsyncTask<Void, Integer, Void> {

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
//                mAppList = mPackageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES
//                        | PackageManager.GET_DISABLED_COMPONENTS);
                }

                // 기존에 있던 데이터 초기화
                mAdapter.clear();

                String[] mLauncherAppList = Tools.getLauncherApp(getActivity());

                AppInfo addInfo = null;

                mAppList:
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

                    if (CountTools.isAddedCheck(getActivity(), mPackageName)) {
                        addInfo.isAdded = true;
                    }

                    boolean isOnly3App = "lee.whdghks913.only3".equalsIgnoreCase(mPackageName);
                    boolean isSystemUI = "com.android.systemui".equalsIgnoreCase(mPackageName);
                    boolean isPhone = "com.android.phone".equalsIgnoreCase(mPackageName);
                    boolean isContacts = "com.android.contacts".equalsIgnoreCase(mPackageName);
                    if (isOnly3App || isSystemUI || isPhone || isContacts) {
                        continue;
                    }

                    for (String launcherApp : mLauncherAppList) {
                        if (launcherApp.equalsIgnoreCase(mPackageName)) {
                            continue mAppList;
                        }
                    }

                    mAdapter.add(addInfo);
                }

                // 알파벳 이름으로 소트(한글, 영어)
                mAdapter.sort();

                /**
                 * 전체 어플 일괄 설정 기능 추가
                 */
                Resources mRes = getResources();
                AppInfo mAllCount = new AppInfo();
                mAllCount.mIcon = mRes.getDrawable(R.drawable.ic_no_app_icon);
                mAllCount.mAppName = mRes.getString(R.string.all_package_add_count);
                mAllCount.mAppPackage = mRes.getString(R.string.help_all_package_add_count);

                mAdapter.add(0, mAllCount);

                /**
                 * 새로 설치되는 어플의 카운트 설정 추가
                 */
//                AppInfo mNewAppCount = new AppInfo();
//                mNewAppCount.mIcon = mRes.getDrawable(R.drawable.ic_no_app_icon);
//                mNewAppCount.mAppName = mRes.getString(R.string.new_app_count);
//
//                int newAppCount = CountTools.getNewAppCount(getActivity());
//                if (newAppCount != -1) {
//                    mNewAppCount.mAppPackage = String.format(getString(R.string.new_app_count_format), newAppCount);
//                } else {
//                    mNewAppCount.mAppPackage = mRes.getString(R.string.help_new_app_count);
//                }
//
//                mAdapter.add(1, mNewAppCount);

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

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
