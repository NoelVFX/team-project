package data_access;

import entity.Assessment;
import entity.Course;
import entity.Topic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A Decorator that keeps each user's courses in memory, so that repeated reads do not go to
 * the database every time.
 *
 * <p>It implements the same {@link CourseDataAccess} interface as the object it wraps, so the
 * use cases cannot tell the difference. The first read for a user loads all of that user's
 * courses from the wrapped object with one call to {@link #getCourses(String)}; later reads are
 * answered from memory. Saves are written through: the wrapped object is updated first, and the
 * cache only changes if that succeeds.</p>
 *
 * <p>The cache stores and hands out copies of the courses. An interactor that changes a course
 * and then fails before saving it therefore cannot leave an unsaved change in the cache.</p>
 *
 * <p>Changes made to the database by another running copy of the program are not seen until
 * the cache is cleared with {@link #clear()} or the program restarts.</p>
 */
public class CachingCourseDataAccessObject implements CourseDataAccess {

    private final CourseDataAccess delegate;

    /** Maps a username to that user's courses by upper-case course code, in enrolment order. */
    private final Map<String, Map<String, Course>> coursesByUser = new HashMap<>();

    /**
     * Creates a cache in front of another course data access object.
     * @param delegate the object that actually stores the courses, such as the MongoDB DAO
     */
    public CachingCourseDataAccessObject(CourseDataAccess delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("The wrapped data access object cannot be null");
        }
        this.delegate = delegate;
    }

    @Override
    public boolean existsByCode(String username, String courseCode) {
        return courseExists(username, courseCode);
    }

    @Override
    public boolean courseExists(String username, String courseCode) {
        return courseCode != null && coursesOf(username).containsKey(key(courseCode));
    }

    @Override
    public Course getCourse(String username, String courseCode) {
        if (courseCode == null) {
            return null;
        }
        final Course cached = coursesOf(username).get(key(courseCode));
        if (cached == null) {
            return null;
        }
        return copyOf(cached);
    }

    /**
     * Returns all of the user's courses in enrolment order.
     */
    @Override
    public List<Course> getCourses(String username) {
        final List<Course> result = new ArrayList<>();
        for (Course course : coursesOf(username).values()) {
            result.add(copyOf(course));
        }
        return result;
    }

    /**
     * Saves the course in the wrapped object, then in the cache. A replaced course keeps its
     * place in the enrolment order, as it does in the database.
     */
    @Override
    public void saveCourse(String username, Course course) {
        delegate.saveCourse(username, course);
        coursesOf(username).put(key(course.getCode()), copyOf(course));
    }

    /**
     * Forgets every cached course, so the next read goes back to the wrapped object.
     */
    public void clear() {
        coursesByUser.clear();
    }

    private Map<String, Course> coursesOf(String username) {
        Map<String, Course> courses = coursesByUser.get(username);
        if (courses == null) {
            courses = new LinkedHashMap<>();
            for (Course course : delegate.getCourses(username)) {
                courses.put(key(course.getCode()), copyOf(course));
            }
            coursesByUser.put(username, courses);
        }
        return courses;
    }

    private static Course copyOf(Course course) {
        final Course copy = new Course(course.getCode(), course.getName(), course.getTargetGrade());
        for (Topic topic : course.getTopics()) {
            copy.addTopic(new Topic(topic.getName(), topic.getConfidence()));
        }
        // Assessments cannot be changed after they are created, so they can be shared.
        copy.setAssessments(new ArrayList<Assessment>(course.getAssessments()));
        return copy;
    }

    private static String key(String courseCode) {
        return courseCode.trim().toUpperCase(Locale.ROOT);
    }
}
