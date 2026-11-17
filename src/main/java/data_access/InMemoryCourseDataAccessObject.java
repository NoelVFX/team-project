package data_access;

import entity.Course;
import use_case.view_course_progress.ViewCourseProgressCourseDataAccessInterface;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * In-memory implementation of the DAO for storing each user's courses.
 * This implementation does NOT persist data between runs of the program.
 */
public class InMemoryCourseDataAccessObject implements ViewCourseProgressCourseDataAccessInterface {

    /** Maps a username to that user's courses, which are keyed by upper-case course code. */
    private final Map<String, Map<String, Course>> coursesByUser = new HashMap<>();

    /**
     * Saves a course for the given user, replacing any course they have with the same code.
     * @param username the username of the student
     * @param course the course to save
     */
    public void save(String username, Course course) {
        if (!coursesByUser.containsKey(username)) {
            coursesByUser.put(username, new HashMap<String, Course>());
        }
        coursesByUser.get(username).put(toKey(course.getCode()), course);
    }

    @Override
    public boolean courseExists(String username, String courseCode) {
        return getCourse(username, courseCode) != null;
    }

    @Override
    public Course getCourse(String username, String courseCode) {
        final Map<String, Course> courses = coursesByUser.get(username);
        if (courses == null || courseCode == null) {
            return null;
        }
        return courses.get(toKey(courseCode));
    }

    private String toKey(String courseCode) {
        return courseCode.trim().toUpperCase(Locale.ROOT);
    }
}
