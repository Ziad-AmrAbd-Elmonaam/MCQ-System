package org.example;

import io.vertx.core.Vertx;
import org.example.Controllers.QuizApiVerticle;
import org.example.database.DatabaseInitializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        System.out.println("working");
        DatabaseInitializer.generateDatabase();

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new QuizApiVerticle());
    }

}