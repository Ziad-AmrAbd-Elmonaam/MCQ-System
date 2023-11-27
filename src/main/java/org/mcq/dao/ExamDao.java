package org.mcq.dao;

import org.mcq.database.DatabaseConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExamDao {

    private Connection connection;

    public ExamDao() {
        try {
            connection = DatabaseConnectionFactory.createDatabaseConnection();

        } catch (SQLException e) {
            e.printStackTrace();

        }
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
