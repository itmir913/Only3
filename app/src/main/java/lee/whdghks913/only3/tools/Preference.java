package lee.whdghks913.only3.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class Preference {
    public static final String appCheckDelay = "appCheckDelay";
    public static final String appStartNotification = "appStartNotification";
    public static final String notificationType = "notificationType";

    public static final String useVibrate = "useVibrate";
    public static final String useTransparentIcon = "useTransparentIcon";

    public static final String BackupRestore = "BackupRestore";
    public static final String openSource = "openSource";
    public static final String ChangeLog = "ChangeLog";
    public static final String appVersion = "appVersion";

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public Preference(Context mContext) {
        mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mPref.edit();
    }

    public Preference(Context mContext, String prefName) {
        mPref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mPref.getBoolean(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mPref.getInt(key, defValue);
    }

    public String getString(String key, String defValue) {
        return mPref.getString(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value).commit();
    }

    public void putInt(String key, int value) {
        mEditor.putInt(key, value).commit();
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value).commit();
    }

    public void clear() {
        mEditor.clear().commit();
    }

    public void remove(String key) {
        mEditor.remove(key).commit();
    }

    /**
     * Preference Backup Restore
     * http://stackoverflow.com/questions/10864462/how-can-i-backup-sharedpreferences-to-sd-card
     */
    public static boolean saveSharedPreferencesToFile(Context mContext, File dst, String prefName) {
        boolean res = false;
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(dst));
            SharedPreferences pref = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
            output.writeObject(pref.getAll());

            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }

    public static boolean loadSharedPreferencesFromFile(Context mContext, File src, String prefName) {
        boolean res = false;
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(src));
            SharedPreferences.Editor prefEdit = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit();
            prefEdit.clear();
            Map<String, ?> entries = (Map<String, ?>) input.readObject();
            for (Map.Entry<String, ?> entry : entries.entrySet()) {
                Object v = entry.getValue();
                String key = entry.getKey();

                if (v instanceof Boolean)
                    prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
                else if (v instanceof Float)
                    prefEdit.putFloat(key, ((Float) v).floatValue());
                else if (v instanceof Integer)
                    prefEdit.putInt(key, ((Integer) v).intValue());
                else if (v instanceof Long)
                    prefEdit.putLong(key, ((Long) v).longValue());
                else if (v instanceof String)
                    prefEdit.putString(key, ((String) v));
            }
            prefEdit.commit();
            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }
}