package org.example.dao;

import org.example.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionDao {
    private Connection connection;


    // Constructor that sets the connection
    public QuestionDao(Connection connection) {
        this.connection = connection;
        System.out.println("connection" + connection);
    }
    public void addQuestion(Question question) {

        String sql = "INSERT INTO questions (id, question_text, choice_a, choice_b, choice_c, choice_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
// question (id,title)
//   answers (id,questionID, title,isRight)
//        user(id, name)
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, question.getId());
            statement.setString(2, question.getQuestionText());
            // Assuming choices is a List with exactly 4 elements
            List<String> choices = question.getChoices();
            statement.setString(3, choices.get(0));
            statement.setString(4, choices.get(1));
            statement.setString(5, choices.get(2));
            statement.setString(6, choices.get(3));
            statement.setString(7, question.getCorrectAnswer());
            System.out.println("dao works");

            statement.executeUpdate();
        } catch (SQLException e) {
            // Handle the exception more gracefully, log it, or rethrow as needed
            e.printStackTrace();
        }
    }

        // Method to retrieve all questions from the database
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String questionText = rs.getString("question_text");
                String correctAnswer = rs.getString("correct_answer");
                List<String> choices = Arrays.asList(
                        rs.getString("choice_a"),
                        rs.getString("choice_b"),
                        rs.getString("choice_c"),
                        rs.getString("choice_d")
                );

                Question question = new Question(id, questionText, choices, correctAnswer);
                questions.add(question);
            }
        } catch (SQLException e) {
            // Handle the exception more gracefully, log it, or rethrow as needed
            e.printStackTrace();
        }
        return questions;
    }


}
