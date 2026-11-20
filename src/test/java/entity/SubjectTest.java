package entity;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubjectTest {

    private static final double DELTA = 1e-9;
    private static final ConfidenceScoringStrategy SCORING = new StandardConfidenceScoring();

    private static Course course(String code, ConfidenceLevel... levels) {
        final Course course = new Course(code, "Some course", LetterGrade.B);
        for (int i = 0; i < levels.length; i++) {
            course.addTopic(new Topic("Topic " + i, levels[i]));
        }
        return course;
    }

    @Test
    void subjectCodeIsTheLeadingLetters() {
        assertEquals("CSC", course("CSC207").getSubjectCode());
        assertEquals("MAT", course("mat137").getSubjectCode());
        assertEquals("STA", course("STA 247").getSubjectCode());
        assertEquals("207", course("207").getSubjectCode());
    }

    @Test
    void standardScores() {
        assertEquals(1.0, SCORING.score(ConfidenceLevel.GREEN), DELTA);
        assertEquals(0.5, SCORING.score(ConfidenceLevel.YELLOW), DELTA);
        assertEquals(0.0, SCORING.score(ConfidenceLevel.RED), DELTA);
    }

    @Test
    void standardLevelThresholds() {
        assertEquals(ConfidenceLevel.GREEN, SCORING.levelFor(1.0));
        assertEquals(ConfidenceLevel.GREEN, SCORING.levelFor(0.75));
        assertEquals(ConfidenceLevel.YELLOW, SCORING.levelFor(0.5));
        assertEquals(ConfidenceLevel.YELLOW, SCORING.levelFor(0.34));
        assertEquals(ConfidenceLevel.RED, SCORING.levelFor(0.25));
        assertEquals(ConfidenceLevel.RED, SCORING.levelFor(0.0));
    }

    @Test
    void averageCountsEveryTopicOfEveryCourse() {
        final Subject subject = new Subject("MAT");
        subject.addCourse(course("MAT137", ConfidenceLevel.GREEN, ConfidenceLevel.RED));
        subject.addCourse(course("MAT223", ConfidenceLevel.GREEN, ConfidenceLevel.YELLOW));
        // (1 + 0 + 1 + 0.5) / 4
        assertEquals(0.625, subject.getAverageConfidence(SCORING).getAsDouble(), DELTA);
        assertEquals(ConfidenceLevel.YELLOW, subject.getOverallConfidence(SCORING).get());
        assertEquals(4, subject.getTopicCount());
        assertEquals(2, subject.countTopics(ConfidenceLevel.GREEN));
        assertEquals(1, subject.countTopics(ConfidenceLevel.YELLOW));
        assertEquals(1, subject.countTopics(ConfidenceLevel.RED));
    }

    @Test
    void subjectWithoutTopicsHasNoAverage() {
        final Subject subject = new Subject("CSC");
        subject.addCourse(course("CSC207"));
        assertFalse(subject.getAverageConfidence(SCORING).isPresent());
        assertFalse(subject.getOverallConfidence(SCORING).isPresent());
    }

    @Test
    void courseFromAnotherSubjectIsRejected() {
        final Subject subject = new Subject("CSC");
        assertThrows(IllegalArgumentException.class, () -> subject.addCourse(course("MAT137")));
    }

    @Test
    void emptyCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Subject(" "));
    }

    @Test
    void coursesAreReadOnly() {
        final Subject subject = new Subject("CSC");
        assertThrows(UnsupportedOperationException.class,
                () -> subject.getCourses().addAll(Collections.singletonList(course("CSC148"))));
        assertEquals(Arrays.asList(), subject.getCourses());
    }
}
