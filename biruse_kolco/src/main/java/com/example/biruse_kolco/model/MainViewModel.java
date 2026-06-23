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
            // Создаём пользователя если нет
            User currentUser = database.userDao().getUser();
            if (currentUser == null) {
                currentUser = new User();
                database.userDao().insertUser(currentUser);
                currentUser = database.userDao().getUser();
            }
            user.postValue(currentUser);
            prefsManager.setUserId(currentUser.getId());

            // Проверяем районы
            List<District> districtList = database.districtDao().getAllDistricts();
            if (districtList.isEmpty()) {
                insertAllDistricts();
                districtList = database.districtDao().getAllDistricts();
            }
            districts.postValue(districtList);

            // Проверяем достижения
            List<Achievement> achievementList = database.achievementDao().getAllAchievements();
            if (achievementList.isEmpty()) {
                insertAllAchievements();
                achievementList = database.achievementDao().getAllAchievements();
            }
            achievements.postValue(achievementList);

            // Обновляем прогресс
            updateProgress();
        });
    }

    private void insertAllDistricts() {
        String[][] districtsData = {
                {"Болховский район", "Болховский район - один из древнейших районов Орловской области", "Основан в XVI веке..."},
                {"Верховский район", "Верховский район - край лесов и полей", "История района начинается с XVII века..."},
                {"Глазуновский район", "Глазуновский район - живописный край", "Основан в XVIII веке..."},
                {"Дмитровский район", "Дмитровский район - земля героев", "Богатая история..."},
                {"Должанский район", "Должанский район - степной край", "Основан в XVIII веке..."},
                {"Залегощенский район", "Залегощенский район - край рек и озер", "История с XVII века..."},
                {"Знаменский район", "Знаменский район - культурный центр", "Богатое культурное наследие..."},
                {"Колпнянский район", "Колпнянский район - аграрный край", "Основан в XVIII веке..."},
                {"Корсаковский район", "Корсаковский район - край лесов", "История с XVII века..."},
                {"Краснозоренский район", "Краснозоренский район - край полей", "Основан в начале XX века..."},
                {"Кромской район", "Кромской район - исторический край", "Богатая история с древних времен..."},
                {"Ливенский район", "Ливенский район - крупнейший район", "История с XVI века..."},
                {"Малоархангельский район", "Малоархангельский район - край церквей", "Богатое духовное наследие..."},
                {"Мценский район", "Мценский район - древний край", "История с XII века..."},
                {"Новодеревеньковский район", "Новодеревеньковский район - край традиций", "Основан в XVIII веке..."},
                {"Новосильский район", "Новосильский район - исторический центр", "Богатая история..."},
                {"Орловский район", "Орловский район - центральный район", "История с основания Орла..."},
                {"Покровский район", "Покровский район - край храмов", "Богатое культурное наследие..."},
                {"Свердловский район", "Свердловский район - промышленный край", "История с XVIII века..."},
                {"Сосковский район", "Сосковский район - край лесов и рек", "Основан в XIX веке..."},
                {"Троснянский район", "Троснянский район - край озер", "История с XVII века..."},
                {"Урицкий район", "Урицкий район - край героев", "Богатая история..."},
                {"Хотынецкий район", "Хотынецкий район - национальный парк", "Уникальная природа..."},
                {"Шаблыкинский район", "Шаблыкинский район - край полей и лесов", "Основан в XVIII веке..."}
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

            updateProgress();
        });
    }
}