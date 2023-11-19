package org.example;

import io.vertx.core.Vertx;
import org.example.Controllers.QuizApiVerticle;
import org.example.Entities.ExamQuestion;
import org.example.database.DatabaseInitializer;
import redis.clients.jedis.Tuple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    //crete a dictionary to store the user questions

    public static void main(String[] args) {
        //init userQuestions


        System.out.println("working");
        DatabaseInitializer.generateDatabase();

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new QuizApiVerticle());
    }

}