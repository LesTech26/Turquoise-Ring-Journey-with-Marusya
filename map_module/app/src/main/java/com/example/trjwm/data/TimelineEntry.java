package com.example.trjwm.data;

public class TimelineEntry {
    private final String period;
    private final String title;
    private final String description;

    public TimelineEntry(String period, String title, String description) {
        this.period = period;
        this.title = title;
        this.description = description;
    }

    public String getPeriod() { return period; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}
