package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.example.Entities.ExamQuestion;
import org.example.Entities.ExamQuestionAnswer;
import org.example.database.AnswerDao;
import redis.clients.jedis.Jedis;

import java.util.List;

public class QuizService {
    private final Jedis jedis;
    private final AnswerDao answerDao;

    public QuizService(Jedis jedis, AnswerDao answerDao) {
        this.jedis = jedis;
        this.answerDao = answerDao;
    }

    public JsonObject validateAnswerAndManageScore(String email, int questionId, int answerId) {
        boolean isCorrect = answerDao.isAnswerCorrect(questionId, answerId);
        int score = updateScoreAndManageQuestion(email, questionId, isCorrect, answerId);

        return new JsonObject()
                .put("isCorrect", isCorrect)
                .put("score", score);
    }

    public ExamQuestion validateAnswerHandler(String email, int questionId, int answerId) {
        try {
            List<ExamQuestion> examQuestionList = getQuestionFromCache(email);

            ExamQuestion question = examQuestionList.stream().filter(x -> x.getId() == questionId).findFirst().orElse(null);
            //check if null throw exception
            if (question == null)
                throw new Exception("Question not found");


            int questoinIndex =
                    examQuestionList.indexOf(question);
            // Validate the answer
            boolean isCorrect = question.getAnswers().stream()
                    .filter(answer -> answer.getId() == answerId)
                    .findFirst()
                    .map(ExamQuestionAnswer::isCorrect)
                    .orElse(false);

            // Return the result
            if (!isCorrect) {
                if (question.getAttempts() == 0) {
                    question.addQuestionBackToPool(answerId); // Use answerId her
                    examQuestionList.set(questoinIndex, question);

                    updateRedis(email, examQuestionList);
                    return question;
                }
            } else {
                if (question.getAttempts() == 0)
                    question.setMark(2);
                else {
                    question.setMark(1);

                }
                //updat value in jsonarray

                examQuestionList.set(questoinIndex, question);
                updateRedis(email, examQuestionList);
            }

//check index is valid

            if (questoinIndex + 1 < examQuestionList.size())
                return (ExamQuestion) examQuestionList.get(questoinIndex + 1);
            else
                return null;
        } catch (Exception e) {
            System.err.println("Error retrieving data from Redis: " + e.getMessage());
            // Handle the exception appropriately
        }
        return null;
    }

    private int updateScoreAndManageQuestion(String email, int questionId, boolean isCorrect, int answerId) {
        String userQuestionKey = email + ":question:" + questionId;
        String userScoreKey = email + ":score";
        int attempts = jedis.incr(userQuestionKey).intValue();

        if (isCorrect) {
            int score = (attempts == 1) ? 2 : 1;
            jedis.incrBy(userScoreKey, score);
            jedis.del(userQuestionKey); // Reset attempts for this question
            return score;
        } else {
            if (attempts == 1) {
                addQuestionBackToPool(email, questionId, answerId); // Use answerId here
            } else {
                jedis.del(userQuestionKey); // Reset attempts for this question
            }
        }
        return 0;
    }

    private void updateRedis(String user, List<ExamQuestion> examQuestions) {

        jedis.set(user, Json.encode(examQuestions));
    }

    private List<ExamQuestion> getQuestionFromCache(String email) {
        try {
            String questionsJson = jedis.get(email);
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(List.class, ExamQuestion.class);
            List<ExamQuestion> examQuestionList = objectMapper.readValue(questionsJson, collectionType);
            return examQuestionList;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private void addQuestionBackToPool(String email, int questionId, int incorrectAnswerId) {
        // Example key for user's question pool
        String userQuestionPoolKey = email + ":questionPool";

        JsonObject questionInfo = new JsonObject()
                .put("questionId", questionId)
                .put("excludeAnswerId", incorrectAnswerId);

        jedis.rpush(userQuestionPoolKey, questionInfo.encode());
    }
    public boolean     isUserValid(String email) {
        return getQuestionFromCache(email) != null;
    }

    public int getExamScore(String email) {
        List<ExamQuestion> examQuestionList = getQuestionFromCache(email);
     return   examQuestionList.stream().mapToInt(ExamQuestion::getMark).sum();
    }
}