package com.example.biruse_kolco.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.biruse_kolco.data.database.entities.User;

@Dao
public interface UserDao {

    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM users LIMIT 1")
    User getUser();

    @Query("UPDATE users SET completed_districts = :count")
    void updateCompletedDistricts(int count);

    @Query("UPDATE users SET points = points + :points")
    void addPoints(int points);

    @Query("UPDATE users SET level = :level")
    void updateLevel(int level);

    @Query("UPDATE users SET total_score = total_score + :score")
    void addTotalScore(int score);

    @Query("UPDATE users SET games_played = games_played + 1")
    void incrementGamesPlayed();
}