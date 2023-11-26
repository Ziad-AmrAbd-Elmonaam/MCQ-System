package org.mcq.service;

import io.vertx.core.json.Json;
import org.mcq.dao.QuestionDao;
import org.mcq.entities.ExamQuestion;
import redis.clients.jedis.Jedis;

import java.util.List;

public class QuestionService {

    private final QuestionDao questionDao;
    private final Jedis jedis;

    public QuestionService(QuestionDao questionDao, Jedis jedis) {
        this.questionDao = questionDao;
        this.jedis = jedis;

    }


    public String getRandomQuestions(String email) {
        String redisData;
        try {
            redisData = jedis.get(email);
            if (redisData != null && !redisData.isEmpty()) {
                return redisData; // Return data if found in Redis
            }
        } catch (Exception e) {
            System.err.println("Error retrieving data from Redis: " + e.getMessage());
        }

        // Fetch questions from the database if not found in Redis
        List<ExamQuestion> examQuestions = questionDao.getRandomQuestions(10);
        if (examQuestions.isEmpty()) {
            // Handle the case where there are no questions
            return "No questions available";
        }

        // Save the fetched questions in Redis and return the JSON representation
        try {
            String jsonExamQuestions = Json.encode(examQuestions);
            jedis.set(email, jsonExamQuestions);
            return jsonExamQuestions;
        } catch (Exception e) {
            System.err.println("Error storing data in Redis: " + e.getMessage());
            return "Error processing request";
        }
    }


}
