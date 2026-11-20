package data_access;

import entity.Assessment;
import entity.ConfidenceLevel;
import entity.Course;
import entity.LetterGrade;
import entity.Topic;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Converts between {@link Course} entities and the MongoDB documents that store them.
 *
 * <p>One document per course:</p>
 * <pre>
 * {
 *   "username": "mia",
 *   "codeKey": "CSC207",
 *   "code": "CSC207",
 *   "name": "Software Design",
 *   "targetGrade": "A",
 *   "topics": [{"name": "Clean Architecture", "confidence": "YELLOW"}],
 *   "assessments": [{"name": "Midterm 1", "mark": 78.0, "weight": 15.0}]
 * }
 * </pre>
 */
final class CourseDocumentMapper {

    static final String USERNAME = "username";
    static final String CODE_KEY = "codeKey";
    static final String CODE = "code";
    static final String NAME = "name";
    static final String TARGET_GRADE = "targetGrade";
    static final String TOPICS = "topics";
    static final String CONFIDENCE = "confidence";
    static final String ASSESSMENTS = "assessments";
    static final String MARK = "mark";
    static final String WEIGHT = "weight";

    private CourseDocumentMapper() {
    }

    /**
     * Returns the key a course code is stored under, so that "csc207" and "CSC207" match.
     * @param courseCode the course code
     * @return the trimmed, upper-case course code
     */
    static String key(String courseCode) {
        return courseCode.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Converts a course into a document.
     * @param username the owner of the course
     * @param course the course
     * @return the document to store
     */
    static Document toDocument(String username, Course course) {
        final List<Document> topics = new ArrayList<>();
        for (Topic topic : course.getTopics()) {
            topics.add(new Document(NAME, topic.getName())
                    .append(CONFIDENCE, topic.getConfidence().getName()));
        }
        final List<Document> assessments = new ArrayList<>();
        for (Assessment assessment : course.getAssessments()) {
            assessments.add(new Document(NAME, assessment.getName())
                    .append(MARK, assessment.getMark())
                    .append(WEIGHT, assessment.getWeight()));
        }
        return new Document(USERNAME, username)
                .append(CODE_KEY, key(course.getCode()))
                .append(CODE, course.getCode())
                .append(NAME, course.getName())
                .append(TARGET_GRADE, course.getTargetGrade().getLabel())
                .append(TOPICS, topics)
                .append(ASSESSMENTS, assessments);
    }

    /**
     * Converts a stored document back into a course.
     * @param document the stored document
     * @return the course
     * @throws IllegalArgumentException if the document holds invalid data
     */
    static Course toCourse(Document document) {
        final Course course = new Course(document.getString(CODE), document.getString(NAME),
                LetterGrade.fromLabel(document.getString(TARGET_GRADE)));

        for (Document topic : document.getList(TOPICS, Document.class, new ArrayList<>())) {
            course.addTopic(new Topic(topic.getString(NAME),
                    ConfidenceLevel.fromName(topic.getString(CONFIDENCE))));
        }

        final List<Assessment> assessments = new ArrayList<>();
        for (Document assessment : document.getList(ASSESSMENTS, Document.class, new ArrayList<>())) {
            assessments.add(new Assessment(assessment.getString(NAME),
                    toDouble(assessment.get(MARK)), toDouble(assessment.get(WEIGHT))));
        }
        course.setAssessments(assessments);
        return course;
    }

    /** Numbers edited by hand in MongoDB Compass may come back as Integer rather than Double. */
    private static double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        throw new IllegalArgumentException("Expected a number but found: " + value);
    }
}
