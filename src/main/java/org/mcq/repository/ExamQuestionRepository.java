package org.mcq.repository;

import org.mcq.entities.ExamQuestion;

import java.util.List;

public interface ExamQuestionRepository {
    List<ExamQuestion> getRandomQuestions(int limit);
}
