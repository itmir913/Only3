package lee.whdghks913.only3.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;

import java.io.File;

import lee.whdghks913.only3.MainActivity;
import lee.whdghks913.only3.R;
import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.ldialogs.CustomListDialog;

public class SettingsFragment extends PreferenceFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * * ListPreference
     * appCheckDelay
     * appStartNotification
     * notificationType
     * * CheckBoxPreference
     * useVibrate
     * useTransparentIcon
     * * Preference
     * BackupRestore
     * openSource
     * ChangeLog
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);

        setOnPreferenceClick(findPreference(lee.whdghks913.only3.tools.Preference.BackupRestore));
        setOnPreferenceClick(findPreference(lee.whdghks913.only3.tools.Preference.openSource));
        setOnPreferenceClick(findPreference(lee.whdghks913.only3.tools.Preference.ChangeLog));

        setOnPreferenceChange(findPreference(lee.whdghks913.only3.tools.Preference.appCheckDelay));
        setOnPreferenceChange(findPreference(lee.whdghks913.only3.tools.Preference.appStartNotification));
        setOnPreferenceChange(findPreference(lee.whdghks913.only3.tools.Preference.notificationType));

        // App Version in Preference
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            findPreference(lee.whdghks913.only3.tools.Preference.appVersion).setSummary(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setOnPreferenceClick(Preference mPreference) {
        mPreference.setOnPreferenceClickListener(onPreferenceClickListener);
    }

    private Preference.OnPreferenceClickListener onPreferenceClickListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String getKey = preference.getKey();

            if (lee.whdghks913.only3.tools.Preference.openSource.equals(getKey)) {
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity(), R.string.license_title, android.R.string.ok);
                builder.content(getString(R.string.license_msg));
                CustomDialog customDialog = builder.build();
                customDialog.show();

            } else if (lee.whdghks913.only3.tools.Preference.ChangeLog.equals(getKey)) {
                CustomDialog.Builder builder = new CustomDialog.Builder(getActivity(), R.string.ChangeLog_title, android.R.string.ok);
                builder.content(getString(R.string.ChangeLog_msg));
                CustomDialog customDialog = builder.build();
                customDialog.show();

            } else if (lee.whdghks913.only3.tools.Preference.BackupRestore.equals(getKey)) {
                CustomListDialog.Builder builder = new CustomListDialog.Builder(getActivity(), getString(R.string.BackupAndRestore), getResources().getStringArray(R.array.BackupRestore));

                builder.darkTheme(false);
//                builder.titleColor(String hex);
//                builder.itemColor(String hex);
//                builder.titleTextSize(int size);
//                builder.itemTextSize(int size);
//                builder.rightToLeft(boolean rightToLeft);

                CustomListDialog customListDialog = builder.build();
                customListDialog.show();

                customListDialog.setListClickListener(new CustomListDialog.ListClickListener() {
                    @Override
                    public void onListItemSelected(int position, String[] strings, String s) {
                        String Path = Environment.getExternalStorageDirectory().toString() + "/Only3/";

                        // Backup
                        if (position == 0) {

                            File folder = new File(Path);
                            if (!folder.exists()) folder.mkdirs();

                            if (lee.whdghks913.only3.tools.Preference.saveSharedPreferencesToFile(getActivity(), new File(Path + "lee.whdghks913.only3_preferences.pref"), "lee.whdghks913.only3_preferences")) {
                                ToastTools.createToast(getActivity(), R.string.preference_backup_complete, true);
                            } else {
								ToastTools.createToast(getActivity(), R.string.preference_backup_fail, true);
                            }
                        }
                        // Restore
                        else if (position == 1) {

                            if (!new File(Path).exists()) {
								ToastTools.createToast(getActivity(), R.string.preference_restore_fail, true);
                                return;
                            }

                            if (lee.whdghks913.only3.tools.Preference.loadSharedPreferencesFromFile(getActivity(), new File(Path + "lee.whdghks913.only3_preferences.pref"), "lee.whdghks913.only3_preferences")) {
                                ToastTools.createToast(getActivity(), R.string.preference_restore_complete, true);

                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
                            } else {
								ToastTools.createToast(getActivity(), R.string.preference_restore_fail, true);
                            }
                        }
                    }
                });
            }

            return true;
        }
    };

    private void setOnPreferenceChange(Preference mPreference) {
        mPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);

        if (mPreference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) mPreference;
            int index = listPreference.findIndexOfValue(listPreference.getValue());
            mPreference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        }
//        else if (mPreference instanceof EditTextPreference) {
//            String values = ((EditTextPreference) mPreference).getText();
//            if (values == null) values = "";
//            onPreferenceChangeListener.onPreferenceChange(mPreference, values);
//        }
    }

    private Preference.OnPreferenceChangeListener onPreferenceChangeListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

//            if (preference instanceof EditTextPreference) preference.setSummary(stringValue);

            if (preference instanceof ListPreference) {
                /**
                 * ListPreference의 경우 stringValue가 entryValues이기 때문에 바로 Summary를
                 * 적용하지 못한다 따라서 설정한 entries에서 String을 로딩하여 적용한다
                 */
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }

            return true;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
