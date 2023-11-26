package org.mcq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.vertx.core.json.Json;
import org.mcq.entities.Exam;
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

    private Exam exam;
    public QuizService(Jedis jedis, ExamDao examDao, ExamHistoryDao examHistoryDao) {
        this.jedis = jedis;
        this.examDao = examDao;
        this.examHistoryDao = examHistoryDao;
    }



    public ExamQuestion validateAnswerHandler(String email, int questionId, int answerId) {


        try {
            List<ExamQuestion> examQuestionList = getQuestionFromCache(email);

            ExamQuestion question = examQuestionList.stream().filter(x -> x.getId() == questionId).findFirst().orElse(null);
            //check if null throw exception
            if (question == null) {

                throw new Exception("Invalid question: Question not found in the list of questions");
            }
            int questionIndex = examQuestionList.indexOf(question);

            // Validate the answer
            boolean isCorrect = question.getAnswers().stream()
                    .filter(answer -> answer.getId() == answerId)
                    .findFirst()
                    .map(ExamQuestionAnswer::isCorrect)
                    .orElse(false);
            //check if answer is found in the list of possible answers
            boolean answerExists = question.getAnswers().stream()
                    .anyMatch(answer -> answer.getId() == answerId);

            if (!answerExists) {
                throw new Exception("Invalid answer: Answer not found in the list of possible answers");
            }

            // Return the result
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
                return (ExamQuestion) examQuestionList.get(questionIndex + 1);
            else {

                examDao.save(email, getExamScore(email), getExamId(email));

                return null;
            }

            //save examId ,email,score to table Exam in database



        } catch (Exception e) {
            System.err.println("Error retrieving data from Redis: " + e.getMessage());

        }
        return null;
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


    public boolean     isUserValid(String email) {
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

}