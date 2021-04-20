package com.farnek.locationpicker.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static android.content.Context.MODE_PRIVATE;
import static com.farnek.locationpicker.utility.AppConstants.SHARED_PREF_USER;

public class AppSharedPreference {

    public static void putString(Context context, PREF_KEY key, String value) {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key.KEY, value);
        editor.commit();
        editor.apply();
    }

    public static String getString(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(key.KEY, null);
    }

    public static void clearString(Context context, PREF_KEY key) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key.KEY);
        editor.clear();
        editor.commit();
    }

    public static SharedPreferences getAppPreferences(Context context) {

        return context.getSharedPreferences(SHARED_PREF_USER, MODE_PRIVATE);

    }

    public static enum PREF_KEY {

        LOG_IN("log_in"),
        DEVICE_TOKEN("device_token"),;

        public final String KEY;

        PREF_KEY(String key) {

            this.KEY = key;
        }
    }

}
