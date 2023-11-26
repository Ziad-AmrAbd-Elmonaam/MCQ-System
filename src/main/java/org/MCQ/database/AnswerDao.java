package org.MCQ.database;

import org.MCQ.Entities.ExamQuestionAnswer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnswerDao {
    private Connection connection;
    public AnswerDao(Connection connection) {
        this.connection = connection;
        System.out.println("connection" + connection);
    }

    public boolean isAnswerCorrect(int questionId, int answerId)  {
        String sql = "SELECT is_correct FROM answers WHERE question_id = ? AND id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, questionId);
            statement.setInt(2, answerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("is_correct");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false; // Return false if no answer is found or if the answer is not correct
    }
    public void addAnswer(ExamQuestionAnswer examQuestionAnswer)
    {
        String sql = "INSERT INTO answers (id, title, is_correct, question_id) VALUES (?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1, examQuestionAnswer.getId());
            statement.setString(2, examQuestionAnswer.getTitle());
            statement.setBoolean(3, examQuestionAnswer.isCorrect());
            statement.setInt(4, examQuestionAnswer.getExamQuestionId());
            statement.executeUpdate();
            System.out.println("Answer added to the database");

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public List<ExamQuestionAnswer> getAllAnswers()
    {
        List<ExamQuestionAnswer> answers = new ArrayList<>();
        String sql = "SELECT * FROM answers";
        try (PreparedStatement statement=connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery())
        {
            while (rs.next())
            {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                boolean is_correct = rs.getBoolean("is_correct");
                int questionId = rs.getInt("question_id");
                ExamQuestionAnswer answer = new ExamQuestionAnswer(id,title,is_correct,questionId);
                answers.add(answer);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
            return answers;

        }
    }

