package org.example.model;

import java.util.List;

public class Question {
    private int id;
    private String questionText;
    private List<String> choices;
    private String correctAnswer;

    // No-argument constructor
    public Question() {
    }

    // Parameterized constructor
    public Question(int id, String questionText, List<String> choices, String correctAnswer) {
        this.id = id;
        this.questionText = questionText;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    // Getters and setters for all fields
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

}
