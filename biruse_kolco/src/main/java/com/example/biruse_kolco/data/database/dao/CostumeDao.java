package com.example.biruse_kolco.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.biruse_kolco.data.database.entities.Costume;

import java.util.List;

@Dao
public interface CostumeDao {

    @Insert
    void insertCostume(Costume costume);

    @Insert
    void insertAllCostumes(List<Costume> costumes);

    @Update
    void updateCostume(Costume costume);

    @Query("SELECT * FROM costumes WHERE district_id = :districtId")
    List<Costume> getCostumesByDistrict(int districtId);

    @Query("SELECT * FROM costumes")
    List<Costume> getAllCostumes();

    // TODO: Для Разработчика 3 - добавить методы для переодевания
}