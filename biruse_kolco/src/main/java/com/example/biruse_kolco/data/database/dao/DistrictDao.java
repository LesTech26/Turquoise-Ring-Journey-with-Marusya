package com.example.biruse_kolco.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.biruse_kolco.data.database.entities.District;

import java.util.List;

@Dao
public interface DistrictDao {

    @Insert
    void insertDistrict(District district);

    @Insert
    void insertAllDistricts(List<District> districts);

    @Update
    void updateDistrict(District district);

    @Query("SELECT * FROM districts ORDER BY order_index ASC")
    List<District> getAllDistricts();

    @Query("SELECT * FROM districts WHERE is_completed = 0 ORDER BY order_index ASC LIMIT 1")
    District getNextIncompleteDistrict();

    @Query("SELECT COUNT(*) FROM districts WHERE is_completed = 1")
    int getCompletedCount();

    @Query("SELECT * FROM districts WHERE id = :districtId")
    District getDistrictById(int districtId);

    @Query("UPDATE districts SET is_completed = 1 WHERE id = :districtId")
    void completeDistrict(int districtId);

    @Query("SELECT * FROM districts WHERE is_completed = 1 ORDER BY order_index ASC")
    List<District> getCompletedDistricts();

    @Query("SELECT COUNT(*) FROM districts")
    int getTotalCount();

    // TODO: Для Разработчика 2 - добавить методы для поиска и фильтрации
}