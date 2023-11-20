package org.example.database;

import org.example.Entities.Exam;
import org.example.Entities.ExamQuestion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExamDao {

    private Connection connection;

    public ExamDao(Connection connection) {
        this.connection = connection;
    }

    public void save(String mail, int score, int examId) {

        String sql = "INSERT INTO exams (email, score, examId) " +
                "SELECT * FROM (SELECT ? AS email, ? AS score, ? AS examId) AS tmp " +
                "WHERE NOT EXISTS (SELECT 1 FROM exams WHERE email = ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, mail);
            pstmt.setInt(2, score);
            pstmt.setInt(3, examId);
            pstmt.setString(4, mail);


            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
