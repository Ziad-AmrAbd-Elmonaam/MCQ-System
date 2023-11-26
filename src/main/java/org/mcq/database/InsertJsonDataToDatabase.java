package org.mcq.database;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class InsertJsonDataToDatabase {
    public static void main(String[] args) {
        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            return;
        }

        // Define the database connection URL
        String url = "jdbc:sqlite:database/database.db";  // Adjust the path as necessary

        try (Connection connection = DriverManager.getConnection(url)) {

            // Parse the provided JSON data
            String jsonData = readJsonFromFile("src/main/resources/question.json");
            System.out.println("jsonData = " + jsonData);


            JsonArray questionsArray = new JsonArray(jsonData);

            for (Object obj : questionsArray) {

                if (obj instanceof JsonObject questionJson) {

                    insertQuestion(connection, questionJson);

                }
            }

            System.out.println("Data inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void insertQuestion(Connection connection, JsonObject questionJson) throws SQLException {

        String insertQuestionSQL = "INSERT INTO questions ( title) VALUES (?)";
        String insertAnswerSQL = "INSERT INTO answers (title, is_correct, question_id) VALUES (?, ?, ?)";

        try (PreparedStatement questionStatement = connection.prepareStatement(insertQuestionSQL, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement answerStatement = connection.prepareStatement(insertAnswerSQL)) {

//            questionStatement.setInt(1, questionJson.getInteger("id"));
            questionStatement.setString(1, questionJson.getString("title"));
            questionStatement.executeUpdate();

            int questionId;
            try (var generatedKeys = questionStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    questionId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve the generated question ID.");
                }
            }

            JsonArray answersArray = questionJson.getJsonArray("answers");
            for (Object obj : answersArray) {
                if (obj instanceof JsonObject) {
                    JsonObject answerJson = (JsonObject) obj;

                    answerStatement.setString(1, answerJson.getString("title"));
                    Boolean isCorrect = answerJson.getBoolean("isCorrect");

                    if (isCorrect == null) {
                        // Handle the case where is_correct is not provided or is null.
                        // For example, you could set a default value or throw an exception.
                        isCorrect = false; // Setting a default value, for example.
                    }
                    answerStatement.setBoolean(2, isCorrect);

                    answerStatement.setInt(3, questionId);
                    answerStatement.executeUpdate();
                }
            }
        }
    }
    private static String readJsonFromFile(String filename) {
        try {
            Path filePath = Path.of(filename);
            System.out.println("Looking for file at: " + filePath.toAbsolutePath());
            byte[] jsonData = Files.readAllBytes(filePath);
            return new String(jsonData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON file: " + e.getMessage(), e);
        }
    }

    public static class DatabaseInitializer {



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
}
