package com.example.biruse_kolco.data.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.biruse_kolco.data.database.entities.Question;

import java.util.List;

@Dao
public interface QuestionDao {

    @Insert
    void insertQuestion(Question question);

    @Insert
    void insertAllQuestions(List<Question> questions);

    @Update
    void updateQuestion(Question question);

    @Query("SELECT * FROM questions WHERE district_id = :districtId")
    List<Question> getQuestionsByDistrict(int districtId);

    @Query("SELECT * FROM questions")
    List<Question> getAllQuestions();

    // ============================================================
    // ДОБАВЛЕННЫЙ МЕТОД ДЛЯ АДМИН-ПАНЕЛИ
    // ============================================================
    @Query("DELETE FROM questions")
    void deleteAllQuestions();
}