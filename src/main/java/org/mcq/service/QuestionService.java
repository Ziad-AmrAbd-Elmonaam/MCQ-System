package org.mcq.service;

import io.vertx.core.json.Json;
import org.mcq.dao.QuestionDao;
import org.mcq.database.DatabaseConnectionFactory;
import org.mcq.database.DatabaseInitializer;
import org.mcq.database.redis.RedisService;
import org.mcq.entities.ExamQuestion;
import redis.clients.jedis.Jedis;

import java.util.List;

public class QuestionService {
    private final Jedis jedis;
    private final QuestionDao questionDao;

    public QuestionService() {
        this.questionDao = new QuestionDao();
        jedis = DatabaseConnectionFactory.createRedisConnection();
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

        List<ExamQuestion> examQuestions = questionDao.getRandomQuestions(10);
        if (examQuestions.isEmpty()) {
            return "No questions available";
        }

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
