package org.mcq;

import io.vertx.core.Vertx;
import org.mcq.controller.QuizController;
import org.mcq.database.DatabaseInitializer;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting application...");

        // Initialize the database
        try {
            DatabaseInitializer.generateDatabase();
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize the database.");
            e.printStackTrace();
            return; // Stop the application if database initialization fails
        }

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new QuizController(), res -> {
            if (res.succeeded()) {
                System.out.println("QuizController Vertical deployed successfully.");
            } else {
                System.err.println("Failed to deploy QuizController Vertical.");
                res.cause().printStackTrace();
            }
        });
    }
}
