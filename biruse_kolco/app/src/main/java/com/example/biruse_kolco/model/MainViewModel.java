package com.example.biruse_kolco.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
    private final MutableLiveData<Boolean> isTrainingComplete = new MutableLiveData<>(false);

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
    public LiveData<Boolean> getIsTrainingComplete() { return isTrainingComplete; }
    public boolean isTrainingAcknowledged() { return prefsManager.isTrainingAcknowledged(); }

    private void loadData() {
        executor.execute(() -> {
            User currentUser = database.userDao().getUser();
            if (currentUser == null) {
                currentUser = new User();
                currentUser.setTotalDistricts(5);
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
        String[][] districtsData = {
                {"Хотынецкий район", "Хотынецкий район - край национального парка Орловское Полесье", "Уникальная природа и история..."},
                {"Болховский район", "Болховский район - один из древнейших районов Орловской области", "Основан в XVI веке..."},
                {"Мценский район", "Мценский район - древний край с богатой историей", "История с XII века..."},
                {"Дмитровский район", "Дмитровский район - самый лесной район области", "Богатая история..."},
                {"Троснянский район", "Троснянский район - южный край области", "История с XVII века..."}
        };

        for (int i = 0; i < districtsData.length; i++) {
            District district = new District();
            district.setName(districtsData[i][0]);
            district.setNameWithAccent(districtsData[i][0]);
            district.setDescription(districtsData[i][1] + "\n\n🔨 TODO: Для Разработчика 2 - добавить полное описание");
            district.setShortHistory(districtsData[i][2] + "\n\n🔨 TODO: Для Разработчика 2 - добавить полную историю");
            district.setCoatOfArms("");
            district.setCompleted(false);
            district.setOrderIndex(i + 1);
            district.setImageUrl("");
            district.setInterestingFacts("🔨 TODO: Для Разработчика 2 - добавить интересные факты");
            district.setHistoryTimeline("🔨 TODO: Для Разработчика 2 - добавить временную ленту");
            district.setCostumeDescription("🔨 TODO: Для Разработчика 2 - добавить описание костюма");
            database.districtDao().insertDistrict(district);
        }
    }

    private void insertAllAchievements() {
        String[][] achievementsData = {
                {"🏆", "Первый шаг", "Изучи первый район", "1"},
                {"🌟", "Юный исследователь", "Изучи 3 района", "3"},
                {"👑", "Хранитель традиций", "Изучи все 5 районов", "5"}
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
                currentUser.setTotalDistricts(5);
                int level = (completed / 2) + 1;
                currentUser.setLevel(level);
                database.userDao().updateUser(currentUser);
                user.postValue(currentUser);
            }

            District next = database.districtDao().getNextIncompleteDistrict();
            nextDistrict.postValue(next);

            boolean complete = completed >= 5;
            isTrainingComplete.postValue(complete);
            if (!complete && prefsManager.isTrainingAcknowledged()) {
                prefsManager.setTrainingAcknowledged(false);
            }

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
        });
    }

    public void completeDistrictByName(String districtName) {
        executor.execute(() -> {
            List<District> allDistricts = database.districtDao().getAllDistricts();
            for (District d : allDistricts) {
                if (d.getName().equals(districtName)) {
                    database.districtDao().completeDistrict(d.getId());
                    database.userDao().addPoints(10);
                    break;
                }
            }
            updateProgress();
        });
    }

    public void uncompleteDistrictByName(String districtName) {
        executor.execute(() -> {
            List<District> allDistricts = database.districtDao().getAllDistricts();
            for (District d : allDistricts) {
                if (d.getName().equals(districtName)) {
                    d.setCompleted(false);
                    database.districtDao().updateDistrict(d);
                    break;
                }
            }
            prefsManager.setTrainingAcknowledged(false);
            updateProgress();
        });
    }

    public void acknowledgeTrainingComplete() {
        prefsManager.setTrainingAcknowledged(true);
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
                currentUser.setTotalDistricts(5);
                database.userDao().updateUser(currentUser);
            }

            List<Achievement> allAchievements = database.achievementDao().getAllAchievements();
            for (Achievement ach : allAchievements) {
                ach.setUnlocked(false);
                ach.setProgress(0);
                database.achievementDao().updateAchievement(ach);
            }

            updateProgress();
        });
    }
}
