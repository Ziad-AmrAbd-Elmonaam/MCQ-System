package org.mcq.entities;

public class Exam {


    private int examId;
    private String email;
    private int score;





    @Override
    public String toString() {
        return "Exam{" +
                ",email='" + email + '\'' +
               ",score=" + score +
                ",examId=" + examId +
                '}';
    }
    public Exam() {
    }


    public Exam(String email, int score, int examId) {

        this.email = email;
        this.score = score;
        this.examId = examId;
    }

    public String getEmail() {
        return email;
    }



    public int getScore() {
        return score;
    }


public int setExamId(int examId) {
        return this.examId = examId;
    }
    public int getExamId() {
        return examId;
    }
}

