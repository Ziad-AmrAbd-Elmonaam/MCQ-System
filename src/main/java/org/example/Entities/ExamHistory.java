package org.example.Entities;

public class ExamHistory {
    private int examId;
    private int score;
    private String examDate;

    public ExamHistory() {
    }

    public ExamHistory(int examId, int score, String examDate) {
        this.examId = examId;
        this.score = score;
        this.examDate = examDate;
    }

    public int getExamId() {
        return examId;
    }



    public int getScore() {
        return score;
    }

    public String getExamDate() {
        return examDate;
    }



}
