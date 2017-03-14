package com.chatitze.android.sinema.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.chatitze.android.sinema.R;


/**
 * Created by chatitze on 13/03/2017.
 */

public class SinemaPreferences {

    private static final boolean DEFAULT_IMAGE_WIFI_ONLY = true;


    /**
     * Returns the image download option set in Preferences. The default of downloading
     * images through wifi only option is "true"
     *
     * @param context Context used to get the SharedPreferences
     * @return true or false depends on the images through wifi only option is selected or not.
     */
    public static boolean isPreferredImageDownload_WifiOnly(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForWifiOnly = context.getString(R.string.pref_wifi_key);
        boolean defaultWifiOnly = context.getResources().getBoolean(R.bool.pref_wifi_default);
        return sharedPreferences.getBoolean(keyForWifiOnly, defaultWifiOnly);
    }

    private static boolean getDefaultImageDownloadOption() {
        return DEFAULT_IMAGE_WIFI_ONLY;
    }
}
