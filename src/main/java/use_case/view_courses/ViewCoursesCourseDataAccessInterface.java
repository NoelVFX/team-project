package use_case.view_courses;

import entity.Course;

import java.util.List;

/**
 * DAO interface for the View Enrolled Courses Use Case.
 */
public interface ViewCoursesCourseDataAccessInterface {

    /**
     * Returns every course the user is enrolled in, in enrolment order.
     * @param username the owner of the courses
     * @return the user's courses; empty if the user has none
     */
    List<Course> getCourses(String username);
}
