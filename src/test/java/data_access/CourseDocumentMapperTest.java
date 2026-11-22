package data_access;

import entity.Assessment;
import entity.ConfidenceLevel;
import entity.Course;
import entity.CourseFactory;
import entity.LetterGrade;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseDocumentMapperTest {

    private static final double DELTA = 1e-9;

    private static Course sampleCourse() {
        final Course course = new CourseFactory().create("csc207", "Software Design", LetterGrade.A,
                Arrays.asList("SOLID", "Clean Architecture"));
        course.setTopicConfidence("Clean Architecture", ConfidenceLevel.YELLOW);
        course.setAssessments(Arrays.asList(
                new Assessment("Midterm 1", 78, 15),
                new Assessment("Assignment 1", 92, 10)));
        return course;
    }

    @Test
    void toDocumentStoresEveryField() {
        final Document document = CourseDocumentMapper.toDocument("mia", sampleCourse());

        assertEquals("mia", document.getString("username"));
        assertEquals("CSC207", document.getString("codeKey"));
        assertEquals("csc207", document.getString("code"));
        assertEquals("Software Design", document.getString("name"));
        assertEquals("A", document.getString("targetGrade"));

        final List<Document> topics = document.getList("topics", Document.class);
        assertEquals(2, topics.size());
        assertEquals("Clean Architecture", topics.get(1).getString("name"));
        assertEquals("YELLOW", topics.get(1).getString("confidence"));

        final List<Document> assessments = document.getList("assessments", Document.class);
        assertEquals(78.0, assessments.get(0).getDouble("mark"), DELTA);
        assertEquals(15.0, assessments.get(0).getDouble("weight"), DELTA);
    }

    @Test
    void roundTripGivesTheSameCourse() {
        final Course course = CourseDocumentMapper.toCourse(
                CourseDocumentMapper.toDocument("mia", sampleCourse()));

        assertEquals("csc207", course.getCode());
        assertEquals("Software Design", course.getName());
        assertEquals(LetterGrade.A, course.getTargetGrade());
        assertEquals(ConfidenceLevel.RED, course.getTopics().get(0).getConfidence());
        assertEquals(ConfidenceLevel.YELLOW, course.getTopics().get(1).getConfidence());
        assertEquals(2, course.getAssessments().size());
        assertEquals(83.6, course.getCurrentPercentage().getAsDouble(), DELTA);
    }

    @Test
    void readsWholeNumbersAndMissingLists() {
        // A document typed by hand, e.g. in MongoDB Compass: integer marks, no topics list.
        final Document document = new Document("username", "mia")
                .append("code", "MAT137").append("name", "Calculus").append("targetGrade", "b+")
                .append("assessments", Arrays.asList(
                        new Document("name", "Test 1").append("mark", 80).append("weight", 20)));

        final Course course = CourseDocumentMapper.toCourse(document);
        assertEquals(LetterGrade.B_PLUS, course.getTargetGrade());
        assertTrue(course.getTopics().isEmpty());
        assertEquals(80.0, course.getCurrentPercentage().getAsDouble(), DELTA);
    }

    @Test
    void rejectsUnknownGrade() {
        final Document document = new Document("code", "MAT137").append("name", "Calculus")
                .append("targetGrade", "Z");
        assertThrows(IllegalArgumentException.class, () -> CourseDocumentMapper.toCourse(document));
    }
}
