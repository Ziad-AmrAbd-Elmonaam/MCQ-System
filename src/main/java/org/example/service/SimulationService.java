package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import org.example.Entities.ExamQuestion;
import org.example.Entities.ExamQuestionAnswer;
import org.example.Entities.User;
import org.example.database.ExamDao;
import org.example.database.ExamHistoryDao;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulationService {
    private Random random = new Random();
    private Jedis jedis;

    ExamHistoryDao examHistoryDao;
    private QuestionService questionService;
    private QuizService quizService;
    private ExamDao examDao;

    public SimulationService(QuestionService questionService, QuizService quizService, ExamDao examDao, ExamHistoryDao examHistoryDao, Jedis jedis) {
        this.questionService = questionService;
        this.quizService = quizService;
        this.examDao = examDao;
        this.examHistoryDao = examHistoryDao;
        this.jedis = jedis; // Initialize jedis here
    }
    public void simulateExamsForUsers() {
        // Generate 100 simulated users
        List<User> simulatedUsers = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            simulatedUsers.add(new User("user" + i + "@example.com"));
        }

        // Assign and submit exams for each simulated user
        simulatedUsers.forEach(user -> {
            String email = user.getEmail();
            String questionsJson = questionService.getRandomQuestions(email);
            List<ExamQuestion> examQuestions = deserializeQuestions(questionsJson);
            submitExam(email, examQuestions);
        });
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

        for (ExamQuestion question : examQuestions) {
            List<ExamQuestionAnswer> answers = question.getAnswers();
            int randomIndex = random.nextInt(answers.size()); // Get a random index
            ExamQuestionAnswer randomAnswer = answers.get(randomIndex); // Get the answer from that index
            int answerId = randomAnswer.getId(); // Use the ID of this answer
            quizService.validateAnswerHandler(email, question.getId(), answerId);
            if (randomAnswer.isCorrect()) {
                score += question.getMark();
            }
        }

        // Save the exam result
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
}