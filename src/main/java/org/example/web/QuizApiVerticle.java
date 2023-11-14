package org.example.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.dao.AnswerDao;
import org.example.dao.QuestionDao;
import org.example.model.Answers;
import org.example.model.Question;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class QuizApiVerticle extends AbstractVerticle {

    private QuestionDao questionDao;
    private AnswerDao answerDao;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // Initialize your SQLite database connection here
        Connection connection = DriverManager.getConnection("jdbc:sqlite:database/database.db");
        generateDatabase();
        this.questionDao = new QuestionDao(connection); // Initialize QuestionDao with the connection
        this.answerDao = new AnswerDao(connection);

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/questions").handler(this::getAllQuestions);
        router.post("/questions").handler(this::addQuestion);
        router.get("/answers").handler(this::getAllAnswers);
        router.post("/answers").handler(this::addAnswers);

        vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8080");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    private void generateDatabase() {
        String url = "jdbc:sqlite:database/database.db";  // Adjust the path as necessary

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS questions (" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL);";
        String sql2 = "CREATE TABLE IF NOT EXISTS answers (" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "is_correct BOOLEAN NOT NULL," +
                "question_id INTEGER NOT NULL," +
                "FOREIGN KEY (question_id) REFERENCES questions (id));";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            stmt.execute(sql2);
            System.out.println("Database created successfully");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void getAllQuestions(RoutingContext context) {
        List<Question> questions = questionDao.getAllQuestions();
        context.response()
                .putHeader("content-type", "application/json")
                .end(Json.encode(questions));
    }

    private void addQuestion(RoutingContext context) {
        Question question = context.getBodyAsJson().mapTo(Question.class);

        // Add logic to insert the question into the database using QuestionDao
        questionDao.addQuestion(question); // Implement this method in your QuestionDao

        context.response()
                .setStatusCode(201) // HTTP 201 Created
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(question));
    }

    private void getAllAnswers(RoutingContext context) {
        List<Answers> answers = answerDao.getAllAnswers();
        context.response()
                .putHeader("content-type", "application/json")
                .end(Json.encode(answers));
    }

    private void addAnswers(RoutingContext context) {
        JsonObject requestBody = context.getBodyAsJson();
        Answers answers = new Answers();

        // Parse JSON data and map it to Answers object
        answers.setId(requestBody.getInteger("id"));
        answers.setTitle(requestBody.getString("title"));
        answers.setIsCorrect(requestBody.getBoolean("is_correct"));
        answers.setQuestionId(requestBody.getInteger("question_id"));

        // Add logic to insert the answers into the database using AnswerDao
        answerDao.addAnswer(answers); // Implement this method in your AnswerDao

        context.response()
                .setStatusCode(201) // HTTP 201 Created
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(answers));
    }
}
