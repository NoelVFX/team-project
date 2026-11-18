package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;

/**
 * A subject, such as "CSC" or "MAT", grouping every course a student is enrolled in
 * whose code starts with that subject code.
 */
public class Subject {

    private final String code;
    private final List<Course> courses = new ArrayList<>();

    /**
     * Creates a subject with no courses.
     * @param code the non-empty subject code, such as "MAT"
     * @throws IllegalArgumentException if the code is empty
     */
    public Subject(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject code cannot be empty");
        }
        this.code = code.trim();
    }

    public String getCode() {
        return code;
    }

    /**
     * Returns the courses of this subject, in the order they were added.
     * @return a read-only view of the courses
     */
    public List<Course> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    /**
     * Adds a course to this subject.
     * @param course the course to add
     * @throws IllegalArgumentException if the course is null or belongs to another subject
     */
    public void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (!course.getSubjectCode().equalsIgnoreCase(code)) {
            throw new IllegalArgumentException(
                    course.getCode() + " does not belong to subject " + code);
        }
        courses.add(course);
    }

    /**
     * Returns how many topics of this subject's courses have the given confidence level.
     * @param level the confidence level to count
     * @return the number of topics at that level
     */
    public int countTopics(ConfidenceLevel level) {
        int count = 0;
        for (Course course : courses) {
            for (Topic topic : course.getTopics()) {
                if (topic.getConfidence() == level) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns the total number of topics in this subject's courses.
     * @return the number of topics
     */
    public int getTopicCount() {
        int count = 0;
        for (Course course : courses) {
            count += course.getTopics().size();
        }
        return count;
    }

    /**
     * Returns the student's average confidence over every topic of every course in this
     * subject, using the score of each topic's confidence level. Every topic counts the same,
     * so a course with more topics has more influence.
     * @return the average confidence from 0 to 1, or empty if the subject has no topics
     */
    public OptionalDouble getAverageConfidence() {
        double total = 0;
        int count = 0;
        for (Course course : courses) {
            for (Topic topic : course.getTopics()) {
                total += topic.getConfidence().getScore();
                count++;
            }
        }
        if (count == 0) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(total / count);
    }
}
