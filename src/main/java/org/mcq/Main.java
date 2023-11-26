package org.mcq;

import io.vertx.core.Vertx;
import org.mcq.controller.QuizController;
import org.mcq.database.DatabaseInitializer;
import org.mcq.database.InsertJsonDataToDatabase;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    //crete a dictionary to store the user questions

    public static void main(String[] args) {
        //init userQuestions


        System.out.println("working");
        DatabaseInitializer.generateDatabase();

        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new QuizController());
    }

}