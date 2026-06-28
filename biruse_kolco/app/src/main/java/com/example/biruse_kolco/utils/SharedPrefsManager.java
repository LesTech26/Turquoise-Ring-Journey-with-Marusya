package com.example.biruse_kolco.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsManager {

    private static final String PREF_NAME = "biruse_kolco_prefs";
    private static final String KEY_SOUND_ENABLED = "sound_enabled";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_TRAINING_ACKNOWLEDGED = "training_acknowledged";

    private static SharedPrefsManager instance;
    private final SharedPreferences prefs;

    private SharedPrefsManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefsManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefsManager(context);
        }
        return instance;
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true);
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, 1);
    }

    public void setUserId(int userId) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
    }

    public boolean isFirstLaunch() {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true);
    }

    public void setFirstLaunch(boolean firstLaunch) {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, firstLaunch).apply();
    }

    public boolean isTrainingAcknowledged() {
        return prefs.getBoolean(KEY_TRAINING_ACKNOWLEDGED, false);
    }

    public void setTrainingAcknowledged(boolean acknowledged) {
        prefs.edit().putBoolean(KEY_TRAINING_ACKNOWLEDGED, acknowledged).apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
