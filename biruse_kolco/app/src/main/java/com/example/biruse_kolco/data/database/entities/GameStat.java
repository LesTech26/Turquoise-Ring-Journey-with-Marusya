package com.example.biruse_kolco.data.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_stats")
public class GameStat {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "game_type")
    private String gameType;

    @ColumnInfo(name = "score")
    private int score;

    @ColumnInfo(name = "high_score")
    private int highScore;

    @ColumnInfo(name = "games_played")
    private int gamesPlayed;

    @ColumnInfo(name = "games_won")
    private int gamesWon;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getGameType() { return gameType; }
    public void setGameType(String gameType) { this.gameType = gameType; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public int getGamesWon() { return gamesWon; }
    public void setGamesWon(int gamesWon) { this.gamesWon = gamesWon; }
}