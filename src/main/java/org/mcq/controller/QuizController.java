package org.mcq.controller;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.mcq.database.DatabaseConnectionFactory;
import org.mcq.database.redis.RedisService;
import org.mcq.entities.ExamHistory;
import org.mcq.entities.ExamQuestion;
import org.mcq.dao.ExamDao;
import org.mcq.dao.ExamHistoryDao;
import org.mcq.router.RouterUtility;
import org.mcq.service.QuestionService;
import org.mcq.service.QuizService;
import org.mcq.service.SimulationService;
import redis.clients.jedis.Jedis;

import java.sql.*;

public class QuizController extends AbstractVerticle {
    private RedisService redisService;

    private ExamDao examDao;
    private ExamHistoryDao examHistoryDao;
    private QuestionService questionService;
    private QuizService quizService;
    private Jedis jedis;
    private Connection connection;

    @Override
    public void start(Promise<Void> startPromise) {
        jedis = DatabaseConnectionFactory.createRedisConnection();
        redisService = new RedisService(jedis);
        initializeDatabaseRelatedObjects();
        RouterUtility.setUpRouter(vertx, this, startPromise);
    }
    private void initializeDatabaseRelatedObjects() {
        examDao = new ExamDao();
        examHistoryDao = new ExamHistoryDao();
        questionService = new QuestionService();
        quizService = new QuizService(jedis, examDao, examHistoryDao);
    }

    public void simulateExamsHandler(RoutingContext context) {
        SimulationService simulationService = new SimulationService(questionService, quizService, examDao, examHistoryDao, redisService);
        simulationService.simulateExamsForUsers();

        context.response()
                .putHeader("content-type", "application/json")
                .end("{\"message\": \"Simulation started\"}");
    }
    public void getRandomQuestionsHandler(RoutingContext context) {
        String email = context.request().getParam("email");
        String questionsJson = questionService.getRandomQuestions(email);
        context.response()
                .putHeader("content-type", "application/json")
                .end(questionsJson);
    }



    public void getExamHistory(RoutingContext context) {
        String email = context.request().getParam("email");
        ExamHistory examHistory = examHistoryDao.getExamHistory(email);
        context.response()
                .putHeader("content-type", "application/json")
                .end(Json.encode(examHistory));
    }


    public void validateAnswer(RoutingContext context) {
        String email = context.pathParam("email");
        String questionId = context.pathParam("questionId");
        String answerId = context.pathParam("answerId");

        try {
            if (!quizService.isUserValid(email)) {
                context.response().setStatusCode(404).end("No questions found for this email");
                return;
            }

            ExamQuestion nextQuestion = quizService.validateAnswerHandler(email, Integer.parseInt(questionId), Integer.parseInt(answerId));
            if (nextQuestion == null) {
                int score = quizService.getExamScore(email);
                context.response().setStatusCode(200).end("Your exam score is " + score + " out of 20");
                redisService.delete(email);
                return;
            }

            context.response()
                    .putHeader("content-type", "application/json")
                    .end(Json.encode(nextQuestion));
        } catch (Exception e) {
            context.response().setStatusCode(500).end("Internal Server Error: " + e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        DatabaseConnectionFactory.closeConnections(connection, jedis);
    }



}
