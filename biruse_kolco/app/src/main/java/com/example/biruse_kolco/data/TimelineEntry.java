package com.example.biruse_kolco.data;

public class TimelineEntry {
    private final String period;
    private final String title;
    private final String shortDescription;
    private final String fullDescription;

    public TimelineEntry(String period, String title, String shortDescription, String fullDescription) {
        this.period = period;
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
    }

    public String getPeriod() { return period; }
    public String getTitle() { return title; }
    public String getShortDescription() { return shortDescription; }
    public String getFullDescription() { return fullDescription; }
}
