package com.example.trjwm.data;

public class MapPoint {
    private final String districtId;
    private final String title;
    private final String description;
    private final double latitude;
    private final double longitude;
    private final String pointType;

    public MapPoint(String districtId, String title, String description,
                    double latitude, double longitude, String pointType) {
        this.districtId = districtId;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointType = pointType;
    }

    public String getDistrictId() {
        return districtId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPointType() {
        return pointType;
    }
}
