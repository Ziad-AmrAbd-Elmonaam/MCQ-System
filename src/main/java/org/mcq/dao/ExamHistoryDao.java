package org.mcq.dao;

import org.mcq.database.DatabaseConnectionFactory;
import org.mcq.entities.ExamHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExamHistoryDao {
    private final Connection connection;
    public ExamHistoryDao() {
        try {
            connection = DatabaseConnectionFactory.createDatabaseConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public ExamHistory getExamHistory(String email) {
        String sql = "SELECT eh.date, eh.id, eh.exam_id, eh.question_id, eh.answer_id, eh.mark, e.score " +
                "FROM exam_history eh " +
                "JOIN exams e ON eh.exam_id = e.examId " +
                "WHERE e.email = ?";

        ExamHistory examHistory = new ExamHistory();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                examHistory.addHistoryDetail(
                        rs.getString("date"),
                        rs.getInt("id"),
                        rs.getInt("exam_id"),
                        rs.getInt("question_id"),
                        rs.getInt("answer_id"),
                        rs.getInt("mark"),
                        rs.getInt("score")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return examHistory;
    }
        public void save(int examId, int questionId, int answerId, int mark) {
        String sql = "INSERT INTO exam_history (date, exam_id, question_id, answer_id, mark) " +
                "SELECT * FROM (SELECT ? AS date, ? AS exam_id, ? AS question_id, ? AS answer_id, ? AS mark) AS tmp " +
                "WHERE NOT EXISTS (SELECT 1 FROM exam_history WHERE question_id = ? AND exam_id =?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(java.time.LocalDate.now()));
            pstmt.setInt(2, examId);
            pstmt.setInt(3, questionId);
            pstmt.setInt(4, answerId);
            pstmt.setInt(5, mark);
            pstmt.setInt(6, questionId);
            pstmt.setInt(7, examId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
