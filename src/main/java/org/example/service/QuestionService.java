package org.example.service;

import io.vertx.core.json.Json;
import org.example.Entities.Exam;
import org.example.Entities.ExamQuestion;
import org.example.database.ExamDao;
import org.example.database.QuestionDao;
import redis.clients.jedis.Jedis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class QuestionService {


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
        System.out.println("Email received in getRandomQuestions: " + email); // Debugging line

        // Unique session key for this particular quiz instance
        String sessionKey = email + ":" + UUID.randomUUID().toString();

        String value;
        if (jedis.exists(sessionKey)) {
            value = jedis.get(sessionKey);
        } else {
            List<ExamQuestion> ExamQuestions = questionDao.getRandomQuestions(10);
            value = Json.encode(ExamQuestions);

            // Set the value in Redis with an expiration time
            jedis.setex(sessionKey, 3600, value); // Expires after 1 hour
        }

        // Save to database
        Exam exam = new Exam();
        exam.setEmail(email);
        System.out.println("Email after setting: " + exam.getEmail()); // Debugging line
        exam.setDate(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now()));
        exam.setTime(DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
        exam.setDuration(3600);


        examDao.save(exam);

        return value;
    }


}
