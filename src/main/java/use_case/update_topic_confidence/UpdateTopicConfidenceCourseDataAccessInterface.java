package use_case.update_topic_confidence;

import entity.Course;

/**
 * DAO interface for the Update Topic Confidence Use Case.
 */
public interface UpdateTopicConfidenceCourseDataAccessInterface {

    /**
     * Returns a user's course by course code.
     *
     * @param username the user who owns the course
     * @param courseCode the course code, compared ignoring case and spaces
     * @return the course, or null when it does not exist
     */
    Course getCourse(String username, String courseCode);

    /**
     * Saves an updated course for a user.
     *
     * @param username the user who owns the course
     * @param course the course to save
     */
    void saveCourse(String username, Course course);
}
