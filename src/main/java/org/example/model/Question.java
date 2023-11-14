package org.example.model;

import java.util.List;

public class Question {
    private int id;
    private String title;

    public Question() {
    }


    // Parameterized constructor
    public Question(int id, String title) {
        this.id = id;
        this.title = title;
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



}
