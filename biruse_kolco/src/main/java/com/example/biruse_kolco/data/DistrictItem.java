package com.example.biruse_kolco.data;

import java.util.List;

public class DistrictItem {
    private final String id;
    private final String name;
    private final String subtitle;
    private final String summary;
    private final String history;
    private final List<String> facts;
    private final List<String> photos;
    private final String coatOfArms;
    private final int accentColor;

    public DistrictItem(String id, String name, String subtitle, String summary,
                        String history, List<String> facts, List<String> photos,
                        String coatOfArms, int accentColor) {
        this.id = id;
        this.name = name;
        this.subtitle = subtitle;
        this.summary = summary;
        this.history = history;
        this.facts = facts;
        this.photos = photos;
        this.coatOfArms = coatOfArms;
        this.accentColor = accentColor;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSubtitle() { return subtitle; }
    public String getSummary() { return summary; }
    public String getHistory() { return history; }
    public List<String> getFacts() { return facts; }
    public List<String> getPhotos() { return photos; }
    public String getCoatOfArms() { return coatOfArms; }
    public int getAccentColor() { return accentColor; }
}
