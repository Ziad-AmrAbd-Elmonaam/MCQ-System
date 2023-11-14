package org.example.dao;

import org.example.model.Answers;

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
    public void addAnswer(Answers answers)
    {
        String sql = "INSERT INTO answers (id, title, is_correct, question_id) VALUES (?, ?, ?, ?)";
        try(PreparedStatement statement = connection.prepareStatement(sql))
        {
            statement.setInt(1,answers.getId());
            statement.setString(2,answers.getTitle());
            statement.setBoolean(3,answers.is_correct());
            statement.setInt(4,answers.getQuestionId());
            statement.executeUpdate();
            System.out.println("Answer added to the database");

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public List<Answers> getAllAnswers()
    {
        List<Answers> answers = new ArrayList<>();
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
                Answers answer = new Answers(id,title,is_correct,questionId);
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

