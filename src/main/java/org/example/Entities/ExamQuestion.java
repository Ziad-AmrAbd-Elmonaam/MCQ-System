package org.example.Entities;

import java.util.ArrayList;
import java.util.List;

public class ExamQuestion {
    private int questionId;
    public int examId;
    private int attempts;
    private int mark;
    private String title;
    private List<ExamQuestionAnswer> answers;





    // Parameterized constructor
    public ExamQuestion(int id, String title, int examId, int attempts, int mark) {
        this.questionId = id;
        this.title = title;
        this.answers = new ArrayList<>();
        this.examId = examId;
        this.attempts = attempts;
        this.mark = mark;
    }

    public ExamQuestion() {
        this.answers = new ArrayList<>();
    }
    public ExamQuestion(int questionId, String questionTitle) {
        this.questionId = questionId;
        this.title = questionTitle;
        this.answers = new ArrayList<>();
    }


    public int getId() {
        return questionId;
    }

    public void setId(int id) {
        this.questionId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void addAnswer(ExamQuestionAnswer answer) {
        this.answers.add(answer);
    }

    // Getter for the answers list
    public List<ExamQuestionAnswer> getAnswers() {
        return answers;
    }

    // Setter for the answers list, if needed
    public void setAnswers(List<ExamQuestionAnswer> answers) {
        this.answers = answers;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }
    public int getAttempts() {
        return attempts;
    }
    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
    public int getMark() {
        return mark;
    }
    public void setMark(int mark) {
        this.mark = mark;
    }


    public void incrementAttempts() {
        this.attempts++;
    }
    public void addQuestionBackToPool ( int answerId) {
        this.answers = this.answers.stream().filter(answer -> answer.getId() != answerId).toList();
        this.attempts = 1;
    }





}
