package org.example.Controllers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.Entities.ExamHistory;
import org.example.Entities.ExamQuestion;
import org.example.database.ExamDao;
import org.example.database.ExamHistoryDao;
import org.example.database.QuestionDao;
import org.example.service.QuestionService;
import org.example.service.QuizService;
import org.example.service.SimulationService;
import redis.clients.jedis.Jedis;

import java.sql.*;
import java.util.List;

public class QuizApiVerticle extends AbstractVerticle {
    private ExamDao examDao;
    private ExamHistoryDao examHistoryDao;
    private QuestionService questionService;
    private QuizService quizService;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Jedis jedis = new Jedis("localhost", 6379);

        Connection connection = DriverManager.getConnection("jdbc:sqlite:database/database.db");
        ExamDao examDao = new ExamDao(connection);
        QuestionDao questionDao = new QuestionDao(connection); // Initialize QuestionDao with the connection
        this.examHistoryDao = new ExamHistoryDao(connection);
        this.questionService = new QuestionService(questionDao, jedis, examDao, examHistoryDao);
        this.quizService = new QuizService(jedis, examDao,examHistoryDao);
        this.examDao= examDao;






        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
//        router.get("/questions").handler(this::getAllQuestions);
        router.get("/random-questions/:email").handler(this::getRandomQuestionsHandler);
        router.get("/answer-question/:email/:questionId/:answerId").handler(this::validateAnswer);
        //get exam history
        router.get("/exam-history/:email").handler(this::getExamHistory);
        router.get("/simulate-exams").handler(this::simulateExamsHandler);





        vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8080");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }


    private void simulateExamsHandler(RoutingContext context) {
        SimulationService simulationService = new SimulationService(questionService, quizService, examDao, examHistoryDao , new Jedis("localhost", 6379));
              simulationService.simulateExamsForUsers();

        context.response()
                .putHeader("content-type", "application/json")
                .end("{\"message\": \"Simulation started\"}");
    }
    private void getRandomQuestionsHandler(RoutingContext context) {
        String email = context.request().getParam("email");
        String questionsJson = questionService.getRandomQuestions(email);
        context.response()
                .putHeader("content-type", "application/json")
                .end(questionsJson);
    }





//    private void getAllQuestions(RoutingContext context) {
//        List<ExamQuestion> ExamQuestions = questionService.getAllQuestions();
//        context.response()
//                .putHeader("content-type", "application/json")
//                .end(Json.encode(ExamQuestions));
//    }

    private void getExamHistory (RoutingContext context) {
        String email = context.request().getParam("email");
        ExamHistory examHistory = examHistoryDao.getExamHistory(email);
        context.response()
                .putHeader("content-type", "application/json")
                .end(Json.encode(examHistory));
    }


    private void validateAnswer(RoutingContext context) {
        // Extract email from the request
        String email = context.request().getParam("email");
        if (email == null || email.isEmpty()) {
            context.response().setStatusCode(400).end("Email is required");
            return;
        }

        // Extract answer from the request
        String answer = context.request().getParam("answerId");
        if (answer == null || answer.isEmpty()) {
            context.response().setStatusCode(400).end("Answer is required");
            return;
        }
        String questionId = context.request().getParam("questionId");
        if (questionId == null || questionId.isEmpty()) {
            context.response().setStatusCode(400).end("QuestionId is required");
            return;
        }

        try {
;
            if (!   quizService.isUserValid(email)) {
                context.response().setStatusCode(404).end("No questions found for this email");
                return;
            }


            //call validateAnswerHandler
            ExamQuestion nextQuestion = quizService.validateAnswerHandler(email, Integer.parseInt(questionId), Integer.parseInt(answer));
            if(nextQuestion==null){
               int score= quizService.getExamScore(email);
                context.response().setStatusCode(200).end("your exam score is "+score+" out of 20");

                return;
            }
            context.response()
                    .putHeader("content-type", "application/json")
                    .end( Json.encode(nextQuestion));
        } catch (Exception e) {
            context.response().setStatusCode(500).end("Internal Server Error: " + e.getMessage());
        }
    }



}
