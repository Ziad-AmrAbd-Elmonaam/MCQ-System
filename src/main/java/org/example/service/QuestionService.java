package org.example.service;

import io.vertx.core.json.Json;
import org.example.Entities.Exam;
import org.example.Entities.ExamQuestion;
import org.example.database.ExamDao;
import org.example.database.ExamHistoryDao;
import org.example.database.QuestionDao;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class QuestionService {

    private final QuestionDao questionDao;
    private final Jedis jedis;
    private final ExamHistoryDao examHistoryDao;

    public QuestionService(QuestionDao questionDao, Jedis jedis, ExamDao examDao, ExamHistoryDao examHistoryDao) {
        this.questionDao = questionDao;
        this.jedis = jedis;
        this.examDao = examDao;
        this.examHistoryDao = examHistoryDao;


    }
    private final ExamDao examDao;

//    public List<ExamQuestion> getAllQuestions() {
//        return questionDao.getAllQuestions();
//    }



    public String getRandomQuestions(String email) {
        // Attempt to retrieve data from Redis
        String redisData;
        try {
            redisData = jedis.get(email);
            if (redisData != null && !redisData.isEmpty()) {
                return redisData; // Return data if found in Redis
            }
        } catch (Exception e) {
            System.err.println("Error retrieving data from Redis: " + e.getMessage());
            // Optional: Consider re-throwing the exception or handling it as per your application's requirements
        }

        // Fetch questions from the database if not found in Redis
        List<ExamQuestion> examQuestions = questionDao.getRandomQuestions(10);
        if (examQuestions.isEmpty()) {
            // Handle the case where there are no questions
            // For example, return a specific message or throw an exception
            return "No questions available"; // Placeholder message
        }

        // Save the fetched questions in Redis and return the JSON representation
        try {
            String jsonExamQuestions = Json.encode(examQuestions);
            jedis.set(email, jsonExamQuestions);
            return jsonExamQuestions;
        } catch (Exception e) {
            System.err.println("Error storing data in Redis: " + e.getMessage());
            // Optional: Consider re-throwing the exception or handling it as per your application's requirements
            return "Error processing request"; // Placeholder error message
        }
    }







}
