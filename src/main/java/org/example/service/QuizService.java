package org.example.service;

import io.vertx.core.json.JsonObject;
import org.example.database.AnswerDao;
import redis.clients.jedis.Jedis;

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
    private void addQuestionBackToPool(String email, int questionId, int incorrectAnswerId) {
        // Example key for user's question pool
        String userQuestionPoolKey = email + ":questionPool";

        JsonObject questionInfo = new JsonObject()
                .put("questionId", questionId)
                .put("excludeAnswerId", incorrectAnswerId);

        jedis.rpush(userQuestionPoolKey, questionInfo.encode());
    }
}