package com.example.biruse_kolco.trjwm.data;

import android.content.Context;

import com.yandex.mapkit.geometry.Point;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MapDataCache {
    private static final String FILE_NAME = "map_data_cache_v4.json";

    private MapDataCache() {
    }

    public static CacheData load(Context context) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            return null;
        }

        try (FileInputStream input = new FileInputStream(file)) {
            byte[] bytes = input.readAllBytes();
            String json = new String(bytes, StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(json);
            return fromJson(root);
        } catch (Exception e) {
            return null;
        }
    }

    public static void save(Context context, CacheData data) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        try (FileOutputStream output = new FileOutputStream(file, false)) {
            output.write(toJson(data).toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    private static JSONObject toJson(CacheData data) throws Exception {
        JSONObject root = new JSONObject();

        JSONObject shapesJson = new JSONObject();
        for (Map.Entry<String, MapShape> entry : data.shapes.entrySet()) {
            JSONObject shapeJson = new JSONObject();
            JSONArray ringsJson = new JSONArray();
            for (List<Point> ring : entry.getValue().rings) {
                JSONArray ringJson = new JSONArray();
                for (Point point : ring) {
                    JSONArray pair = new JSONArray();
                    pair.put(point.getLatitude());
                    pair.put(point.getLongitude());
                    ringJson.put(pair);
                }
                ringsJson.put(ringJson);
            }
            shapeJson.put("rings", ringsJson);
            shapesJson.put(entry.getKey(), shapeJson);
        }

        JSONArray pointsJson = new JSONArray();
        for (MapPointEntry entry : data.points) {
            JSONObject pointJson = new JSONObject();
            pointJson.put("districtId", entry.districtId);
            pointJson.put("title", entry.title);
            pointJson.put("description", entry.description);
            pointJson.put("lat", entry.point.getLatitude());
            pointJson.put("lon", entry.point.getLongitude());
            pointJson.put("type", entry.type);
            pointsJson.put(pointJson);
        }

        root.put("shapes", shapesJson);
        root.put("points", pointsJson);
        return root;
    }

    private static CacheData fromJson(JSONObject root) throws Exception {
        CacheData data = new CacheData();
        JSONObject shapesJson = root.optJSONObject("shapes");
        if (shapesJson != null) {
            JSONArray names = shapesJson.names();
            for (int i = 0; names != null && i < names.length(); i++) {
                String key = names.getString(i);
                JSONObject shapeJson = shapesJson.getJSONObject(key);
                JSONArray ringsJson = shapeJson.getJSONArray("rings");
                MapShape shape = new MapShape();
                for (int ringIndex = 0; ringIndex < ringsJson.length(); ringIndex++) {
                    JSONArray ringJson = ringsJson.getJSONArray(ringIndex);
                    List<Point> ring = new ArrayList<>();
                    for (int pointIndex = 0; pointIndex < ringJson.length(); pointIndex++) {
                        JSONArray pair = ringJson.getJSONArray(pointIndex);
                        ring.add(new Point(pair.getDouble(0), pair.getDouble(1)));
                    }
                    shape.rings.add(ring);
                }
                data.shapes.put(key, shape);
            }
        }

        JSONArray pointsJson = root.optJSONArray("points");
        if (pointsJson != null) {
            for (int i = 0; i < pointsJson.length(); i++) {
                JSONObject pointJson = pointsJson.getJSONObject(i);
                data.points.add(new MapPointEntry(
                        pointJson.getString("districtId"),
                        pointJson.getString("title"),
                        pointJson.getString("description"),
                        new Point(pointJson.getDouble("lat"), pointJson.getDouble("lon")),
                        pointJson.optString("type", "district")
                ));
            }
        }

        return data;
    }

    public static final class CacheData {
        public final LinkedHashMap<String, MapShape> shapes = new LinkedHashMap<>();
        public final List<MapPointEntry> points = new ArrayList<>();
    }

    public static final class MapShape {
        public final List<List<Point>> rings = new ArrayList<>();
    }

    public static final class MapPointEntry {
        public final String districtId;
        public final String title;
        public final String description;
        public final Point point;
        public final String type;

        public MapPointEntry(String districtId, String title, String description, Point point, String type) {
            this.districtId = districtId;
            this.title = title;
            this.description = description;
            this.point = point;
            this.type = type;
        }
    }
}
