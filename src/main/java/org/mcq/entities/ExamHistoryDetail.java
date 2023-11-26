package org.mcq.entities;

public class ExamHistoryDetail {
    private String date;
    private int id;
    private int examId;
    private int questionId;
    private int answerId;
    private int mark;
//    private int score;

    // Constructor
    public ExamHistoryDetail(String date, int id, int examId, int questionId, int answerId, int mark) {
        this.date = date;
        this.id = id;
        this.examId = examId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.mark = mark;
//        this.score = score;
    }

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

//    public int getScore() {
//        return score;
//    }

//    public void setScore(int score) {
//        this.score = score;
//    }
}
