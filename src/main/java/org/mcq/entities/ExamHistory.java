package org.mcq.entities;

import java.util.ArrayList;
import java.util.List;

public class ExamHistory {
    private int examId;
    private int score;
    private String examDate; // Changed to Date type to match with addHistoryDetail
    private List<ExamHistoryDetail> details;

    // Use only one constructor for default initialization
    public ExamHistory() {
        this.details = new ArrayList<>();
    }

    // Constructor for initializing with specific values
    public ExamHistory(int examId, int score, String examDate) {
        this.examId = examId;
        this.score = score;
        this.examDate = examDate;
        this.details = new ArrayList<>(); // Initialize details list here as well
    }

    // Getters and setters
    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public List<ExamHistoryDetail> getDetails() {
        return details;
    }

    // Method to add a detail to the list
    public void addHistoryDetail(String date, int id, int examId, int questionId, int answerId, int mark, int score) {
        if (this.details.isEmpty()) {
            this.examId = examId;
            this.examDate = date;
        } this.score += mark;

        // Now, add the detail
        ExamHistoryDetail detail = new ExamHistoryDetail(date, id, examId, questionId, answerId, mark);
        this.details.add(detail);
    }
}
