package data_access;

import com.mongodb.client.MongoDatabase;
import entity.Assessment;
import entity.ConfidenceLevel;
import entity.Course;
import entity.CourseFactory;
import entity.LetterGrade;
import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the Mongo DAOs against a real MongoDB server. They use (and then delete) a separate
 * test database, never the app's own one. They are skipped when no server can be reached;
 * set MONGODB_URI to run them against MongoDB Atlas.
 */
class MongoDataAccessObjectTest {

    private static final String TEST_DATABASE = "learning_tracker_test";

    /** Connected once for the whole class, so a missing server costs one timeout, not one per test. */
    private static MongoDatabase database;
    private static String unavailableReason;

    private MongoCourseDataAccessObject courseDao;
    private MongoUserDataAccessObject userDao;

    @BeforeAll
    static void connect() {
        final String uri = System.getenv(MongoConfig.URI_VARIABLE);
        try {
            database = MongoConfig.connect(uri == null || uri.isEmpty() ? MongoConfig.DEFAULT_URI : uri,
                    TEST_DATABASE);
        }
        catch (IllegalStateException ex) {
            unavailableReason = "No MongoDB server available: " + ex.getMessage();
        }
    }

    @BeforeEach
    void setUp() {
        Assumptions.assumeTrue(database != null, unavailableReason);
        database.drop();
        courseDao = new MongoCourseDataAccessObject(database);
        userDao = new MongoUserDataAccessObject(database, new UserFactory());
    }

    @AfterEach
    void tearDown() {
        if (database != null) {
            database.drop();
        }
    }

    private static Course course(String code, String name) {
        return new CourseFactory().create(code, name, LetterGrade.A_MINUS, Arrays.asList("Topic 1", "Topic 2"));
    }

    @Test
    void savesAndLoadsCourse() {
        final Course saved = course("CSC207", "Software Design");
        saved.setTopicConfidence("Topic 1", ConfidenceLevel.GREEN);
        saved.setAssessments(Collections.singletonList(new Assessment("Midterm", 70, 20)));
        courseDao.saveCourse("mia", saved);

        assertTrue(courseDao.courseExists("mia", " csc207 "));
        assertTrue(courseDao.existsByCode("mia", "CSC207"));
        final Course loaded = courseDao.getCourse("mia", "csc207");
        assertEquals("Software Design", loaded.getName());
        assertEquals(ConfidenceLevel.GREEN, loaded.getTopics().get(0).getConfidence());
        assertEquals(70.0, loaded.getCurrentPercentage().getAsDouble(), 1e-9);
    }

    @Test
    void missingCourseIsNull() {
        assertFalse(courseDao.courseExists("mia", "MAT137"));
        assertNull(courseDao.getCourse("mia", "MAT137"));
        assertNull(courseDao.getCourse("mia", null));
    }

    @Test
    void coursesBelongToTheirOwner() {
        courseDao.saveCourse("mia", course("CSC207", "Software Design"));
        assertFalse(courseDao.courseExists("paul", "CSC207"));
        assertTrue(courseDao.getCourses("paul").isEmpty());
    }

    @Test
    void savingAgainReplacesAndKeepsOrder() {
        courseDao.saveCourse("mia", course("CSC207", "Software Design"));
        courseDao.saveCourse("mia", course("MAT137", "Calculus"));
        courseDao.saveCourse("mia", course("STA247", "Probability"));

        final Course updated = courseDao.getCourse("mia", "CSC207");
        updated.setTargetGrade(LetterGrade.A_PLUS);
        courseDao.saveCourse("mia", updated);

        final List<Course> courses = courseDao.getCourses("mia");
        assertEquals(3, courses.size());
        assertEquals("CSC207", courses.get(0).getCode());
        assertEquals(LetterGrade.A_PLUS, courses.get(0).getTargetGrade());
        assertEquals("MAT137", courses.get(1).getCode());
        assertEquals("STA247", courses.get(2).getCode());
    }

    @Test
    void savesAndLoadsUser() {
        assertFalse(userDao.existsByName("mia"));
        assertNull(userDao.get("mia"));

        userDao.save(new User("mia", "secret"));
        assertTrue(userDao.existsByName("mia"));
        assertEquals("secret", userDao.get("mia").getPassword());

        userDao.changePassword(new User("mia", "new-secret"));
        assertEquals("new-secret", userDao.get("mia").getPassword());
    }
}
