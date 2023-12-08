package org.mcq.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.mcq.dao.QuestionDao;
import org.mcq.entities.ExamQuestion;
import org.mcq.entities.ExamQuestionAnswer;

import java.util.List;

@RestController
public class QuestionController {
    private final QuestionDao questionDao;

    public QuestionController() {
        this.questionDao = new QuestionDao();
    }

    @PostMapping("/addQuestionAndAnswers")
    public void addQuestionAndAnswers(ExamQuestion question, List<ExamQuestionAnswer> answers) {
        questionDao.addQuestion(question);
        for (ExamQuestionAnswer answer : answers) {
            questionDao.addAnswer(answer);
        }
    }
}
