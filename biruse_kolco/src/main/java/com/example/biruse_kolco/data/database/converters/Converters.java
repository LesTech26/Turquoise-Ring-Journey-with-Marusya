package com.example.biruse_kolco.data.database.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromStringList(List<String> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<String> toStringList(String data) {
        if (data == null) {
            return null;
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(data, type);
    }

    @TypeConverter
    public static String fromStringArray(String[] array) {
        return gson.toJson(array);
    }

    @TypeConverter
    public static String[] toStringArray(String data) {
        if (data == null) {
            return null;
        }
        Type type = new TypeToken<String[]>() {}.getType();
        return gson.fromJson(data, type);
    }
}