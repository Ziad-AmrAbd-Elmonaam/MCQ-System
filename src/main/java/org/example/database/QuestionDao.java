    package org.example.database;

    import org.example.Entities.Answers;
    import org.example.Entities.Question;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class QuestionDao {
        private Connection connection;


        // Constructor that sets the connection
        public QuestionDao(Connection connection) {
            this.connection = connection;
            System.out.println("connection" + connection);
        }
        public void addQuestion(Question question) {
            String sql = "INSERT INTO questions (id, title) VALUES (?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set the id and title of the question
                statement.setInt(1, question.getId());
                statement.setString(2, question.getTitle());

                // Execute the update
                statement.executeUpdate();
                System.out.println("Question added to the database");
            } catch (SQLException e) {
                // Handle the exception more gracefully, log it, or rethrow as needed
                e.printStackTrace();
            }
        }


        // Method to retrieve all questions from the database
        public List<Question> getAllQuestions() {
            List<Question> questions = new ArrayList<>();
            String sql = "SELECT * FROM questions";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");

                    Question question = new Question(id, title);
                    questions.add(question);
                }
            } catch (SQLException e) {
                // Handle the exception more gracefully, log it, or rethrow as needed
                e.printStackTrace();
            }
            return questions;
        }

        public List<Question> getRandomQuestions(int limit) {
            List<Question> randomQuestions = new ArrayList<>();
            String sql = "SELECT q.id AS questionID, "
                    + "q.title AS questionTitle, "
                    + "a.id AS answerID, "
                    + "a.title AS answerTitle, "
                    + "a.is_correct AS answerCorrect "
                    + "FROM (SELECT id, title "
                    + "FROM questions "
                    + "ORDER BY RANDOM() "
                    + "LIMIT ?) AS q "
                    + "LEFT JOIN (SELECT id, question_id, title, is_correct "
                    + "FROM answers "
                    + "ORDER BY RANDOM()) AS a ON q.id = a.question_id ";



            try (PreparedStatement statement = connection.prepareStatement( sql)) {
                statement.setInt(1, limit);

                try (ResultSet rs = statement.executeQuery()) {
                    Map<Integer, Question> questionsMap = new HashMap<>();
                    while (rs.next()) {
                        int questionId = rs.getInt("questionID");
                        String questionTitle = rs.getString("questionTitle");
                        int answerId = rs.getInt("answerID");
                        String answerTitle = rs.getString("answerTitle");
                        boolean answerCorrect = rs.getBoolean("answerCorrect");

                        // Check if the question is already in the map
                        Question question = questionsMap.get(questionId);
                        if (question == null) {

                            question = new Question(questionId, questionTitle);
                            questionsMap.put(questionId, question);
                        }

                        // Add the answer to the question
                        Answers answer = new Answers(answerId, answerTitle, answerCorrect, questionId);
                        question.addAnswer(answer); // Ensure that Question class has a method to add answers
                    }


                    // Convert the map values to a list
                    randomQuestions.addAll(questionsMap.values());
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Replace with more robust error handling
            }
            return randomQuestions;
        }





    }
