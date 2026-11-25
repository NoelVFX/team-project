package data_access;

import entity.Assessment;
import entity.ConfidenceLevel;
import entity.Course;
import entity.LetterGrade;
import entity.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CachingCourseDataAccessObjectTest {

    private static final String ALICE = "alice";

    private CountingCourseDataAccess database;
    private CachingCourseDataAccessObject cache;

    @BeforeEach
    void setUp() {
        database = new CountingCourseDataAccess();
        database.saveCourse(ALICE, course("CSC207", "Software Design"));
        database.saveCourse(ALICE, course("MAT137", "Calculus"));
        database.saveCalls = 0;
        cache = new CachingCourseDataAccessObject(database);
    }

    @Test
    void repeatedReadsLoadFromTheDatabaseOnlyOnce() {
        assertTrue(cache.courseExists(ALICE, "CSC207"));
        assertEquals("Software Design", cache.getCourse(ALICE, "csc207").getName());
        assertEquals(2, cache.getCourses(ALICE).size());
        assertFalse(cache.existsByCode(ALICE, "ECO101"));

        assertEquals(1, database.getCoursesCalls);
        assertEquals(0, database.getCourseCalls);
    }

    @Test
    void eachUserIsLoadedSeparately() {
        database.saveCourse("bob", course("PHY131", "Physics"));

        assertEquals(2, cache.getCourses(ALICE).size());
        assertEquals(1, cache.getCourses("bob").size());
        assertEquals(2, database.getCoursesCalls);
    }

    @Test
    void savesAreWrittenThroughAndReadBackFromTheCache() {
        cache.getCourses(ALICE);
        final Course updated = cache.getCourse(ALICE, "CSC207");
        updated.setTopicConfidence("Clean Architecture", ConfidenceLevel.GREEN);
        cache.saveCourse(ALICE, updated);

        assertEquals(1, database.saveCalls);
        assertEquals(ConfidenceLevel.GREEN,
                database.getCourse(ALICE, "CSC207").getTopics().get(0).getConfidence());
        assertEquals(ConfidenceLevel.GREEN,
                cache.getCourse(ALICE, "CSC207").getTopics().get(0).getConfidence());
        assertEquals(1, database.getCoursesCalls);
    }

    @Test
    void replacedCourseKeepsItsPlaceAndNewCourseGoesLast() {
        cache.saveCourse(ALICE, course("csc207", "Software Design Updated"));
        cache.saveCourse(ALICE, course("ECO101", "Economics"));

        final List<Course> courses = cache.getCourses(ALICE);
        assertEquals(3, courses.size());
        assertEquals("Software Design Updated", courses.get(0).getName());
        assertEquals("MAT137", courses.get(1).getCode());
        assertEquals("ECO101", courses.get(2).getCode());
    }

    @Test
    void changingAReturnedCourseWithoutSavingDoesNotChangeTheCache() {
        final Course unsaved = cache.getCourse(ALICE, "CSC207");
        unsaved.setTopicConfidence("Clean Architecture", ConfidenceLevel.GREEN);
        unsaved.setAssessments(Collections.singletonList(new Assessment("Midterm", 90, 30)));

        final Course fresh = cache.getCourse(ALICE, "CSC207");
        assertEquals(ConfidenceLevel.RED, fresh.getTopics().get(0).getConfidence());
        assertTrue(fresh.getAssessments().isEmpty());
    }

    @Test
    void failedSaveLeavesTheCacheUnchanged() {
        cache.getCourses(ALICE);
        database.failNextSave = true;

        assertThrows(IllegalStateException.class,
                () -> cache.saveCourse(ALICE, course("ECO101", "Economics")));
        assertFalse(cache.courseExists(ALICE, "ECO101"));
    }

    @Test
    void copiesKeepTopicsAssessmentsAndTargetGrade() {
        final Course original = course("STA247", "Probability");
        original.setTargetGrade(LetterGrade.B_PLUS);
        original.setAssessments(Collections.singletonList(new Assessment("Quiz", 80, 10)));
        cache.saveCourse(ALICE, original);

        final Course copy = cache.getCourse(ALICE, "STA247");
        assertNotSame(original, copy);
        assertEquals(LetterGrade.B_PLUS, copy.getTargetGrade());
        assertEquals("Clean Architecture", copy.getTopics().get(0).getName());
        assertEquals(80, copy.getAssessments().get(0).getMark());
    }

    @Test
    void clearMakesTheNextReadGoBackToTheDatabase() {
        cache.getCourses(ALICE);
        cache.clear();
        cache.getCourses(ALICE);

        assertEquals(2, database.getCoursesCalls);
    }

    @Test
    void nullCourseCodeIsNeverFound() {
        assertFalse(cache.courseExists(ALICE, null));
        assertNull(cache.getCourse(ALICE, null));
    }

    @Test
    void wrappingNothingIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new CachingCourseDataAccessObject(null));
    }

    private static Course course(String code, String name) {
        final Course course = new Course(code, name, LetterGrade.A_MINUS);
        course.addTopic(new Topic("Clean Architecture", ConfidenceLevel.RED));
        return course;
    }

    /**
     * Stands in for the database and counts how often it is called.
     */
    private static class CountingCourseDataAccess implements CourseDataAccess {

        private final Map<String, Map<String, Course>> coursesByUser = new LinkedHashMap<>();
        private int getCoursesCalls;
        private int getCourseCalls;
        private int saveCalls;
        private boolean failNextSave;

        @Override
        public boolean existsByCode(String username, String courseCode) {
            return courseExists(username, courseCode);
        }

        @Override
        public boolean courseExists(String username, String courseCode) {
            return getCourse(username, courseCode) != null;
        }

        @Override
        public Course getCourse(String username, String courseCode) {
            getCourseCalls++;
            return coursesByUser.getOrDefault(username, new LinkedHashMap<>())
                    .get(courseCode.toUpperCase(Locale.ROOT));
        }

        @Override
        public List<Course> getCourses(String username) {
            getCoursesCalls++;
            return new ArrayList<>(coursesByUser.getOrDefault(username, new LinkedHashMap<>()).values());
        }

        @Override
        public void saveCourse(String username, Course course) {
            if (failNextSave) {
                failNextSave = false;
                throw new IllegalStateException("Database unavailable");
            }
            saveCalls++;
            coursesByUser.computeIfAbsent(username, user -> new LinkedHashMap<>())
                    .put(course.getCode().toUpperCase(Locale.ROOT), course);
        }
    }
}
