package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


    public class DatabaseInitializer {



        public static void generateDatabase() {
            String url = "jdbc:sqlite:database/database.db";
            String sqlCreateExams = "CREATE TABLE IF NOT EXISTS exams (" +
                    "id INTEGER PRIMARY KEY," +
                    "email TEXT NOT NULL," +
                    "date TEXT NOT NULL," +
                    "time TEXT NOT NULL," +
                    "duration INTEGER NOT NULL," +
                    "total_marks INTEGER NOT NULL);";

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
                stmt.execute(sqlCreateQuestions);
                stmt.execute(sqlCreateAnswers);
                stmt.execute(sqlCreateExams);
                System.out.println("Database created successfully");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
