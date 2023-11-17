package org.example.Entities;

public class Exam {


    private int id;
    private String email;
    private String date;
    private String time;
    private int duration;
    private int totalMarks;

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


    public Exam(int id, String email, String date, String time, int duration, int totalMarks) {
        this.id = id;
        this.email = email;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.totalMarks = totalMarks;
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

}

