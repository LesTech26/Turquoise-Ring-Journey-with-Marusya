package com.example.biruse_kolco.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.biruse_kolco.data.database.entities.Achievement;

import java.util.List;

@Dao
public interface AchievementDao {

    @Insert
    void insertAchievement(Achievement achievement);

    @Insert
    void insertAllAchievements(List<Achievement> achievements);

    @Update
    void updateAchievement(Achievement achievement);

    @Query("SELECT * FROM achievements ORDER BY requirement ASC")
    List<Achievement> getAllAchievements();

    @Query("SELECT * FROM achievements WHERE is_unlocked = 1")
    List<Achievement> getUnlockedAchievements();

    @Query("UPDATE achievements SET is_unlocked = 1, progress = :progress WHERE id = :achievementId")
    void unlockAchievement(int achievementId, int progress);
}