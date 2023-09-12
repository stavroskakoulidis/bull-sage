package com.example.bullsage.models;

public class LeaderBoard {
    private String name;
    private String image;
    private int correctAnswers;
    private int totalQuizzes;
    private float weightedScore;


    public LeaderBoard() {
    }

    public LeaderBoard(String name, String image, int correctAnswers, int totalQuizzes, float weightedScore) {
        this.name = name;
        this.image = image;
        this.correctAnswers = correctAnswers;
        this.totalQuizzes = totalQuizzes;
        this.weightedScore = weightedScore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(int totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public float getWeightedScore() {
        return weightedScore;
    }

    public void setWeightedScore(float weightedScore) {
        this.weightedScore = weightedScore;
    }
}
