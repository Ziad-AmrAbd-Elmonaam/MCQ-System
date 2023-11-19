package org.example.Entities;

import java.util.List;

public class Exam {


    private int id;
    private String email;
    private int score;
    private int examId;
    private String date;
    private String time;
    private int duration;
    private int totalMarks;
    private List<ExamQuestion> questions;

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", duration=" + duration +
                ", totalMarks=" + totalMarks +
                '}';
    }
    public Exam() {
    }


    public Exam(int id, String email, String date, String time, int duration, int totalMarks, List<ExamQuestion> questions) {
        this.id = id;
        this.email = email;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.totalMarks = totalMarks;
        this.questions = questions;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDuration(int i) {
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getDuration() {
        return duration;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setId(int anInt) {
        this.id = anInt;
    }

    public ExamQuestion[] getQuestions() {
        return new ExamQuestion[0];

    }

    public int getId() {
        return id;
    }
}

