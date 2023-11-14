package org.example.dao;

import org.example.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDao {
    private Connection connection;


    // Constructor that sets the connection
    public QuestionDao(Connection connection) {
        this.connection = connection;
        System.out.println("connection" + connection);
    }
    public void addQuestion(Question question) {
        String sql = "INSERT INTO questions (id, title) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set the id and title of the question
            statement.setInt(1, question.getId());
            statement.setString(2, question.getTitle());

            // Execute the update
            statement.executeUpdate();
            System.out.println("Question added to the database");
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
                String title = rs.getString("title");

                Question question = new Question(id, title);
                questions.add(question);
            }
        } catch (SQLException e) {
            // Handle the exception more gracefully, log it, or rethrow as needed
            e.printStackTrace();
        }
        return questions;
    }



}
