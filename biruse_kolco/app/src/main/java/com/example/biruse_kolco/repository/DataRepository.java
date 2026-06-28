package com.example.biruse_kolco.repository;

import android.content.Context;

import com.example.biruse_kolco.data.database.AppDatabase;
import com.example.biruse_kolco.data.database.entities.Achievement;
import com.example.biruse_kolco.data.database.entities.District;
import com.example.biruse_kolco.data.database.entities.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataRepository {

    private static DataRepository instance;
    private final AppDatabase database;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private DataRepository(Context context) {
        database = AppDatabase.getInstance(context);
    }

    public static synchronized DataRepository getInstance(Context context) {
        if (instance == null) {
            instance = new DataRepository(context);
        }
        return instance;
    }

    public void getUser(DataCallback<User> callback) {
        executor.execute(() -> {
            User user = database.userDao().getUser();
            if (callback != null) {
                callback.onResult(user);
            }
        });
    }

    public void getAllDistricts(DataCallback<List<District>> callback) {
        executor.execute(() -> {
            List<District> districts = database.districtDao().getAllDistricts();
            if (callback != null) {
                callback.onResult(districts);
            }
        });
    }

    public void getDistrictById(int id, DataCallback<District> callback) {
        executor.execute(() -> {
            District district = database.districtDao().getDistrictById(id);
            if (callback != null) {
                callback.onResult(district);
            }
        });
    }

    public void getNextDistrict(DataCallback<District> callback) {
        executor.execute(() -> {
            District district = database.districtDao().getNextIncompleteDistrict();
            if (callback != null) {
                callback.onResult(district);
            }
        });
    }

    public void getAchievements(DataCallback<List<Achievement>> callback) {
        executor.execute(() -> {
            List<Achievement> achievements = database.achievementDao().getAllAchievements();
            if (callback != null) {
                callback.onResult(achievements);
            }
        });
    }

    public void getCompletedCount(DataCallback<Integer> callback) {
        executor.execute(() -> {
            int count = database.districtDao().getCompletedCount();
            if (callback != null) {
                callback.onResult(count);
            }
        });
    }

    public void completeDistrict(int districtId, DataCallback<Void> callback) {
        executor.execute(() -> {
            database.districtDao().completeDistrict(districtId);
            database.userDao().addPoints(10);
            database.userDao().addTotalScore(10);
            updateAchievements();
            if (callback != null) {
                callback.onResult(null);
            }
        });
    }

    private void updateAchievements() {
        int completed = database.districtDao().getCompletedCount();
        List<Achievement> achievements = database.achievementDao().getAllAchievements();
        for (Achievement ach : achievements) {
            if (!ach.isUnlocked() && completed >= ach.getRequirement()) {
                ach.setUnlocked(true);
                ach.setProgress(completed);
                database.achievementDao().updateAchievement(ach);
            }
        }
    }

    public void resetProgress(DataCallback<Void> callback) {
        executor.execute(() -> {
            List<District> districts = database.districtDao().getAllDistricts();
            for (District district : districts) {
                district.setCompleted(false);
                database.districtDao().updateDistrict(district);
            }

            User user = database.userDao().getUser();
            if (user != null) {
                user.setCompletedDistricts(0);
                user.setPoints(0);
                user.setLevel(1);
                user.setTotalScore(0);
                database.userDao().updateUser(user);
            }

            List<Achievement> achievements = database.achievementDao().getAllAchievements();
            for (Achievement ach : achievements) {
                ach.setUnlocked(false);
                ach.setProgress(0);
                database.achievementDao().updateAchievement(ach);
            }

            if (callback != null) {
                callback.onResult(null);
            }
        });
    }

    public interface DataCallback<T> {
        void onResult(T result);
    }
}