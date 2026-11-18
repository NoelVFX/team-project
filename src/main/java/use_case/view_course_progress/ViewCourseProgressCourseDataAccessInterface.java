package use_case.view_course_progress;

import entity.Course;

/**
 * DAO interface for the View Course Progress Use Case.
 */
public interface ViewCourseProgressCourseDataAccessInterface {

    /**
     * Checks whether the given user is enrolled in the given course.
     * @param username the username of the student
     * @param courseCode the code of the course to look for
     * @return true if the user is enrolled in a course with that code
     */
    boolean courseExists(String username, String courseCode);

    /**
     * Returns the given user's course with the given code.
     * @param username the username of the student
     * @param courseCode the code of the course to look up
     * @return the course, or null if the user is not enrolled in it
     */
    Course getCourse(String username, String courseCode);
}
