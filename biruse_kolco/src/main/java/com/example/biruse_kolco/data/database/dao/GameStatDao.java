package com.example.biruse_kolco.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.biruse_kolco.data.database.entities.GameStat;

import java.util.List;

@Dao
public interface GameStatDao {

    @Insert
    void insertGameStat(GameStat gameStat);

    @Update
    void updateGameStat(GameStat gameStat);

    @Query("SELECT * FROM game_stats WHERE user_id = :userId")
    List<GameStat> getGameStatsByUser(int userId);

    @Query("SELECT * FROM game_stats WHERE user_id = :userId AND game_type = :gameType")
    GameStat getGameStatByUserAndType(int userId, String gameType);

    @Query("UPDATE game_stats SET score = score + :points WHERE id = :statId")
    void addPoints(int statId, int points);

    // TODO: Для Разработчика 4 - добавить методы для статистики игр
}