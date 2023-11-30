package org.mcq.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import org.mcq.database.redis.RedisService;
import org.mcq.entities.ExamQuestion;
import org.mcq.entities.ExamQuestionAnswer;
import org.mcq.entities.User;
import org.mcq.dao.ExamDao;
import redis.clients.jedis.Jedis;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulationService {
    private final Random random = new Random();

    private final QuestionService questionService;
    private final QuizService quizService;
    private final ExamDao examDao;
    private final RedisService redisService;

    public SimulationService() {
        this.questionService = new QuestionService();
        this.quizService = new QuizService();
        this.examDao = new ExamDao();
        this.redisService = new RedisService(new Jedis());
    }
    public void simulateExamsForUsers() {
        List<User> simulatedUsers = generateRandomUsers();

        simulatedUsers.forEach(user -> {
            String email = user.getEmail();
            String questionsJson = questionService.getRandomQuestions(email);
            List<ExamQuestion> examQuestions = deserializeQuestions(questionsJson);
            submitExam(email, examQuestions);
            redisService.delete(email);
        });
    }

    private List<User> generateRandomUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new User(generateRandomEmail()));
        }
        return users;
    }
    private List<ExamQuestion> deserializeQuestions(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json,
                    new TypeReference<>() {
                    });
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private void submitExam(String email, List<ExamQuestion> examQuestions) {
        int score = 0;
ExamQuestion question = examQuestions.get(0);
        do {
            List<ExamQuestionAnswer> answers = question.getAnswers();
            int randomIndex = random.nextInt(answers.size()); // Get a random index
            ExamQuestionAnswer randomAnswer = answers.get(randomIndex); // Get the answer from that index
            int answerId = randomAnswer.getId(); // Use the ID of this answer

            try {
                question = quizService.validateAnswerHandler(email, question.getId(), answerId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } while (question != null);

        // Save the exam result
        // set score from redis
         score= quizService.getExamScore(email);
        int examId = quizService.getExamId(email);
        examDao.save(email, score, examId);
    }




    private List<ExamQuestion> getQuestionsFromRedis(String email) {
        String questionsJson = redisService.get(email);
        if (questionsJson == null || questionsJson.isEmpty()) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(questionsJson, new TypeReference<>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String generateRandomEmail() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String alphaNumeric = alphabet + numbers;
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < 100; i++) {
            int index = rnd.nextInt(alphaNumeric.length());
            sb.append(alphaNumeric.charAt(index));
        }
        sb.append("@example.com");
        return sb.toString();
    }
}