package org.mcq.controller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import org.mcq.database.DatabaseConnectionFactory;
import org.mcq.database.redis.RedisService;
import org.mcq.entities.ExamHistory;
import org.mcq.entities.ExamQuestion;
import org.mcq.router.RouterUtility;
import org.mcq.service.HistoryService;
import org.mcq.service.QuestionService;
import org.mcq.service.QuizService;
import org.mcq.service.SimulationService;
import redis.clients.jedis.Jedis;

/**
 * Controller class for managing quizzes and exams using Vert.x.
 */
public class QuizController extends AbstractVerticle {
    private SimulationService simulationService;
    private RedisService redisService;
    private HistoryService historyService;
    private QuestionService questionService;
    private QuizService quizService;
    private Jedis jedis;

    /**
     * Verticle start method.
     *
     * @param startPromise Promise object for asynchronous startup
     */
    @Override
    public void start(Promise<Void> startPromise) {
        // Establish connection to Redis
        jedis = DatabaseConnectionFactory.createRedisConnection();
        redisService = new RedisService(jedis);

        // Initialize services
        historyService = new HistoryService();
        questionService = new QuestionService();
        quizService = new QuizService();
        simulationService = new SimulationService();

        // Set up Vert.x router
        RouterUtility.setUpRouter(vertx, this, startPromise);
    }

    /**
     * Handler for simulating exams for users.
     *
     * @param context Routing context
     */
    public void simulateExamsHandler(RoutingContext context) {
        simulationService.simulateExamsForUsers();
        context.response()
                .putHeader("content-type", "application/json")
                .end("{\"message\": \"Simulation started\"}");
    }

    /**
     * Handler for getting random questions for a user.
     *
     * @param context Routing context
     */
    public void getRandomQuestionsHandler(RoutingContext context) {
        String email = context.request().getParam("email");
        String questionsJson = questionService.getRandomQuestions(email);
        context.response()
                .putHeader("content-type", "application/json")
                .end(questionsJson);
    }

    /**
     * Handler for retrieving exam history for a user.
     *
     * @param context Routing context
     */
    public void getExamHistory(RoutingContext context) {
        String email = context.request().getParam("email");
        ExamHistory examHistory = historyService.getExamHistory(email);
        context.response()
                .putHeader("content-type", "application/json")
                .end(Json.encode(examHistory));
    }

    /**
     * Handler for validating user answers during an ongoing exam.
     *
     * @param context Routing context
     */
    public void validateAnswer(RoutingContext context) {
        String email = context.pathParam("email");
        String questionId = context.pathParam("questionId");
        String answerId = context.pathParam("answerId");

        try {
            ExamQuestion nextQuestion = quizService.processAnswer(email, Integer.parseInt(questionId), Integer.parseInt(answerId));

            if (nextQuestion != null) {
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(Json.encode(nextQuestion));
            } else {
                handleExamCompletion(context, email);
            }
        } catch (NumberFormatException e) {
            context.response().setStatusCode(400).end("Invalid number format for questionId or answerId");
        } catch (IllegalArgumentException e) {
            context.response().setStatusCode(404).end(e.getMessage());
        } catch (Exception e) {
            context.response().setStatusCode(500).end("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Private method to handle exam completion.
     *
     * @param context Routing context
     * @param email   User's email
     */
    private void handleExamCompletion(RoutingContext context, String email) {
        try {
            String result = quizService.handleExamCompletion(email);
            context.response().setStatusCode(200).end(result);
        } catch (IllegalArgumentException e) {
            context.response().setStatusCode(404).end(e.getMessage());
        } catch (Exception e) {
            context.response().setStatusCode(500).end("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Verticle stop method. Closes the connection to Redis.
     *
     * @throws Exception Thrown on failure to close the connection
     */
    @Override
    public void stop() throws Exception {
        DatabaseConnectionFactory.closeRedisConnection(jedis);
    }
}
