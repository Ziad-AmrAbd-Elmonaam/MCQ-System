package org.example.model;

public class Answers {
    private  int id;
    private String title;
    private boolean is_correct;
    private int questionId;
    public Answers() {
    }

    public Answers(int id, String title, boolean is_correct, int questionId) {
        this.id = id;
        this.title = title;
        this.is_correct = is_correct;
        this.questionId = questionId;
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
        public boolean is_correct() {
            return is_correct;
        }
        public void setIs_correct(boolean is_correct) {
            this.is_correct = is_correct;
        }
        public boolean isCorrect() {
            return is_correct;
        }
        public int getQuestionId() {
            return questionId;
        }
        public void setQuestionId(int questionId) {
            this.questionId = questionId;
        }
        public void setIsCorrect(boolean is_correct) {
            this.is_correct = is_correct;
        }
}
