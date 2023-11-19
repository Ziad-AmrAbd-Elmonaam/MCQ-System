package org.example.service;

import io.vertx.core.json.Json;
import org.example.Entities.Exam;
import org.example.Entities.ExamQuestion;
import org.example.database.ExamDao;
import org.example.database.QuestionDao;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class QuestionService {

//    public static Dictionary<String, List<ExamQuestion>> userQuestions;



    private final QuestionDao questionDao;
    private final Jedis jedis;

    public QuestionService(QuestionDao questionDao, Jedis jedis, ExamDao examDao) {
        this.questionDao = questionDao;
        this.jedis = jedis;
        this.examDao = examDao;


    }
    private final ExamDao examDao;

    public List<ExamQuestion> getAllQuestions() {
        return questionDao.getAllQuestions();
    }



    public String getRandomQuestions(String email) {
        String redisData = null;
        try {
            redisData = jedis.get(email);
        } catch (Exception e) {
            System.err.println("Error retrieving data from Redis: " + e.getMessage());
            // Handle the exception appropriately
        }

        if (redisData != null && !redisData.isEmpty()) {
            // Data exists in Redis, return it
            return redisData;
        } else {
            // Data doesn't exist in Redis, fetch from database and store in Redis
            List<ExamQuestion> examQuestions = questionDao.getRandomQuestions(10);
            if (examQuestions.isEmpty()) {
                // Handle the case where there are no questions
                // For example, return a specific message or throw an exception
            } else {
                // Save the fetched questions in Redis
                try {
                    jedis.set(email, Json.encode(examQuestions));
                } catch (Exception e) {
                    System.err.println("Error storing data in Redis: " + e.getMessage());
                    // Handle the exception appropriately
                }
            }
            // Return the JSON representation of the questions
            return Json.encode(examQuestions);
        }
    }



}
