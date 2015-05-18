package lee.whdghks913.only3.fragment.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFlat;

import java.util.List;

import lee.whdghks913.only3.R;
import lee.whdghks913.only3.service.Only3Service;
import lee.whdghks913.only3.tools.CountTools;
import lee.whdghks913.only3.tools.ToastTools;
import lee.whdghks913.only3.tools.Tools;

public class AppFragment extends Fragment {
    ListView mListView;
    AppInfoAdapter mAdapter;
    View mLoadingContainer;

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

        mAdapter = new AppInfoAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo mInfo = mAdapter.getItem(position);
                boolean isAdded = mInfo.isAdded;

                if (isAdded) {
                    boolean isExceed = CountTools.isExceedCount(getActivity(), mInfo.mAppPackage);
                    if (isExceed) {
                        showExceedCountDialog(mInfo.mIcon, mInfo.mAppName, mInfo.mAppPackage);
                    } else {
                        showEditCountDialog(mInfo.mIcon, mInfo.mAppName, mInfo.mAppPackage);
                    }
                } else {
                    showAddCountDialog(mInfo.mIcon, mInfo.mAppName, mInfo.mAppPackage);
                }
            }
        });

        if (Tools.getServiceRunning(getActivity())) {
            ((LinearLayout) mView.findViewById(R.id.mServiceInfoLayout)).setVisibility(View.VISIBLE);

            ButtonFlat mLoadingRetry = (ButtonFlat) mView.findViewById(R.id.mLoadingRetry);
            mLoadingRetry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Tools.getServiceRunning(getActivity())) {
                        // 서비스 실행중이므로 중지한다.
                        getActivity().stopService(new Intent(getActivity(), Only3Service.class));
                        ToastTools.createToast(getActivity(), getString(R.string.info_loading), false);
                    }
                }
            });
        }

        return mView;
    }

    /**
     * Start Task that is loading app list
     */
    private void startTask() {
        new AppTask().execute();
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

    private void showAddCountDialog(final Drawable mIcon, final String mAppName, final String mAppPackage) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());

        View mView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_add_package, null);

        ImageView AppImage = (ImageView) mView.findViewById(R.id.AppImage);
        TextView AppName = (TextView) mView.findViewById(R.id.AppName);
        TextView PackageName = (TextView) mView.findViewById(R.id.PackageName);

        AppImage.setImageDrawable(mIcon);
        AppName.setText(mAppName);
        PackageName.setText(mAppPackage);

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
        ((ButtonFlat) mView.findViewById(R.id.mOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int inputCount = Tools.StringToInt(((EditText) mDialog.findViewById(R.id.inputCount)).getText().toString());
                if (inputCount < CountTools.MinCount) {
                    Toast.makeText(getActivity(), R.string.fix_min_count, Toast.LENGTH_SHORT).show();
                    return;
                }

                CountTools.addPackageAllCount(getActivity(), mAppPackage, inputCount);
                startTask();
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void showEditCountDialog(final Drawable mIcon, final String mAppName, final String mAppPackage) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());

        View mView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_edit_package, null);

        ImageView AppImage = (ImageView) mView.findViewById(R.id.AppImage);
        TextView AppName = (TextView) mView.findViewById(R.id.AppName);
        TextView PackageName = (TextView) mView.findViewById(R.id.PackageName);
        TextView showCurrentCount = (TextView) mView.findViewById(R.id.showCurrentCount);

        AppImage.setImageDrawable(mIcon);
        AppName.setText(mAppName);
        PackageName.setText(mAppPackage);
        showCurrentCount.setText(String.format(getString(R.string.count_string_format), CountTools.getAllCount(getActivity(), mAppPackage), CountTools.getCurrentCount(getActivity(), mAppPackage)));

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
                int inputCount = Tools.StringToInt(((EditText) mDialog.findViewById(R.id.inputCount)).getText().toString());
                if (inputCount < CountTools.MinCount) {
                    Toast.makeText(getActivity(), R.string.fix_min_count, Toast.LENGTH_SHORT).show();
                    return;
                }

                CountTools.addPackageAllCount(getActivity(), mAppPackage, inputCount);
                startTask();
                mDialog.dismiss();
            }
        });
        ((ButtonFlat) mView.findViewById(R.id.mRemove)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountTools.removePackage(getActivity(), mAppPackage);
                startTask();
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }

    private void showExceedCountDialog(final Drawable mIcon, final String mAppName, final String mAppPackage) {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(getActivity());

        View mView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_exceed_package, null);

        ImageView AppImage = (ImageView) mView.findViewById(R.id.AppImage);
        TextView AppName = (TextView) mView.findViewById(R.id.AppName);
        TextView PackageName = (TextView) mView.findViewById(R.id.PackageName);
        TextView showCurrentCount = (TextView) mView.findViewById(R.id.showCurrentCount);

        AppImage.setImageDrawable(mIcon);
        AppName.setText(mAppName);
        PackageName.setText(mAppPackage);
        showCurrentCount.setText(String.format(getString(R.string.count_string_format), CountTools.getAllCount(getActivity(), mAppPackage), CountTools.getCurrentCount(getActivity(), mAppPackage)));

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

    /**
     * AppList Loading Task
     */
    private class AppTask extends AsyncTask<Void, Integer, Void> {

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

                for (int i = 0; i < mAppList.size(); i++) {

                    ResolveInfo app = mAppList.get(i);
                    addInfo = new AppInfo();

                    // App Icon
                    addInfo.mIcon = app.loadIcon(mPackageManager);
                    // App Name
                    addInfo.mAppName = app.loadLabel(mPackageManager).toString();
                    // App Package Name
                    addInfo.mAppPackage = app.activityInfo.packageName;

                    if (CountTools.isAddedCheck(getActivity(), app.activityInfo.packageName)) {
                        addInfo.isAdded = true;
                    }

                    if ("lee.whdghks913.only3".equals(app.activityInfo.packageName))
                        continue;

                    for (String launcherApp : mLauncherAppList) {
                        if (launcherApp.equals(app.activityInfo.packageName)) {
                            continue;
                        }
                    }

                    mAdapter.add(addInfo);
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

    @Override
    public void onResume() {
        super.onResume();

        startTask();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
