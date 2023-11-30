package org.mcq.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.vertx.core.json.Json;
import org.mcq.database.DatabaseConnectionFactory;
import org.mcq.entities.ExamQuestion;
import org.mcq.entities.ExamQuestionAnswer;
import org.mcq.dao.ExamDao;
import org.mcq.dao.ExamHistoryDao;
import redis.clients.jedis.Jedis;

import java.util.List;

public class QuizService {
    private final Jedis jedis;
    private final ExamDao examDao;
    private final ExamHistoryDao examHistoryDao;


    public QuizService() {
         jedis = DatabaseConnectionFactory.createRedisConnection();
         examDao = new ExamDao();
         examHistoryDao = new ExamHistoryDao();
    }
    public ExamQuestion processAnswer(String email, int questionId, int answerId) throws Exception {
        if (!isUserValid(email)) {
            throw new IllegalArgumentException("No questions found for this email");
        }

        ExamQuestion nextQuestion = validateAnswerHandler(email, questionId, answerId);
        if (nextQuestion == null) {
            saveExamResult(email);
        }
        return nextQuestion;
    }
    private void saveExamResult(String email) {
        int score = getExamScore(email);
        int examId = getExamId(email);
        examDao.save(email, score, examId);
    }
    public ExamQuestion validateAnswerHandler(String email, int questionId, int answerId) throws Exception {
            List<ExamQuestion> examQuestionList = getQuestionFromCache(email);
            ExamQuestion question = examQuestionList.stream().filter(x -> x.getId() == questionId).findFirst().orElse(null);
            if (question == null) {
                throw new Exception("Invalid question: Question not found in the list of questions");
            }
            int questionIndex = examQuestionList.indexOf(question);
            boolean isCorrect = question.getAnswers().stream()
                    .filter(answer -> answer.getId() == answerId)
                    .findFirst()
                    .map(ExamQuestionAnswer::isCorrect)
                    .orElse(false);
            boolean answerExists = question.getAnswers().stream()
                    .anyMatch(answer -> answer.getId() == answerId);
            if (!answerExists) {
                throw new Exception("Invalid answer: Answer not found in the list of possible answers");
            }
            if (!isCorrect) {
                if (question.getAttempts() == 0) {
                    question.addQuestionBackToPool(answerId); // Use answerId her
                    examQuestionList.set(questionIndex, question);
                    updateRedis(email, examQuestionList);
                    return question;
                }
            } else {
                if (question.getAttempts() == 0)
                    question.setMark(2);
                else {
                    question.setMark(1);
                }
                examQuestionList.set(questionIndex, question);
                updateRedis(email, examQuestionList);
            }
            examHistoryDao.save(getExamId(email),questionId,answerId,question.getMark());

            if (questionIndex + 1 < examQuestionList.size())
                return examQuestionList.get(questionIndex + 1);
            else {
                examDao.save(email, getExamScore(email), getExamId(email));


                return null;
            }
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
            return objectMapper.readValue(questionsJson, collectionType);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }



    public boolean isUserValid(String email) {
        return getQuestionFromCache(email) != null;
    }

    public int getExamScore(String email) {
        List<ExamQuestion> examQuestionList = getQuestionFromCache(email);
     return   examQuestionList.stream().mapToInt(ExamQuestion::getMark).sum();
    }

    public int getExamId(String email) {
        List<ExamQuestion> examQuestionList = getQuestionFromCache(email);
        return   examQuestionList.get(0).getExamId();
    }

    public void removeExamFromCache(String email){
        jedis.del(email);
    }

}