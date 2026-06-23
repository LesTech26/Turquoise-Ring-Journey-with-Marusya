package com.example.biruse_kolco.data.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_name")
    private String userName = "Путешественник";

    @ColumnInfo(name = "level")
    private int level = 1;

    @ColumnInfo(name = "points")
    private int points = 0;

    @ColumnInfo(name = "completed_districts")
    private int completedDistricts = 0;

    @ColumnInfo(name = "total_districts")
    private int totalDistricts = 24;

    @ColumnInfo(name = "sound_enabled")
    private boolean soundEnabled = true;

    @ColumnInfo(name = "avatar_id")
    private int avatarId = 0;

    @ColumnInfo(name = "total_score")
    private int totalScore = 0;

    @ColumnInfo(name = "games_played")
    private int gamesPlayed = 0;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getCompletedDistricts() { return completedDistricts; }
    public void setCompletedDistricts(int completedDistricts) { this.completedDistricts = completedDistricts; }

    public int getTotalDistricts() { return totalDistricts; }
    public void setTotalDistricts(int totalDistricts) { this.totalDistricts = totalDistricts; }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.soundEnabled = soundEnabled; }

    public int getAvatarId() { return avatarId; }
    public void setAvatarId(int avatarId) { this.avatarId = avatarId; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }
}