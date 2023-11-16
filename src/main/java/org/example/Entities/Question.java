package org.example.Entities;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private int id;
    private String title;
    private List<Answers> answers;
    public int attemps =0;

    public Question() {

        this.answers = new ArrayList<>();
    }


    // Parameterized constructor
    public Question(int id, String title) {
        this.id = id;
        this.title = title;
        this.answers = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void addAnswer(Answers answer) {
        this.answers.add(answer);
    }

    // Getter for the answers list
    public List<Answers> getAnswers() {
        return answers;
    }

    // Setter for the answers list, if needed
    public void setAnswers(List<Answers> answers) {
        this.answers = answers;
    }



}
