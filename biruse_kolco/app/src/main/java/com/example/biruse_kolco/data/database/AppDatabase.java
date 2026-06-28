package com.example.biruse_kolco.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.biruse_kolco.data.database.converters.Converters;
import com.example.biruse_kolco.data.database.dao.AchievementDao;
import com.example.biruse_kolco.data.database.dao.CostumeDao;
import com.example.biruse_kolco.data.database.dao.DistrictDao;
import com.example.biruse_kolco.data.database.dao.GameStatDao;
import com.example.biruse_kolco.data.database.dao.QuestionDao;
import com.example.biruse_kolco.data.database.dao.UserDao;
import com.example.biruse_kolco.data.database.entities.Achievement;
import com.example.biruse_kolco.data.database.entities.Costume;
import com.example.biruse_kolco.data.database.entities.District;
import com.example.biruse_kolco.data.database.entities.GameStat;
import com.example.biruse_kolco.data.database.entities.Question;
import com.example.biruse_kolco.data.database.entities.User;

@Database(entities = {
        User.class,
        District.class,
        Costume.class,
        GameStat.class,
        Question.class,
        Achievement.class
}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract DistrictDao districtDao();
    public abstract CostumeDao costumeDao();
    public abstract GameStatDao gameStatDao();
    public abstract QuestionDao questionDao();
    public abstract AchievementDao achievementDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "biruse_kolco_db"
                    )
                    // УБИРАЕМ createFromAsset - БД создаётся пустой
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}