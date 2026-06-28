package com.example.biruse_kolco.data.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "achievements")
public class Achievement {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "icon")
    private String icon;

    @ColumnInfo(name = "is_unlocked")
    private boolean isUnlocked;

    @ColumnInfo(name = "requirement")
    private int requirement;

    @ColumnInfo(name = "progress")
    private int progress;

    @ColumnInfo(name = "category")
    private String category; // "districts", "games", "costumes"

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }

    public int getRequirement() { return requirement; }
    public void setRequirement(int requirement) { this.requirement = requirement; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}