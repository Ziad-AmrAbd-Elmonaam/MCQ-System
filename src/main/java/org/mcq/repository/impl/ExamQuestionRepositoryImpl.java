package org.mcq.repository.impl;

import org.mcq.database.DatabaseConnectionFactory;
import org.mcq.entities.ExamQuestion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExamQuestionRepositoryImpl implements org.mcq.repository.ExamQuestionRepository {
    private Connection connection;
    public ExamQuestionRepositoryImpl() {
        try {
            this.connection = DatabaseConnectionFactory.createDatabaseConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Could not establish database connection", e);
        }
    }
    @Override
    public List<ExamQuestion> getRandomQuestions(int limit) {
        // Implement the method to fetch random questions using JDBC
        // ...
        return new ArrayList<>(); // placeholder for actual implementation
    }



}
