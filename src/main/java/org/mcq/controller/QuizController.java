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



public class QuizController extends AbstractVerticle {
    private SimulationService simulationService;
    private RedisService redisService;
    private HistoryService historyService;
    private QuestionService questionService;
    private QuizService quizService;
    private Jedis jedis;

    @Override
    public void start(Promise<Void> startPromise) {
        jedis = DatabaseConnectionFactory.createRedisConnection();
        redisService = new RedisService(jedis);
        historyService = new HistoryService();
        questionService = new QuestionService();
        quizService = new QuizService();
        simulationService = new SimulationService();
        RouterUtility.setUpRouter(vertx, this, startPromise);
    }

    public void simulateExamsHandler(RoutingContext context) {
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
        ExamHistory examHistory = historyService.getExamHistory(email);
          context.response()
                    .putHeader("content-type", "application/json")
                    .end(Json.encode(examHistory));
    }


    public void validateAnswer(RoutingContext context) {
        String email = context.pathParam("email");
        String questionId = context.pathParam("questionId");
        String answerId = context.pathParam("answerId");

        try {
            ExamQuestion nextQuestion = quizService.processAnswer(email, Integer.parseInt(questionId), Integer.parseInt(answerId));

            if (nextQuestion == null ) {
                if (!redisService.isKeyExists(email)) {
                    context.response().setStatusCode(404).end("Exam already submitted");
                    return;
                }

                // Otherwise, proceed to calculate the score and remove the exam from cache.
                int score = quizService.getExamScore(email);
                quizService.removeExamFromCache(email);
                context.response().setStatusCode(200).end("Your exam score is " + score + " out of 20");
            } else {
                context.response()
                        .putHeader("content-type", "application/json")
                        .end(Json.encode(nextQuestion));
            }
        } catch (NumberFormatException e) {
            context.response().setStatusCode(400).end("Invalid number format for questionId or answerId");
        } catch (IllegalArgumentException e) {
            context.response().setStatusCode(404).end(("you have already submitted the exam"));
        } catch (Exception e) {
            context.response().setStatusCode(500).end("Internal Server Error: " + e.getMessage());
        }
    }



    @Override
    public void stop() throws Exception {
        DatabaseConnectionFactory.closeRedisConnection(jedis);
    }



}
