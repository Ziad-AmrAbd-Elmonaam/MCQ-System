package org.MCQ.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import org.MCQ.Entities.ExamQuestion;
import org.MCQ.Entities.ExamQuestionAnswer;
import org.MCQ.Entities.User;
import org.MCQ.database.ExamDao;
import org.MCQ.database.ExamHistoryDao;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulationService {
    private Random random = new Random();
    private Jedis jedis;

    ExamHistoryDao examHistoryDao;
    private final QuestionService questionService;
    private QuizService quizService;
    private ExamDao examDao;

    public SimulationService(QuestionService questionService, QuizService quizService, ExamDao examDao, ExamHistoryDao examHistoryDao, Jedis jedis) {
        this.questionService = questionService;
        this.quizService = quizService;
        this.examDao = examDao;
        this.examHistoryDao = examHistoryDao;
        this.jedis = jedis;
    }
    public void simulateExamsForUsers() {
        // Generate 100 simulated users
        List<User> simulatedUsers = generateRandomUsers(); // Generate 10 random users

        // Assign and submit exams for each simulated user
        simulatedUsers.forEach(user -> {
            String email = user.getEmail();
            String questionsJson = questionService.getRandomQuestions(email);
            List<ExamQuestion> examQuestions = deserializeQuestions(questionsJson);
            submitExam(email, examQuestions);
            jedis.del(email); // Delete the questions from Redis
        });
    }

    private List<User> generateRandomUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            users.add(new User(generateRandomEmail()));
        }
        return users;
    }
    private List<ExamQuestion> deserializeQuestions(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json,
                    new TypeReference<List<ExamQuestion>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private void submitExam(String email, List<ExamQuestion> examQuestions) {
        int score = 0;
ExamQuestion question = examQuestions.get(0);
        while (true) {
            List<ExamQuestionAnswer> answers = question.getAnswers();
            int randomIndex = random.nextInt(answers.size()); // Get a random index
            ExamQuestionAnswer randomAnswer = answers.get(randomIndex); // Get the answer from that index
            int answerId = randomAnswer.getId(); // Use the ID of this answer
        question= quizService.validateAnswerHandler(email, question.getId(), answerId);
            if (question == null) {
                break;
            }
        }

        // Save the exam result
        // set score from redis
         score= quizService.getExamScore(email);
        int examId = quizService.getExamId(email);
        examDao.save(email, score, examId);
    }


    private int getRandomAnswerId(String email) {
        List<ExamQuestion> examQuestions = getQuestionsFromRedis(email);
        if (examQuestions == null || examQuestions.isEmpty()) {
            throw new IllegalStateException("No questions available for email: " + email);
        }

        // Randomly select a question
        ExamQuestion randomQuestion = examQuestions.get(random.nextInt(examQuestions.size()));
        if (randomQuestion.getAnswers() == null || randomQuestion.getAnswers().isEmpty()) {
            throw new IllegalStateException("No answers available for the selected question.");
        }

        // Randomly select an answer ID from the question
        ExamQuestionAnswer randomAnswer = randomQuestion.getAnswers().get(random.nextInt(randomQuestion.getAnswers().size()));
        return randomAnswer.getId();
    }

    private List<ExamQuestion> getQuestionsFromRedis(String email) {
        String questionsJson = jedis.get(email);
        if (questionsJson == null || questionsJson.isEmpty()) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(questionsJson, new TypeReference<List<ExamQuestion>>() {});
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
        for (int i = 0; i < 100; i++) { // Generate 10-character long username part
            int index = rnd.nextInt(alphaNumeric.length());
            sb.append(alphaNumeric.charAt(index));
        }
        sb.append("@example.com");
        return sb.toString();
    }
}