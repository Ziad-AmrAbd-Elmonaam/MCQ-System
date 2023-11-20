package org.example.database;

import org.example.Entities.ExamHistory;
import org.example.Entities.ExamQuestion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ExamHistoryDao {
    private Connection connection;
    public ExamHistoryDao(Connection connection) {
        this.connection = connection;
    }
        public ExamHistory getExamHistory (String mail) {
            String sql = "SELECT eh.date, eh.id, eh.exam_id, eh.question_id, eh.answer_id, eh.mark, e.score " +
                    "FROM exam_history eh " +
                    "JOIN exams e ON eh.exam_id = e.examId " +
                    "WHERE e.email = ?";
              ExamHistory examHistory = new ExamHistory();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, mail);
                java.sql.ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                }

        } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return examHistory;
        }
        public void save(int examId, int questionId, int answerId, int mark) {
        String sql = "INSERT INTO exam_history (date, exam_id, question_id, answer_id, mark) " +
                "SELECT * FROM (SELECT ? AS date, ? AS exam_id, ? AS question_id, ? AS answer_id, ? AS mark) AS tmp " +
                "WHERE NOT EXISTS (SELECT 1 FROM exam_history WHERE question_id = ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(java.time.LocalDate.now()));
            pstmt.setInt(2, examId);
            pstmt.setInt(3, questionId);
            pstmt.setInt(4, answerId);
            pstmt.setInt(5, mark);
            pstmt.setInt(6, questionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
