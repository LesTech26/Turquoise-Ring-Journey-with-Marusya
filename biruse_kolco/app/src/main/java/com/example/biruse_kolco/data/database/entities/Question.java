package com.example.biruse_kolco.data.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "questions")
public class Question {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "district_id")
    private int districtId;

    @ColumnInfo(name = "question_text")
    private String questionText;

    @ColumnInfo(name = "option_a")
    private String optionA;

    @ColumnInfo(name = "option_b")
    private String optionB;

    @ColumnInfo(name = "option_c")
    private String optionC;

    @ColumnInfo(name = "correct_answer")
    private int correctAnswer; // 1, 2 или 3

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDistrictId() { return districtId; }
    public void setDistrictId(int districtId) { this.districtId = districtId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}