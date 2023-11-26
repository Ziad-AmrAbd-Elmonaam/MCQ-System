package org.mcq.entities;

public class ExamQuestionAnswer {
    private  int id;
    private String title;
    private boolean isCorrect;
    private int examQuestionId;
    public ExamQuestionAnswer() {
    }

    public ExamQuestionAnswer(int id, String title, boolean isCorrect, int examQuestionId) {
        this.id = id;
        this.title = title;
        this.isCorrect = isCorrect;
        this.examQuestionId = examQuestionId;
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
        public void setCorrect(boolean correct) {
            this.isCorrect = correct;
        }
        public boolean isCorrectAnswer( int answerId) {
            return isCorrect;
        }

        public boolean isCorrect() {
            return isCorrect;
        }
        public int getExamQuestionId() {
            return examQuestionId;
        }
        public void setExamQuestionId(int examQuestionId) {
            this.examQuestionId = examQuestionId;
        }
        public void setIsCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }
}
