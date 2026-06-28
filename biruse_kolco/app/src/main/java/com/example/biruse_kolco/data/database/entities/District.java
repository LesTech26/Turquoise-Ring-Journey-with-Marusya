package com.example.biruse_kolco.data.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "districts")
public class District {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "name_with_accent")
    private String nameWithAccent;

    @ColumnInfo(name = "short_history")
    private String shortHistory;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "coat_of_arms")
    private String coatOfArms;

    @ColumnInfo(name = "is_completed")
    private boolean isCompleted;

    @ColumnInfo(name = "order_index")
    private int orderIndex;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "interesting_facts")
    private String interestingFacts;

    @ColumnInfo(name = "history_timeline")
    private String historyTimeline;

    @ColumnInfo(name = "costume_description")
    private String costumeDescription;

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameWithAccent() { return nameWithAccent; }
    public void setNameWithAccent(String nameWithAccent) { this.nameWithAccent = nameWithAccent; }

    public String getShortHistory() { return shortHistory; }
    public void setShortHistory(String shortHistory) { this.shortHistory = shortHistory; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoatOfArms() { return coatOfArms; }
    public void setCoatOfArms(String coatOfArms) { this.coatOfArms = coatOfArms; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getInterestingFacts() { return interestingFacts; }
    public void setInterestingFacts(String interestingFacts) { this.interestingFacts = interestingFacts; }

    public String getHistoryTimeline() { return historyTimeline; }
    public void setHistoryTimeline(String historyTimeline) { this.historyTimeline = historyTimeline; }

    public String getCostumeDescription() { return costumeDescription; }
    public void setCostumeDescription(String costumeDescription) { this.costumeDescription = costumeDescription; }
}