package org.mcq.router;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.common.dsl.Schemas;
import org.mcq.controller.QuizController;

public class RouterUtility {
    public static void setUpRouter(Vertx vertx, QuizController controller, Promise<Void> startPromise) {
        SchemaRouter schemaRouter = SchemaRouter.create(vertx, new SchemaRouterOptions());
        SchemaParser schemaParser = SchemaParser.createDraft201909SchemaParser(schemaRouter);



        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/random-questions/:email")
                .handler(ValidationHandlerBuilder
                        .create(schemaParser)
                        .pathParameter(Parameters.param("email", Schemas.stringSchema()))
                        .build())
                .handler(controller::getRandomQuestionsHandler);


        router.get("/answer-question/:email/:questionId/:answerId")
                .handler(ValidationHandlerBuilder
                        .create(schemaParser)
                        .pathParameter(Parameters.param("email", Schemas.stringSchema()))
                        .pathParameter(Parameters.param("questionId", Schemas.intSchema()))
                        .pathParameter(Parameters.param("answerId", Schemas.intSchema()))
                        .build())
                .handler(controller::validateAnswer);        router.get("/exam-history/:email").handler(controller::getExamHistory);
        router.get("/simulate-exams").handler(controller::simulateExamsHandler);

        vertx.createHttpServer().requestHandler(router).listen(8080, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                System.out.println("HTTP server started on port 8080");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }
}
