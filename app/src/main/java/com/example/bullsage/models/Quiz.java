package com.example.bullsage.models;

import org.json.JSONArray;

public class Quiz {
    private String question;
    private String correctAnswer;
    private JSONArray wrongAnswers;

    public Quiz() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public JSONArray getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(JSONArray wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }
}
