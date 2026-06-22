package com.example.trjwm.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedHashSet;
import java.util.Set;

public final class ProgressStore {
    private static final String PREFS = "district_progress";
    private static final String KEY_COMPLETED = "completed_ids";

    private ProgressStore() {
    }

    public static Set<String> getCompletedIds(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        Set<String> values = prefs.getStringSet(KEY_COMPLETED, new LinkedHashSet<String>());
        return values == null ? new LinkedHashSet<String>() : new LinkedHashSet<>(values);
    }

    public static boolean isCompleted(Context context, String districtId) {
        return getCompletedIds(context).contains(districtId);
    }

    public static void setCompleted(Context context, String districtId, boolean completed) {
        LinkedHashSet<String> ids = new LinkedHashSet<>(getCompletedIds(context));
        if (completed) {
            ids.add(districtId);
        } else {
            ids.remove(districtId);
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putStringSet(KEY_COMPLETED, ids)
                .apply();
    }

    public static int getCompletedCount(Context context) {
        return getCompletedIds(context).size();
    }
}
