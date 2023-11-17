package org.example.database;

import org.example.Entities.Exam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExamDao {

    private Connection connection;

    public ExamDao(Connection connection) {
        this.connection = connection;
    }

    public void save(Exam exam) {
        System.out.println("Saving exam: " + exam);
        String sql = "INSERT INTO exams (email, date, time, duration, total_marks) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, exam.getEmail());
            pstmt.setString(2, exam.getDate());
            pstmt.setString(3, exam.getTime());
            pstmt.setInt(4, exam.getDuration());
            pstmt.setInt(5, exam.getTotalMarks());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
