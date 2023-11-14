package org.example.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.example.dao.QuestionDao;
import org.example.model.Question;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class QuizApiVerticle extends AbstractVerticle {

    private QuestionDao questionDao;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // Initialize your SQLite database connection here
        Connection connection = DriverManager.getConnection("jdbc:sqlite:database/database.db");
        generateDatabase();
        this.questionDao = new QuestionDao(connection); // Initialize QuestionDao with the connection

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/questions").handler(this::getAllQuestions);
        router.post("/questions").handler(this::addQuestion);

        vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8080");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    private void generateDatabase()
    {
        String url = "jdbc:sqlite:database/database.db";  // Adjust the path as necessary

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS questions (" +
                "id INTEGER PRIMARY KEY," +
                "question_text TEXT NOT NULL," +
                "choice_a TEXT," +
                "choice_b TEXT," +
                "choice_c TEXT," +
                "choice_d TEXT," +
                "correct_answer TEXT);";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // Create a new table
            stmt.execute(sql);
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
}
