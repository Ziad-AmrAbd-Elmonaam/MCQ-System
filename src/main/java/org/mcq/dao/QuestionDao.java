    package org.mcq.dao;

    import org.mcq.database.DatabaseConnectionFactory;
    import org.mcq.entities.ExamQuestionAnswer;
    import org.mcq.entities.ExamQuestion;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class QuestionDao {
        private final Connection connection;


        public QuestionDao() {
            try {
                this.connection = DatabaseConnectionFactory.createDatabaseConnection();

            }
            catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        public void addQuestion(ExamQuestion examQuestion) {
            String sql = "INSERT INTO questions (id, title) VALUES (?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, examQuestion.getId());
                statement.setString(2, examQuestion.getTitle());


                // Execute the update
                statement.executeUpdate();
                System.out.println("Question added to the database");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void addAnswer(ExamQuestionAnswer examQuestionAnswer) {
            String sql = "INSERT INTO answers (id, title, is_correct, question_id) VALUES (?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, examQuestionAnswer.getId());
                statement.setString(2, examQuestionAnswer.getTitle());
                statement.setBoolean(3, examQuestionAnswer.isCorrect());
                statement.setInt(4, examQuestionAnswer.getQuestionId());

                // Execute the update
                statement.executeUpdate();
                System.out.println("Answer added to the database");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



        public List<ExamQuestion> getRandomQuestions(int limit) {
            List<ExamQuestion> randomExamQuestions = new ArrayList<>();
            String sql = "SELECT (SELECT MAX(IFNULL(examId, 0)) FROM exams) AS initialExamId, "
                    + "q.id AS questionID, q.title AS questionTitle, "
                    + "a.id AS answerID, a.title AS answerTitle, a.is_correct AS answerCorrect "
                    + "FROM (SELECT id, title FROM questions ORDER BY RANDOM() LIMIT ?) AS q "
                    + "LEFT JOIN (SELECT id, question_id, title, is_correct, "
                    + "ROW_NUMBER() OVER (PARTITION BY question_id ORDER BY RANDOM()) AS rn "
                    + "FROM answers) AS a ON q.id = a.question_id "
                    + "ORDER BY q.id, a.rn";


            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, limit);

                try (ResultSet rs = statement.executeQuery()) {
                    Map<Integer, ExamQuestion> questionsMap = new HashMap<>();
                    int examId = 0;
                        examId = rs.getInt("initialExamId") + 1;

                    while (rs.next()) {


                        int questionId = rs.getInt("questionID");
                        String questionTitle = rs.getString("questionTitle");
                        int answerId = rs.getInt("answerID");
                        String answerTitle = rs.getString("answerTitle");
                        boolean answerCorrect = rs.getBoolean("answerCorrect");

                        ExamQuestion examQuestion = questionsMap.get(questionId);
                        if (examQuestion == null) {
                            examQuestion = new ExamQuestion(questionId, questionTitle, examId, 0, 0);
                            questionsMap.put(questionId, examQuestion);
                        }

                        ExamQuestionAnswer answer = new ExamQuestionAnswer(answerId, answerTitle, answerCorrect, questionId);
                        examQuestion.addAnswer(answer);
                    }
                    randomExamQuestions.addAll(questionsMap.values());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return randomExamQuestions;
        }






    }
