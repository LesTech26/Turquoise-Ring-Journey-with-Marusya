package com.example.biruse_kolco.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.biruse_kolco.data.DistrictRepository;
import com.example.biruse_kolco.data.ProgressStore;
import com.example.biruse_kolco.data.database.AppDatabase;
import com.example.biruse_kolco.data.database.entities.Achievement;
import com.example.biruse_kolco.data.database.entities.District;
import com.example.biruse_kolco.data.database.entities.User;
import com.example.biruse_kolco.utils.SharedPrefsManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

    private final AppDatabase database;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final SharedPrefsManager prefsManager;

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<List<District>> districts = new MutableLiveData<>();
    private final MutableLiveData<List<Achievement>> achievements = new MutableLiveData<>();
    private final MutableLiveData<District> nextDistrict = new MutableLiveData<>();
    private final MutableLiveData<Integer> completedCount = new MutableLiveData<>(0);

    public MainViewModel(Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        prefsManager = SharedPrefsManager.getInstance(application);
        loadData();
    }

    public LiveData<User> getUser() { return user; }
    public LiveData<List<District>> getDistricts() { return districts; }
    public LiveData<List<Achievement>> getAchievements() { return achievements; }
    public LiveData<District> getNextDistrict() { return nextDistrict; }
    public LiveData<Integer> getCompletedCount() { return completedCount; }

    private void loadData() {
        executor.execute(() -> {
            User currentUser = database.userDao().getUser();
            if (currentUser == null) {
                currentUser = new User();
                database.userDao().insertUser(currentUser);
                currentUser = database.userDao().getUser();
            }
            user.postValue(currentUser);
            prefsManager.setUserId(currentUser.getId());

            List<District> districtList = database.districtDao().getAllDistricts();
            if (districtList.isEmpty()) {
                insertAllDistricts();
                districtList = database.districtDao().getAllDistricts();
            }
            districts.postValue(districtList);
            syncProgressStore(districtList);

            List<Achievement> achievementList = database.achievementDao().getAllAchievements();
            if (achievementList.isEmpty()) {
                insertAllAchievements();
                achievementList = database.achievementDao().getAllAchievements();
            }
            achievements.postValue(achievementList);

            updateProgress();
        });
    }

    private void insertAllDistricts() {
        List<com.example.biruse_kolco.data.DistrictItem> source = DistrictRepository.getDistricts();
        for (int i = 0; i < source.size(); i++) {
            com.example.biruse_kolco.data.DistrictItem item = source.get(i);
            District district = new District();
            district.setName(item.getName());
            district.setNameWithAccent(item.getName());
            district.setDescription(item.getSummary());
            district.setShortHistory(item.getHistory());
            district.setCoatOfArms(item.getCoatOfArms());
            district.setCompleted(false);
            district.setOrderIndex(i + 1);
            district.setImageUrl("");
            district.setInterestingFacts(joinFacts(item.getFacts()));
            district.setHistoryTimeline(item.getSubtitle());
            district.setCostumeDescription("Материалы медиатеки доступны в разделе района");
            database.districtDao().insertDistrict(district);
        }
    }

    private String joinFacts(List<String> facts) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < facts.size(); i++) {
            if (i > 0) {
                builder.append("\n\n");
            }
            builder.append(facts.get(i));
        }
        return builder.toString();
    }

    private void insertAllAchievements() {
        String[][] achievementsData = {
                {"🏆", "Первый шаг", "Изучи первый район", "1"},
                {"🌟", "Юный исследователь", "Изучи 5 районов", "5"},
                {"🎖️", "Знаток края", "Изучи 10 районов", "10"},
                {"👑", "Хранитель традиций", "Изучи все 24 района", "24"}
        };

        for (String[] data : achievementsData) {
            Achievement achievement = new Achievement();
            achievement.setIcon(data[0]);
            achievement.setTitle(data[1]);
            achievement.setDescription(data[2]);
            achievement.setRequirement(Integer.parseInt(data[3]));
            achievement.setUnlocked(false);
            achievement.setProgress(0);
            database.achievementDao().insertAchievement(achievement);
        }
    }

    public void updateProgress() {
        executor.execute(() -> {
            int completed = database.districtDao().getCompletedCount();
            completedCount.postValue(completed);

            User currentUser = database.userDao().getUser();
            if (currentUser != null) {
                currentUser.setCompletedDistricts(completed);
                int level = (completed / 5) + 1;
                currentUser.setLevel(level);
                database.userDao().updateUser(currentUser);
                user.postValue(currentUser);
            }

            District next = database.districtDao().getNextIncompleteDistrict();
            nextDistrict.postValue(next);

            List<Achievement> allAchievements = database.achievementDao().getAllAchievements();
            for (Achievement ach : allAchievements) {
                if (!ach.isUnlocked() && completed >= ach.getRequirement()) {
                    ach.setUnlocked(true);
                    ach.setProgress(completed);
                    database.achievementDao().updateAchievement(ach);
                }
            }
            achievements.postValue(database.achievementDao().getAllAchievements());
        });
    }

    public void completeDistrict(int districtId) {
        executor.execute(() -> {
            database.districtDao().completeDistrict(districtId);
            database.userDao().addPoints(10);
            updateProgress();
            syncProgressStore(database.districtDao().getAllDistricts());
        });
    }

    public void toggleSound(boolean enabled) {
        prefsManager.setSoundEnabled(enabled);
        User currentUser = user.getValue();
        if (currentUser != null) {
            currentUser.setSoundEnabled(enabled);
            executor.execute(() -> database.userDao().updateUser(currentUser));
        }
    }

    public void resetProgress() {
        executor.execute(() -> {
            List<District> allDistricts = database.districtDao().getAllDistricts();
            for (District district : allDistricts) {
                district.setCompleted(false);
                database.districtDao().updateDistrict(district);
            }

            User currentUser = database.userDao().getUser();
            if (currentUser != null) {
                currentUser.setCompletedDistricts(0);
                currentUser.setPoints(0);
                currentUser.setLevel(1);
                database.userDao().updateUser(currentUser);
            }

            List<Achievement> allAchievements = database.achievementDao().getAllAchievements();
            for (Achievement ach : allAchievements) {
                ach.setUnlocked(false);
                ach.setProgress(0);
                database.achievementDao().updateAchievement(ach);
            }

            syncProgressStore(allDistricts);
            updateProgress();
        });
    }

    private void syncProgressStore(List<District> districtList) {
        for (District district : districtList) {
            String districtKey = districtKeyForOrder(district.getOrderIndex());
            if (districtKey != null) {
                ProgressStore.setCompleted(getApplication(), districtKey, district.isCompleted());
            }
        }
    }

    private String districtKeyForOrder(int orderIndex) {
        List<com.example.biruse_kolco.data.DistrictItem> source = DistrictRepository.getDistricts();
        if (orderIndex <= 0 || orderIndex > source.size()) {
            return null;
        }
        return source.get(orderIndex - 1).getId();
    }
}
