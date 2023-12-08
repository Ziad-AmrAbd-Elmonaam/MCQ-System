package org.mcq.service;

import org.mcq.dao.ExamHistoryDao;
import org.mcq.entities.ExamHistory;

public class HistoryService {
    private final ExamHistoryDao examHistoryDao;

    public HistoryService() {
        examHistoryDao = new ExamHistoryDao();
    }



    public ExamHistory getExamHistory(String email) {
        return examHistoryDao.getExamHistory(email);
    }
}
