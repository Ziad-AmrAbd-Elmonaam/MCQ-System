package org.mcq.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void generateDatabase() {
        String url = "jdbc:sqlite:database/database.db";
        String sqlCreateExams = "CREATE TABLE IF NOT EXISTS exams (" +
                "examId INTEGER NOT NULL," +
                "email TEXT NOT NULL," +
                "score INTEGER NOT NULL);";
        String sqlCreateExamHistory = "CREATE TABLE IF NOT EXISTS exam_history (" +
                "date TEXT NOT NULL," +
                "id INTEGER PRIMARY KEY," +
                "exam_id INTEGER NOT NULL," +
                "question_id INTEGER NOT NULL," +
                "answer_id INTEGER NOT NULL," +
                "mark INTEGER NOT NULL," +
                "FOREIGN KEY (exam_id) REFERENCES exams (examId)," +
                "FOREIGN KEY (question_id) REFERENCES questions (id)," +
                "FOREIGN KEY (answer_id) REFERENCES answers (id));";

        String sqlCreateQuestions = "CREATE TABLE IF NOT EXISTS questions (" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL);";

        String sqlCreateAnswers = "CREATE TABLE IF NOT EXISTS answers (" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "is_correct BOOLEAN NOT NULL," +
                "question_id INTEGER NOT NULL," +
                "FOREIGN KEY (question_id) REFERENCES questions (id));";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCreateExamHistory);
            stmt.execute(sqlCreateQuestions);
            stmt.execute(sqlCreateAnswers);
            stmt.execute(sqlCreateExams);
            System.out.println("Database created successfully");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
