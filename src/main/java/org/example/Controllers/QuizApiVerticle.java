package org.example.Controllers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.Entities.ExamQuestion;
import org.example.database.AnswerDao;
import org.example.database.ExamDao;
import org.example.database.QuestionDao;
import org.example.service.QuestionService;
import org.example.service.QuizService;
import redis.clients.jedis.Jedis;

import java.sql.*;
import java.util.List;

public class QuizApiVerticle extends AbstractVerticle {
    private ExamDao examDao;
    private QuestionService questionService;
    private QuizService quizService;
    private Jedis jedis ;

    private QuestionDao questionDao;
    private AnswerDao answerDao;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.jedis= new Jedis("localhost", 6379);

        Connection connection = DriverManager.getConnection("jdbc:sqlite:database/database.db");
        this.examDao = new ExamDao(connection);
        this.questionDao = new QuestionDao(connection); // Initialize QuestionDao with the connection
        this.answerDao = new AnswerDao(connection);
        this.questionService = new QuestionService(questionDao, jedis, examDao);
        this.quizService = new QuizService(jedis, answerDao);




        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/questions").handler(this::getAllQuestions);
        router.get("/random-questions").handler(this::getRandomQuestionsHandler);
        router.get("/validate-answer").handler(this::validateAnswerHandler);
        router.get("/start-exam").handler(this::startExam);
//        router.post("/start-exam").handler(this::startExamHandler);
//        router.post("/answer-question").handler(this::answerQuestionHandler);
//        router.post("/finalize-exam").handler(this::finalizeExamHandler);



        vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8080");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }
    private void startExam(RoutingContext context) {
    }
//    private void startExamHandler(RoutingContext context) {
//        // Extract the body as a JsonObject
//        JsonObject requestBody = context.getBodyAsJson();
//
//        // Now get the email from the requestBody JsonObject
//        String email = requestBody.getString("email");
//        if (email == null || email.trim().isEmpty()) {
//            context.response().setStatusCode(400).end("Email is required");
//            return;
//        }
//
//        // Start the exam by getting random questions for the user
//        String questionsJson = questionService.getRandomQuestions(email);
//        context.response()
//                .putHeader("content-type", "application/json")
//                .end(questionsJson);
//    }


    // Answer Question Endpoint
//    private void answerQuestionHandler(RoutingContext context) {
//        JsonObject requestBody = context.getBodyAsJson();
//        String email = requestBody.getString("email");
//        int questionId = requestBody.getInteger("question_id");
//        int answerId = requestBody.getInteger("answer_id");
//
//        JsonObject response = quizService.validateAnswerAndManageScore(email, questionId, answerId);
//        context.response()
//                .putHeader("content-type", "application/json")
//                .end(response.encode());
//    }

    // Finalize Exam Endpoint (if needed)
//    private void finalizeExamHandler(RoutingContext context) {
//        // Extract the body as a JsonObject
//        JsonObject requestBody = context.getBodyAsJson();
//
//        // Now get the email from the requestBody JsonObject
//        String email = requestBody.getString("email");
//        if (email == null || email.trim().isEmpty()) {
//            context.response().setStatusCode(400).end("Email is required");
//            return;
//        }
//
//        int finalScore = getFinalScoreForUser(email);
//
//        context.response()
//                .putHeader("content-type", "application/json")
//                .end(new JsonObject().put("finalScore", finalScore).encode());
//    }


    // Mock method to get the final score for a user
// Replace this with your actual implementation.
//    private int getFinalScoreForUser(String email) {
//        // Here you would have the logic to calculate the final score or retrieve it from Redis
//        // For the sake of this example, let's assume we are retrieving it from Redis.
//        String userScoreKey = email + ":score";
//        String scoreStr = jedis.get(userScoreKey);
//        int score = 0;
//        if (scoreStr != null) {
//            score = Integer.parseInt(scoreStr);
//            // Optionally, you can delete the user's score from Redis after retrieving it
//            jedis.del(userScoreKey);
//        }
//        return score;
//    }
    private void validateAnswerHandler(RoutingContext context) {
        JsonObject requestBody = context.getBodyAsJson();
        String email = requestBody.getString("email");
        int questionId = requestBody.getInteger("question_id");
        int answerId = requestBody.getInteger("answer_id");

        JsonObject response = quizService.validateAnswerAndManageScore(email, questionId, answerId);

        context.response()
                .putHeader("content-type", "application/json")
                .end(response.encode());
    }




    private void getRandomQuestionsHandler(RoutingContext context) {
        String email = "ziad@gmail.com";
        String questionsJson = questionService.getRandomQuestions(email);
        context.response()
                .putHeader("content-type", "application/json")
                .end(questionsJson);
    }




    private void getAllQuestions(RoutingContext context) {
        List<ExamQuestion> ExamQuestions = questionService.getAllQuestions();
        context.response()
                .putHeader("content-type", "application/json")
                .end(Json.encode(ExamQuestions));
    }


}
